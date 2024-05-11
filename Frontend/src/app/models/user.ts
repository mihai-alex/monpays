import { EStatus } from '../constants/enums/e-status';
import { EUserStatus } from '../constants/enums/e-user-status';
import { AuditEntry } from './audit-entry';
import { UserHistoryEntry } from './user-history-entry';
import { UserPending } from './user-pending';

export class User {
  userName!: string; // this is required when getting user from backend (unique identifier)
  password?: string; // this remains null when getting user from backend, but is required when creating a new user
  firstName!: string;
  lastName!: string;
  emailAddress!: string;
  phoneNumber!: string;
  address!: string;
  profileName!: string; // TODO: should this be edited by the user? Get backend request
  status?: EUserStatus;

  mfaEnabled!: boolean;

  history: UserHistoryEntry[] = [];
  audit: AuditEntry[] = [];
  pendingEntity: UserPending | null = null;
}
