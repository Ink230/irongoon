#!/usr/bin/env bash
set -euo pipefail

# Only the two automated channels may be replaced; manual version tags are separate.
case "$RELEASE_TAG" in
  irongoon-latest|irongoon-future) ;;
  *) echo "Unsupported automated release tag: $RELEASE_TAG" >&2; exit 1 ;;
esac

shopt -s nullglob
assets=("$BUNDLE_DIR"/*.zip)
if [[ ${#assets[@]} -ne "$ASSET_COUNT" ]]; then
  echo "Expected $ASSET_COUNT ZIPs; found ${#assets[@]}" >&2
  exit 1
fi

if gh release view "$RELEASE_TAG" > /dev/null 2>&1; then
  gh release upload "$RELEASE_TAG" "${assets[@]}" --clobber
  gh api --method PATCH "repos/${GH_REPO}/git/refs/tags/${RELEASE_TAG}" -f sha="$RELEASE_SHA" -F force=true
  gh release edit "$RELEASE_TAG" --title "$RELEASE_TITLE" --notes-file "$NOTES_FILE" --prerelease
  # Retire old versions/platform bundles only after all new uploads succeed.
  gh release view "$RELEASE_TAG" --json assets --jq '.assets[].name' > "$RUNNER_TEMP/release-assets.txt"
  while IFS= read -r asset; do
    if [[ ! -f "$BUNDLE_DIR/$asset" ]]; then
      gh release delete-asset "$RELEASE_TAG" "$asset" --yes
    fi
  done < "$RUNNER_TEMP/release-assets.txt"
else
  gh release create "$RELEASE_TAG" "${assets[@]}" --target "$RELEASE_SHA" --title "$RELEASE_TITLE" --notes-file "$NOTES_FILE" --prerelease
fi
