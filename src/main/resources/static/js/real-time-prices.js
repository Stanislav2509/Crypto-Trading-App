 let socket = new SockJS('/ws');
    let stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {

        stompClient.subscribe('/topic/prices', function(message) {
            let prices = JSON.parse(message.body);

            for (const [pair, price] of Object.entries(prices)) {
                let rows = document.querySelectorAll("#priceTable tr");
                for (let row of rows) {
                    if (row.cells[0].innerText === pair) {
                        row.cells[1].innerText = parseFloat(price).toFixed(8).replace(/\.?0+$/, '');
                      }
                }
            }
        });
    });