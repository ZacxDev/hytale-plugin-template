#!/usr/bin/env bash
#
# Validates PNG textures for Hytale compatibility.
# All PNGs MUST be 8-bit/channel RGBA. 16-bit PNGs crash the client.
#
# Usage: ./scripts/validate-textures.sh [directory]
#        Default directory: src/main/resources
#
# Requirements: file command (coreutils), optionally ImageMagick for auto-fix

set -euo pipefail

SEARCH_DIR="${1:-src/main/resources}"
ERRORS=0
FIXED=0

if [[ ! -d "$SEARCH_DIR" ]]; then
    echo "Error: Directory '$SEARCH_DIR' not found"
    exit 1
fi

echo "Validating PNG textures in: $SEARCH_DIR"
echo "---"

while IFS= read -r -d '' png; do
    info=$(file "$png")

    if echo "$info" | grep -q "16-bit"; then
        echo "INVALID: $png"
        echo "         Found: 16-bit/channel (will crash Hytale client)"
        echo "         Required: 8-bit/channel RGBA"

        # Auto-fix if ImageMagick is available
        if command -v magick &> /dev/null; then
            echo "         Fixing with ImageMagick..."
            magick "$png" -depth 8 -type TrueColorAlpha "PNG32:$png"
            echo "         Fixed!"
            ((FIXED++))
        else
            echo "         To fix: magick \"$png\" -depth 8 -type TrueColorAlpha PNG32:\"$png\""
            ((ERRORS++))
        fi
        echo ""
    elif echo "$info" | grep -q "8-bit/color RGBA"; then
        echo "OK: $png"
    elif echo "$info" | grep -q "PNG image"; then
        # PNG but not RGBA - might be RGB, grayscale, etc.
        echo "WARN: $png"
        echo "      Format: $(echo "$info" | sed 's/.*PNG image data, //')"
        echo "      Expected: 8-bit/color RGBA"
        echo ""
    fi
done < <(find "$SEARCH_DIR" -name "*.png" -type f -print0)

echo "---"
if [[ $ERRORS -gt 0 ]]; then
    echo "Found $ERRORS invalid texture(s). Install ImageMagick to auto-fix."
    exit 1
elif [[ $FIXED -gt 0 ]]; then
    echo "Fixed $FIXED texture(s)."
    exit 0
else
    echo "All textures valid."
    exit 0
fi
