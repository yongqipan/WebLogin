#!/usr/bin/env bash
# ============================================================
# WebLogin 编译与打包脚本
#
# 功能：
#   1. 构建前端（Vue 3 + Vite）
#   2. 构建后端（Spring Boot，生成可执行 jar）
#   3. 将前端静态资源合并进后端 jar（单 jar 即完整应用）
#   4. 用 jpackage 生成自包含应用目录（内嵌 JRE，免装 Java）
#   5. Windows 环境下可生成 msi / exe 安装包
#
# 用法：
#   ./build.sh                    完整构建 + 生成自包含应用目录
#   ./build.sh --type msi         完整构建 + 生成 Windows 安装包（需在 Windows 上运行）
#   ./build.sh --skip-frontend    跳过前端构建（复用已有产物）
#   ./build.sh --help             显示帮助
#
# 产物输出到项目根目录的 dist/ 下。
# ============================================================

set -euo pipefail

# ---------- 基础配置 ----------
APP_NAME="WebLogin"
BACKEND_JAR="login-backend-0.0.1-SNAPSHOT.jar"
MAIN_CLASS="org.springframework.boot.loader.launch.JarLauncher"
VENDOR="WebLogin"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"
OUT_DIR="$ROOT_DIR/dist"
STAGE_DIR="$OUT_DIR/.stage"

BUILD_FRONTEND=true
PKG_TYPE="app-image"

# ---------- 参数解析 ----------
usage() {
    sed -n '2,18p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
    exit 0
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --skip-frontend) BUILD_FRONTEND=false ;;
        --type) PKG_TYPE="${2:-app-image}"; shift ;;
        -h|--help) usage ;;
        *) echo "未知参数: $1"; exit 1 ;;
    esac
    shift
done

# ---------- 平台检测 ----------
detect_os() {
    case "$(uname -s)" in
        MINGW*|MSYS*|CYGWIN*) echo "windows" ;;
        Darwin*) echo "macos" ;;
        *) echo "linux" ;;
    esac
}
OS="$(detect_os)"
echo "==> 检测到平台: $OS"

