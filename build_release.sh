#!/bin/bash
set -e

echo "Building signed Release App Bundle (.abb) and APK (.apk)..."
STORE_PASSWORD=android KEY_PASSWORD=android gradle :app:bundleRelease :app:assembleRelease

echo "Done! The signed release outputs are ready in folder Release/:"
echo " - Release/Release.abb (Signed Bundle for Google Play)"
echo " - Release/Release.apk (Signed APK for direct install/testing)"
