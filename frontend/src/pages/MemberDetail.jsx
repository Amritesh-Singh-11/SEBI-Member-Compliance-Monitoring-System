import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import RiskBadge from '../components/RiskBadge';
import { 
  Building2, 
  FileText, 
  AlertTriangle, 
  Activity, 
  ShieldAlert, 
  RefreshCw,
  ArrowLeft,
  Mail,
  Phone,
  MapPin,
  Calendar
} from 'lucide-react';

const MemberDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [member, setMember] = useState(null);
  const [riskAssessment, setRiskAssessment] = useState(null);
  const [records, setRecords] = useState([]);
  const [violations, setViolations] = useState([]);
  const [documents, setDocuments] = useState([]);
  const [activeTab, setActiveTab] = useState('overview');
  const [loading, setLoading] = useState(true);

  const fetchMemberData = async () => {
    setLoading(true);
    try {
      const [mRes, rRes, recRes, vRes, dRes] = await Promise.all([
        api.get(`/members/${id}`),
        api.get(`/risk/members/${id}`),
        api.get(`/compliance/records`, { params: { memberId: id, size: 50 } }),
        api.get(`/violations`, { params: { memberId: id, size: 50 } }),
        api.get(`/documents`, { params: { memberId: id, size: 50 } })
      ]);

      if (mRes.success) setMember(mRes.data);
      if (rRes.success) setRiskAssessment(rRes.data);
      if (recRes.success) setRecords(recRes.data.content);
      if (vRes.success) setViolations(vRes.data.content);
      if (dRes.success) setDocuments(dRes.data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMemberData();
  }, [id]);

  const handleRecalculateRisk = async () => {
    try {
      const res = await api.post(`/risk/recalculate/${id}`);
      if (res.success) {
        setRiskAssessment(res.data);
        fetchMemberData();
      }
    } catch (err) {
      alert(err.message || 'Error recalculating risk');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-teal-500"></div>
      </div>
    );
  }

  if (!member) {
    return <div className="p-8 text-center text-slate-400">Member profile not found.</div>;
  }

  const factors = riskAssessment?.contributingFactorsJson ? JSON.parse(riskAssessment.contributingFactorsJson) : null;

  return (
    <div className="space-y-6">
      {/* Back Button */}
      <button
        onClick={() => navigate('/members')}
        className="flex items-center space-x-2 text-xs font-semibold text-slate-500 hover:text-slate-900 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Back to Regulated Members Registry</span>
      </button>

      {/* Member Header Card */}
      <div className="glass-card p-6 rounded-2xl flex flex-col lg:flex-row lg:items-center justify-between gap-6">
        <div className="flex items-start space-x-4">
          <div className="w-14 h-14 rounded-2xl bg-teal-50 border border-teal-200 text-teal-700 flex items-center justify-center font-bold text-xl shrink-0">
            {member.memberCode.substring(0, 3)}
          </div>
          <div>
            <div className="flex items-center space-x-3">
              <h1 className="text-xl font-bold text-slate-900">{member.organizationName}</h1>
              <StatusBadge status={member.status} />
            </div>
            <div className="flex flex-wrap items-center gap-4 mt-2 text-xs text-slate-500">
              <span>Code: <strong className="text-slate-800">{member.memberCode}</strong></span>
              <span>•</span>
              <span>SEBI Reg: <strong className="text-slate-800 font-mono">{member.registrationNumber}</strong></span>
              <span>•</span>
              <span>Category: <strong className="text-slate-800">{member.memberType}</strong></span>
            </div>
          </div>
        </div>

        {/* Risk Score Highlight Pill */}
        <div className="bg-white border border-slate-200 shadow-sm p-4 rounded-xl flex items-center space-x-4">
          <div className="text-right">
            <div className="text-[10px] uppercase tracking-wider text-slate-500 font-semibold">Academic Risk Level</div>
            <div className="text-lg font-bold text-slate-900 mt-0.5">{member.riskScore} / 100</div>
          </div>
          <RiskBadge level={member.riskLevel} />
          <button
            onClick={handleRecalculateRisk}
            title="Recalculate Risk Score"
            className="p-2 bg-slate-100 hover:bg-slate-200 text-teal-700 rounded-lg transition-colors"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="flex space-x-2 border-b border-slate-200 pb-2">
        {['overview', 'compliance', 'documents', 'violations', 'risk'].map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-4 py-2 rounded-xl text-xs font-semibold uppercase tracking-wider transition-all ${
              activeTab === tab
                ? 'bg-teal-50 text-teal-700 border border-teal-200 font-bold shadow-xs'
                : 'text-slate-500 hover:text-slate-900 hover:bg-slate-100'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      {activeTab === 'overview' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="glass-card p-4 rounded-xl">
              <div className="text-xs text-slate-500 font-medium">Total Filings Assigned</div>
              <div className="text-xl font-bold text-slate-900 mt-1">{records.length}</div>
            </div>
            <div className="glass-card p-4 rounded-xl">
              <div className="text-xs text-slate-500 font-medium">Active Violations</div>
              <div className="text-xl font-bold text-rose-600 mt-1">{violations.length}</div>
            </div>
            <div className="glass-card p-4 rounded-xl">
              <div className="text-xs text-slate-500 font-medium">Documents Vaulted</div>
              <div className="text-xl font-bold text-teal-700 mt-1">{documents.length}</div>
            </div>
          </div>

          <div className="glass-card p-5 rounded-2xl">
            <h3 className="text-sm font-semibold text-slate-900 mb-3">Entity Contact Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs text-slate-700">
              <div className="flex items-center space-x-2">
                <Mail className="w-4 h-4 text-teal-600" />
                <span>{member.contactEmail}</span>
              </div>
              <div className="flex items-center space-x-2">
                <Phone className="w-4 h-4 text-teal-600" />
                <span>{member.contactPhone || 'N/A'}</span>
              </div>
              <div className="flex items-center space-x-2">
                <MapPin className="w-4 h-4 text-teal-600" />
                <span>{member.address}, {member.city}, {member.state}</span>
              </div>
              <div className="flex items-center space-x-2">
                <Calendar className="w-4 h-4 text-teal-600" />
                <span>Reg Validity: {member.registrationValidity}</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'compliance' && (
        <div className="glass-card p-5 rounded-2xl">
          <h3 className="text-sm font-semibold text-slate-900 mb-4">Assigned Compliance Filings</h3>
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-100/90 text-slate-600 border-b border-slate-200">
              <tr>
                <th className="p-3">Requirement</th>
                <th className="p-3">Due Date</th>
                <th className="p-3">Submitted On</th>
                <th className="p-3">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {records.map((r) => (
                <tr key={r.id}>
                  <td className="p-3 font-medium text-slate-900">{r.requirement?.title}</td>
                  <td className="p-3 text-slate-500">{r.dueDate}</td>
                  <td className="p-3 text-slate-500">{r.submissionDate || '-'}</td>
                  <td className="p-3"><StatusBadge status={r.status} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'risk' && (
        <div className="glass-card p-5 rounded-2xl space-y-4">
          <h3 className="text-sm font-semibold text-slate-900 flex items-center space-x-2">
            <Activity className="w-4 h-4 text-teal-600" />
            <span>Academic Explainable Risk Breakdown</span>
          </h3>
          {factors?.explanations && (
            <div className="space-y-2">
              <p className="text-xs text-slate-500">Contributing factors for {member.riskLevel} score ({member.riskScore}):</p>
              <ul className="list-disc list-inside text-xs text-slate-700 space-y-1">
                {factors.explanations.map((exp, idx) => (
                  <li key={idx} className="text-amber-700 font-medium">{exp}</li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default MemberDetail;
