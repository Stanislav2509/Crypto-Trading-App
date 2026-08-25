import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login.jsx";
import RealTimePrices from "./pages/RealTimePrices.jsx";
import Wallet from "./pages/Wallet.jsx";
import TransactionHistory from "./pages/TransactionHistory.jsx";
import WarningResetProfile from "./pages/WarningResetProfile.jsx";
import Chart from "./pages/Chart.jsx";
import DealDetails from "./pages/DealDetails.jsx";
import Register from "./pages/Register.jsx";
import VerifyEmail from "./pages/VerifyEmail.jsx";
import AddFunds from "./pages/AddFunds.jsx";
import AddFundsSuccess from "./pages/AddFundsSuccess.jsx";
import ConvertCurrency from "./pages/ConvertCurrency.jsx";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/verify-email" element={<VerifyEmail />} />
        <Route path="/real-time-prices" element={<RealTimePrices />} />
        <Route path="/wallet" element={<Wallet />} />
        <Route path="/transaction-history" element={<TransactionHistory />} />
        <Route path="/warning-reset-profile" element={<WarningResetProfile />} />
        <Route path="/add-funds" element={<AddFunds />} />
        <Route path="/add-funds/success" element={<AddFundsSuccess />} />
        <Route path="/convert-currency" element={<ConvertCurrency />} />
        <Route path="/chart/:pair" element={<Chart />} />
        <Route path="/buy-details/:id" element={<DealDetails type="buy" />} />
        <Route path="/sell-details/:id" element={<DealDetails type="sell" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
