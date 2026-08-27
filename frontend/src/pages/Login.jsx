import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/login-style.css";
import loginMatrixImage from "../assets/login-matrix.jpg";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        setError("Invalid email or password.");
        return;
      }

      const token = await response.text();
      localStorage.setItem("token", token);
      navigate("/real-time-prices");
    } catch (err) {
      setError("Unable to reach the server. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-page">
      <div className="container">
        <img className="img" src={loginMatrixImage} alt="" />

      <div className="form-container">
           <h1 className="title">Login</h1>
          <form onSubmit={handleSubmit}>

            <div>
              <input
                id="email"
                name="email"
                type="text"
                placeholder="Email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                required
              />
            </div>

            <div>
              <input
                id="password"
                name="password"
                type="password"
                placeholder="Password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                required
              />
            </div>

            {error && <p className="error-message">{error}</p>}

            <div>
              <button type="submit" disabled={loading}>
                {loading ? "Logging in..." : "Login"}
              </button>
            </div>
          </form>

          <div className="signup-link">
            <span>Don't have a profile?</span> <Link to="/register">Sign up!</Link>
          </div>

        </div>
      </div>
    </main>
  );
}

export default Login;
