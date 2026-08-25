export const USD_TO_EUR_RATE = 0.86;

export function currencySymbol(currency) {
  return currency === "EUR" ? "€" : "$";
}

export function convertUsdForDisplay(usdAmount, targetCurrency) {
  const num = typeof usdAmount === "number" ? usdAmount : parseFloat(usdAmount);
  if (Number.isNaN(num)) return num;
  return targetCurrency === "EUR" ? num * USD_TO_EUR_RATE : num;
}
