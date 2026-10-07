import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/Layout';

import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Members from './pages/Members';
import MemberDetail from './pages/MemberDetail';
import Compliance from './pages/Compliance';
import ComplianceCalendar from './pages/ComplianceCalendar';
import Documents from './pages/Documents';
import Rules from './pages/Rules';
import Violations from './pages/Violations';
import CorrectiveActions from './pages/CorrectiveActions';
import RiskAnalytics from './pages/RiskAnalytics';
import Notifications from './pages/Notifications';
import Regulations from './pages/Regulations';
import Complaints from './pages/Complaints';
import Reports from './pages/Reports';
import AuditTrail from './pages/AuditTrail';

const ProtectedRoute = ({ children }) => {
  const { token } = useAuth();
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

const App = () => {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route path="/" element={
            <ProtectedRoute>
              <Layout />
            </ProtectedRoute>
          }>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="members" element={<Members />} />
            <Route path="members/:id" element={<MemberDetail />} />
            <Route path="compliance" element={<Compliance />} />
            <Route path="compliance/calendar" element={<ComplianceCalendar />} />
            <Route path="documents" element={<Documents />} />
            <Route path="rules" element={<Rules />} />
            <Route path="violations" element={<Violations />} />
            <Route path="corrective-actions" element={<CorrectiveActions />} />
            <Route path="risk" element={<RiskAnalytics />} />
            <Route path="notifications" element={<Notifications />} />
            <Route path="regulations" element={<Regulations />} />
            <Route path="complaints" element={<Complaints />} />
            <Route path="reports" element={<Reports />} />
            <Route path="audit" element={<AuditTrail />} />
          </Route>

          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;
