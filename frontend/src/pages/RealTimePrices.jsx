import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import Navbar from "../components/Navbar.jsx";
import "../styles/real-time-prices.css";

function formatPrice(value) {
  const num = parseFloat(value);
  if (Number.isNaN(num)) return value;
  return num.toFixed(8).replace(/\.?0+$/, "");
}

function RealTimePrices() {
  const [prices, setPrices] = useState({});
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
        const [pricesData, userData] = await Promise.all([
          authFetch("/api/prices"),
          authFetch("/api/user/me"),
        ]);

        if (pricesData) setPrices(pricesData);
        if (userData) setUser(userData);
      } catch (err) {
        setError("Unable to load real-time prices. Please try again.");
      }
    }

    loadInitialData();

    const stompClient = new Client({
      webSocketFactory: () => new SockJS("/ws"),
      onConnect: () => {
        stompClient.subscribe("/topic/prices", (message) => {
          const incoming = JSON.parse(message.body);
          setPrices((prev) => ({ ...prev, ...incoming }));
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

  const sortedPairs = Object.entries(prices).sort(([a], [b]) => a.localeCompare(b));

  return (
    <div className="real-time-prices-page">
      <Navbar user={user} onLogout={handleLogout} />
      <main>
        <h1>Real-time Crypto Prices</h1>

        {error && <p className="error-message">{error}</p>}

        <table border="1">
          <thead>
            <tr>
              <th>Pair</th>
              <th>Price (USD)</th>
            </tr>
          </thead>
          <tbody>
            {sortedPairs.map(([pair, price]) => (
              <tr key={pair}>
                <td>
                  <Link to={`/chart/${pair.replace("/", "-")}`}>{pair}</Link>
                </td>
                <td>{formatPrice(price)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </main>
    </div>
  );
}

export default RealTimePrices;
