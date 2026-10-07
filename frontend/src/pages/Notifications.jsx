import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { Bell, Check, CheckCheck } from 'lucide-react';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const res = await api.get('/notifications', { params: { size: 50 } });
      if (res.success) setNotifications(res.data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleMarkAllRead = async () => {
    try {
      const res = await api.patch('/notifications/read-all');
      if (res.success) fetchNotifications();
    } catch (err) {
      alert('Error updating notifications');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-wide">In-App Compliance Alerts</h1>
          <p className="text-xs text-slate-500 mt-1">Real-Time Notifications & Deadline Escalations</p>
        </div>
        <button
          onClick={handleMarkAllRead}
          className="flex items-center space-x-1.5 px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-teal-700 border border-slate-200 rounded-xl text-xs font-semibold shadow-xs"
        >
          <CheckCheck className="w-4 h-4" />
          <span>Mark All Read</span>
        </button>
      </div>

      <div className="glass-card p-4 rounded-2xl space-y-3">
        {loading ? (
          <div className="p-8 text-center text-slate-500">Loading notifications...</div>
        ) : (
          notifications.map((n) => (
            <div
              key={n.id}
              className={`p-4 rounded-xl border flex items-start justify-between transition-all ${
                n.readStatus ? 'bg-slate-50/60 border-slate-200 text-slate-500' : 'bg-white border-teal-200 text-slate-900 shadow-sm'
              }`}
            >
              <div className="space-y-1">
                <div className="text-xs font-bold text-teal-700">{n.title}</div>
                <p className="text-xs text-slate-700">{n.message}</p>
                <div className="text-[10px] text-slate-400 font-medium">{n.createdAt?.replace('T', ' ').substring(0, 16)}</div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default Notifications;
