
  let socket = new SockJS('/ws');
  let stompClient = Stomp.over(socket);

  stompClient.connect({}, function(frame) {

      stompClient.subscribe('/topic/asset', function(message) {
          let prices = JSON.parse(message.body);

          let rows = document.querySelectorAll("#assetTable tr");
          rows.forEach(row => {
              const cryptoType = row.cells[0].innerText;
              row.cells[2].innerText = prices[cryptoType].moneyCurrency.toFixed(2);

              let profitLoss = prices[cryptoType].profitLoss;
              let profitLossCell = document.getElementById(`${cryptoType}`);
                profitLossCell.innerText = profitLoss.toFixed(2);

          profitLossCell.classList.remove("profit", "loss", "neutral");

            if (profitLoss < 0) {
                profitLossCell.style.color = "red";
            } else if (profitLoss > 0) {
                profitLossCell.style.color = "green";
            }
          });
      });
  });