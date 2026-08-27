import { useEffect, useRef } from "react";

const GLYPHS = "アイウエオカキクケコサシスセソ0123456789$€".split("");

// Decorative digital-rain panel reused by the auth-flow pages in place of the
// old stock-photo side panels. Subtle by design: low opacity, slow cadence,
// frozen for prefers-reduced-motion.
function MatrixRain({ className }) {
  const canvasRef = useRef(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    const fontSize = 16;
    let columns = 0;
    let drops = [];
    let width = 0;
    let height = 0;

    function resize() {
      const rect = canvas.parentElement.getBoundingClientRect();
      width = canvas.width = rect.width;
      height = canvas.height = rect.height;
      columns = Math.floor(width / fontSize);
      drops = Array.from({ length: columns }, () => Math.random() * -height / fontSize);
    }

    resize();
    window.addEventListener("resize", resize);

    function draw() {
      ctx.fillStyle = "rgba(6, 10, 8, 0.15)";
      ctx.fillRect(0, 0, width, height);
      ctx.font = `${fontSize}px Consolas, monospace`;

      for (let i = 0; i < columns; i++) {
        const glyph = GLYPHS[Math.floor(Math.random() * GLYPHS.length)];
        const y = drops[i] * fontSize;
        ctx.fillStyle = "rgba(51, 255, 122, 0.55)";
        ctx.fillText(glyph, i * fontSize, y);

        if (y > height && Math.random() > 0.975) {
          drops[i] = 0;
        }
        drops[i] += 0.4;
      }
    }

    if (prefersReducedMotion) {
      ctx.fillStyle = "rgba(6, 10, 8, 1)";
      ctx.fillRect(0, 0, width, height);
      draw();
      return () => window.removeEventListener("resize", resize);
    }

    const timer = setInterval(draw, 60);
    return () => {
      clearInterval(timer);
      window.removeEventListener("resize", resize);
    };
  }, []);

  return (
    <div className={className} style={{ position: "relative", overflow: "hidden", background: "#060a08" }}>
      <canvas ref={canvasRef} style={{ display: "block", width: "100%", height: "100%" }} />
    </div>
  );
}

export default MatrixRain;
