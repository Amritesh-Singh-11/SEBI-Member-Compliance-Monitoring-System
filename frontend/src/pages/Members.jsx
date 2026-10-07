import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import RiskBadge from '../components/RiskBadge';
import { useAuth } from '../context/AuthContext';
import { Search, Plus, Filter, Building2, Eye, ShieldAlert } from 'lucide-react';

const Members = () => {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('');
  const [riskLevel, setRiskLevel] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showModal, setShowModal] = useState(false);

  const { isAdmin, isOfficer } = useAuth();
  const navigate = useNavigate();

  // Form State
  const [formData, setFormData] = useState({
    memberCode: '',
    organizationName: '',
    registrationNumber: '',
    memberType: 'STOCK_BROKER',
    registrationDate: new Date().toISOString().split('T')[0],
    registrationValidity: '2030-12-31',
    status: 'ACTIVE',
    contactEmail: '',
    contactPhone: '',
    city: 'Mumbai',
    state: 'Maharashtra'
  });

  const fetchMembers = async () => {
    setLoading(true);
    try {
      const res = await api.get('/members', {
        params: { query, status: status || undefined, riskLevel: riskLevel || undefined, page, size: 10 }
      });
      if (res.success) {
        setMembers(res.data.content);
        setTotalPages(res.data.totalPages);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMembers();
  }, [query, status, riskLevel, page]);

  const handleCreateMember = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/members', formData);
      if (res.success) {
        setShowModal(false);
        fetchMembers();
      }
    } catch (err) {
      alert(err.message || 'Error creating member');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-wide">Regulated Market Intermediaries</h1>
          <p className="text-xs text-slate-500 mt-1">
            Registered Stock Brokers & Intermediary Entities
          </p>
        </div>

        {(isAdmin || isOfficer) && (
          <button
            onClick={() => setShowModal(true)}
            className="flex items-center space-x-2 px-4 py-2 bg-teal-600 hover:bg-teal-700 text-white font-bold rounded-xl text-xs shadow-md shadow-teal-600/20 transition-all self-start sm:self-auto"
          >
            <Plus className="w-4 h-4" />
            <span>Register New Member</span>
          </button>
        )}
      </div>

      {/* Filter Bar */}
      <div className="glass-card p-4 rounded-2xl flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
          <input
            type="text"
            placeholder="Search by code, org name, reg no..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2 bg-white border border-slate-300 rounded-xl text-xs text-slate-900 placeholder-slate-400 focus:outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600 transition-colors shadow-xs"
          />
        </div>

        <div className="flex flex-wrap items-center gap-3 w-full md:w-auto">
          <select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
            className="bg-white border border-slate-300 text-xs text-slate-700 rounded-xl px-3 py-2 focus:outline-none focus:border-teal-600 shadow-xs"
          >
            <option value="">All Statuses</option>
            <option value="ACTIVE">ACTIVE</option>
            <option value="SUSPENDED">SUSPENDED</option>
            <option value="EXPIRED">EXPIRED</option>
          </select>

          <select
            value={riskLevel}
            onChange={(e) => setRiskLevel(e.target.value)}
            className="bg-white border border-slate-300 text-xs text-slate-700 rounded-xl px-3 py-2 focus:outline-none focus:border-teal-600 shadow-xs"
          >
            <option value="">All Risk Levels</option>
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
            <option value="CRITICAL">CRITICAL</option>
          </select>
        </div>
      </div>

      {/* Members Table */}
      <div className="glass-card rounded-2xl overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-slate-500">Loading members...</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-700">
              <thead className="bg-slate-100/90 text-slate-600 uppercase text-[10px] tracking-wider border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Code</th>
                  <th className="p-3.5">Organization Name</th>
                  <th className="p-3.5">SEBI Reg. Number</th>
                  <th className="p-3.5">Type</th>
                  <th className="p-3.5">Status</th>
                  <th className="p-3.5">Risk Score</th>
                  <th className="p-3.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {members.map((m) => (
                  <tr key={m.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="p-3.5 font-bold text-teal-700">{m.memberCode}</td>
                    <td className="p-3.5 font-semibold text-slate-900">{m.organizationName}</td>
                    <td className="p-3.5 text-slate-500 font-mono text-[11px]">{m.registrationNumber}</td>
                    <td className="p-3.5 text-slate-700">{m.memberType}</td>
                    <td className="p-3.5">
                      <StatusBadge status={m.status} />
                    </td>
                    <td className="p-3.5">
                      <RiskBadge level={m.riskLevel} score={m.riskScore} />
                    </td>
                    <td className="p-3.5 text-right">
                      <button
                        onClick={() => navigate(`/members/${m.id}`)}
                        className="inline-flex items-center space-x-1.5 px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-teal-700 border border-slate-200 rounded-lg text-xs font-semibold transition-colors"
                      >
                        <Eye className="w-3.5 h-3.5" />
                        <span>View Profile</span>
                      </button>
                    </td>
                  </tr>
                ))}
                {members.length === 0 && (
                  <tr>
                    <td colSpan="7" className="p-8 text-center text-slate-500">
                      No regulated members found matching filter parameters.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Register Member Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="glass-card w-full max-w-lg p-6 rounded-2xl border border-slate-200 shadow-xl space-y-4">
            <h2 className="text-base font-bold text-slate-900 flex items-center space-x-2">
              <Building2 className="w-5 h-5 text-teal-600" />
              <span>Register Regulated Entity</span>
            </h2>

            <form onSubmit={handleCreateMember} className="space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-[11px] font-medium text-slate-700 mb-1">Member Code</label>
                  <input
                    type="text"
                    required
                    placeholder="DEMO-SB-001"
                    value={formData.memberCode}
                    onChange={(e) => setFormData({ ...formData, memberCode: e.target.value })}
                    className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-900 focus:outline-none focus:border-teal-600"
                  />
                </div>
                <div>
                  <label className="block text-[11px] font-medium text-slate-700 mb-1">SEBI Reg No.</label>
                  <input
                    type="text"
                    required
                    placeholder="DEMO-SB-9999"
                    value={formData.registrationNumber}
                    onChange={(e) => setFormData({ ...formData, registrationNumber: e.target.value })}
                    className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-900 focus:outline-none focus:border-teal-600"
                  />
                </div>
              </div>

              <div>
                <label className="block text-[11px] font-medium text-slate-700 mb-1">Organization Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Acme Securities Pvt Ltd"
                  value={formData.organizationName}
                  onChange={(e) => setFormData({ ...formData, organizationName: e.target.value })}
                  className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-900 focus:outline-none focus:border-teal-600"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-[11px] font-medium text-slate-700 mb-1">Contact Email</label>
                  <input
                    type="email"
                    required
                    placeholder="compliance@acme.demo"
                    value={formData.contactEmail}
                    onChange={(e) => setFormData({ ...formData, contactEmail: e.target.value })}
                    className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-900 focus:outline-none focus:border-teal-600"
                  />
                </div>
                <div>
                  <label className="block text-[11px] font-medium text-slate-700 mb-1">Contact Phone</label>
                  <input
                    type="text"
                    placeholder="+91 9820011223"
                    value={formData.contactPhone}
                    onChange={(e) => setFormData({ ...formData, contactPhone: e.target.value })}
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
                  className="px-4 py-2 bg-teal-600 hover:bg-teal-700 text-white rounded-xl text-xs font-bold shadow-xs"
                >
                  Save Entity
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Members;
