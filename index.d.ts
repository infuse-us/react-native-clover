interface ObjectRef {
  id: string;
}

interface Result {
  success: boolean;
  reason?: string;
  message?: string;
}

interface Transaction {
  id: string;
  amount: number;
  createdTime: string;
}

interface Payment extends Transaction {
  externalPaymentId: string;
  offline: boolean;
  tipAmount: number;
  order: ObjectRef;
}

interface Refund extends Transaction {
  payment: ObjectRef;
}

interface Credit extends Transaction { }

interface CardEntryMethod {
  ICC_CONTACT: number;
  MAG_STRIPE: number;
  MANUAL: number;
  NFC_CONTACTLESS: number;
  VAULTED_CARD: number;
  ALL: number;
}

interface DataEntryLocation {
  NONE: string;
  ON_PAPER: string;
  ON_SCREEN: string;
}

interface VoidReason {
  AUTH_CLOSED_NEW_CARD: string;
  DEVELOPER_PAY_PARTIAL_AUTH: string;
  DEVELOPER_PAY_TIP_ADJUST_FAILED: string;
  FAILED: string;
  GIFTCARD_LOAD_FAILED: string;
  NOT_APPROVED: string; 
  REJECT_DUPLICATE: string; 
  REJECT_OFFLINE: string; 
  REJECT_PARTIAL_AUTH: string;
  REJECT_SIGNATURE: string;
  TRANSPORT_ERROR: string; 
  USER_CANCEL: string; 
  USER_CUSTOMER_CANCEL: string; 
  USER_GIFTCARD_LOAD_CANCEL: string; 
}

interface SaleOption {
  amount: number;
  externalPaymentId?: string;
  generateExternalPaymentId?: boolean;
  cardEntryMethods?: number;
  disableRestartTransactionOnFail?: boolean;
  disableDuplicateChecking?: boolean;
  disablePrinting?: boolean;
  disableReceiptSelection?: boolean;
  signatureEntryLocation? : string;
  signatureThreshold? : number;
}

interface SaleResult extends Result {
  payment: Payment;
}

interface RefundOption {
  paymentId: string;
  orderId: string;
  amount?: number;
}

interface RefundResult extends Result {
  refund: Refund;
}

interface ManualRefundOption {
  amount: number;
  externalPaymentId?: string;
  generateExternalPaymentId?: boolean;
  cardEntryMethods?: number;
  disableRestartTransactionOnFail?: boolean;
}

interface ManualRefundResult extends Result {
  credit: Credit;
}

interface VoidPaymentOption {
  paymentId: string;
  orderId: string;
  voidReason: string;
}

interface VoidPaymentResult extends Result {
  paymentId: string;
}

declare const _default: {
  // Payment Methods
  initializePaymentConnector: (raid: string) => void;
  sale: (option: SaleOption) => Promise<SaleResult>;
  refund: (option: RefundOption) => Promise<RefundResult>;
  manualRefund: (option: ManualRefundOption) => Promise<ManualRefundResult>;
  voidPayment: (option: VoidPaymentOption) => Promise<VoidPaymentResult>;
  voidPaymentRefund: () => Promise<object>;
  cancelSPA: () => Promise<object>;

  enableCustomerMode: () => void;
  disableCustomerMode: () => void;
  getMerchant: () => Promise<object>;
  print: (imagePath: string) => Promise<object>;
  startAccountChooserIfNeeded: () => Promise<object>;

  // Misc Methods
  isFlex: () => boolean;
  isMini: () => boolean;
  getSpaVersion: () => string;

  // Constants
  CARD_ENTRY_METHOD: CardEntryMethod;
  DATA_ENTRY_LOCATION: DataEntryLocation;
  VOID_REASON: VoidReason;
}

export default _default;