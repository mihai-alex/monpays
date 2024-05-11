import { Component, Input } from '@angular/core';
import { SidenavItem } from "../../models/sidenav-item";

@Component({
  selector: 'app-sidenav-entry',
  templateUrl: './sidenav-entry.component.html',
  styleUrls: ['./sidenav-entry.component.scss']
})
export class SidenavEntryComponent {
  @Input() sidenavEntry!: SidenavItem;

  constructor(
  ) {}

}
