import { Component } from '@angular/core';
import {OperationService} from "../../services/operation.service";
import {SidenavItem} from "../../models/sidenav-item";
import {Observable} from "rxjs";

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss'],
})
export class SidenavComponent {
  sidenavItems!: Observable<SidenavItem[]>

  constructor(
    private operationService: OperationService
  ) {
  }

  ngOnInit(): void {
    this.sidenavItems = this.operationService.getMenu()
  }

  public notify() {
    this.sidenavItems = this.operationService.getMenu()
  }
}
