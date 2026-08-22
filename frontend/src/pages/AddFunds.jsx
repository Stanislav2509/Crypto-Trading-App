import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import Navbar from "../components/Navbar.jsx";
import "../styles/add-funds.css";

const MIN_AMOUNT = 1;
const MAX_AMOUNT = 10000;
const AMOUNT_PATTERN = /^\d*\.?\d{0,2}$/;

function validateAmount(rawValue) {
  const value = rawValue.trim();
  if (value === "" || value === ".") {
    return "Please enter an amount.";
  }

  const numeric = Number(value);
  if (!Number.isFinite(numeric)) {
    return "Please enter a valid amount.";
  }
  if (numeric < MIN_AMOUNT) {
    return `Minimum amount is $${MIN_AMOUNT.toFixed(2)}.`;
  }
  if (numeric > MAX_AMOUNT) {
    return `Maximum amount is $${MAX_AMOUNT.toFixed(2)}.`;
  }

  return "";
}

function AddFunds() {
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");
  const [amount, setAmount] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const canceled = searchParams.get("canceled") === "true";

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    async function authFetch(url, options) {
      const response = await fetch(url, {
        ...options,
        headers: { ...(options?.headers || {}), Authorization: `Bearer ${token}` },
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
        setError("Unable to load user. Please try again.");
      }
    }

    loadUser();
  }, [navigate]);

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login");
  }

  function handleAmountChange(event) {
    const value = event.target.value;
    if (AMOUNT_PATTERN.test(value)) {
      setAmount(value);
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    const validationMessage = validateAmount(amount);
    if (validationMessage) {
      setError(validationMessage);
      return;
    }

    setError("");
    setLoading(true);

    try {
      const response = await fetch("/api/payments/checkout-session", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ amount: Number(amount) }),
      });

      if (response.status === 401 || response.status === 403) {
        localStorage.removeItem("token");
        navigate("/login");
        return;
      }

      if (!response.ok) {
        const data = await response.json().catch(() => null);
        throw new Error(data?.error || `Request failed with status ${response.status}`);
      }

      const data = await response.json();
      window.location.href = data.checkoutUrl;
    } catch (err) {
      setError(err.message || "Unable to start checkout. Please try again.");
      setLoading(false);
    }
  }

  return (
    <div className="add-funds-page">
      <Navbar user={user} onLogout={handleLogout} />
      <main>
        <h1>Add Funds</h1>
        <p>Top up your virtual trading balance using a Stripe test payment. No real money is charged.</p>

        {canceled && <p className="info-message">Payment canceled. No funds were added.</p>}
        {error && <p className="error-message">{error}</p>}

        <form className="amount-form" onSubmit={handleSubmit}>
          <label htmlFor="amount">Amount (USD)</label>
          <input
            id="amount"
            name="amount"
            type="text"
            inputMode="decimal"
            autoComplete="off"
            placeholder="250.00"
            value={amount}
            onChange={handleAmountChange}
            disabled={loading}
          />
          <p className="amount-hint">
            Minimum: ${MIN_AMOUNT.toFixed(2)} &nbsp;|&nbsp; Maximum: ${MAX_AMOUNT.toFixed(2)}
          </p>
          <button type="submit" disabled={loading}>
            {loading ? "Redirecting..." : "Continue to Stripe"}
          </button>
        </form>
      </main>
    </div>
  );
}

export default AddFunds;
