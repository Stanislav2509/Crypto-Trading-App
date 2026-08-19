import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import "../styles/verify-email.css";

function VerifyEmail() {
  const [searchParams] = useSearchParams();
  const email = searchParams.get("email") ?? "";
  const navigate = useNavigate();

  const [code, setCode] = useState("");
  const [hasVerificationError, setHasVerificationError] = useState(false);
  const [verifying, setVerifying] = useState(false);
  const [resending, setResending] = useState(false);

  async function handleVerify(event) {
    event.preventDefault();
    setHasVerificationError(false);
    setVerifying(true);

    try {
      const response = await fetch("/api/auth/verify-email", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, code }),
      });

      if (!response.ok) {
        setHasVerificationError(true);
        return;
      }

      navigate("/login");
    } catch (err) {
      setHasVerificationError(true);
    } finally {
      setVerifying(false);
    }
  }

  async function handleResend(event) {
    event.preventDefault();
    setResending(true);

    try {
      await fetch("/api/auth/resend-verification-code", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
      });
    } finally {
      setResending(false);
    }
  }

  return (
    <div className="verify-email-page">
      <div className="verify-page">
        <div className="verify-card">
          <div className="verify-icon">✉️</div>

          <h1>Verify your email</h1>

          <p className="verify-text">We sent a 6-digit verification code to:</p>
          <p className="email-text">{email}</p>

          <form className="verify-form" onSubmit={handleVerify}>
            <input type="hidden" name="email" value={email} readOnly />

            <label htmlFor="code">Verification code</label>
            <input
              type="text"
              id="code"
              name="code"
              maxLength={6}
              placeholder="Enter 6-digit code"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              required
            />

            {hasVerificationError && (
              <p className="error-message">Invalid or expired verification code.</p>
            )}

            <button type="submit" disabled={verifying}>
              {verifying ? "Verifying..." : "Verify Account"}
            </button>
          </form>

          <form className="resend-form" onSubmit={handleResend}>
            <button type="submit" className="resend-button" disabled={resending}>
              {resending ? "Sending..." : "Send new code"}
            </button>
          </form>

          <Link to="/register" className="back-link">Back to registration</Link>
        </div>
      </div>
    </div>
  );
}

export default VerifyEmail;
