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
    <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col h-screen sticky top-0 z-30">
      {/* Brand Header */}
      <div className="p-5 border-b border-slate-800">
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-lg bg-gradient-to-tr from-teal-500 to-cyan-400 flex items-center justify-center font-extrabold text-slate-950 text-lg shadow-lg shadow-teal-500/20">
            RL
          </div>
          <div>
            <h1 className="font-extrabold text-base tracking-wide text-white">ReguLens</h1>
            <p className="text-[10px] uppercase tracking-wider text-teal-400 font-semibold">SEBI RegTech Portal</p>
          </div>
        </div>

        {/* Academic RegTech Badge */}
        <div className="mt-3 px-2.5 py-1 rounded bg-amber-500/10 border border-amber-500/20 text-amber-400 text-[10px] font-medium text-center">
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
                `flex items-center space-x-3 px-3 py-2.5 rounded-lg text-xs font-medium transition-all ${
                  isActive
                    ? 'bg-teal-500/15 text-teal-300 border border-teal-500/30 font-semibold'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`
              }
            >
              <Icon className="w-4 h-4 text-slate-400" />
              <span>{item.name}</span>
            </NavLink>
          );
        })}
      </nav>

      {/* Footer Disclaimer */}
      <div className="p-4 border-t border-slate-800 text-[10px] text-slate-500 text-center leading-relaxed">
        ReguLens RegTech Prototype.<br/>
        Not affiliated with official SEBI.
      </div>
    </aside>
  );
};

export default Sidebar;
