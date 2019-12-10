
# @​infuse/react-native-clover

React Native native module for the [Clover SDK](https://github.com/clover/clover-android-sdk).

## Getting started

`$ yarn add @infuse/react-native-clover`

### Mostly automatic installation (Pre-0.60)

`$ react-native link @infuse/react-native-clover`

### Manual installation (Pre-0.60)

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.infuse.clover.bridge.RNCloverBridgePackage;` to the imports at the top of the file
  - Add `new RNCloverBridgePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':@infuse_react-native-clover'
  	project(':@infuse_react-native-clover').projectDir = new File(rootProject.projectDir, 	'../node_modules/@infuse/react-native-clover/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      implementation project(':@infuse_react-native-clover')
  	```


## Usage
```javascript
import Clover, { useScanner } from '@infuse/react-native-clover';

// Hook to register and listen to connected Clover scanner, tested on flex and mini gen 2
useScanner(callback, enabled);

Clover.authenticate(forceValidateToken: Boolean = false, timeout: Number = 10000) => ({
  success: Boolean,
  authToken: String,
  errorMessage: String,
})

Clover.getMerchant().then({ data } => { ... });

Clover.enableCustomerMode();
Clover.disableCustomerMode();

Clover.print(String imagePath).then(...);
/**
 * Print Payment Option
 * 
 * orderId: string - Required
 * paymentId: string - Required
 * flags: array - optional, array of PrintJob flags
 **/
Clover.printPayment(option);

// Use this in situations where you are not ensured to have account access permission, API 26+
Clover.startAccountChooserIfNeeded().then({ success: bool } => { ... });

// Register Scanner for listening to CLOVER.EVENT.BARCODE_SCANNER, tested on Flex and Mini Gen 2
Clover.registerScanner();
Clover.unregisterScanner();

Clover.isFlex();
Clover.isMini();
Clover.getSpaVersion();

// This should be called as early as possible during app load before calling any payment method
Clover.initializePaymentConnector(String raid);

/**
 * Sale Option
 *  
 * amount: int - Required
 * externalPaymentId: string - Required, unless generateExternalPaymentId is true
 * printReceipt: bool - optional, auto print receipt without selection
 * generateExternalPaymentId: bool - optional, unless externalPaymentId is not provided, default false
 * cardEntryMethods: int - optional, see CARD_ENTRY_METHODS, defaults to MAG_STRIPE | ICC_CONTACT | NFC_CONTACTLESS
 * disableDuplicateChecking: bool -  optional, default false
 * disableRestartTransactionOnFail: bool - optional, default false
 * disablePrinting: bool - optional, default false
 * disableReceiptSelection: bool - optional, default false
 * signatureEntryLocation: string - optional, see DATA_ENTRY_LOCATION, defaults to merchant settings
 * signatureThreshold: int - optional, defaults to merchant settings
 * autoAcceptSignature: boolean - optional, whether to ask for signature confirmation, default true
 * tipAmount: int - optional, if TipMode is set to TIP_PROVIDED, this must be set
 * tippableAmount: int - optional, amount used to calculate tip
 * tipMode: string - optional, see TIP_MODE, defaults to merchant settings
 * tipSuggestions: array - optional, see [TipSuggestions](https://docs.clover.com/clover-platform/docs/using-per-transaction-settings#section--tips-)
 *     TipSuggestion {
 *        name: string,
 *        percentage: int,
 *     }
 */
/**
 * Sale Result
 * 
 * success: bool
 * message: string
 * reason: string
 * payment: object
 */
Clover.sale(option).then(result => {});

/**
 * Refund Payment Option
 * 
 * paymentId: string - required
 * orderId: string - required
 * printReceipt: bool - optional, auto print receipt without selection
 * amount: int - optional, for partial refunds
 * setFullRefund: boolean: optional, overrides amount
 */
/**
 * Refund Payment Result
 * 
 * success: bool
 * message: string
 * reason: string
 * refund: object
 */
Clover.refundPayment(option).then(result => {});

/**
 * Manual Refund Option
 * 
 * amount: int - required
 * externalPaymentId: string - required
 * printReceipt: bool - optional, auto print receipt without selection
 */
/**
 * Manual Refund Result
 * 
 * success: bool
 * message: string
 * reason: string
 * credit: object
 */
Clover.manualRefund(option).then(result => {});

/**
 * Void Payment Option
 * 
 * paymentId: string, required
 * orderId: string, required
 * voidReason: string, required, see VOID_REASON
 * printReceipt: bool - optional, auto print receipt without selection
 */
/**
 * Void Payment Result
 * 
 * success: bool
 * message: string
 * reason: string
 * paymentId: string
 */
Clover.voidPayment(option).then(result => {});
```

## Contants

* `HARDWARE_SERIAL_NUMBER`
* [CARD_ENTRY_METHODS](https://docs.clover.com/clover-platform/docs/using-per-transaction-settings#section--other-functions-)
    * `MAG_STRIPE`
    * `ICC_CONTACT`
    * `NFC_CONTACTLESS`
    * `VAULTED_CARD`
    * `MANUAL`
    * `ALL`
* [DATA_ENTRY_LOCATION](https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/payments/DataEntryLocation.html)
* [VOID_REASON](https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/order/VoidReason.html)
* [TIP_MODE](https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/payments/TipMode.html)
    * `NO_TIP`
    * `TIP_PROVIDED`
    * `ON_SCREEN_BEFORE_PAYMENT`
* [PRINT_JOB_FLAG](https://clover.github.io/clover-android-sdk/com/clover/sdk/v1/printer/job/PrintJob.html)
* `EVENT`
    * `BARCODE_SCANNER`
  
## Troubleshooting

* If you aren't getting anything resolved when calling `getMerchant` try these following solutions:
  - Make sure that you are running this in an app that is registered on Clover.
  - Make sure you have account permission before running `getMerchant`. This can be an issue on Android 8.0+. See `startAccountChooserIfNeeded`.
  - If you've just uninstalled and reinstalled the app, it can take a while for the device to refresh the app's access. Restart the clover device to force a refresh.