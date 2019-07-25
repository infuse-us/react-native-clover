import { NativeModules } from 'react-native';

const { RNCloverBridge } = NativeModules;

export default {
  ...RNCloverBridge,
  authenticate: (force = false, timeout = 10000) =>
    RNCloverBridge.authenticate(force, timeout),
  isFlex: () => RNCloverBridge.isFlex,
  isMini: () => RNCloverBridge.isMini,
  getSpaVersion: () => RNCloverBridge.getSpaVersion,
};
