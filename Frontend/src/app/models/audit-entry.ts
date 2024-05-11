import { Operation } from './operation';

export class AuditEntry {
  username!: string;
  operation!: Operation;
  uniqueEntityIdentifier!: string; // TODO: change to the unique identifier for each entity type, which is a string
  timestamp!: string;
}
