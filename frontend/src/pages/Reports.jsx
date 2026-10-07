import React from 'react';
import { FileSpreadsheet, Download, FileText, ShieldCheck } from 'lucide-react';

const Reports = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-900 tracking-wide">Compliance & Risk Report Generator</h1>
        <p className="text-xs text-slate-500 mt-1">Export Executive Master PDF Summaries & Audit CSV Datasets</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* PDF Executive Master Report */}
        <div className="glass-card p-6 rounded-2xl space-y-4 border-teal-200">
          <div className="w-12 h-12 rounded-xl bg-teal-50 border border-teal-200 text-teal-700 flex items-center justify-center">
            <FileText className="w-6 h-6" />
          </div>
          <div>
            <h2 className="text-base font-bold text-slate-900">Executive Compliance PDF Report</h2>
            <p className="text-xs text-slate-500 mt-1">
              Comprehensive report detailing member risk scores, active violations, and regulatory compliance status.
            </p>
          </div>

          <a
            href="http://localhost:8080/api/v1/reports/compliance/pdf"
            target="_blank"
            rel="noreferrer"
            className="inline-flex items-center space-x-2 px-4 py-2.5 bg-teal-600 hover:bg-teal-700 text-white font-bold rounded-xl text-xs shadow-md shadow-teal-600/20 transition-all"
          >
            <Download className="w-4 h-4" />
            <span>Download Master PDF Report</span>
          </a>
        </div>

        {/* CSV Data Export */}
        <div className="glass-card p-6 rounded-2xl space-y-4 border-cyan-200">
          <div className="w-12 h-12 rounded-xl bg-cyan-50 border border-cyan-200 text-cyan-700 flex items-center justify-center">
            <FileSpreadsheet className="w-6 h-6" />
          </div>
          <div>
            <h2 className="text-base font-bold text-slate-900">Member Compliance CSV Dataset</h2>
            <p className="text-xs text-slate-500 mt-1">
              Export raw tabular data of all registered stock brokers, registration numbers, risk levels, and statuses.
            </p>
          </div>

          <a
            href="http://localhost:8080/api/v1/reports/compliance/csv"
            target="_blank"
            rel="noreferrer"
            className="inline-flex items-center space-x-2 px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-cyan-800 border border-slate-200 rounded-xl text-xs font-bold transition-all shadow-xs"
          >
            <Download className="w-4 h-4" />
            <span>Export CSV Dataset</span>
          </a>
        </div>
      </div>
    </div>
  );
};

export default Reports;
