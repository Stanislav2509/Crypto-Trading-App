import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/register.css";
import registerImage from "../assets/register.jpg";

function Register() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [registrationError, setRegistrationError] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();
    setFieldErrors({});
    setRegistrationError(false);
    setLoading(true);

    try {
      const response = await fetch("/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          firstName,
          lastName,
          email,
          password,
          confirmPassword,
          phoneNumber,
        }),
      });

      if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        if (body.error) {
          setRegistrationError(true);
        } else {
          setFieldErrors(body);
        }
        return;
      }

      navigate(`/verify-email?email=${encodeURIComponent(email)}`);
    } catch (err) {
      setRegistrationError(true);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="register-page">
      <div className="container">
        <div className="form-wrapper">
          <h1 className="title">Sign up</h1>

          <form onSubmit={handleSubmit}>
            {registrationError && <p className="register_error">Registration Error occurred</p>}

            <div className="user-data">
              {fieldErrors.firstName && <small>{fieldErrors.firstName}</small>}
              <input
                type="text"
                placeholder="First name"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                minLength={3}
                maxLength={20}
                required
              />

              {fieldErrors.lastName && <small>{fieldErrors.lastName}</small>}
              <input
                type="text"
                placeholder="Last name"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                minLength={3}
                maxLength={20}
                required
              />

              {fieldErrors.email && <small>{fieldErrors.email}</small>}
              <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />

              {fieldErrors.password && <small>{fieldErrors.password}</small>}
              <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                minLength={6}
                maxLength={30}
                required
              />

              <input
                type="password"
                placeholder="Confirm password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                minLength={6}
                maxLength={30}
                required
              />

              {fieldErrors.phoneNumber && <small>{fieldErrors.phoneNumber}</small>}
              <input
                type="tel"
                placeholder="Phone number"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                minLength={10}
                maxLength={13}
                required
              />
            </div>

            <div className="btn_submit">
              <button type="submit" disabled={loading}>
                {loading ? "Signing up..." : "Sign up"}
              </button>
            </div>
          </form>

          <div className="signup-link">
            <span>Already have an account?</span> <Link to="/login">Login</Link>
          </div>
        </div>
      </div>
      <img className="img" src={registerImage} alt="Register Image" />
    </main>
  );
}

export default Register;
