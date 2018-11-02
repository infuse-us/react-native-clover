import { NativeModules, NativeEventEmitter} from 'react-native';

const CustomModule = NativeModules.ReactNativeCloverSDKModule;
const CustomModuleEmitter = new NativeEventEmitter(CustomModule);
