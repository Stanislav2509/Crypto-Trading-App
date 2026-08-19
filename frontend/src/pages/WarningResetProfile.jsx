import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar.jsx";
import warningImage from "../assets/warning.png";
import "../styles/warning-reset-profile.css";

function WarningResetProfile() {
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    async function authFetch(url, options) {
      const response = await fetch(url, {
        ...options,
        headers: { ...(options?.headers || {}), Authorization: `Bearer ${token}` },
      });

      if (response.status === 401 || response.status === 403) {
        localStorage.removeItem("token");
        navigate("/login");
        return null;
      }

      if (!response.ok) {
        throw new Error(`Request to ${url} failed with status ${response.status}`);
      }

      return response.json();
    }

    async function loadUser() {
      try {
        const userData = await authFetch("/api/user/me");
        if (userData) setUser(userData);
      } catch (err) {
        setError("Unable to load user. Please try again.");
      }
    }

    loadUser();
  }, [navigate]);

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login");
  }

  async function handleConfirmReset() {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    try {
      const response = await fetch("/api/user/reset-profile", {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.status === 401 || response.status === 403) {
        localStorage.removeItem("token");
        navigate("/login");
        return;
      }

      if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
      }

      navigate("/real-time-prices");
    } catch (err) {
      setError("Unable to reset profile. Please try again.");
    }
  }

  function handleCancel() {
    navigate("/real-time-prices");
  }

  return (
    <div className="warning-reset-profile-page">
      <Navbar user={user} onLogout={handleLogout} />
      <main>
        <div className="content-wrapper">
          <h1>WARNING!!!</h1>
          <h2>Are you sure you want to reset your profile?</h2>

          {error && <p className="error-message">{error}</p>}

          <div className="button-container">
            <button type="button" onClick={handleConfirmReset}>YES</button>
            <button type="button" onClick={handleCancel}>NO</button>
          </div>
        </div>
        <img className="img" src={warningImage} alt="Warning Image" />
      </main>
    </div>
  );
}

export default WarningResetProfile;
