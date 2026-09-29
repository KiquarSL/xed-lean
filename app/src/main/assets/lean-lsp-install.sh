#!/system/bin/sh
set -e

source "utils" 2>/dev/null

# CONFIGURATION
LEAN_VERSION="4.34.1"
ELAN_BIN="$HOME/.elan/bin"
LEAN_BIN="$ELAN_BIN/lean"

# HELPERS
check_lean() {
    if [ ! -x "$LEAN_BIN" ]; then
        error "Lean not found at $LEAN_BIN"
        error "Install Lean first: https://lean-lang.org/install/"
        exit 1
    fi

    local installed_version
    installed_version=$("$LEAN_BIN" --version 2>/dev/null | head -n1 | awk '{print $NF}' || echo "unknown")

    if [ "$installed_version" != "$LEAN_VERSION" ]; then
        warn "Lean version mismatch: expected $LEAN_VERSION, got $installed_version"
        warn "LSP may not work correctly. Update Lean via elan."
    fi

    info "Lean found: $installed_version"
}

# MAIN
case "$1" in
    --uninstall)
        info "Nothing to uninstall — LSP uses the system Lean installation."
        info "To remove Lean itself, use elan: elan self uninstall"
        exit 0
        ;;
    --update)
        info "Checking Lean installation..."
        check_lean
        info "Update Lean via elan: elan update"
        exit 0
        ;;
    *)
        check_lean

        if ! grep -q "export PATH=\$PATH:\$HOME/.elan/bin" ~/.bashrc 2>/dev/null; then
            echo "export PATH=\$PATH:\$HOME/.elan/bin" >> ~/.bashrc
        fi

        info "All done! Lean LSP is ready to use."
        exit 0
        ;;
esac