import React from 'react';

const RiskBadge = ({ level, score }) => {
  const getRiskStyles = (lvl) => {
    switch (lvl?.toUpperCase()) {
      case 'LOW':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';
      case 'MEDIUM':
        return 'bg-amber-50 text-amber-700 border-amber-200';
      case 'HIGH':
        return 'bg-orange-50 text-orange-700 border-orange-200';
      case 'CRITICAL':
        return 'bg-rose-50 text-rose-700 border-rose-200';
      default:
        return 'bg-slate-100 text-slate-700 border-slate-200';
    }
  };

  return (
    <span className={`inline-flex items-center space-x-1 px-2.5 py-0.5 rounded-full text-[11px] font-semibold border ${getRiskStyles(level)}`}>
      <span>{level || 'LOW'}</span>
      {score !== undefined && score !== null && (
        <span className="opacity-80">({score}/100)</span>
      )}
    </span>
  );
};

export default RiskBadge;
