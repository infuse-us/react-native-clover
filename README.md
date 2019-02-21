
# clover-sdk-react-native-bridge

## Getting started

`$ yarn add https://bitbucket.cardconnect.com/scm/clov/clover-sdk-react-native-bridge.git`

### Mostly automatic installation

`$ react-native link clover-sdk-react-native-bridge`

### Manual installation



#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.infuse.cloversdkreactnativebridge.RNCloverBridgePackage;` to the imports at the top of the file
  - Add `new RNCloverBridgePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':clover-sdk-react-native-bridge'
  	project(':clover-sdk-react-native-bridge').projectDir = new File(rootProject.projectDir, 	'../node_modules/clover-sdk-react-native-bridge/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      implementation project(':clover-sdk-react-native-bridge')
  	```


## Usage
```javascript
import Clover from 'clover-sdk-react-native-bridge';

Clover.getMerchant().then(merchant => { ... });

Clover.enableCustomerMode();
Clover.disableCustomerMode();

Clover.print(String imagePath).then(...);

// bitwise-or cardEntryFlag constants to allow multiple entry methods
Clover.startExternalSecurePayment({
  amount: int,
  externalService: string,
  cardEntryFlag: Clover.CARD_ENTRY_METHOD.MAG_STRIPE,
}).then(...);

// Use this in situations where you are not ensured to have account access permission, API 26+
Clover.startAccountChooserIfNeeded().then({ success: bool } => { ... });

////////////////////////////////////////////////
// Deprecated, will be removed in future release
////////////////////////////////////////////////
Clover.getSSID().then(...);

Clover.getConnectionStrength().then(...);

Clover.getIPAddress().then(...);

Clover.getEthernetMacAddress().then(...);

Clover.enableSound();

Clover.disableSound();

```

## Contants

* `CARD_ENTRY_METHODS`
  - `MAG_STRING`
  - `ICC_CONTACT`
  - `NFC_CONTACTLESS`
  - `VAULTED_CARD`
  - `MANUAL`
  
## Troubleshooting

* If you aren't getting anything resolved when calling `getMerchant` try these following solutions:
  - Make sure that you are running this in an app that is registered on Clover.
  - Make sure you have account permission before running `getMerchant`. This can be an issue on Android 8.0+. See `startAccountChooserIfNeeded`.
  - If you've just uninstalled and reinstalled the app, it can take a while for the device to refresh the app's access. Restart the clover device to force a refresh.