import { Link } from "react-router-dom";
import "../styles/nav-style.css";

function Navbar({ user, onLogout }) {
  return (
    <header>
      <Link className="home" to="/real-time-prices">Home Page</Link>

      <Link className="home" to="/transaction-history">Transaction History</Link>

      <Link className="home" to="/wallet">View Wallet</Link>

      <Link className="home" to="/warning-reset-profile">Reset Profile</Link>

      <button type="button" onClick={onLogout}>Logout</button>

      <div className="dropdown">
        <button type="button" className="dropbtn">
          👤 <span>{user?.email}</span> ▼
        </button>
        <div className="dropdown-content">
          <p><strong>First name: </strong> <span>{user?.firstName}</span></p>
          <p><strong>Last name: </strong> <span>{user?.lastName}</span></p>
          <p><strong>Balance: </strong> <span>{user?.balance}</span> <strong>USD</strong></p>
        </div>
      </div>
    </header>
  );
}

export default Navbar;
