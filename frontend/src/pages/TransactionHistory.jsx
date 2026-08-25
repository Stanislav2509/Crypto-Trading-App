import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar.jsx";
import { currencySymbol } from "../utils/currency.js";
import "../styles/transaction-history.css";

function formatQuantity(value) {
  if (value === null || value === undefined) return "0";
  const num = parseFloat(value);
  if (Number.isNaN(num)) return value;
  return num.toString().replace(/(\.\d*?)0+$/, "$1").replace(/\.$/, "");
}

function formatCash(value, currency) {
  if (value === null || value === undefined) return "0";
  const num = parseFloat(value);
  if (Number.isNaN(num)) return value;
  const label = currency ?? "USD";
  return `${currencySymbol(label)}${num.toFixed(2)} ${label}`;
}

function formatSignedCash(value, currency) {
  if (value === null || value === undefined) return "0";
  const num = parseFloat(value);
  if (Number.isNaN(num)) return value;
  const label = currency ?? "USD";
  const sign = num > 0 ? "+" : "";
  return `${sign}${currencySymbol(label)}${num.toFixed(2)} ${label}`;
}

function formatDateTime(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  const pad = (n) => String(n).padStart(2, "0");
  return `${pad(date.getDate())}.${pad(date.getMonth() + 1)}.${date.getFullYear()} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function profitLossClass(profitLoss) {
  if (profitLoss > 0) return "profit";
  if (profitLoss < 0) return "loss";
  return "neutral";
}

function TransactionHistory() {
  const [transactions, setTransactions] = useState([]);
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

    async function loadInitialData() {
      try {
        const [transactionsData, userData] = await Promise.all([
          authFetch("/api/transactions"),
          authFetch("/api/user/me"),
        ]);

        if (transactionsData) setTransactions(transactionsData);
        if (userData) setUser(userData);
      } catch (err) {
        setError("Unable to load transaction history. Please try again.");
      }
    }

    loadInitialData();
  }, [navigate]);

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login");
  }

  return (
    <div className="transaction-history-page">
      <Navbar user={user} onLogout={handleLogout} />
      <main>
        <h2>Transaction History</h2>

        {error && <p className="error-message">{error}</p>}

        <table border="1">
          <thead>
            <tr>
              <th>Crypto Type</th>
              <th>Transaction Type</th>
              <th>Spend Money</th>
              <th>Receive Crypto</th>
              <th>Spend Crypto</th>
              <th>Receive Money</th>
              <th>Price During the deal</th>
              <th>Date time</th>
              <th>Profit/Loss</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((tran, index) => (
              <tr key={index}>
                <td>{tran.cryptoSymbol}</td>
                <td>{tran.transactionType}</td>
                <td>{tran.spendMoney == null ? "0" : formatCash(tran.spendMoney, tran.currency)}</td>
                <td>{formatQuantity(tran.receiveCrypto)}</td>
                <td>{formatQuantity(tran.spendCrypto)}</td>
                <td>{tran.receiveMoney == null ? "0" : formatCash(tran.receiveMoney, tran.currency)}</td>
                <td>{formatQuantity(tran.currCryptoPrice)} USD</td>
                <td>{formatDateTime(tran.dateTime)}</td>
                <td className={profitLossClass(tran.profitLoss)}>
                  {formatSignedCash(tran.profitLoss, tran.currency)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </main>
    </div>
  );
}

export default TransactionHistory;
