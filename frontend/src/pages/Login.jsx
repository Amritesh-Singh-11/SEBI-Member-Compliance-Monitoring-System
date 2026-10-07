import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Lock, AlertCircle, ShieldCheck } from 'lucide-react';

const Login = () => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login, loading } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    const res = await login(usernameOrEmail, password);
    if (res.success) {
      navigate('/dashboard');
    } else {
      setError(res.message);
    }
  };

  const handleDemoPreset = (u, p) => {
    setUsernameOrEmail(u);
    setPassword(p);
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center items-center p-4 relative overflow-hidden text-slate-900">
      {/* Light Background Subtle Orbs */}
      <div className="absolute top-1/4 left-1/3 w-96 h-96 bg-teal-500/10 rounded-full blur-3xl pointer-events-none"></div>
      <div className="absolute bottom-1/4 right-1/3 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none"></div>

      <div className="w-full max-w-md z-10">
        {/* Header Branding */}
        <div className="text-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-teal-600 to-cyan-500 mx-auto flex items-center justify-center font-extrabold text-white text-2xl shadow-xl shadow-teal-500/20 mb-3">
            RL
          </div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">ReguLens</h1>
          <p className="text-xs uppercase tracking-widest text-teal-700 font-bold mt-1">
            SEBI Member Compliance Monitoring Platform
          </p>
          <div className="mt-3 inline-block px-3 py-1 rounded-full bg-amber-50 border border-amber-200 text-amber-800 text-xs font-semibold">
            Academic RegTech Prototype
          </div>
        </div>

        {/* Login Form Card */}
        <div className="bg-white/95 backdrop-blur-md p-8 rounded-2xl shadow-xl border border-slate-200">
          <h2 className="text-lg font-bold text-slate-800 mb-6 flex items-center space-x-2">
            <Lock className="w-5 h-5 text-teal-600" />
            <span>Secure Access Sign In</span>
          </h2>

          {error && (
            <div className="mb-6 p-3 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs flex items-center space-x-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1.5">Username or Email</label>
              <input
                type="text"
                required
                value={usernameOrEmail}
                onChange={(e) => setUsernameOrEmail(e.target.value)}
                placeholder="e.g. admin or officer"
                className="w-full px-4 py-2.5 bg-slate-50/70 border border-slate-200 rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:bg-white focus:border-teal-500 focus:ring-2 focus:ring-teal-500/20 transition-all"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1.5">Password</label>
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full px-4 py-2.5 bg-slate-50/70 border border-slate-200 rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:bg-white focus:border-teal-500 focus:ring-2 focus:ring-teal-500/20 transition-all"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 bg-gradient-to-r from-teal-600 to-cyan-600 hover:from-teal-700 hover:to-cyan-700 text-white font-bold rounded-xl text-sm shadow-md shadow-teal-600/20 transition-all disabled:opacity-50 cursor-pointer"
            >
              {loading ? 'Authenticating...' : 'Sign In to ReguLens Portal'}
            </button>
          </form>

          {/* Quick Demo Credentials Presets */}
          <div className="mt-8 pt-6 border-t border-slate-200">
            <p className="text-[11px] font-semibold text-slate-500 text-center mb-3">Quick Academic Demo Roles:</p>
            <div className="grid grid-cols-3 gap-2">
              <button
                type="button"
                onClick={() => handleDemoPreset('admin', 'Admin@123')}
                className="p-2.5 bg-slate-50 hover:bg-teal-50/60 border border-slate-200 hover:border-teal-300 rounded-xl text-left transition-all cursor-pointer"
              >
                <div className="text-[11px] font-bold text-teal-700 flex items-center justify-between">
                  <span>Admin</span>
                  <ShieldCheck className="w-3 h-3 text-teal-600" />
                </div>
                <div className="text-[9px] text-slate-500 font-medium">System Admin</div>
              </button>

              <button
                type="button"
                onClick={() => handleDemoPreset('officer', 'Officer@123')}
                className="p-2.5 bg-slate-50 hover:bg-teal-50/60 border border-slate-200 hover:border-teal-300 rounded-xl text-left transition-all cursor-pointer"
              >
                <div className="text-[11px] font-bold text-cyan-700 flex items-center justify-between">
                  <span>Officer</span>
                  <ShieldCheck className="w-3 h-3 text-cyan-600" />
                </div>
                <div className="text-[9px] text-slate-500 font-medium">Compliance Spec</div>
              </button>

              <button
                type="button"
                onClick={() => handleDemoPreset('member_user', 'Member@123')}
                className="p-2.5 bg-slate-50 hover:bg-teal-50/60 border border-slate-200 hover:border-teal-300 rounded-xl text-left transition-all cursor-pointer"
              >
                <div className="text-[11px] font-bold text-amber-700 flex items-center justify-between">
                  <span>Broker</span>
                  <ShieldCheck className="w-3 h-3 text-amber-600" />
                </div>
                <div className="text-[9px] text-slate-500 font-medium">ABC Securities</div>
              </button>
            </div>
          </div>
        </div>

        <p className="text-[10px] text-slate-500 text-center mt-6 font-medium">
          Educational RegTech prototype platform. Does not connect to official SEBI networks.
        </p>
      </div>
    </div>
  );
};

export default Login;
