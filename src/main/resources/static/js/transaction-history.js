    document.querySelectorAll("#profitLoss").forEach(function (el) {
        let number = parseFloat(el.textContent);
        let roundedNumber = number.toFixed(2);
        el.innerText = roundedNumber;
    });