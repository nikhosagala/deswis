import { NgModule } from '@angular/core';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
// Angular Modules
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import {
  MdButtonModule,
  MdCardModule,
  MdCheckboxModule,
  MdGridListModule,
  MdIconModule,
  MdMenuModule,
  MdSidenavModule,
  MdSliderModule,
  MdTabsModule,
  MdToolbarModule
} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
// External Modules
import { SlimLoadingBarModule } from 'ng2-slim-loading-bar';
import { CollapseModule, PaginationModule, RatingModule } from 'ngx-bootstrap';
import { AgmCoreModule } from '@agm/core';
import { MaterializeModule } from 'ng2-materialize';
// Main File
import { AppComponent } from './pages/app.component';
import { AppRoutingModule } from './app-routing.module';
import { PageNotFoundComponent } from './components/utils/errors/page-not-found.component';
// Component
import { AboutComponent } from './pages/about/about.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { DestinationComponent } from './pages/destination/destination.component';
import { DestinationDetailComponent } from './pages/destination/destination-detail.component';
import { CategoryDestinationComponent } from './pages/category/category-destination.component';
import { CardComponent } from './components/card/card.component';
import { CardRecommendationComponent } from './components/card/card-recommendation.component';
import { GlobalErrorHandler } from './services/error.handler';
import { RecommendationComponent } from './pages/recommendation/recommendation.component';
import { SettingComponent } from './components/setting/setting.component';
import { SettingAdminComponent } from './components/setting/setting-admin.component';
import { SurveyModalComponent } from './components/modal/survey-modal.component';
import { SliderComponent } from './components/slider/slider.component';
// Service
import { ApplicationService } from './services/application.service';
import { DestinationService } from './services/destination.service';
import { CategoryService } from './services/category.service';
import { RecommendationService } from './services/recommendation.service';
// Observable class extensions
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/throw';
// Observable operators
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/switchMap';
import { SurveyService } from './services/survey.service';
import { SurveyComponent } from './pages/survey/survey.component';

@NgModule({
  entryComponents: [SettingAdminComponent, SurveyModalComponent],
  declarations: [
    AppComponent,
    AboutComponent,
    DashboardComponent,
    DestinationComponent,
    DestinationDetailComponent,
    CategoryDestinationComponent,
    RecommendationComponent,
    PageNotFoundComponent,
    SettingComponent,
    SettingAdminComponent,
    SurveyModalComponent,
    SurveyComponent,
    SliderComponent,
    CardComponent,
    CardRecommendationComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule,
    CollapseModule.forRoot(),
    PaginationModule.forRoot(),
    RatingModule.forRoot(),
    MdCardModule,
    MdSidenavModule,
    MdMenuModule,
    MdToolbarModule,
    MdGridListModule,
    MdButtonModule,
    MdSliderModule,
    MdTabsModule,
    MdIconModule,
    MdCheckboxModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    SlimLoadingBarModule.forRoot(),
    MaterializeModule.forRoot(),
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyDRdrNb2Vl4wJ4SUM1X3_E_4nAkEs935IE'
    })
  ],
  providers: [
    ApplicationService,
    DestinationService,
    CategoryService,
    RecommendationService,
    SurveyService,
    {provide: GlobalErrorHandler, useClass: GlobalErrorHandler},
    {provide: LocationStrategy, useClass: HashLocationStrategy}
  ],
  bootstrap: [AppComponent],
  exports: [SlimLoadingBarModule]
})

export class AppModule {
}
