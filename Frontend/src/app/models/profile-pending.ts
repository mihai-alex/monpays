import { EProfileType } from '../constants/enums/e-profile-type';
import { EStatus } from '../constants/enums/e-status';
import { Operation } from './operation';

export class ProfilePending {
  type!: EProfileType;
  rights: Operation[] = [];
  status!: EStatus;
}
