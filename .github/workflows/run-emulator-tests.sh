#!/usr/bin/env bash
set -eu

echo "===== Stabilizing ADB ====="
adb kill-server || true

i=1
while [ "$i" -le 5 ]; do
  if adb start-server; then
    break
  fi
  echo "adb start-server failed (attempt $i/5)"
  sleep 2
  i=$((i + 1))
done

i=1
while [ "$i" -le 5 ]; do
  if adb devices; then
    break
  fi
  echo "adb devices failed (attempt $i/5)"
  sleep 2
  i=$((i + 1))
done

echo "===== ADB Devices ====="
adb devices

echo "===== Waiting for Android Boot ====="
adb wait-for-device

until [ "$(adb shell getprop sys.boot_completed | tr -d '\r')" = "1" ]; do
  echo "Android not fully booted yet..."
  sleep 2
done

until [ "$(adb shell getprop init.svc.bootanim | tr -d '\r')" = "stopped" ]; do
  echo "Boot animation still running..."
  sleep 2
done

adb shell input keyevent 82 || true
adb shell wm dismiss-keyguard || true

echo "===== Device Information ====="
adb shell getprop ro.build.version.release
adb shell getprop ro.product.model

echo "===== Starting Appium ====="
appium --address 127.0.0.1 --port 4723 --base-path / --log appium.log > appium-console.log 2>&1 &

echo "===== Waiting for Appium ====="
i=1
until curl -fsS http://127.0.0.1:4723/status >/dev/null 2>&1; do
  if [ "$i" -ge 30 ]; then
    echo "Timed out waiting for Appium"
    exit 1
  fi
  echo "Waiting for Appium (attempt $i/30)"
  sleep 2
  i=$((i + 1))
done

echo "===== Verifying APK ====="
if [ ! -f apps/app.apk ]; then
  echo "ERROR: apps/app.apk not found"
  ls -la apps || true
  exit 1
fi

echo "===== Running Maven Tests ====="
mvn --batch-mode --no-transfer-progress clean test -DexplicitWait=60

echo "===== Generating Allure HTML Report ====="
mvn --batch-mode --no-transfer-progress allure:report
