import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import { Search, Filter, Calendar, FileCheck2, Upload } from 'lucide-react';

const Compliance = () => {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('');

  const fetchRecords = async () => {
    setLoading(true);
    try {
      const res = await api.get('/compliance/records', {
        params: { status: statusFilter || undefined, size: 50 }
      });
      if (res.success) {
        setRecords(res.data.content);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecords();
  }, [statusFilter]);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-wide">Compliance Period Filings</h1>
          <p className="text-xs text-slate-500 mt-1">Regulatory Deadlines & Submission Tracking</p>
        </div>
      </div>

      <div className="glass-card p-4 rounded-2xl flex items-center justify-between">
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="bg-white border border-slate-300 text-xs text-slate-700 rounded-xl px-3 py-2 focus:outline-none focus:border-teal-600 shadow-xs"
        >
          <option value="">All Statuses</option>
          <option value="PENDING">PENDING</option>
          <option value="SUBMITTED">SUBMITTED</option>
          <option value="COMPLIANT">COMPLIANT</option>
          <option value="OVERDUE">OVERDUE</option>
          <option value="REJECTED">REJECTED</option>
        </select>
      </div>

      <div className="glass-card rounded-2xl overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500">Loading compliance records...</div>
        ) : (
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-100/90 text-slate-600 uppercase text-[10px] tracking-wider border-b border-slate-200">
              <tr>
                <th className="p-3.5">Member Code</th>
                <th className="p-3.5">Filing Requirement</th>
                <th className="p-3.5">Period</th>
                <th className="p-3.5">Due Date</th>
                <th className="p-3.5">Submitted On</th>
                <th className="p-3.5">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {records.map((r) => (
                <tr key={r.id} className="hover:bg-slate-50/80">
                  <td className="p-3.5 font-bold text-teal-700">{r.member?.memberCode}</td>
                  <td className="p-3.5 font-semibold text-slate-900">{r.requirement?.title}</td>
                  <td className="p-3.5 text-slate-500">{r.periodStart} to {r.periodEnd}</td>
                  <td className="p-3.5 font-semibold text-amber-700">{r.dueDate}</td>
                  <td className="p-3.5 text-slate-500">{r.submissionDate || '-'}</td>
                  <td className="p-3.5"><StatusBadge status={r.status} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default Compliance;