# ---------- 依赖检查 ----------
check_deps() {
    local missing=()
    for cmd in node npm; do
        command -v "$cmd" >/dev/null 2>&1 || missing+=("$cmd")
    done
    for cmd in java mvn; do
        command -v "$cmd" >/dev/null 2>&1 || missing+=("$cmd")
    done
    if [[ ${#missing[@]} -gt 0 ]]; then
        echo "缺少依赖: ${missing[*]}，请先安装后再运行"
        exit 1
    fi
    echo "==> 依赖检查通过 (node/$(node -v) java/$(java -version 2>&1 | head -1 | sed 's/.*version "\([^"]*\)".*/\1/'))"
}

# ---------- 构建前端 ----------
build_frontend() {
    if [[ "$BUILD_FRONTEND" == false ]]; then
        if [[ -d "$FRONTEND_DIR/dist" ]]; then
            echo "==> 跳过前端构建，使用已有产物 $FRONTEND_DIR/dist"
            return
        fi
        echo "!! 已指定 --skip-frontend，但 frontend/dist 不存在，仍执行构建"
    fi

    echo "==> 构建前端..."
    cd "$FRONTEND_DIR"
    npm install
    npm run build
    echo "==> 前端构建完成: $FRONTEND_DIR/dist"
}

# ---------- 构建后端 ----------
build_backend() {
    echo "==> 构建后端..."
    cd "$BACKEND_DIR"
    mvn -q clean package -DskipTests
    echo "==> 后端构建完成: $BACKEND_DIR/target/$BACKEND_JAR"
}

# ---------- 合并前端产物到后端 jar ----------
merge_frontend_into_jar() {
    echo "==> 合并前端静态资源到后端 jar..."
    local merged_jar="$OUT_DIR/$BACKEND_JAR"
    mkdir -p "$OUT_DIR"
    rm -f "$merged_jar"
    cp "$BACKEND_DIR/target/$BACKEND_JAR" "$merged_jar"

    local static_root="$STAGE_DIR/BOOT-INF/classes/static"
    rm -rf "$STAGE_DIR"
    mkdir -p "$static_root"
    cp -r "$FRONTEND_DIR/dist/." "$static_root/"

    # 静态资源写进 jar 的 BOOT-INF/classes/static，由 Spring Boot 自动托管
    (cd "$STAGE_DIR" && zip -q -g -r "$merged_jar" BOOT-INF)

    rm -rf "$STAGE_DIR"
    echo "==> 单 jar 就绪: $merged_jar"
}

# ---------- 生成自包含应用 / 安装包 ----------
package_with_jpackage() {
    echo "==> 使用 jpackage 生成 ($PKG_TYPE) ..."

    # --input 与 --dest 必须分离：jpackage 会把 input 内容复制进应用目录，
    # 若两者同目录会导致自包含目录被递归复制、体积无限增长。
    local input_dir="$OUT_DIR/.jpackage-input"
    mkdir -p "$input_dir"
    rm -f "$input_dir"/*.jar
    cp "$OUT_DIR/$BACKEND_JAR" "$input_dir/"

    local jp_args=(
        --type "$PKG_TYPE"
        --name "$APP_NAME"
        --input "$input_dir"
        --main-jar "$BACKEND_JAR"
        --main-class "$MAIN_CLASS"
        --vendor "$VENDOR"
        --dest "$OUT_DIR"
    )

    if [[ "$PKG_TYPE" == "msi" || "$PKG_TYPE" == "exe" ]] && [[ "$OS" == "windows" ]]; then
        jp_args+=(--win-shortcut --win-menu --win-menu-group "$VENDOR")
    fi

    if ! jpackage "${jp_args[@]}"; then
        echo
        echo "!! jpackage 生成 $PKG_TYPE 失败。"
        if [[ "$PKG_TYPE" == "msi" || "$PKG_TYPE" == "exe" ]]; then
            echo "   Windows 安装包 (msi/exe) 必须在 Windows 环境运行本脚本。"
            echo "   Linux/macOS 请使用默认的 app-image 模式，或使用 rpm/deb。"
        fi
        exit 1
    fi

    rm -rf "$input_dir"
}

# ---------- 打包 zip（Linux/macOS 自包含应用目录压缩）----------
archive_app_image() {
    if [[ "$PKG_TYPE" != "app-image" ]]; then
        return
    fi
    local app_dir="$OUT_DIR/$APP_NAME"
    if [[ -d "$app_dir" ]]; then
        echo "==> 压缩自包含应用为 zip..."
        cd "$OUT_DIR"
        if command -v zip >/dev/null 2>&1; then
            zip -q -r "$APP_NAME.zip" "$APP_NAME"
            echo "==> 生成: $OUT_DIR/$APP_NAME.zip"
        else
            echo "==> 未安装 zip，跳过压缩。自包含应用目录在: $app_dir"
        fi
    fi
}

# ---------- 主流程 ----------
main() {
    check_deps
    build_frontend
    build_backend
    merge_frontend_into_jar

    if ! command -v jpackage >/dev/null 2>&1; then
        echo "!! 未找到 jpackage。已将单 jar 输出到 $OUT_DIR，跳过自包含打包。"
        exit 0
    fi

    package_with_jpackage
    archive_app_image

    echo
    echo "=============================================="
    echo "构建完成！产物在: $OUT_DIR"
    if [[ "$PKG_TYPE" == "app-image" ]]; then
        echo "  - 单 jar : $OUT_DIR/$BACKEND_JAR"
        echo "  - 自包含目录: $OUT_DIR/$APP_NAME/ (内嵌 JRE，免装 Java)"
        echo "  - zip 包 : $OUT_DIR/$APP_NAME.zip (可拷贝到任意机器解压运行)"
    fi
    echo "=============================================="
}

main
