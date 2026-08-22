import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar.jsx";
import "../styles/add-funds.css";

function AddFundsSuccess() {
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    async function authFetch(url) {
      const response = await fetch(url, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.status === 401 || response.status === 403) {
        localStorage.removeItem("token");
        navigate("/login");
        return null;
      }

      if (!response.ok) {
        throw new Error(`Request to ${url} failed with status ${response.status}`);
      }

      return response.json();
    }

    async function loadUser() {
      try {
        const userData = await authFetch("/api/user/me");
        if (userData) setUser(userData);
      } catch (err) {
        setError("Payment completed, but we couldn't refresh your balance. Please reload the page.");
      }
    }

    loadUser();
  }, [navigate]);

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login");
  }

  return (
    <div className="add-funds-page">
      <Navbar user={user} onLogout={handleLogout} />
      <main>
        <h1>Payment Successful</h1>
        <p>Your Stripe test payment was received. Your virtual balance has been updated.</p>

        {error && <p className="error-message">{error}</p>}

        {user && (
          <p className="current-balance">
            Current balance: <strong>{user.balance} USD</strong>
          </p>
        )}

        <button type="button" onClick={() => navigate("/real-time-prices")}>
          Continue Trading
        </button>
      </main>
    </div>
  );
}

export default AddFundsSuccess;
