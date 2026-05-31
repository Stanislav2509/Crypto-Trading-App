import "./nav-style.css";

function Navbar({ user, onLogout }) {
  return (
    <header>

      <a className="home" href="/real-time-prices">
        Home Page
      </a>

      <button onClick={() => window.location.href = "/transaction-history"}>
        Transaction History
      </button>

      <button onClick={() => window.location.href = "/wallet"}>
        View Wallet
      </button>

      <button onClick={() => window.location.href = "/warning-reset-profile"}>
        Reset Profile
      </button>

      <button onClick={onLogout}>
        Logout
      </button>

      <div className="dropdown">

        <button className="dropbtn">
          👤 {user.username} ▼
        </button>

        <div className="dropdown-content">
          <p>
            <strong>First name: </strong>
            {user.firstName}
          </p>

          <p>
            <strong>Last name: </strong>
            {user.lastName}
          </p>

          <p>
            <strong>Balance: </strong>
            {user.balance} USD
          </p>
        </div>

      </div>

    </header>
  );
}

export default Navbar;