import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import Navbar from "../components/Navbar.jsx";
import { currencySymbol, convertUsdForDisplay } from "../utils/currency.js";
import "../styles/wallet.css";

function formatQuantity(value) {
  const num = parseFloat(value);
  if (Number.isNaN(num)) return value;
  return num.toString().replace(/(\.\d*?)0+$/, "$1").replace(/\.$/, "");
}

function formatMoney(value) {
  const num = parseFloat(value);
  if (Number.isNaN(num)) return value;
  return num.toFixed(2);
}

function formatSignedMoney(value) {
  const num = parseFloat(value);
  if (Number.isNaN(num)) return value;
  const sign = num > 0 ? "+" : "";
  return `${sign}${num.toFixed(2)}`;
}

function profitLossClass(profitLoss) {
  if (profitLoss > 0) return "profit";
  if (profitLoss < 0) return "loss";
  return "neutral";
}

function Wallet() {
  const [assets, setAssets] = useState([]);
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
        const [assetsData, userData] = await Promise.all([
          authFetch("/api/wallet"),
          authFetch("/api/user/me"),
        ]);

        if (assetsData) setAssets(assetsData);
        if (userData) setUser(userData);
      } catch (err) {
        setError("Unable to load wallet. Please try again.");
      }
    }

    loadInitialData();

    const stompClient = new Client({
      webSocketFactory: () => new SockJS("/ws"),
      onConnect: () => {
        stompClient.subscribe("/topic/asset", (message) => {
          const incoming = JSON.parse(message.body);
          setAssets((prev) =>
            prev.map((asset) => {
              const update = incoming[asset.symbol];
              if (!update) return asset;
              // Normalize the WebSocket Asset shape (moneyCurrency/profitLoss)
              // into the canonical wallet-state shape (marketValueUsd/profitLossUsd)
              // so REST-loaded and live-updated rows never diverge in field names.
              return {
                ...asset,
                marketValueUsd: update.moneyCurrency,
                profitLossUsd: update.profitLoss,
              };
            })
          );
        });
      },
    });
    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [navigate]);

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login");
  }

  const balanceCurrency = user?.balanceCurrency ?? "USD";

  return (
    <div className="wallet-page">
      <Navbar user={user} onLogout={handleLogout} />
      <main>
        <h2>Assets</h2>

        {error && <p className="error-message">{error}</p>}

        <table border="1">
          <thead>
            <tr>
              <th>Crypto Type</th>
              <th>Total Quantity</th>
              <th>Current Value</th>
              <th>Profit/Loss</th>
            </tr>
          </thead>
          <tbody>
            {assets.map((asset) => {
              const displayValue = convertUsdForDisplay(asset.marketValueUsd, balanceCurrency);
              const displayProfitLoss = convertUsdForDisplay(asset.profitLossUsd, balanceCurrency);
              return (
                <tr key={asset.symbol}>
                  <td>
                    {asset.symbol && asset.symbol.includes("/") ? (
                      <Link to={`/chart/${asset.symbol.replace("/", "-")}`}>{asset.symbol}</Link>
                    ) : (
                      asset.symbol
                    )}
                  </td>
                  <td>{formatQuantity(asset.totalQuantity)}</td>
                  <td>
                    {currencySymbol(balanceCurrency)}
                    {formatMoney(displayValue)} {balanceCurrency}
                  </td>
                  <td className={profitLossClass(displayProfitLoss)}>
                    {formatSignedMoney(displayProfitLoss)} {balanceCurrency}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </main>
    </div>
  );
}

export default Wallet;
