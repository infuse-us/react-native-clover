import { NativeModules } from 'react-native';

const { RNCloverBridge } = NativeModules;

export default {
  ...RNCloverBridge,
  isFlex: () => RNCloverBridge.isFlex,
  isMini: () => RNCloverBridge.isMini,
  getSpaVersion: () => RNCloverBridge.getSpaVersion,
};
