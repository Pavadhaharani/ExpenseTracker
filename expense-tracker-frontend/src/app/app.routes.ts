import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { publicGuard } from './core/guards/public.guard';
import { AppLayout } from './components/app-layout/app-layout';
import { LoginPage } from './pages/login/login';
import { RegisterPage } from './pages/register/register';
import { DashboardPage } from './pages/dashboard/dashboard';
import { ExpensesListPage } from './pages/expenses-list/expenses-list';
import { AddExpensePage } from './pages/add-expense/add-expense';
import { EditExpensePage } from './pages/edit-expense/edit-expense';
import { MonthlyReportPage } from './pages/monthly-report/monthly-report';
import { CategoryReportPage } from './pages/category-report/category-report';

export const routes: Routes = [
  { path: 'login', component: LoginPage, canActivate: [publicGuard] },
  { path: 'register', component: RegisterPage, canActivate: [publicGuard] },
  {
    path: '',
    component: AppLayout,
    canActivate: [authGuard],
    canActivateChild: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: DashboardPage },
      { path: 'expenses', component: ExpensesListPage },
      { path: 'expenses/new', component: AddExpensePage },
      { path: 'expenses/:id/edit', component: EditExpensePage },
      { path: 'reports/monthly', component: MonthlyReportPage },
      { path: 'reports/category', component: CategoryReportPage }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
