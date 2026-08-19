import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar.jsx";
import "../styles/deal-details.css";

function formatQuantity(value) {
  if (value === null || value === undefined) return "0";
  const num = parseFloat(value);
  if (Number.isNaN(num)) return value;
  return num.toString().replace(/(\.\d*?)0+$/, "$1").replace(/\.$/, "");
}

function formatMoney(value) {
  if (value === null || value === undefined) return "0";
  const num = parseFloat(value);
  if (Number.isNaN(num)) return value;
  return num.toFixed(2);
}

function DealDetails({ type }) {
  const { id } = useParams();
  const navigate = useNavigate();

  const [transaction, setTransaction] = useState(null);
  const [assets, setAssets] = useState([]);
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");

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
        const [transactionData, assetsData, userData] = await Promise.all([
          authFetch(`/api/transactions/${id}`),
          authFetch("/api/wallet"),
          authFetch("/api/user/me"),
        ]);

        if (transactionData) setTransaction(transactionData);
        if (assetsData) setAssets(assetsData);
        if (userData) setUser(userData);
      } catch (err) {
        setError("Unable to load deal details. Please try again.");
      }
    }

    loadInitialData();
  }, [id, navigate]);

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login");
  }

  return (
    <div className="deal-details-page">
      <Navbar user={user} onLogout={handleLogout} />
      <main>
        <h2>{type === "sell" ? "Sell Details" : "Buy Details"}</h2>

        {error && <p className="error-message">{error}</p>}

        {transaction && (
          <>
            {type === "sell" ? (
              <p><strong>Receive: </strong> <span>{formatMoney(transaction.receiveMoney)}</span> <strong> USD</strong></p>
            ) : (
              <p><strong>Receive: </strong> <span>{formatQuantity(transaction.receiveCrypto)}</span> <strong> {transaction.cryptoSymbol}</strong></p>
            )}

            <p><strong>Current Price of {transaction.cryptoSymbol}</strong> <span>{formatQuantity(transaction.currCryptoPrice)}</span> <strong> USD</strong></p>

            {type === "sell" ? (
              <p><strong>Cost </strong> <span>{formatQuantity(transaction.spendCrypto)}</span> <strong>{transaction.cryptoSymbol}</strong></p>
            ) : (
              <p><strong>Cost </strong> <span>{formatMoney(transaction.spendMoney)}</span> <strong> USD</strong></p>
            )}
          </>
        )}

        <br />
        <br />

        <p><strong>Current Assets</strong></p>
        <table border="1">
          <thead>
            <tr>
              <th>Crypto Type</th>
              <th>Total Quantity</th>
              <th>Money in Currency</th>
            </tr>
          </thead>
          <tbody>
            {assets.map((asset) => (
              <tr key={asset.symbol}>
                <td>{asset.symbol}</td>
                <td>{formatQuantity(asset.totalQuantity)}</td>
                <td>{formatMoney(asset.moneyCurrency)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </main>
    </div>
  );
}

export default DealDetails;
