#!/bin/bash
set -e

echo "Building signed Release App Bundle (.aab) and APK (.apk)..."
STORE_PASSWORD=android KEY_PASSWORD=android gradle :app:bundleRelease :app:assembleRelease

echo "Done! The signed release outputs are located at:"
echo " - Release/Release.apk (Signed APK)"
echo " - Release/app-release.apk (Signed APK)"
echo " - Release/Release.aab (Signed AAB Bundle)"
echo " - Release/Release.abb (Signed AAB/ABB Bundle)"
echo " - Release.apk"
echo " - Release.aab"
echo " - Release.abb"
