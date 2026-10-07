import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import { Calendar as CalendarIcon, List, Clock, AlertCircle } from 'lucide-react';

const ComplianceCalendar = () => {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [viewMode, setViewMode] = useState('list'); // list or month

  useEffect(() => {
    const fetchCalendarRecords = async () => {
      setLoading(true);
      try {
        const res = await api.get('/compliance/records', { params: { size: 100 } });
        if (res.success) setRecords(res.data.content);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchCalendarRecords();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-wide">Regulatory Compliance Calendar</h1>
          <p className="text-xs text-slate-500 mt-1">Timeline of Upcoming & Overdue Filing Deadlines</p>
        </div>
        <div className="flex items-center space-x-2 bg-white border border-slate-200 p-1 rounded-xl shadow-xs">
          <button
            onClick={() => setViewMode('list')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition-colors ${
              viewMode === 'list' ? 'bg-teal-600 text-white' : 'text-slate-500 hover:text-slate-900'
            }`}
          >
            <List className="w-3.5 h-3.5" />
            <span>List View</span>
          </button>
          <button
            onClick={() => setViewMode('month')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition-colors ${
              viewMode === 'month' ? 'bg-teal-600 text-white' : 'text-slate-500 hover:text-slate-900'
            }`}
          >
            <CalendarIcon className="w-3.5 h-3.5" />
            <span>Month Schedule</span>
          </button>
        </div>
      </div>

      <div className="glass-card p-6 rounded-2xl">
        <h2 className="text-sm font-semibold text-slate-900 mb-4 flex items-center space-x-2">
          <Clock className="w-4 h-4 text-teal-600" />
          <span>Filing Deadlines Timeline</span>
        </h2>

        {loading ? (
          <div className="p-8 text-center text-slate-500">Loading calendar events...</div>
        ) : (
          <div className="space-y-3">
            {records.map((r) => (
              <div key={r.id} className="p-4 bg-white border border-slate-200 hover:border-teal-400 rounded-xl flex items-center justify-between transition-all shadow-xs">
                <div className="space-y-1">
                  <div className="text-xs font-bold text-slate-900">{r.requirement?.title}</div>
                  <div className="text-[11px] text-slate-500">
                    Member: <strong className="text-teal-700">{r.member?.organizationName} ({r.member?.memberCode})</strong>
                  </div>
                </div>
                <div className="flex items-center space-x-4 text-xs">
                  <div className="text-right">
                    <div className="text-[10px] text-slate-400 uppercase font-semibold">Due Date</div>
                    <div className="font-semibold text-amber-700">{r.dueDate}</div>
                  </div>
                  <StatusBadge status={r.status} />
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ComplianceCalendar;
