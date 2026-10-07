import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import RiskBadge from '../components/RiskBadge';
import { 
  Building2, 
  CheckCircle2, 
  Clock, 
  AlertOctagon, 
  ShieldAlert, 
  TrendingUp, 
  ArrowRight,
  RefreshCw
} from 'lucide-react';
import { 
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell, PieChart, Pie
} from 'recharts';

const Dashboard = () => {
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchMetrics = async () => {
    setLoading(true);
    try {
      const res = await api.get('/dashboard/metrics');
      if (res.success) {
        setMetrics(res.data);
      }
    } catch (err) {
      console.error("Dashboard metrics load error", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMetrics();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-teal-500"></div>
      </div>
    );
  }

  const riskData = metrics?.riskLevelDistribution ? [
    { name: 'LOW', value: metrics.riskLevelDistribution.LOW || 0, color: '#10B981' },
    { name: 'MEDIUM', value: metrics.riskLevelDistribution.MEDIUM || 0, color: '#F59E0B' },
    { name: 'HIGH', value: metrics.riskLevelDistribution.HIGH || 0, color: '#F97316' },
    { name: 'CRITICAL', value: metrics.riskLevelDistribution.CRITICAL || 0, color: '#EF4444' }
  ] : [];

  const statusData = metrics?.complianceStatusDistribution ? [
    { name: 'Compliant', value: metrics.complianceStatusDistribution.COMPLIANT || 0, fill: '#10B981' },
    { name: 'Pending', value: metrics.complianceStatusDistribution.PENDING || 0, fill: '#3B82F6' },
    { name: 'Submitted', value: metrics.complianceStatusDistribution.SUBMITTED || 0, fill: '#8B5CF6' },
    { name: 'Overdue', value: metrics.complianceStatusDistribution.OVERDUE || 0, fill: '#F59E0B' },
    { name: 'Rejected', value: metrics.complianceStatusDistribution.REJECTED || 0, fill: '#EF4444' }
  ] : [];

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-wide">Executive Compliance Dashboard</h1>
          <p className="text-xs text-slate-500 mt-1">
            Real-Time Regulatory Monitoring & Risk Intelligence for Stock Brokers
          </p>
        </div>
        <button
          onClick={fetchMetrics}
          className="flex items-center space-x-2 px-3.5 py-2 bg-white border border-slate-300 hover:border-teal-500 rounded-xl text-xs font-semibold text-slate-700 hover:text-teal-700 transition-all shadow-xs self-start md:self-auto"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          <span>Refresh Data</span>
        </button>
      </div>

      {/* Top Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div 
          onClick={() => navigate('/members')}
          className="glass-card glass-card-hover p-5 rounded-2xl cursor-pointer"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-500">Total Members</span>
            <div className="w-8 h-8 rounded-lg bg-teal-50 text-teal-600 flex items-center justify-center">
              <Building2 className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-bold text-slate-900 mt-3">{metrics?.totalMembers || 0}</div>
          <div className="text-[11px] text-teal-700 mt-1 font-semibold">{metrics?.activeMembers || 0} Active Entities</div>
        </div>

        <div 
          onClick={() => navigate('/compliance')}
          className="glass-card glass-card-hover p-5 rounded-2xl cursor-pointer"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-500">Compliance Rate</span>
            <div className="w-8 h-8 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <TrendingUp className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-bold text-slate-900 mt-3">{metrics?.overallComplianceRate || 100}%</div>
          <div className="text-[11px] text-emerald-700 mt-1 font-semibold">{metrics?.compliantMembers || 0} Filings Verified</div>
        </div>

        <div 
          onClick={() => navigate('/violations')}
          className="glass-card glass-card-hover p-5 rounded-2xl cursor-pointer"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-500">Open Violations</span>
            <div className="w-8 h-8 rounded-lg bg-amber-50 text-amber-600 flex items-center justify-center">
              <AlertOctagon className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-bold text-slate-900 mt-3">{metrics?.openViolationsCount || 0}</div>
          <div className="text-[11px] text-amber-700 mt-1 font-semibold">Requires Investigation</div>
        </div>

        <div 
          onClick={() => navigate('/risk')}
          className="glass-card glass-card-hover p-5 rounded-2xl cursor-pointer"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-500">High Risk Members</span>
            <div className="w-8 h-8 rounded-lg bg-rose-50 text-rose-600 flex items-center justify-center">
              <ShieldAlert className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-bold text-slate-900 mt-3">{metrics?.highCriticalRiskMembersCount || 0}</div>
          <div className="text-[11px] text-rose-700 mt-1 font-semibold">Critical Inspection Flag</div>
        </div>
      </div>

      {/* Analytics Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Compliance Status Distribution */}
        <div className="glass-card p-5 rounded-2xl lg:col-span-2">
          <h2 className="text-sm font-semibold text-slate-900 mb-4">Compliance Status Distribution</h2>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={statusData}>
                <XAxis dataKey="name" stroke="#64748B" fontSize={12} />
                <YAxis stroke="#64748B" fontSize={12} />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#FFFFFF', borderColor: '#E2E8F0', borderRadius: '8px', color: '#0F172A', boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}
                />
                <Bar dataKey="value" radius={[6, 6, 0, 0]}>
                  {statusData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.fill} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Risk Distribution Pie */}
        <div className="glass-card p-5 rounded-2xl">
          <h2 className="text-sm font-semibold text-slate-900 mb-4">Risk Profile Breakdown</h2>
          <div className="h-64 flex items-center justify-center">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={riskData}
                  cx="50%"
                  cy="50%"
                  innerRadius={55}
                  outerRadius={80}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {riskData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip 
                  contentStyle={{ backgroundColor: '#FFFFFF', borderColor: '#E2E8F0', borderRadius: '8px', color: '#0F172A', boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className="grid grid-cols-2 gap-2 mt-2">
            {riskData.map((item) => (
              <div key={item.name} className="flex items-center space-x-2 text-xs text-slate-700">
                <span className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: item.color }}></span>
                <span>{item.name}: {item.value}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Top High Risk Members Spotlight Table */}
      <div className="glass-card p-5 rounded-2xl">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-sm font-semibold text-slate-900 flex items-center space-x-2">
            <ShieldAlert className="w-4 h-4 text-rose-600" />
            <span>High Risk Member Spotlights</span>
          </h2>
          <button 
            onClick={() => navigate('/members?riskLevel=HIGH')}
            className="text-xs font-semibold text-teal-700 hover:text-teal-800 flex items-center space-x-1"
          >
            <span>View All Members</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-100/90 text-slate-600 uppercase text-[10px] tracking-wider border-b border-slate-200">
              <tr>
                <th className="p-3">Member Code</th>
                <th className="p-3">Organization Name</th>
                <th className="p-3">SEBI Reg No.</th>
                <th className="p-3">Risk Level</th>
                <th className="p-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {metrics?.topHighRiskMembers?.map((m) => (
                <tr key={m.id} className="hover:bg-slate-50/80 transition-colors">
                  <td className="p-3 font-semibold text-slate-900">{m.memberCode}</td>
                  <td className="p-3 font-medium text-slate-800">{m.organizationName}</td>
                  <td className="p-3 text-slate-500">{m.registrationNumber}</td>
                  <td className="p-3">
                    <RiskBadge level={m.riskLevel} score={m.riskScore} />
                  </td>
                  <td className="p-3 text-right">
                    <button
                      onClick={() => navigate(`/members/${m.id}`)}
                      className="px-2.5 py-1 bg-teal-50 hover:bg-teal-100 text-teal-700 border border-teal-200 rounded text-[11px] font-semibold transition-colors"
                    >
                      Audit Member
                    </button>
                  </td>
                </tr>
              ))}
              {(!metrics?.topHighRiskMembers || metrics.topHighRiskMembers.length === 0) && (
                <tr>
                  <td colSpan="5" className="p-6 text-center text-slate-500">
                    No high risk members flagged. System operating normally.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
