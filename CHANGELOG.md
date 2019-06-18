# Changelog

## [0.0.2](https://bitbucket.org/infuse-team/react-native-clover/compare/v0.0.2%0Dv0.0.1) - (2019-06-18)

### Removed
- Remove unneeded android permissions from the manifest.

### Fixed
- Fix styling in Changelog.

## [0.0.1](https://bitbucket.org/infuse-team/react-native-clover/commits/tag/v0.0.1) - (2019-06-18)
This is the initial versioned/tracked release of `@infuse/react-native-clover` as part of our initiative to create a shared/reusable React Native native module for the Clover SDK.

### Added
- Add a Changelog.
- Add [@infuse/eslint-config](https://bitbucket.org/infuse-team/eslint-config-infuse/src/master/) and create a .eslintrc.

### Changed
- Rename repo from `clover-sdk-react-native-bridge` to `react-native-clover`. Also update Android code and JS code to reflect this change.
- Reset Version to 0.0.1 as part of this clean up.
- Update .gitignore to match one created from `react-native-cli`.
- Update Readme to reflect new changes.
- Update package.json to reflect new changes.

### Removed
- Remove react methods that were unrelated to the Clover SDK. The list of removed methods are:
  * `getSSID`
  * `getConnectionStrength`
  * `getIPAddress`
  * `getEthernetMacAddress`
  * `enabledSound`
  * `disableSound`
These will be extracted into a separate react native native module for use.
- Remove react methods and android code related to starting external payments through Clover. These will be extracted into a separate react native module for use. This also includes removal of the '/libs' folder.

### Fixed
- Clean up various parts of the android code.