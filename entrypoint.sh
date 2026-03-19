#!/bin/bash

# Exit on error
set -e

echo "========================================="
echo " Starting Virtual Desktop Environment    "
echo "========================================="

# 1. Start Xvfb (Virtual Framebuffer)
# Screen 0 with 1280x800 resolution and 24-bit color
echo "-> Starting Xvfb on display :1"
Xvfb :1 -screen 0 1280x800x24 &
export DISPLAY=:1

# Wait for Xvfb to be ready
sleep 1

# 2. Start Fluxbox (Window Manager)
# This is necessary so the JavaFX windows can be moved/resized
echo "-> Starting Fluxbox window manager"
fluxbox &

# 3. Start x11vnc Server
# -nopw runs without password (safe inside a container for local testing)
echo "-> Starting VNC server"
x11vnc -display :1 -nopw -forever -shared -bg

# 4. Start noVNC (Web GUI)
# This proxies VNC traffic to WebSockets on port 8080
echo "-> Starting noVNC on port 8080"
if [ -d "/usr/share/novnc" ]; then
    /usr/share/novnc/utils/launch.sh --vnc localhost:5900 --listen 8080 &
elif [ -d "/usr/share/novnc-utils" ]; then
    websockify --web /usr/share/novnc 8080 localhost:5900 &
else
    echo "Warning: noVNC launch script not found in standard paths, trying websockify directly"
    websockify --web /usr/share/novnc 8080 localhost:5900 &
fi

echo "========================================="
echo " Starting Java Application               "
echo "========================================="

# 5. Run the JavaFX Application
# Use exec to forward signals
exec java -jar app.jar
