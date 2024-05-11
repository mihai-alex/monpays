import { EStatus } from '../constants/enums/e-status';

export class UserPending {
  userName!: string; // this is required when getting user from backend (unique identifier)
  firstName!: string;
  lastName!: string;
  emailAddress!: string;
  phoneNumber!: string;
  address!: string;
  profileName!: string;
  status!: EStatus;
}
