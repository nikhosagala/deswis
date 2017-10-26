import {Inject, Injectable} from "@angular/core";
import {Router} from "@angular/router";

import {default as swal} from "sweetalert2";

@Injectable()
export class GlobalErrorHandler {
  public static NOT_FOUND = 404;
  public static BAD_REQUEST = 400;
  public static INTERNAL_ERROR = 500;
  
  constructor(@Inject(Router) public router?: Router) {
  }
  
  public checkAndHandling(error: any) {
    
    switch (error.status) {
      case GlobalErrorHandler.NOT_FOUND:
        this.notFoundHandler();
        break;
      
      case GlobalErrorHandler.BAD_REQUEST:
        this.badRequestHandler();
        break;
      
      case GlobalErrorHandler.INTERNAL_ERROR:
        this.internalErrorHandler();
        break;
      
      default:
        this.defaultHandler();
        break;
    }
  }
  
  private notFoundHandler() {
    
    swal({
      title: "Oooppss! 404 API",
      type: "error",
      showCancelButton: false
    }).then(function () {
      this.router.navigateByUrl('/');
    }.bind(this))
  }
  
  private badRequestHandler() {
    swal({
      title: "Oooppss! 400 API",
      type: "error",
      showCancelButton: false
    }).then(function () {
      this.router.navigateByUrl('dashboard');
    }.bind(this))
  }
  
  private internalErrorHandler() {
    swal({
      title: "Oooppss ! 500 API",
      type: "error",
      showCancelButton: false
    }).then(function () {
      this.router.navigateByUrl('dashboard');
    }.bind(this))
  }
  
  private defaultHandler() {
    swal({
      title: "Oooppss! Unknown Error",
      type: "error",
      showCancelButton: false
    }).then(function () {
      this.router.navigateByUrl('dashboard');
    }.bind(this))
  }
}
