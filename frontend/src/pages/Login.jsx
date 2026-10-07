import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Lock, AlertCircle } from 'lucide-react';

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
    <div className="min-h-screen bg-slate-950 flex flex-col justify-center items-center p-4 relative overflow-hidden">
      {/* Background Orbs */}
      <div className="absolute top-1/4 left-1/3 w-96 h-96 bg-teal-500/10 rounded-full blur-3xl pointer-events-none"></div>
      <div className="absolute bottom-1/4 right-1/3 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none"></div>

      <div className="w-full max-w-md z-10">
        {/* Header Branding */}
        <div className="text-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-teal-500 to-cyan-400 mx-auto flex items-center justify-center font-extrabold text-slate-950 text-2xl shadow-xl shadow-teal-500/20 mb-3">
            RL
          </div>
          <h1 className="text-3xl font-extrabold text-white tracking-tight">ReguLens</h1>
          <p className="text-xs uppercase tracking-widest text-teal-400 font-semibold mt-1">
            SEBI Member Compliance Monitoring Platform
          </p>
          <div className="mt-3 inline-block px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/20 text-amber-400 text-xs font-medium">
            Academic RegTech Prototype
          </div>
        </div>

        {/* Login Form Card */}
        <div className="glass-card p-8 rounded-2xl shadow-2xl border border-slate-800">
          <h2 className="text-lg font-semibold text-white mb-6 flex items-center space-x-2">
            <Lock className="w-5 h-5 text-teal-400" />
            <span>Secure Access Sign In</span>
          </h2>

          {error && (
            <div className="mb-6 p-3 rounded-lg bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs flex items-center space-x-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Username or Email</label>
              <input
                type="text"
                required
                value={usernameOrEmail}
                onChange={(e) => setUsernameOrEmail(e.target.value)}
                placeholder="e.g. admin or officer"
                className="w-full px-4 py-2.5 bg-slate-900/90 border border-slate-700/80 rounded-xl text-sm text-white placeholder-slate-500 focus:outline-none focus:border-teal-500 transition-colors"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Password</label>
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full px-4 py-2.5 bg-slate-900/90 border border-slate-700/80 rounded-xl text-sm text-white placeholder-slate-500 focus:outline-none focus:border-teal-500 transition-colors"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 bg-gradient-to-r from-teal-500 to-cyan-500 text-slate-950 font-bold rounded-xl text-sm shadow-lg shadow-teal-500/25 hover:from-teal-400 hover:to-cyan-400 transition-all disabled:opacity-50"
            >
              {loading ? 'Authenticating...' : 'Sign In to ReguLens Portal'}
            </button>
          </form>

          {/* Quick Demo Credentials Presets */}
          <div className="mt-8 pt-6 border-t border-slate-800">
            <p className="text-[11px] font-medium text-slate-400 text-center mb-3">Quick Academic Demo Roles:</p>
            <div className="grid grid-cols-3 gap-2">
              <button
                type="button"
                onClick={() => handleDemoPreset('admin', 'Admin@123')}
                className="p-2 bg-slate-800/80 hover:bg-slate-800 border border-slate-700 rounded-lg text-left transition-colors"
              >
                <div className="text-[11px] font-semibold text-teal-400">Admin</div>
                <div className="text-[9px] text-slate-400">System Admin</div>
              </button>
              <button
                type="button"
                onClick={() => handleDemoPreset('officer', 'Officer@123')}
                className="p-2 bg-slate-800/80 hover:bg-slate-800 border border-slate-700 rounded-lg text-left transition-colors"
              >
                <div className="text-[11px] font-semibold text-cyan-400">Officer</div>
                <div className="text-[9px] text-slate-400">Compliance Spec</div>
              </button>
              <button
                type="button"
                onClick={() => handleDemoPreset('member_user', 'Member@123')}
                className="p-2 bg-slate-800/80 hover:bg-slate-800 border border-slate-700 rounded-lg text-left transition-colors"
              >
                <div className="text-[11px] font-semibold text-amber-400">Broker</div>
                <div className="text-[9px] text-slate-400">ABC Securities</div>
              </button>
            </div>
          </div>
        </div>

        <p className="text-[10px] text-slate-500 text-center mt-6">
          Educational RegTech prototype platform. Does not connect to official SEBI networks.
        </p>
      </div>
    </div>
  );
};

export default Login;
