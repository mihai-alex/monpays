import { EProfileType } from '../constants/enums/e-profile-type';
import { EStatus } from '../constants/enums/e-status';
import { Operation } from './operation';

export class ProfileHistoryEntry {
  type!: EProfileType;
  name!: string;
  rights: Operation[] = [];
  status!: EStatus;
}
