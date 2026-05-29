import { useState } from 'react';
import { api } from '@/lib/api';
import { useRouter } from 'next/navigation';

const RegisterForm = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [role, setRole] = useState('CUSTOMER');
  const [error, setError] = useState('');
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/auth/register', { name, email, password, role });
      router.push('/login');
    } catch (err) {
      setError('Failed to register. Please try again.');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 max-w-sm mx-auto">
      {error && <p className="text-red-500">{error}</p>}
      <input
        type="text"
        placeholder="Name"
        value={name}
        onChange={(e) => setName(e.target.value)}
        className="w-full px-4 py-2 border rounded-md"
        required
      />
      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        className="w-full px-4 py-2 border rounded-md"
        required
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        className="w-full px-4 py-2 border rounded-md"
        required
      />
      <select value={role} onChange={(e) => setRole(e.target.value)} className="w-full px-4 py-2 border rounded-md">
        <option value="CUSTOMER">Customer</option>
        <option value="VENDOR">Vendor</option>
      </select>
      <button type="submit" className="w-full bg-blue-500 text-white py-2 rounded-md">
        Register
      </button>
    </form>
  );
};

export default RegisterForm;
