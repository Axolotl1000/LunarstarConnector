#!/bin/bash

base_url="https://github.com/cloudflare/cloudflared/releases/download/2025.8.0"

declare -A files=(
  ["darwin-amd64"]="cloudflared-darwin-amd64.tgz"
  ["darwin-arm64"]="cloudflared-darwin-arm64.tgz"
  ["linux-386"]="cloudflared-linux-386"
  ["linux-amd64"]="cloudflared-linux-amd64"
  ["linux-arm"]="cloudflared-linux-arm"
  ["linux-arm64"]="cloudflared-linux-arm64"
  ["linux-armhf"]="cloudflared-linux-armhf"
  ["windows-386"]="cloudflared-windows-386.exe"
  ["windows-amd64"]="cloudflared-windows-amd64.exe"
)

mkdir -p downloads
cd downloads || exit 1

for platform in "${!files[@]}"; do
  file="${files[$platform]}"
  url="${base_url}/${file}"
  echo "⬇️ Downloading $file ..."
  curl -L -o "$file" "$url"

  if [[ "$file" == *.tgz ]]; then
    echo "📂 Unzip $file ..."
    tmpdir=$(mktemp -d)
    tar -xzf "$file" -C "$tmpdir"

    executable_path=$(find "$tmpdir" -type f -name "cloudflared" -perm /u+x | head -n 1)

    if [[ -z "$executable_path" ]]; then
      echo "⚠️ Cannot find executable inside $file"
      rm -rf "$tmpdir"
      rm -f "$file"
      continue
    fi

    mv "$executable_path" "$platform"
    rm -rf "$tmpdir"
    rm -f "$file"
  else
    mv "$file" "$platform"
  fi
done

chmod +x darwin-* linux-* 2>/dev/null || true

echo "✅ Done"
