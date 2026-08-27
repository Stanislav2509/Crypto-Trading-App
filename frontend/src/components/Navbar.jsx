import { NavLink } from "react-router-dom";
import "../styles/nav-style.css";

function navLinkClass({ isActive }) {
  return isActive ? "home active" : "home";
}

function Navbar({ user, onLogout }) {
  const balanceCurrency = user?.balanceCurrency ?? "USD";
  const currencySymbol = balanceCurrency === "EUR" ? "€" : "$";

  return (
    <header>
      <span className="brand">MATRIX TRADER</span>

      <nav className="nav-links">
        <NavLink className={navLinkClass} to="/real-time-prices">Home Page</NavLink>

        <NavLink className={navLinkClass} to="/transaction-history">Transaction History</NavLink>

        <NavLink className={navLinkClass} to="/wallet">View Wallet</NavLink>

        <NavLink className={navLinkClass} to="/add-funds">Add Funds</NavLink>

        <NavLink className={navLinkClass} to="/convert-currency">Convert Currency</NavLink>

        <NavLink className={navLinkClass} to="/warning-reset-profile">Reset Profile</NavLink>
      </nav>

      <button type="button" className="mx-btn-danger logout-btn" onClick={onLogout}>Logout</button>

      <div className="dropdown">
        <button type="button" className="dropbtn">
          <span className="user-icon">👤</span> <span>{user?.email}</span> <span className="caret">▾</span>
        </button>
        <div className="dropdown-content">
          <p><strong>First name: </strong> <span>{user?.firstName}</span></p>
          <p><strong>Last name: </strong> <span>{user?.lastName}</span></p>
          <p><strong>Balance: </strong> <span>{currencySymbol}{user?.balance}</span> <strong>{balanceCurrency}</strong></p>
        </div>
      </div>
    </header>
  );
}

export default Navbar;
