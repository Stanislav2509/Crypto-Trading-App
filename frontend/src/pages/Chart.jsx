import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createChart, ColorType } from "lightweight-charts";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import Navbar from "../components/Navbar.jsx";
import { USD_TO_EUR_RATE } from "../utils/currency.js";
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
  const [spendAmount, setSpendAmount] = useState("");
  const [receiveCrypto, setReceiveCrypto] = useState("");
  const [spendCrypto, setSpendCrypto] = useState("");
  const [receiveAmount, setReceiveAmount] = useState("");

  const chartContainerRef = useRef(null);
  const chartRef = useRef(null);
  const seriesRef = useRef(null);
  const lastCandleRef = useRef(null);
  const appliedIntervalRef = useRef(interval);

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

  async function tradeFetch(url, body) {
    const token = localStorage.getItem("token");
    const response = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
      body: JSON.stringify(body),
    });

    if (response.status === 401 || response.status === 403) {
      localStorage.removeItem("token");
      navigate("/login");
      return null;
    }

    let data = null;
    try {
      data = await response.json();
    } catch {
      data = null;
    }

    if (!response.ok) {
      throw new Error(data?.error || "Trade could not be completed. Please try again.");
    }

    return data;
  }

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    appliedIntervalRef.current = interval;

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
      width: chartContainerRef.current.clientWidth,
      height: chartContainerRef.current.clientHeight,
      layout: {
        background: { type: ColorType.Solid, color: "#000000" },
        textColor: "#d1d5db",
      },
      grid: {
        vertLines: { color: "rgba(255, 255, 255, 0.07)" },
        horzLines: { color: "rgba(255, 255, 255, 0.07)" },
      },
      timeScale: {
        timeVisible: true,
        secondsVisible: false,
        borderColor: "#2a2a2a",
      },
      rightPriceScale: {
        visible: true,
        borderVisible: true,
        borderColor: "#2a2a2a",
        textColor: "#d1d5db",
        autoScale: true,
      },
    });

    const candleSeries = chart.addCandlestickSeries({
      priceScaleId: "right",
      upColor: "#2962ff",
      downColor: "#ffffff",
      borderUpColor: "#2962ff",
      borderDownColor: "#ffffff",
      wickUpColor: "#2962ff",
      wickDownColor: "#ffffff",
      priceLineColor: "#5b6b7c",
    });

    chartRef.current = chart;
    seriesRef.current = candleSeries;

    function handleResize() {
      if (chartContainerRef.current) {
        chart.resize(chartContainerRef.current.clientWidth, chartContainerRef.current.clientHeight);
      }
    }
    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
      chart.remove();
      chartRef.current = null;
      seriesRef.current = null;
    };
  }, []);

  useEffect(() => {
    if (!chartData || !seriesRef.current) return;
    const formattedData = chartData.candles.map((c) => ({
      time: Number(c.time),
      open: Number(c.open),
      high: Number(c.high),
      low: Number(c.low),
      close: Number(c.close),
    }));
    seriesRef.current.setData(formattedData);

    const latest = formattedData[formattedData.length - 1];
    lastCandleRef.current = latest ? { ...latest } : null;
  }, [chartData]);

  function applyLiveTick(livePrice) {
    const last = lastCandleRef.current;
    const series = seriesRef.current;
    if (!last || !series) return;

    const bucketSeconds = (appliedIntervalRef.current || 1) * 60;
    const nowSeconds = Math.floor(Date.now() / 1000);
    const bucketStart = Math.floor(nowSeconds / bucketSeconds) * bucketSeconds;

    if (bucketStart < last.time) {
      return;
    }

    if (bucketStart === last.time) {
      const updated = {
        time: last.time,
        open: last.open,
        high: Math.max(last.high, livePrice),
        low: Math.min(last.low, livePrice),
        close: livePrice,
      };
      lastCandleRef.current = updated;
      series.update(updated);
      return;
    }

    const newCandle = {
      time: bucketStart,
      open: last.close,
      high: livePrice,
      low: livePrice,
      close: livePrice,
    };
    lastCandleRef.current = newCandle;
    series.update(newCandle);
  }

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) return;
    fetch("/api/prices", { headers: { Authorization: `Bearer ${token}` } }).catch(() => {});
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) return;

    const normalizedPair = pair.replace("-", "/");

    const stompClient = new Client({
      webSocketFactory: () => new SockJS("/ws"),
      onConnect: () => {
        stompClient.subscribe("/topic/prices", (message) => {
          const incoming = JSON.parse(message.body);
          const rawPrice = incoming[normalizedPair];
          if (rawPrice === undefined) return;
          const livePrice = Number(rawPrice);
          if (Number.isNaN(livePrice)) return;
          applyLiveTick(livePrice);
        });
      },
    });
    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [pair]);

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login");
  }

  const balanceCurrency = user?.balanceCurrency ?? "USD";

  function handleShowInterval(e) {
    e.preventDefault();
    setInterval_(selectedInterval);
  }

  function handleSpendAmountChange(e) {
    const value = e.target.value;
    const max = parseFloat(chartData?.userBalance);
    const numeric = parseFloat(value);
    const clamped = !Number.isNaN(max) && numeric > max ? String(max) : value;
    setSpendAmount(clamped);

    const spend = parseFloat(clamped);
    const currentPrice = parseFloat(chartData?.currentPrice);
    const usdSpend = balanceCurrency === "EUR" ? spend / USD_TO_EUR_RATE : spend;
    const receive = usdSpend / currentPrice;
    setReceiveCrypto(Number.isNaN(receive) ? "" : receive.toFixed(6));
  }

  function handleMaxAmount() {
    const value = chartData?.userBalance ?? "";
    setSpendAmount(String(value));
    const spend = parseFloat(value);
    const currentPrice = parseFloat(chartData?.currentPrice);
    const usdSpend = balanceCurrency === "EUR" ? spend / USD_TO_EUR_RATE : spend;
    const receive = usdSpend / currentPrice;
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
    const usdValue = spend * currentPrice;
    const receive = balanceCurrency === "EUR" ? usdValue * USD_TO_EUR_RATE : usdValue;
    setReceiveAmount(Number.isNaN(receive) ? "" : receive.toFixed(2));
  }

  function handleMaxCrypto() {
    const value = chartData?.quantityCrypto ?? "";
    setSpendCrypto(String(value));
    const spend = parseFloat(value);
    const currentPrice = parseFloat(chartData?.currentPrice);
    const usdValue = spend * currentPrice;
    const receive = balanceCurrency === "EUR" ? usdValue * USD_TO_EUR_RATE : usdValue;
    setReceiveAmount(Number.isNaN(receive) ? "" : receive.toFixed(2));
  }

  async function handleBuySubmit(e) {
    e.preventDefault();
    setError("");
    try {
      const result = await tradeFetch("/api/buy", {
        pair,
        spend: parseFloat(spendAmount),
      });
      if (result && result.transactionId) {
        navigate(`/buy-details/${result.transactionId}`);
      }
    } catch (err) {
      setError(err.message || "Unable to complete purchase. Please try again.");
    }
  }

  async function handleSellSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      const result = await tradeFetch("/api/sell", {
        pair,
        spend: parseFloat(spendCrypto),
      });
      if (result && result.transactionId) {
        navigate(`/sell-details/${result.transactionId}`);
      }
    } catch (err) {
      setError(err.message || "Unable to complete sale. Please try again.");
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
                  <label>Spend {balanceCurrency} </label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    max={chartData?.userBalance}
                    id="spendAmount"
                    name="spend"
                    value={spendAmount}
                    onChange={handleSpendAmountChange}
                    required
                  />
                  <button className="btn-max" type="button" onClick={handleMaxAmount}>MAX</button>
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

                  <label>Receive {balanceCurrency} </label>
                  <input type="text" id="receiveAmount" name="receive" value={receiveAmount} readOnly />

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
