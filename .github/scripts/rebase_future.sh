#!/usr/bin/env bash
set -euo pipefail

# Run in a disposable, fully fetched checkout of this Irongoon repository.
git fetch origin refs/heads/main.future:refs/remotes/origin/main.future
before=$(git rev-parse refs/remotes/origin/main.future)
git checkout --detach "$before"

if ! git merge-base --is-ancestor "$MAIN_SHA" HEAD; then
  git cat-file -e "$MAIN_SHA^{commit}"
  if ! expected_tree=$(git merge-tree --write-tree "$MAIN_SHA" "$before"); then
    echo "main.future requires manual conflict resolution; its remote branch was not changed" >&2
    exit 1
  fi
  # Rebase first-parent changes, preserving merge resolutions as ordinary commits.
  # Recreating old merges with --rebase-merges can lose merge-only fixes.
  mapfile -t commits < <(git rev-list --reverse --first-parent "$MAIN_SHA..$before")
  git checkout --detach "$MAIN_SHA"
  for commit in "${commits[@]}"; do
    if git diff-tree --quiet "$commit^1" "$commit"; then
      continue
    fi
    read -ra parents <<< "$(git rev-list --parents -n 1 "$commit")"
    options=()
    if [[ ${#parents[@]} -gt 2 ]]; then
      options=(-m 1)
    fi
    if ! git cherry-pick -x --empty=drop "${options[@]}" "$commit"; then
      git cherry-pick --abort
      echo "main.future requires manual conflict resolution; its remote branch was not changed" >&2
      exit 1
    fi
  done
  if [[ "$(git rev-parse HEAD^{tree})" != "$expected_tree" ]]; then
    echo "Rebased content differs from the clean merge result; main.future was not changed" >&2
    exit 1
  fi
fi

after=$(git rev-parse HEAD)
if [[ "$PUBLISH_FUTURE" == "true" && "$before" != "$after" ]]; then
  # The explicit lease refuses to replace concurrent work on main.future.
  git push --force-with-lease="refs/heads/main.future:$before" origin HEAD:refs/heads/main.future
fi
echo "irongoon_sha=$after" >> "$GITHUB_OUTPUT"
echo "main.future: $before -> $after"
