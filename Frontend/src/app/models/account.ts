import { EAccountLockStatus } from '../constants/enums/e-account-lock-status';
import { EAccountStatus } from '../constants/enums/e-account-status';
import { EStatus } from '../constants/enums/e-status';
import { AccountHistoryEntry } from './account-history-entry';
import { AccountPending } from './account-pending';
import { AuditEntry } from './audit-entry';

export class Account {
  accountNumber!: string;
  owner!: string;
  name!: string;
  transactionLimit!: number;
  currency!: string;
  status!: EAccountStatus;
  accountLockStatus!: EAccountLockStatus;
  history: AccountHistoryEntry[] = [];
  audit: AuditEntry[] = [];
  pendingEntity: AccountPending | null = null;
}
