import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import { useAuth } from '../context/AuthContext';
import { ShieldAlert, Plus, CheckCircle, Clock } from 'lucide-react';

const CorrectiveActions = () => {
  const [actions, setActions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [violations, setViolations] = useState([]);

  const [formData, setFormData] = useState({
    violationId: '',
    description: '',
    rootCause: '',
    responsiblePerson: '',
    targetDate: '2026-10-30'
  });

  const { isOfficer, isAdmin } = useAuth();

  const fetchActions = async () => {
    setLoading(true);
    try {
      const res = await api.get('/corrective-actions', { params: { size: 50 } });
      if (res.success) setActions(res.data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchViolations = async () => {
    try {
      const res = await api.get('/violations', { params: { size: 50 } });
      if (res.success) setViolations(res.data.content);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchActions();
    fetchViolations();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/corrective-actions', null, {
        params: formData
      });
      if (res.success) {
        setShowModal(false);
        fetchActions();
      }
    } catch (err) {
      alert(err.message || 'Error creating CAPA');
    }
  };

  const handleVerify = async (id, status) => {
    const remarks = prompt(`Verification remarks for marking CAPA as ${status}:`);
    try {
      const res = await api.patch(`/corrective-actions/${id}/verify`, null, {
        params: { status, verificationRemarks: remarks || undefined }
      });
      if (res.success) fetchActions();
    } catch (err) {
      alert(err.message || 'Error verifying CAPA');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-wide">Corrective Actions (CAPA)</h1>
          <p className="text-xs text-slate-500 mt-1">Root Cause Analysis & Remediation Evidence Verification</p>
        </div>

        {(isOfficer || isAdmin) && (
          <button
            onClick={() => setShowModal(true)}
            className="flex items-center space-x-2 px-4 py-2 bg-teal-600 hover:bg-teal-700 text-white font-bold rounded-xl text-xs shadow-md shadow-teal-600/20 transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Issue CAPA Plan</span>
          </button>
        )}
      </div>

      <div className="glass-card rounded-2xl overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500">Loading CAPA tickets...</div>
        ) : (
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-100/90 text-slate-600 uppercase text-[10px] tracking-wider border-b border-slate-200">
              <tr>
                <th className="p-3.5">Action Code</th>
                <th className="p-3.5">Violation</th>
                <th className="p-3.5">Remediation Description</th>
                <th className="p-3.5">Target Date</th>
                <th className="p-3.5">Status</th>
                <th className="p-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {actions.map((a) => (
                <tr key={a.id} className="hover:bg-slate-50/80">
                  <td className="p-3.5 font-bold text-amber-700">{a.actionCode}</td>
                  <td className="p-3.5 font-semibold text-slate-900">{a.violation?.violationCode}</td>
                  <td className="p-3.5 text-slate-700 max-w-xs truncate">{a.description}</td>
                  <td className="p-3.5 font-semibold text-slate-700">{a.targetDate}</td>
                  <td className="p-3.5"><StatusBadge status={a.status} /></td>
                  <td className="p-3.5 text-right">
                    {(isOfficer || isAdmin) && a.status !== 'COMPLETED' && (
                      <button
                        onClick={() => handleVerify(a.id, 'COMPLETED')}
                        className="px-2.5 py-1 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200 rounded text-xs font-semibold"
                      >
                        Verify Completion
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Issue CAPA Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="glass-card w-full max-w-lg p-6 rounded-2xl border border-slate-200 shadow-xl space-y-4">
            <h2 className="text-base font-bold text-slate-900 flex items-center space-x-2">
              <ShieldAlert className="w-5 h-5 text-amber-600" />
              <span>Issue Corrective Action Plan</span>
            </h2>

            <form onSubmit={handleCreate} className="space-y-3">
              <div>
                <label className="block text-[11px] font-medium text-slate-700 mb-1">Target Violation</label>
                <select
                  required
                  value={formData.violationId}
                  onChange={(e) => setFormData({ ...formData, violationId: e.target.value })}
                  className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-900 focus:outline-none focus:border-teal-600"
                >
                  <option value="">-- Choose Violation --</option>
                  {violations.map(v => (
                    <option key={v.id} value={v.id}>{v.violationCode} - {v.title}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-[11px] font-medium text-slate-700 mb-1">Remediation Description</label>
                <textarea
                  required
                  rows="3"
                  placeholder="Describe mandatory corrective steps..."
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-900 focus:outline-none focus:border-teal-600"
                ></textarea>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-[11px] font-medium text-slate-700 mb-1">Responsible Officer</label>
                  <input
                    type="text"
                    placeholder="Chief Compliance Officer"
                    value={formData.responsiblePerson}
                    onChange={(e) => setFormData({ ...formData, responsiblePerson: e.target.value })}
                    className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-900 focus:outline-none focus:border-teal-600"
                  />
                </div>
                <div>
                  <label className="block text-[11px] font-medium text-slate-700 mb-1">Target Resolution Date</label>
                  <input
                    type="date"
                    required
                    value={formData.targetDate}
                    onChange={(e) => setFormData({ ...formData, targetDate: e.target.value })}
                    className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-900 focus:outline-none focus:border-teal-600"
                  />
                </div>
              </div>

              <div className="flex justify-end space-x-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-amber-600 hover:bg-amber-700 text-white rounded-xl text-xs font-bold shadow-xs"
                >
                  Issue Plan
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default CorrectiveActions;
