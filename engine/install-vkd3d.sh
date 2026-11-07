#!/bin/bash
# Install VKD3D-Proton 3.1 (D3D12) into the Wine prefix. Run after setup-engine.sh
set -e
apt install -y zstd
cd ~
wget -nc https://github.com/HansKristian-Work/vkd3d-proton/releases/download/v3.1/vkd3d-proton-3.1.tar.zst
tar --zstd -xf vkd3d-proton-3.1.tar.zst
cp vkd3d-proton-3.1/x64/*.dll ${WINEPREFIX:-$HOME/prefix10}/drive_c/windows/system32/
cp vkd3d-proton-3.1/x86/*.dll ${WINEPREFIX:-$HOME/prefix10}/drive_c/windows/syswow64/
for d in d3d12 d3d12core; do
  box64 ~/wine10/bin/wine reg add 'HKCU\Software\Wine\DllOverrides' /v $d /d native /f || true
done
echo "VKD3D-Proton installed"
