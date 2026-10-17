#!/usr/bin/env sh
# Lightweight Gradle bootstrap for GitHub Actions
set -eu
GRADLE_VERSION=8.9
DIST="${HOME}/.gradle/wrapper/dists/gradle-${GRADLE_VERSION}-bin/manual"
mkdir -p "$DIST"
if [ ! -x "$DIST/gradle-${GRADLE_VERSION}/bin/gradle" ]; then
  ARCHIVE="$DIST/gradle-${GRADLE_VERSION}-bin.zip"
  if [ ! -f "$ARCHIVE" ]; then
    curl -fsSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "$ARCHIVE"
  fi
  unzip -q -o "$ARCHIVE" -d "$DIST"
fi
exec "$DIST/gradle-${GRADLE_VERSION}/bin/gradle" "$@"
