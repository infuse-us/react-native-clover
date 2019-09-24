# Changelog

## [0.0.8](https://bitbucket.org/infuse-team/react-native-clover/compare/v0.0.8%0Dv0.0.7) - (2019-09-24)

### Added
- Add `HARDWARE_SERIAL_NUMBER` to constants. Clover uses a deprecated method to obtain this value and other libraries have started to migrate away.

### Changed
- Moved constants logic to a separate `Constants` class.
- Refactor how to initialize the `BridgePaymentConnector` in order to lower code reuse and simplify error handling when calling a paymentConnector method when it hasn't been initialized yet.

## [0.0.7](https://bitbucket.org/infuse-team/react-native-clover/compare/v0.0.7%0Dv0.0.6) - (2019-09-20)

### Added
- Add `BridgeServiceConnector` to handle Clover service connection rather than loading it on the ReactNativeModule.
- Add `printReceipt` flag to `sale`, `manualRefund`, `voidPayment` and `refundPayment`.
- Add `PrintJob` flags to constants. See [PrintJob](https://clover.github.io/clover-android-sdk/com/clover/sdk/v1/printer/job/PrintJob.html).
- Add `printPayment` ReactMethod to bridge.

### Changed
- Change `BridgePaymentConnectorListener` with additional overloaded `onResult` methods.
- Change `BridgePaymentConnector` and refactored to use `BridgeServiceConnector`. Update the `BridgePaymentConnector` methods. Add logic to handle `printReceipt`.
- Refactor `Payments` methods to DRY it up a bit.
- Change `PaymentConnectorListener` for `sale`, `manualRefund`, `voidPayment`, and `refundPayment` to use updated `BridgePaymentConnectorListener`.
- Update `index.d.ts` to reflect changes.

### Removed
- Remove all ServiceConnector logic and variables from `RNCloverBridgeModule`.

## [0.0.6](https://bitbucket.org/infuse-team/react-native-clover/compare/v0.0.6%0Dv0.0.5) - (2019-07-30)

### Added
- Expose `setFullRefund` to the JS bridge.

### Changed
- Clean up and refactor `BridgePaymentConnector` logic.

## [0.0.5](https://bitbucket.org/infuse-team/react-native-clover/compare/v0.0.5%0Dv0.0.4) - (2019-07-24)

### Added
- Add maps `TIP_MODE` to constants. See [TipMode](https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/payments/TipMode.html).
- Add `tipMode`, `tippableAmount`, `tipSuggestions`, and `tipAmount` as optional parameters to sale. See [Tips](https://docs.clover.com/clover-platform/docs/using-per-transaction-settings#section--tips-) for details on these settings.
- Add `autoAcceptSignature` as optional parameter to sale.
- Add method `authenticate` to module
- Experiment with adding JSDocs to d.ts file

### Changed
- Refactor PaymentConnectorListener logic

## [0.0.4](https://bitbucket.org/infuse-team/react-native-clover/compare/v0.0.4%0Dv0.0.3) - (2019-07-16)

### Changed
- Update Readme to reflect changes.

### Fixed
- Clean up some commented code.
- Add validation that bridgePaymentConnector has been initialized before calling payment methods.

## [0.0.3](https://bitbucket.org/infuse-team/react-native-clover/compare/v0.0.3%0Dv0.0.2) - (2019-07-16)

### Added
- Add a typing definition file.
- Add a .prettierrc file.
- Add maps `DATA_ENTRY_LOCATION` and `VOID_REASON` to constants.
- Add in additional payment methods (`VoidPayment`, `VoidPaymentRefund`).

### Changed
- *BREAKING* Add new method `initializePaymentConnector` that must be called with a RAID before the payment methods can be called.
- *BREAKING* Rename payment methods to standardize with what the Clover paymentConnector already calls them.
- Rename payment package classes and methods. Add a new `Payments` class to hold various static constants and methods.
- Clean up and refactor payment methods.
- Add validation checks to the payment method's option parameter.
- Update gradle tool and gradle wrapper versions to match RN ~0.60.
- Update react-native peerDependecies to support react-native@^0.60.

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