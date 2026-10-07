import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import { useAuth } from '../context/AuthContext';
import { AlertOctagon, UserPlus, ShieldCheck, FileSpreadsheet } from 'lucide-react';

const Violations = () => {
  const [violations, setViolations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [severityFilter, setSeverityFilter] = useState('');

  const { isOfficer, isAdmin } = useAuth();

  const fetchViolations = async () => {
    setLoading(true);
    try {
      const res = await api.get('/violations', {
        params: { severity: severityFilter || undefined, size: 50 }
      });
      if (res.success) setViolations(res.data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchViolations();
  }, [severityFilter]);

  const handleUpdateStatus = async (vId, status) => {
    const remarks = prompt(`Remarks for marking violation as ${status}:`);
    try {
      const res = await api.patch(`/violations/${vId}/status`, null, {
        params: { status, resolutionRemarks: remarks || undefined }
      });
      if (res.success) fetchViolations();
    } catch (err) {
      alert(err.message || 'Error updating status');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-wide">Compliance Violations Lifecycle</h1>
          <p className="text-xs text-slate-500 mt-1">Rule-Triggered Breaches & Investigation Workflow</p>
        </div>
      </div>

      <div className="glass-card p-4 rounded-2xl flex items-center justify-between">
        <select
          value={severityFilter}
          onChange={(e) => setSeverityFilter(e.target.value)}
          className="bg-white border border-slate-300 text-xs text-slate-700 rounded-xl px-3 py-2 focus:outline-none focus:border-teal-600 shadow-xs"
        >
          <option value="">All Severities</option>
          <option value="CRITICAL">CRITICAL</option>
          <option value="HIGH">HIGH</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="LOW">LOW</option>
        </select>
      </div>

      <div className="glass-card rounded-2xl overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500">Loading violations...</div>
        ) : (
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-100/90 text-slate-600 uppercase text-[10px] tracking-wider border-b border-slate-200">
              <tr>
                <th className="p-3.5">Violation Code</th>
                <th className="p-3.5">Member Entity</th>
                <th className="p-3.5">Title</th>
                <th className="p-3.5">Severity</th>
                <th className="p-3.5">Status</th>
                <th className="p-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {violations.map((v) => (
                <tr key={v.id} className="hover:bg-slate-50/80">
                  <td className="p-3.5 font-bold text-rose-600">{v.violationCode}</td>
                  <td className="p-3.5 font-semibold text-slate-900">{v.member?.organizationName} ({v.member?.memberCode})</td>
                  <td className="p-3.5 text-slate-700">{v.title}</td>
                  <td className="p-3.5"><StatusBadge status={v.severity} /></td>
                  <td className="p-3.5"><StatusBadge status={v.status} /></td>
                  <td className="p-3.5 text-right space-x-2">
                    {(isOfficer || isAdmin) && v.status !== 'RESOLVED' && v.status !== 'CLOSED' && (
                      <button
                        onClick={() => handleUpdateStatus(v.id, 'RESOLVED')}
                        className="px-2.5 py-1 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200 rounded text-xs font-semibold"
                      >
                        Resolve
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default Violations;
