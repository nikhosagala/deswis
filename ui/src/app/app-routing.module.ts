import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { DestinationComponent } from './pages/destination/destination.component';
import { DestinationDetailComponent } from './pages/destination/destination-detail.component';
import { CategoryDestinationComponent } from './pages/category/category-destination.component';
import { RecommendationComponent } from './pages/recommendation/recommendation.component';
import { PageNotFoundComponent } from './components/utils/errors/page-not-found.component';
import { AboutComponent } from './pages/about/about.component';
import { SurveyComponent } from './pages/survey/survey.component';

const routes: Routes = [
  {path: '', redirectTo: '/dashboard', pathMatch: 'full'},
  {path: 'about', component: AboutComponent},
  {path: 'dashboard', component: DashboardComponent},
  {path: 'destination/:id', component: DestinationDetailComponent},
  {path: 'destination', component: DestinationComponent},
  {path: 'category/:id', component: CategoryDestinationComponent},
  {path: 'recommendation/:id', component: RecommendationComponent},
  {path: 'survey/:id', component: SurveyComponent},
  {path: '**', component: PageNotFoundComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule {
}
