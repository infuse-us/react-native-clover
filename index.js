import { NativeModules } from 'react-native';

const { RNCloverBridge } = NativeModules;

// /**
//  *
//  * @param {object} option
//  * @param {number} option.amount
//  * @param {string} option.externalPaymentId
//  * @param {number} [option.cardEntryMethods]
//  * @param {boolean} [option.disablePrinting=false]
//  * @param {number} [option.disableReceiptSelection=1]
//  * @return {promise}
//  */
// function sale(option) {
//   return RNCloverBridge.sale(...option);
// }

export default {
  ...RNCloverBridge,
  // sale: RNCloverBridge.sale,
  // refund: RNCloverBridge.refund,
  isFlex: () => RNCloverBridge.isFlex,
  isMini: () => RNCloverBridge.isMini,
  getSpaVersion: () => RNCloverBridge.getSpaVersion,
};
