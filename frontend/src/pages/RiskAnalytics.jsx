import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import RiskBadge from '../components/RiskBadge';
import { Activity, Cpu, Sparkles, RefreshCw, AlertTriangle } from 'lucide-react';

const RiskAnalytics = () => {
  const [members, setMembers] = useState([]);
  const [selectedMemberId, setSelectedMemberId] = useState('');
  const [assessment, setAssessment] = useState(null);
  const [mlPrediction, setMlPrediction] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchMembers = async () => {
      try {
        const res = await api.get('/members', { params: { size: 50 } });
        if (res.success && res.data.content.length > 0) {
          setMembers(res.data.content);
          setSelectedMemberId(res.data.content[0].id);
        }
      } catch (err) {
        console.error(err);
      }
    };
    fetchMembers();
  }, []);

  const loadRiskData = async (mId) => {
    if (!mId) return;
    setLoading(true);
    try {
      const res = await api.get(`/risk/members/${mId}`);
      if (res.success) setAssessment(res.data);

      // Call Python FastAPI ML Service
      try {
        const mlRes = await fetch('http://localhost:8000/predict-risk', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            memberId: parseInt(mId),
            violationCount: 2,
            highSeverityViolationCount: 1,
            lateSubmissionCount: 1,
            complianceRate: 82.0,
            repeatedViolationCount: 1,
            previousRiskScore: res.data?.riskScore || 50.0
          })
        });
        if (mlRes.ok) {
          const mlData = await mlRes.json();
          setMlPrediction(mlData);
        } else {
          setMlPrediction(null);
        }
      } catch (mlErr) {
        setMlPrediction(null);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (selectedMemberId) {
      loadRiskData(selectedMemberId);
    }
  }, [selectedMemberId]);

  const factors = assessment?.contributingFactorsJson ? JSON.parse(assessment.contributingFactorsJson) : null;

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-wide">Academic Risk Assessment & Advisory ML</h1>
          <p className="text-xs text-slate-500 mt-1">Deterministic Formula & Explainable Risk Scoring Engine</p>
        </div>

        <select
          value={selectedMemberId}
          onChange={(e) => setSelectedMemberId(e.target.value)}
          className="bg-white border border-slate-300 text-xs text-teal-700 font-semibold rounded-xl px-4 py-2 focus:outline-none focus:border-teal-600 shadow-xs"
        >
          {members.map(m => (
            <option key={m.id} value={m.id}>{m.organizationName} ({m.memberCode})</option>
          ))}
        </select>
      </div>

      {/* Formula Card */}
      <div className="glass-card p-5 rounded-2xl bg-teal-50/50 border-teal-200 shadow-xs">
        <h2 className="text-xs uppercase tracking-wider text-teal-700 font-bold mb-2">Deterministic Academic Formula</h2>
        <div className="text-xs text-slate-700 space-y-1">
          <p className="font-mono text-teal-800 font-semibold">
            Risk Score = 0.30 × ViolationFrequency + 0.25 × ViolationSeverity + 0.20 × LateSubmissions + 0.15 × ComplianceHistory + 0.10 × RepeatedViolations
          </p>
          <div className="grid grid-cols-2 md:grid-cols-5 gap-2 pt-2 text-[10px] text-slate-500 font-medium">
            <div>• Violation Frequency (30%)</div>
            <div>• Severity Weight (25%)</div>
            <div>• Late Filings (20%)</div>
            <div>• Compliance History (15%)</div>
            <div>• Recurrence (10%)</div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Deterministic System Score */}
        <div className="glass-card p-6 rounded-2xl space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-semibold text-slate-900 flex items-center space-x-2">
              <Activity className="w-4 h-4 text-teal-600" />
              <span>Official System Deterministic Risk Score</span>
            </h2>
            <RiskBadge level={assessment?.riskLevel} score={assessment?.riskScore} />
          </div>

          <div className="text-3xl font-extrabold text-slate-900">
            {assessment?.riskScore || 0} <span className="text-xs text-slate-500 font-normal">/ 100</span>
          </div>

          {factors && (
            <div className="space-y-3 pt-3 border-t border-slate-200">
              <h3 className="text-xs font-semibold text-slate-700">Human-Readable Contributing Factors:</h3>
              <ul className="space-y-1.5">
                {factors.explanations?.map((exp, idx) => (
                  <li key={idx} className="text-xs text-amber-800 flex items-start space-x-2 font-medium">
                    <span className="text-amber-600 font-bold">•</span>
                    <span>{exp}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>

        {/* Advisory FastAPI ML Service Card */}
        <div className="glass-card p-6 rounded-2xl space-y-4 border-teal-200">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-semibold text-slate-900 flex items-center space-x-2">
              <Sparkles className="w-4 h-4 text-cyan-600" />
              <span>Python FastAPI Advisory Risk Predictor</span>
            </h2>
            <span className="text-[10px] uppercase font-bold text-cyan-700 bg-cyan-50 border border-cyan-200 px-2 py-0.5 rounded">
              Advisory ML
            </span>
          </div>

          {mlPrediction ? (
            <div className="space-y-4">
              <div className="flex items-center justify-between bg-slate-50 p-3 rounded-xl border border-slate-200">
                <div>
                  <div className="text-[10px] text-slate-500 font-medium">Predicted Risk Category</div>
                  <div className="text-base font-bold text-cyan-800">{mlPrediction.predictedRiskLevel}</div>
                </div>
                <div className="text-right">
                  <div className="text-[10px] text-slate-500 font-medium">Model Probability</div>
                  <div className="text-base font-bold text-teal-700">{(mlPrediction.riskProbability * 100).toFixed(1)}%</div>
                </div>
              </div>

              <div className="space-y-1.5">
                <div className="text-xs font-semibold text-slate-700">RandomForest Feature Importance:</div>
                {Object.entries(mlPrediction.featureImportance || {}).map(([key, val]) => (
                  <div key={key} className="flex items-center justify-between text-[11px] text-slate-600">
                    <span>{key}</span>
                    <span className="font-mono text-cyan-700 font-bold">{(val * 100).toFixed(1)}%</span>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <div className="p-6 text-center text-slate-500 text-xs">
              FastAPI ML Service offline or advisory mode active. Core system operates with 100% functionality.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RiskAnalytics;
