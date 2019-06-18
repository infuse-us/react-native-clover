
# @infuse/react-native-clover

React Native native module for the [Clover SDK](https://github.com/clover/clover-android-sdk).

## Getting started

`$ yarn add https://bitbucket.cardconnect.com/scm/clov/clover-sdk-react-native-bridge.git`

### Mostly automatic installation

`$ react-native link @infuse/react-native-clover`

### Manual installation



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
import Clover from '@infuse/react-native-clover';

Clover.getMerchant().then({ data } => { ... });

Clover.enableCustomerMode();
Clover.disableCustomerMode();

Clover.print(String imagePath).then(...);

// Use this in situations where you are not ensured to have account access permission, API 26+
Clover.startAccountChooserIfNeeded().then({ success: bool } => { ... });

Clover.isFlex();
Clover.isMini();
Clover.getSpaVersion();

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