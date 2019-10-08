import { NativeModules, NativeEventEmitter } from 'react-native';
import { useEffect } from 'react';

const { RNCloverBridge } = NativeModules;

export default {
  ...RNCloverBridge,
  authenticate: (force = false, timeout = 10000) =>
    RNCloverBridge.authenticate(force, timeout),
  getMerchant: () => RNCloverBridge.getMerchant(),
  isFlex: () => RNCloverBridge.isFlex,
  isMini: () => RNCloverBridge.isMini,
  getSpaVersion: () => RNCloverBridge.getSpaVersion,
  disableCustomerMode: (requirePasscode = false) =>
    RNCloverBridge.disableCustomerMode(requirePasscode),
};

export const useScanner = (callback, enabled = true) => {
  useEffect(() => {
    if (enabled) {
      const eventEmitter = new NativeEventEmitter(RNCloverBridge);
      const listener = eventEmitter.addListener(
        RNCloverBridge.EVENT.BARCODE_SCANNER,
        callback,
      );
      RNCloverBridge.registerScanner();
      return () => {
        listener.remove();
        RNCloverBridge.unregisterScanner();
      };
    }
  }, [enabled, callback]);
};
