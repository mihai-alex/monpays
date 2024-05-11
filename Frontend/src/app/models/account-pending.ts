import { EAccountLockStatus } from "../constants/enums/e-account-lock-status";
import { EAccountStatus } from "../constants/enums/e-account-status";

export class AccountPending {
    accountNumber!: string;
    ownerUserName!: string;
    currency!: string;
    name!: string;
    transactionLimit!: number;
    status!: EAccountStatus;
    accountLockStatus!: EAccountLockStatus;
}
