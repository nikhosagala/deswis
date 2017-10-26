import {Inject, Injectable} from "@angular/core";
import {Http, RequestMethod, URLSearchParams} from "@angular/http";

import "rxjs/add/operator/toPromise";

import {API_ROUTES} from "../components/utils/routes";
import {CrudService} from "./crud.service";
import {GlobalErrorHandler} from "./error.handler";

@Injectable()
export class CategoryService extends CrudService {

  constructor(@Inject(Http) http?: Http, @Inject(GlobalErrorHandler) handleError?: GlobalErrorHandler) {
    super(http, handleError);
    this.urlAPI = API_ROUTES.CATEGORIES;
  }

  public getChildren(node?): Promise<any> {
    let params: URLSearchParams = new URLSearchParams();

    if (node) {
      params.set('node', node);
    }
    return this.http.get(this.urlAPI.GET_CHILD, {
      search: params,
      headers: this.getHeaders(RequestMethod.Get)
    })
      .toPromise()
      .then(response => response.json())
      .catch(super.handleError.bind(this))
  }
}
