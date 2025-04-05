<script th:inline="javascript">
    window.candleData = [[${candles}]];
</script>

const formattedData = candleData.map(c => ({
        time: c.time,
        open: c.open,
        high: c.high,
        low: c.low,
        close: c.close
    }));

    const chart = LightweightCharts.createChart(document.getElementById('chart'), {
        width: window.innerWidth * 0.95,
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