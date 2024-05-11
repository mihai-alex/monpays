export class PaymentHistoryEntry {
  number!: string;
  timestamp!: string;
  currency!: string;
  amount!: number;
  debitAccount!: string; // the money is transferred FROM this account
  creditAccount!: string; // the money is transferred INTO this account
  description!: string;
  type!: string;
  status!: string;
}
