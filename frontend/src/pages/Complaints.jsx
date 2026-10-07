import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import { MessageSquare, Plus } from 'lucide-react';

const Complaints = () => {
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchComplaints = async () => {
      setLoading(true);
      try {
        const res = await api.get('/complaints');
        if (res.success) setComplaints(res.data.content);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchComplaints();
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-900 tracking-wide">Compliance Complaints Tracker</h1>
        <p className="text-xs text-slate-500 mt-1">Internal Investor Grievances & Resolution Monitoring (Academic Prototype)</p>
      </div>

      <div className="glass-card rounded-2xl overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500">Loading complaints...</div>
        ) : (
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-100/90 text-slate-600 uppercase text-[10px] tracking-wider border-b border-slate-200">
              <tr>
                <th className="p-3.5">Complaint Code</th>
                <th className="p-3.5">Member Entity</th>
                <th className="p-3.5">Category</th>
                <th className="p-3.5">Received Date</th>
                <th className="p-3.5">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {complaints.map((c) => (
                <tr key={c.id} className="hover:bg-slate-50/80">
                  <td className="p-3.5 font-bold text-teal-700">{c.complaintCode}</td>
                  <td className="p-3.5 font-semibold text-slate-900">{c.member?.organizationName}</td>
                  <td className="p-3.5 text-slate-700">{c.category}</td>
                  <td className="p-3.5 text-slate-500">{c.receivedDate}</td>
                  <td className="p-3.5"><StatusBadge status={c.status} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default Complaints;
