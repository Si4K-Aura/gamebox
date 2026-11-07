#!/bin/bash
# GAMEBOX engine setup: run inside Ubuntu (proot) as root
set -e
apt update && apt upgrade -y
apt install -y git build-essential cmake python3 wget curl xz-utils \
  libvulkan1 vulkan-tools libsdl2-2.0-0 libusb-1.0-0 libxkbcommon0 \
  libwayland-client0 libgstreamer1.0-0 libgstreamer-plugins-base1.0-0 \
  libx11-6 libxext6 libxxf86vm1 libpulse0 gcc-mingw-w64-x86-64

# Box64
cd ~
[ -d box64 ] || git clone --depth 1 https://github.com/ptitSeb/box64.git
mkdir -p box64/build && cd box64/build
cmake .. -DARM_DYNAREC=ON -DCMAKE_BUILD_TYPE=RelWithDebInfo
make -j4
make install

# Wine 10.0 (not 11.x, it crashes here)
if [ ! -d ~/wine10/bin ]; then
  mkdir -p ~/wine10 && cd ~/wine10
  wget https://github.com/Kron4ek/Wine-Builds/releases/download/10.0/wine-10.0-amd64-wow64.tar.xz
  tar -xf wine-10.0-amd64-wow64.tar.xz --strip-components=1
  rm wine-10.0-amd64-wow64.tar.xz
fi

# Mesa / Turnip for Adreno
cd ~
wget -nc https://github.com/lfdevs/mesa-for-android-container/releases/download/mesa-26.3.0-devel-20260824/mesa-for-android-container_26.3.0-devel-20260824_ubuntu_resolute_arm64.tar.gz
tar -zxf mesa-for-android-container_*.tar.gz -C /
ldconfig

# Environment
grep -q WINEPREFIX ~/.bashrc || cat >> ~/.bashrc <<'EOF'
export BOX64_LOG=0
export WINEPREFIX=$HOME/prefix10
export WINEDEBUG=fixme-all
EOF
export BOX64_LOG=0 WINEPREFIX=$HOME/prefix10 WINEDEBUG=fixme-all

# DXVK 2.4.1
cd ~
wget -nc https://github.com/doitsujin/dxvk/releases/download/v2.4.1/dxvk-2.4.1.tar.gz
tar -xzf dxvk-2.4.1.tar.gz
box64 ~/wine10/bin/wine wineboot -u || true
box64 ~/wine10/bin/wineserver -w || true
cp dxvk-2.4.1/x64/*.dll $WINEPREFIX/drive_c/windows/system32/
cp dxvk-2.4.1/x32/*.dll $WINEPREFIX/drive_c/windows/syswow64/
for d in d3d9 d3d10core d3d11 dxgi; do
  box64 ~/wine10/bin/wine reg add 'HKCU\Software\Wine\DllOverrides' /v $d /d native /f || true
done
echo "GAMEBOX engine setup done"
