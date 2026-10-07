import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { BookOpen, ExternalLink, ShieldCheck } from 'lucide-react';

const Regulations = () => {
  const [regulations, setRegulations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRegs = async () => {
      setLoading(true);
      try {
        const res = await api.get('/regulations');
        if (res.success) setRegulations(res.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchRegs();
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-900 tracking-wide">Regulatory Circulars & Framework Library</h1>
        <p className="text-xs text-slate-500 mt-1">SEBI Reference Master Directions & Version Control</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {regulations.map((r) => (
          <div key={r.id} className="glass-card p-5 rounded-2xl space-y-3">
            <div className="flex items-start justify-between">
              <div className="space-y-1">
                <span className="text-[10px] font-bold text-teal-700 uppercase tracking-wider bg-teal-50 border border-teal-200 px-2 py-0.5 rounded">
                  v{r.version}
                </span>
                <h3 className="text-sm font-bold text-slate-900 mt-1">{r.title}</h3>
                <div className="text-[11px] text-slate-500 font-mono">Ref: {r.referenceNumber}</div>
              </div>
            </div>

            <p className="text-xs text-slate-700 leading-relaxed">{r.description}</p>

            <div className="pt-3 border-t border-slate-200 flex items-center justify-between text-xs text-slate-500">
              <span>Effective: <strong className="text-slate-800">{r.effectiveDate}</strong></span>
              {r.sourceUrl && (
                <a
                  href={r.sourceUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="text-teal-700 hover:text-teal-800 font-semibold flex items-center space-x-1"
                >
                  <span>Official Circular</span>
                  <ExternalLink className="w-3.5 h-3.5" />
                </a>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Regulations;
