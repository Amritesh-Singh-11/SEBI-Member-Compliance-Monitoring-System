import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Building2, 
  FileCheck2, 
  FileText, 
  Cpu, 
  AlertTriangle, 
  ShieldAlert, 
  Activity, 
  Bell, 
  BookOpen, 
  MessageSquare, 
  FileSpreadsheet, 
  History, 
  Calendar
} from 'lucide-react';

const Sidebar = () => {
  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Regulated Members', path: '/members', icon: Building2 },
    { name: 'Compliance Filings', path: '/compliance', icon: FileCheck2 },
    { name: 'Compliance Calendar', path: '/compliance/calendar', icon: Calendar },
    { name: 'Document Vault', path: '/documents', icon: FileText },
    { name: 'Rule Engine', path: '/rules', icon: Cpu },
    { name: 'Violations Lifecycle', path: '/violations', icon: AlertTriangle },
    { name: 'Corrective Actions (CAPA)', path: '/corrective-actions', icon: ShieldAlert },
    { name: 'Risk Analytics', path: '/risk', icon: Activity },
    { name: 'Notifications', path: '/notifications', icon: Bell },
    { name: 'Regulatory Library', path: '/regulations', icon: BookOpen },
    { name: 'Grievance Tracker', path: '/complaints', icon: MessageSquare },
    { name: 'Report Generator', path: '/reports', icon: FileSpreadsheet },
    { name: 'Audit Trail', path: '/audit', icon: History }
  ];

  return (
    <aside className="w-64 bg-white border-r border-slate-200 flex flex-col h-screen sticky top-0 z-30 shadow-xs">
      {/* Brand Header */}
      <div className="p-5 border-b border-slate-200">
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-teal-600 to-cyan-500 flex items-center justify-center font-extrabold text-white text-base shadow-md shadow-teal-500/20">
            RL
          </div>
          <div>
            <h1 className="font-extrabold text-base tracking-wide text-slate-900">ReguLens</h1>
            <p className="text-[10px] uppercase tracking-wider text-teal-700 font-bold">SEBI RegTech Portal</p>
          </div>
        </div>

        {/* Academic RegTech Badge */}
        <div className="mt-3 px-2.5 py-1 rounded-md bg-amber-50 border border-amber-200 text-amber-800 text-[10px] font-semibold text-center">
          Academic RegTech Prototype
        </div>
      </div>

      {/* Navigation Menu */}
      <nav className="flex-1 overflow-y-auto p-3 space-y-1">
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center space-x-3 px-3 py-2.5 rounded-xl text-xs font-medium transition-all group ${
                  isActive
                    ? 'bg-teal-50 text-teal-700 border border-teal-200 font-semibold shadow-2xs'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100/70'
                }`
              }
            >
              {({ isActive }) => (
                <>
                  <Icon className={`w-4 h-4 transition-colors ${isActive ? 'text-teal-600' : 'text-slate-400 group-hover:text-slate-600'}`} />
                  <span>{item.name}</span>
                </>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* Footer Disclaimer */}
      <div className="p-4 border-t border-slate-200 text-[10px] text-slate-500 text-center leading-relaxed bg-slate-50/50">
        ReguLens RegTech Prototype.<br/>
        Not affiliated with official SEBI.
      </div>
    </aside>
  );
};

export default Sidebar;
