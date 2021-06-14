import { NativeModules } from 'react-native';

type ReactNativeBundleType = {
  multiply(a: number, b: number): Promise<number>;
};

const { ReactNativeBundle } = NativeModules;

export default ReactNativeBundle as ReactNativeBundleType;
