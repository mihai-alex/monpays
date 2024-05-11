import { EProfileStatus } from '../constants/enums/e-profile-status';
import { EProfileType } from '../constants/enums/e-profile-type';
import { EStatus } from '../constants/enums/e-status';
import { AuditEntry } from './audit-entry';
import { Operation } from './operation';
import { ProfileHistoryEntry } from './profile-history-entry';
import { ProfilePending } from './profile-pending';

export class Profile {
  name!: string; // this property is required, it is an unique identifier
  type!: EProfileType;
  rights: Operation[] = [];
  status!: EProfileStatus;
  history: ProfileHistoryEntry[] = [];
  audit: AuditEntry[] = [];
  pendingEntity: ProfilePending | null = null;
}
