import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import { useAuth } from '../context/AuthContext';
import { FileText, Upload, Download, CheckCircle2, XCircle, FileCheck, ShieldAlert } from 'lucide-react';

const Documents = () => {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showUploadModal, setShowUploadModal] = useState(false);
  const [members, setMembers] = useState([]);
  const [records, setRecords] = useState([]);

  // Upload Form State
  const [uploadData, setUploadData] = useState({
    memberId: '',
    complianceRecordId: '',
    documentType: 'CYBER_AUDIT',
    expiryDate: '2027-12-31'
  });
  const [selectedFile, setSelectedFile] = useState(null);

  const { isOfficer, isAdmin } = useAuth();

  const fetchDocuments = async () => {
    setLoading(true);
    try {
      const res = await api.get('/documents', { params: { size: 50 } });
      if (res.success) setDocuments(res.data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchInitialData = async () => {
    try {
      const [mRes, rRes] = await Promise.all([
        api.get('/members', { params: { size: 50 } }),
        api.get('/compliance/records', { params: { size: 50 } })
      ]);
      if (mRes.success) setMembers(mRes.data.content);
      if (rRes.success) setRecords(rRes.data.content);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchDocuments();
    fetchInitialData();
  }, []);

  const handleUploadSubmit = async (e) => {
    e.preventDefault();
    if (!selectedFile) {
      alert('Please select a file to upload');
      return;
    }

    const form = new FormData();
    form.append('memberId', uploadData.memberId);
    form.append('complianceRecordId', uploadData.complianceRecordId);
    form.append('documentType', uploadData.documentType);
    if (uploadData.expiryDate) form.append('expiryDate', uploadData.expiryDate);
    form.append('file', selectedFile);

    try {
      const res = await api.post('/documents/upload', form, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      if (res.success) {
        setShowUploadModal(false);
        setSelectedFile(null);
        fetchDocuments();
      }
    } catch (err) {
      alert(err.message || 'Error uploading document');
    }
  };

  const handleReview = async (docId, status) => {
    const reason = status === 'REJECTED' ? prompt('Reason for rejection:') : null;
    try {
      const res = await api.patch(`/documents/${docId}/review`, null, {
        params: { status, rejectionReason: reason || undefined }
      });
      if (res.success) fetchDocuments();
    } catch (err) {
      alert(err.message || 'Error reviewing document');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-wide">Document Evidence Vault</h1>
          <p className="text-xs text-slate-500 mt-1">Audit Trail Documents & Certificate Verification</p>
        </div>

        <button
          onClick={() => setShowUploadModal(true)}
          className="flex items-center space-x-2 px-4 py-2 bg-teal-600 hover:bg-teal-700 text-white font-bold rounded-xl text-xs shadow-md shadow-teal-600/20 transition-all"
        >
          <Upload className="w-4 h-4" />
          <span>Upload Document Proof</span>
        </button>
      </div>

      <div className="glass-card rounded-2xl overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500">Loading document vault...</div>
        ) : (
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-100/90 text-slate-600 uppercase text-[10px] tracking-wider border-b border-slate-200">
              <tr>
                <th className="p-3.5">Member Code</th>
                <th className="p-3.5">Filename</th>
                <th className="p-3.5">Type</th>
                <th className="p-3.5">Uploaded By</th>
                <th className="p-3.5">Uploaded At</th>
                <th className="p-3.5">Status</th>
                <th className="p-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {documents.map((d) => (
                <tr key={d.id} className="hover:bg-slate-50/80">
                  <td className="p-3.5 font-bold text-teal-700">{d.member?.memberCode}</td>
                  <td className="p-3.5 font-semibold text-slate-900 flex items-center space-x-2">
                    <FileText className="w-4 h-4 text-slate-400 shrink-0" />
                    <span>{d.originalFilename}</span>
                  </td>
                  <td className="p-3.5 text-slate-700">{d.documentType}</td>
                  <td className="p-3.5 text-slate-500">{d.uploadedBy}</td>
                  <td className="p-3.5 text-slate-500">{d.uploadedAt?.replace('T', ' ').substring(0, 16)}</td>
                  <td className="p-3.5"><StatusBadge status={d.status} /></td>
                  <td className="p-3.5 text-right space-x-2">
                    <a
                      href={`http://localhost:8080/api/v1/documents/${d.id}/download`}
                      target="_blank"
                      rel="noreferrer"
                      className="inline-flex items-center space-x-1 px-2.5 py-1 bg-slate-100 hover:bg-slate-200 text-teal-700 border border-slate-200 rounded text-xs font-semibold transition-colors"
                    >
                      <Download className="w-3.5 h-3.5" />
                      <span>Download</span>
                    </a>

                    {(isOfficer || isAdmin) && d.status === 'UPLOADED' && (
                      <>
                        <button
                          onClick={() => handleReview(d.id, 'APPROVED')}
                          className="px-2.5 py-1 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200 rounded text-xs font-semibold"
                        >
                          Approve
                        </button>
                        <button
                          onClick={() => handleReview(d.id, 'REJECTED')}
                          className="px-2.5 py-1 bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 rounded text-xs font-semibold"
                        >
                          Reject
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Upload Document Modal */}
      {showUploadModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="glass-card w-full max-w-lg p-6 rounded-2xl border border-slate-200 shadow-xl space-y-4">
            <h2 className="text-base font-bold text-slate-900 flex items-center space-x-2">
              <Upload className="w-5 h-5 text-teal-600" />
              <span>Upload Compliance Proof Evidence</span>
            </h2>

            <form onSubmit={handleUploadSubmit} className="space-y-3">
              <div>
                <label className="block text-[11px] font-medium text-slate-700 mb-1">Select Member Entity</label>
                <select
                  required
                  value={uploadData.memberId}
                  onChange={(e) => setUploadData({ ...uploadData, memberId: e.target.value })}
                  className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-900 focus:outline-none focus:border-teal-600"
                >
                  <option value="">-- Choose Stock Broker --</option>
                  {members.map(m => (
                    <option key={m.id} value={m.id}>{m.organizationName} ({m.memberCode})</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-[11px] font-medium text-slate-700 mb-1">Select Filing Period Record</label>
                <select
                  required
                  value={uploadData.complianceRecordId}
                  onChange={(e) => setUploadData({ ...uploadData, complianceRecordId: e.target.value })}
                  className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-900 focus:outline-none focus:border-teal-600"
                >
                  <option value="">-- Choose Requirement Record --</option>
                  {records.map(r => (
                    <option key={r.id} value={r.id}>
                      {r.member?.memberCode} - {r.requirement?.title} (Due: {r.dueDate})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-[11px] font-medium text-slate-700 mb-1">Attach File (PDF, DOCX, XLSX, PNG)</label>
                <input
                  type="file"
                  required
                  onChange={(e) => setSelectedFile(e.target.files[0])}
                  className="w-full px-3 py-2 bg-white border border-slate-300 rounded-lg text-xs text-slate-700"
                />
              </div>

              <div className="flex justify-end space-x-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowUploadModal(false)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-teal-600 hover:bg-teal-700 text-white rounded-xl text-xs font-bold shadow-xs"
                >
                  Upload Evidence
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Documents;
