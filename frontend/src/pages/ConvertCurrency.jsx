import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar.jsx";
import { USD_TO_EUR_RATE, currencySymbol } from "../utils/currency.js";
import "../styles/convert-currency.css";

function formatMoney(value) {
  const num = parseFloat(value);
  if (Number.isNaN(num)) return "0.00";
  return num.toFixed(2);
}

function ConvertCurrency() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [converting, setConverting] = useState(false);
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
        setError("Unable to load user. Please try again.");
      } finally {
        setLoading(false);
      }
    }

    loadUser();
  }, [navigate]);

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login");
  }

  async function handleConvert() {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    setError("");
    setConverting(true);

    try {
      const response = await fetch("/api/user/convert-balance", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ targetCurrency }),
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

      const updatedUser = await response.json();
      setUser(updatedUser);
    } catch (err) {
      setError(err.message || "Unable to convert balance. Please try again.");
    } finally {
      setConverting(false);
    }
  }

  const currentCurrency = user?.balanceCurrency ?? "USD";
  const targetCurrency = currentCurrency === "USD" ? "EUR" : "USD";
  const balance = parseFloat(user?.balance ?? 0);
  const preview = currentCurrency === "USD" ? balance * USD_TO_EUR_RATE : balance / USD_TO_EUR_RATE;
  const inverseRateLabel =
    currentCurrency === "USD"
      ? `1 USD = ${USD_TO_EUR_RATE} EUR`
      : `1 EUR ≈ ${(1 / USD_TO_EUR_RATE).toFixed(4)} USD`;

  return (
    <div className="convert-currency-page">
      <Navbar user={user} onLogout={handleLogout} />
      <main>
        <h1>Convert Currency</h1>
        <p>Convert your entire balance between USD and EUR at a fixed demo rate.</p>

        {error && <p className="error-message">{error}</p>}

        {loading ? (
          <p>Loading...</p>
        ) : (
          user && (
            <div className="convert-box">
              <p className="current-balance">
                Current balance:{" "}
                <strong>
                  {currencySymbol(currentCurrency)}
                  {formatMoney(user.balance)} {currentCurrency}
                </strong>
              </p>

              <p className="rate-hint">{inverseRateLabel}</p>

              <p className="preview">
                After conversion:{" "}
                <strong>
                  {currencySymbol(targetCurrency)}
                  {formatMoney(preview)} {targetCurrency}
                </strong>
              </p>

              <button type="button" onClick={handleConvert} disabled={converting}>
                {converting ? "Converting..." : `Convert to ${targetCurrency}`}
              </button>
            </div>
          )
        )}
      </main>
    </div>
  );
}

export default ConvertCurrency;
