#!/bin/bash
set -e

echo "Building signed Release App Bundle (.aab)..."
STORE_PASSWORD=android KEY_PASSWORD=android gradle :app:bundleRelease

echo "Done! The signed release bundle is located at:"
echo " - Release/Release.aab"
echo " - Release/Release.abb"
echo " - Release/app-release.aab"
echo " - Release.aab"
echo " - Release.abb"
