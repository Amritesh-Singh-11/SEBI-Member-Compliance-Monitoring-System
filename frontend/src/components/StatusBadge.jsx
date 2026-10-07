import React from 'react';

const StatusBadge = ({ status }) => {
  const getStyles = (st) => {
    switch (st?.toUpperCase()) {
      case 'COMPLIANT':
      case 'APPROVED':
      case 'RESOLVED':
      case 'COMPLETED':
      case 'ACTIVE':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';
      case 'PENDING':
      case 'SUBMITTED':
      case 'UNDER_REVIEW':
      case 'IN_PROGRESS':
        return 'bg-blue-50 text-blue-700 border-blue-200';
      case 'OVERDUE':
      case 'ACTION_REQUIRED':
      case 'OPEN':
      case 'MEDIUM':
        return 'bg-amber-50 text-amber-700 border-amber-200';
      case 'REJECTED':
      case 'NON_COMPLIANT':
      case 'CRITICAL':
      case 'HIGH':
      case 'SUSPENDED':
        return 'bg-rose-50 text-rose-700 border-rose-200';
      default:
        return 'bg-slate-100 text-slate-700 border-slate-200';
    }
  };

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-semibold border ${getStyles(status)}`}>
      {status || 'UNKNOWN'}
    </span>
  );
};

export default StatusBadge;
