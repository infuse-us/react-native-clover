
# @​infuse/react-native-clover

React Native native module for the [Clover SDK](https://github.com/clover/clover-android-sdk).

## Getting started

`$ yarn add https://bitbucket.org/infuse-team/react-native-clover.git`

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

### Search Orders

```javascript
import Clover from '@infuse/react-native-clover';

 Clover.searchOrders(
      50,             // limit
      0,              // offset
      null,           // sort category
      null,           // sort order
      null,           // search category, i.e. 'CREATED_TIME', 'ID'
      null            // search term, i.e '1570728546000-1580737599999', 'KDJ'
    ).then(({ orders }) => { ... });
```
You can query Clover database for orders that match sorting or searching criteria. This method can also be used as a generic fetch method for Clover orders.

#### Arguments
| Position   | Name               | Type             | Can be NULL        | Default            | Description                                       | 
| -----------| ------------------ | ---------------- | -------------------|------------------- | --------------------------------------------------|
| 1          | Limit              | number           | No                 |      -             | The number of orders that will be returned        | 
| 2          | Offset             | number           | No                 |      -             | Excludes the first N records                      | 
| 3          | Sort Category      | string           | Yes                | 'LAST_MODIFIED'    | The category that orders will be sorted against   | 
| 4          | Sort Order         | string           | Yes                |  'ASC'             | Sort order - ASC or DESC                          | 
| 5          | Search Category    | string           | Yes                |   null             | The category that orders will be searched against | 
| 6          | Search Term        | string           | Yes                |   null             | The keyword that orders will be searched against  | 

#### Supported Categories
The method supports these categories for sorting and searching:
  * AMOUNT_CREDITED
  * AMOUNT_PAID
  * AMOUNT_REFUNDED
  * CREATED_TIME
  * CURRENCY
  * CUSTOMER_ID
  * CUSTOMER_NAME
  * DELETED
  * EMPLOYEE_NAME
  * ID
  * LAST_MODIFIED
  * NOTE
  * ORDER_TYPE
  * PAYMENT_STATE
  * STATE
  * TENDERS
  * TITLE
  * TOTAL

More information here: https://clover.github.io/clover-android-sdk/com/clover/sdk/v3/order/OrderContract.SummaryColumns.html

If search category is `CREATED_TIME` or `LAST_MODIFIED`, you must provide start time and end time for the query to work. The search term has to be in format `'{startTime}-{endTime}'` where startTime and endTime are Unix timestamps in miliseconds. For example:

```javascript
  Clover.searchOrders(
    50,             // limit
    0,              // offset
    null,           // sort category
    null,           // sort order
    'LAST_MODIFIED',
    '1570728546000-1580737599999'
  ).then(({ orders }) => { ... });
```
  The result orders are similar to what would be returned from Clover REST API call. The method also returns all column data from matching orders:
  ```javascript
   Clover.searchOrders(
      50,             // limit
      0,              // offset
      null,           // sort category
      null,           // sort order
      null,           // search category
      null            // search term
    ).then(({ allColumnData }) => { ... });
  ```
  An example:

 ![all column data example](https://cdn-std.droplr.net/files/acc_717368/TLV5Ha)


### Get merchant
```javascript
Clover.getMerchant().then({ data } => { ... });
```
### Enter Customer Mode
```javascript
Clover.enableCustomerMode();
Clover.disableCustomerMode();
```
### Print from an image
```javascript
Clover.print(String imagePath).then(...);
```
### Print with option
```javascript
/**
 * Print Payment Option
 * 
 * orderId: string - Required
 * paymentId: string - Required
 * flags: array - optional, array of PrintJob flags
 **/
Clover.printPayment(option);
```
### Hook to register and listen to connected Clover scanner, tested on flex and mini gen 2
```javascript
import Clover, { useScanner } from '@infuse/react-native-clover';

useScanner(callback, enabled);
```
### Authenticate with Clover
```javascript
Clover.authenticate(forceValidateToken: Boolean = false, timeout: Number = 10000) => ({
  success: Boolean,
  authToken: String,
  errorMessage: String,
})
```
### Use this in situations where you are not ensured to have account access permission, API 26+
```javascript
Clover.startAccountChooserIfNeeded().then({ success: bool } => { ... });
```
### Register Scanner for listening to CLOVER.EVENT.BARCODE_SCANNER, tested on Flex and Mini Gen 2
```javascript
Clover.registerScanner();
Clover.unregisterScanner();
```
### Helper methods to detect Clover device types
```javascript
Clover.isFlex();
Clover.isMini();
Clover.getSpaVersion();
```
## Payment Methods
### This should be called as early as possible during app load before calling any payment method
```javascript
Clover.initializePaymentConnector(String raid);
```
### Sale option
```javascript
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
```

### Refund Payment option
```javascript
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
```
### Manual Refund
```javascript
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
```
### Void Payment
```javascript
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
## Constants

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