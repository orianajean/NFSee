#!/bin/bash

set -e

SCHEME="NFSee"
BUNDLE_ID="life.WorkingOnIt.WellnessTools.NFSee"
# Use a simulator that exists on current Xcode (iPhone 17 on Xcode 26; fallback: iPhone 16e, iPhone 15)
SIMULATOR_NAME="iPhone 17"
DESTINATION="platform=iOS Simulator,name=$SIMULATOR_NAME"
DERIVED_DATA="./build"

echo "🚀 Building $SCHEME..."
xcodebuild \
  -scheme "$SCHEME" \
  -destination "$DESTINATION" \
  -derivedDataPath "$DERIVED_DATA" \
  -quiet \
  build

APP_PATH="$DERIVED_DATA/Build/Products/Debug-iphonesimulator/NFSee.app"
if [[ ! -d "$APP_PATH" ]]; then
  echo "❌ Build failed: $APP_PATH not found"
  exit 1
fi

echo "📱 Booting simulator and launching app..."
# Boot the target simulator so we install to the correct OS version
xcrun simctl boot "$SIMULATOR_NAME" 2>/dev/null || true
open -a Simulator
sleep 3
xcrun simctl install booted "$APP_PATH"
xcrun simctl launch booted "$BUNDLE_ID"

echo "✅ NFSee is running on the simulator."
