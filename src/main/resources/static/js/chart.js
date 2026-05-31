const currentPrice = window.currentPrice;
const userBalance = window.userBalance;
const quantityCrypto = window.quantityCrypto;


   const formattedData = candleData.map(c => ({
        time: c.time,
        open: c.open,
        high: c.high,
        low: c.low,
        close: c.close
    }));

    const chart = LightweightCharts.createChart(document.getElementById('chart'), {
        width: 100,
        height: 500,
        layout: {
            backgroundColor: '#fff',
            textColor: '#333',
        },
        grid: {
            vertLines: { color: '#eee' },
            horzLines: { color: '#eee' },
        },
        timeScale: {
            timeVisible: true,
            secondsVisible: false,
        },
    });

    const candleSeries = chart.addCandlestickSeries();
    candleSeries.setData(formattedData);

    setTimeout(() => {
        chart.resize(window.innerWidth * 0.95, 500);
    }, 100);

    function showBuyForm() {
        document.getElementById("buyForm").style.display = "block";
        document.getElementById("sellForm").style.display = "none";
    }

    function showSellForm() {
        document.getElementById("sellForm").style.display = "block";
        document.getElementById("buyForm").style.display = "none";
    }


    function validateSpendUsd() {
        const spendInput = document.getElementById("spendUsd");
        const max = parseFloat(spendInput.max);
        const value = parseFloat(spendInput.value);

        if (value > max) {
            spendInput.value = max;
        }

        calculateReceiveCrypto();
    }

    function setMaxUsd() {
         document.getElementById("spendUsd").value = userBalance;
        calculateReceiveCrypto();
    }
        document.getElementById("spendUsd").addEventListener("input", calculateReceiveCrypto);

    function calculateReceiveCrypto() {
       const spend = parseFloat(document.getElementById("spendUsd").value);
        const receive = spend / currentPrice;
        document.getElementById("receiveCrypto").value = isNaN(receive) ? '' : receive.toFixed(6);
    }

function validateSpendCrypto() {
        const spendInput = document.getElementById("spendCrypto");
        const max = parseFloat(spendInput.max);
        const value = parseFloat(spendInput.value);

        if (value > max) {
            spendInput.value = max;
        }

        calculateReceiveUsd();
    }

    function setMaxCrypto() {
        document.getElementById("spendCrypto").value = quantityCrypto;
        calculateReceiveUsd();
    }
        document.getElementById("spendCrypto").addEventListener("input", calculateReceiveUsd);

    function calculateReceiveUsd() {
       const spend = parseFloat(document.getElementById("spendCrypto").value);
        const receive = spend * currentPrice;
        document.getElementById("receiveUsd").value = isNaN(receive) ? '' : receive.toFixed(2);
    }