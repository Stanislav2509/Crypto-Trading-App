import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createChart } from "lightweight-charts";
import Navbar from "../components/Navbar.jsx";
import "../styles/chart.css";

const INTERVAL_OPTIONS = [
  { value: 1, label: "1 minute" },
  { value: 5, label: "5 minutes" },
  { value: 60, label: "1 hour" },
  { value: 1440, label: "1 day" },
  { value: 10080, label: "1 week" },
];

function Chart() {
  const { pair } = useParams();
  const navigate = useNavigate();

  const [interval, setInterval_] = useState(1);
  const [selectedInterval, setSelectedInterval] = useState(1);
  const [chartData, setChartData] = useState(null);
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");

  const [activeForm, setActiveForm] = useState("buy");
  const [spendUsd, setSpendUsd] = useState("");
  const [receiveCrypto, setReceiveCrypto] = useState("");
  const [spendCrypto, setSpendCrypto] = useState("");
  const [receiveUsd, setReceiveUsd] = useState("");

  const chartContainerRef = useRef(null);
  const chartRef = useRef(null);
  const seriesRef = useRef(null);

  function authFetch(url, options) {
    const token = localStorage.getItem("token");
    return fetch(url, {
      ...options,
      headers: { ...(options?.headers || {}), Authorization: `Bearer ${token}` },
    }).then((response) => {
      if (response.status === 401 || response.status === 403) {
        localStorage.removeItem("token");
        navigate("/login");
        return null;
      }
      if (!response.ok) {
        throw new Error(`Request to ${url} failed with status ${response.status}`);
      }
      return response.json();
    });
  }

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    async function loadInitialData() {
      try {
        const [chart, userData] = await Promise.all([
          authFetch(`/api/chart/${pair}?interval=${interval}`),
          authFetch("/api/user/me"),
        ]);

        if (chart) setChartData(chart);
        if (userData) setUser(userData);
      } catch (err) {
        setError("Unable to load chart data. Please try again.");
      }
    }

    loadInitialData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pair, interval, navigate]);

  useEffect(() => {
    if (!chartContainerRef.current) return;

    const chart = createChart(chartContainerRef.current, {
      width: 100,
      height: 500,
      layout: {
        backgroundColor: "#fff",
        textColor: "#333",
      },
      grid: {
        vertLines: { color: "#eee" },
        horzLines: { color: "#eee" },
      },
      timeScale: {
        timeVisible: true,
        secondsVisible: false,
      },
    });

    const candleSeries = chart.addCandlestickSeries();

    chartRef.current = chart;
    seriesRef.current = candleSeries;

    setTimeout(() => {
      chart.resize(window.innerWidth * 0.95, 500);
    }, 100);

    return () => {
      chart.remove();
      chartRef.current = null;
      seriesRef.current = null;
    };
  }, []);

  useEffect(() => {
    if (!chartData || !seriesRef.current) return;
    const formattedData = chartData.candles.map((c) => ({
      time: c.time,
      open: c.open,
      high: c.high,
      low: c.low,
      close: c.close,
    }));
    seriesRef.current.setData(formattedData);
  }, [chartData]);

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login");
  }

  function handleShowInterval(e) {
    e.preventDefault();
    setInterval_(selectedInterval);
  }

  function handleSpendUsdChange(e) {
    const value = e.target.value;
    const max = parseFloat(chartData?.userBalance);
    const numeric = parseFloat(value);
    const clamped = !Number.isNaN(max) && numeric > max ? String(max) : value;
    setSpendUsd(clamped);

    const spend = parseFloat(clamped);
    const currentPrice = parseFloat(chartData?.currentPrice);
    const receive = spend / currentPrice;
    setReceiveCrypto(Number.isNaN(receive) ? "" : receive.toFixed(6));
  }

  function handleMaxUsd() {
    const value = chartData?.userBalance ?? "";
    setSpendUsd(String(value));
    const spend = parseFloat(value);
    const currentPrice = parseFloat(chartData?.currentPrice);
    const receive = spend / currentPrice;
    setReceiveCrypto(Number.isNaN(receive) ? "" : receive.toFixed(6));
  }

  function handleSpendCryptoChange(e) {
    const value = e.target.value;
    const max = parseFloat(chartData?.quantityCrypto);
    const numeric = parseFloat(value);
    const clamped = !Number.isNaN(max) && numeric > max ? String(max) : value;
    setSpendCrypto(clamped);

    const spend = parseFloat(clamped);
    const currentPrice = parseFloat(chartData?.currentPrice);
    const receive = spend * currentPrice;
    setReceiveUsd(Number.isNaN(receive) ? "" : receive.toFixed(2));
  }

  function handleMaxCrypto() {
    const value = chartData?.quantityCrypto ?? "";
    setSpendCrypto(String(value));
    const spend = parseFloat(value);
    const currentPrice = parseFloat(chartData?.currentPrice);
    const receive = spend * currentPrice;
    setReceiveUsd(Number.isNaN(receive) ? "" : receive.toFixed(2));
  }

  async function handleBuySubmit(e) {
    e.preventDefault();
    try {
      const result = await authFetch("/api/buy", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          pair,
          spend: parseFloat(spendUsd),
          receive: parseFloat(receiveCrypto),
        }),
      });
      if (result) navigate(`/buy-details/${result.transactionId}`);
    } catch (err) {
      setError("Unable to complete purchase. Please try again.");
    }
  }

  async function handleSellSubmit(e) {
    e.preventDefault();
    try {
      const result = await authFetch("/api/sell", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          pair,
          spend: parseFloat(spendCrypto),
          receive: parseFloat(receiveUsd),
        }),
      });
      if (result) navigate(`/sell-details/${result.transactionId}`);
    } catch (err) {
      setError("Unable to complete sale. Please try again.");
    }
  }

  return (
    <div className="chart-page">
      <Navbar user={user} onLogout={handleLogout} />
      <main>
        <div className="intro">
          <h2>{chartData ? `${chartData.name} (${chartData.pair})` : pair}</h2>

          <form onSubmit={handleShowInterval}>
            <label className="interval" htmlFor="interval">Time frame:</label>
            <select
              name="interval"
              id="interval"
              value={selectedInterval}
              onChange={(e) => setSelectedInterval(Number(e.target.value))}
            >
              {INTERVAL_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
            <button className="btn-show" type="submit">Show</button>
          </form>
        </div>

        {error && <p className="error-message">{error}</p>}

        <div className="chart-trade">
          <div id="chart" className="chart-container" ref={chartContainerRef}></div>

          <div className="trade">
            <div className="btn">
              <button className="btn-buy" type="button" onClick={() => setActiveForm("buy")}>Buy</button>
              <button className="btn-sell" type="button" onClick={() => setActiveForm("sell")}>Sell</button>
            </div>

            {activeForm === "buy" && (
              <div id="buyForm">
                <form onSubmit={handleBuySubmit}>
                  <label>Spend USD </label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    max={chartData?.userBalance}
                    id="spendUsd"
                    name="spend"
                    value={spendUsd}
                    onChange={handleSpendUsdChange}
                    required
                  />
                  <button className="btn-max" type="button" onClick={handleMaxUsd}>MAX</button>
                  <br />

                  <label>Receive {chartData?.pair ?? pair}</label>
                  <input type="text" id="receiveCrypto" name="receive" value={receiveCrypto} readOnly />

                  <br />
                  <button className="buy-btn" type="submit">Buy</button>
                </form>
              </div>
            )}

            {activeForm === "sell" && (
              <div id="sellForm">
                <form onSubmit={handleSellSubmit}>
                  <label>Spend {chartData?.pair ?? pair}</label>
                  <input
                    type="number"
                    step="any"
                    min="0"
                    max={chartData?.quantityCrypto}
                    id="spendCrypto"
                    name="spend"
                    value={spendCrypto}
                    onChange={handleSpendCryptoChange}
                    required
                  />
                  <button className="btn-max" type="button" onClick={handleMaxCrypto}>MAX</button>
                  <br />

                  <label>Receive USD </label>
                  <input type="text" id="receiveUsd" name="receive" value={receiveUsd} readOnly />

                  <br />
                  <button className="sell-btn" type="submit">Sell</button>
                </form>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

export default Chart;
