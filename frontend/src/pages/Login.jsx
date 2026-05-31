import "./login-style.css";

function Login() {

  function handleSubmit(event) {
    event.preventDefault();

    async function handleSubmit(event) {
  event.preventDefault();

  const formData = new FormData(event.target);

  const response = await fetch("http://localhost:8080/login", {
    method: "POST",
    body: formData,
    credentials: "include"
  });

  if (response.ok) {
    window.location.href = "/real-time-prices";
  }
}
  }

  return (
    <main>
      <div className="container">

        <img
          className="img"
          src="/images/login.jpg"
          alt="Login"
        />

        <div className="form-container">

          <h1 className="title">Login</h1>

          <form onSubmit={handleSubmit}>

            <div>
              <input
                id="email"
                name="email"
                type="text"
                placeholder="Email"
                required
              />
            </div>

            <div>
              <input
                id="password"
                name="password"
                type="password"
                placeholder="Password"
                required
              />
            </div>

            <div>
              <button type="submit">
                Login
              </button>
            </div>

          </form>

          <div className="signup-link">
            <span>Don't have a profile?</span>

            <a href="/register">
              Sign up!
            </a>
          </div>

        </div>

      </div>
    </main>
  );
}

export default Login;