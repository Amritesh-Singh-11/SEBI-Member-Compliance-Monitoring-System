import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import { Cpu, Play, CheckCircle, AlertOctagon, Terminal } from 'lucide-react';

const Rules = () => {
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [executing, setExecuting] = useState(false);
  const [logs, setLogs] = useState(null);

  const fetchRules = async () => {
    setLoading(true);
    try {
      const res = await api.get('/rules');
      if (res.success) setRules(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRules();
  }, []);

  const handleRunRuleEngine = async () => {
    setExecuting(true);
    try {
      const res = await api.post('/rules/execute');
      if (res.success) {
        setLogs(res.data);
      }
    } catch (err) {
      alert(err.message || 'Error running rule engine');
    } finally {
      setExecuting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-wide">Configurable Compliance Rule Engine</h1>
          <p className="text-xs text-slate-500 mt-1">Automated Regulatory Rule Evaluation & Violation Detection</p>
        </div>

        <button
          onClick={handleRunRuleEngine}
          disabled={executing}
          className="flex items-center space-x-2 px-4 py-2 bg-teal-600 hover:bg-teal-700 text-white font-bold rounded-xl text-xs shadow-md shadow-teal-600/20 transition-all disabled:opacity-50"
        >
          <Play className="w-4 h-4 fill-white" />
          <span>{executing ? 'Executing Rule Engine...' : 'Run Automated Rule Engine'}</span>
        </button>
      </div>

      <div className="glass-card rounded-2xl overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500">Loading rules library...</div>
        ) : (
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-100/90 text-slate-600 uppercase text-[10px] tracking-wider border-b border-slate-200">
              <tr>
                <th className="p-3.5">Rule Code</th>
                <th className="p-3.5">Rule Title</th>
                <th className="p-3.5">Category</th>
                <th className="p-3.5">Condition Expression</th>
                <th className="p-3.5">Severity</th>
                <th className="p-3.5">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {rules.map((r) => (
                <tr key={r.id} className="hover:bg-slate-50/80">
                  <td className="p-3.5 font-bold text-teal-700">{r.ruleCode}</td>
                  <td className="p-3.5 font-semibold text-slate-900">{r.ruleName}</td>
                  <td className="p-3.5 text-slate-500">{r.category}</td>
                  <td className="p-3.5 font-mono text-[11px] text-teal-800 font-semibold">{r.conditionExpression}</td>
                  <td className="p-3.5"><StatusBadge status={r.severityImpact} /></td>
                  <td className="p-3.5 text-slate-700">{r.actionType}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Execution Logs Drawer / Console */}
      {logs && (
        <div className="glass-card p-5 rounded-2xl space-y-3 border-teal-500/30">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-semibold text-slate-900 flex items-center space-x-2">
              <Terminal className="w-4 h-4 text-teal-600" />
              <span>Rule Engine Execution Audit Logs</span>
            </h2>
            <span className="text-xs text-teal-700 font-bold">{logs.length} Rules Processed</span>
          </div>

          <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 max-h-60 overflow-y-auto font-mono text-xs space-y-2">
            {logs.map((log, idx) => (
              <div key={idx} className={`p-2.5 rounded border ${log.triggered ? 'bg-rose-50 border-rose-200 text-rose-800' : 'bg-white border-slate-200 text-slate-700 shadow-xs'}`}>
                <div className="flex items-center justify-between font-bold">
                  <span>[{log.rule?.ruleCode}] {log.rule?.ruleName}</span>
                  <span>{log.triggered ? 'TRIGGERED (VIOLATION CREATED)' : 'PASSED (FALSE)'}</span>
                </div>
                <p className="mt-1 text-[11px] opacity-90">{log.executionDetails}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Rules;
