import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { History, Shield } from 'lucide-react';

const AuditTrail = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAudit = async () => {
      setLoading(true);
      try {
        const res = await api.get('/audit-logs');
        if (res.success) setLogs(res.data.content);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchAudit();
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-900 tracking-wide">Immutable System Audit Trail</h1>
        <p className="text-xs text-slate-500 mt-1">Cryptographic Security & Action Log History (Read-Only)</p>
      </div>

      <div className="glass-card rounded-2xl overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500">Loading audit trail...</div>
        ) : (
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-100/90 text-slate-600 uppercase text-[10px] tracking-wider border-b border-slate-200">
              <tr>
                <th className="p-3.5">Timestamp</th>
                <th className="p-3.5">Actor</th>
                <th className="p-3.5">Action Event</th>
                <th className="p-3.5">Entity Type</th>
                <th className="p-3.5">Entity ID</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200 font-mono text-[11px]">
              {logs.map((log) => (
                <tr key={log.id} className="hover:bg-slate-50/80">
                  <td className="p-3.5 text-slate-500">{log.timestamp?.replace('T', ' ').substring(0, 19)}</td>
                  <td className="p-3.5 font-bold text-teal-700">{log.actorUsername}</td>
                  <td className="p-3.5 text-slate-900 font-sans font-semibold">{log.action}</td>
                  <td className="p-3.5 text-cyan-700">{log.entityType}</td>
                  <td className="p-3.5 text-slate-500">#{log.entityId || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default AuditTrail;
