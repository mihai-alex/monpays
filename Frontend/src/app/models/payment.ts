import { AuditEntry } from './audit-entry';
import { PaymentHistoryEntry } from './payment-history-entry';

export class Payment {
  number!: string;
  timestamp?: string;
  currency!: string;
  amount!: number;
  debitAccountNumber!: string; // the money is transferred FROM this account
  creditAccountNumber!: string; // the money is transferred INTO this account
  description!: string;
  type?: string;
  status?: string;

  history: PaymentHistoryEntry[] = [];
  audit: AuditEntry[] = [];
}
