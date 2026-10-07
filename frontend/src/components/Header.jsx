import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Bell, LogOut, User as UserIcon, Shield } from 'lucide-react';
import api from '../api/axios';

const Header = () => {
  const { user, logout } = useAuth();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    const fetchUnread = async () => {
      try {
        const res = await api.get('/notifications/unread-count');
        if (res.success && res.data) {
          setUnreadCount(res.data.unreadCount || 0);
        }
      } catch (err) {
        // Silent catch
      }
    };
    if (user) {
      fetchUnread();
    }
  }, [user]);

  return (
    <header className="h-16 bg-white/80 backdrop-blur border-b border-slate-200 px-6 flex items-center justify-between sticky top-0 z-20 shadow-xs">
      <div className="flex items-center space-x-3">
        <span className="text-xs font-medium text-slate-500">Environment:</span>
        <span className="px-2 py-0.5 rounded text-[11px] font-semibold bg-teal-50 text-teal-700 border border-teal-200">
          Stock Broker RegTech Mode
        </span>
      </div>

      <div className="flex items-center space-x-5">
        {/* Notifications Icon */}
        <a href="/notifications" className="relative p-2 text-slate-500 hover:text-slate-900 transition-colors">
          <Bell className="w-5 h-5" />
          {unreadCount > 0 && (
            <span className="absolute top-1.5 right-1.5 w-4 h-4 rounded-full bg-red-500 text-white text-[10px] font-bold flex items-center justify-center">
              {unreadCount > 9 ? '9+' : unreadCount}
            </span>
          )}
        </a>

        {/* User Pill */}
        <div className="flex items-center space-x-3 pl-4 border-l border-slate-200">
          <div className="w-8 h-8 rounded-full bg-teal-50 border border-teal-200 flex items-center justify-center text-teal-600">
            <UserIcon className="w-4 h-4" />
          </div>
          <div className="text-left hidden sm:block">
            <div className="text-xs font-semibold text-slate-900 flex items-center space-x-1.5">
              <span>{user?.fullName || user?.username}</span>
              <Shield className="w-3 h-3 text-teal-600 inline" />
            </div>
            <div className="text-[10px] text-slate-500 font-medium">
              {user?.roles?.[0]?.replace('ROLE_', '') || 'USER'}
            </div>
          </div>

          {/* Logout button */}
          <button
            onClick={logout}
            title="Logout"
            className="p-2 text-slate-400 hover:text-rose-600 transition-colors rounded-lg hover:bg-slate-100"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;
