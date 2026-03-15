const express = require("express");
const app = express();

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

app.get("/risk-score", async (req, res) => {
  const amount = Number(req.query.amount ?? 0);
  const x = Math.random();

  // 20% chậm + error → TimeLimiter timeout + Retry
  if (x < 0.2) {
    await sleep(1200);
    return res.status(500).json({ error: "slow + failure" });
  }

  // 20% chậm nhưng OK → TimeLimiter timeout nhưng response hợp lệ
  if (x < 0.4) {
    await sleep(1200);
    const score = Math.min(100, Math.round(amount / 10));
    return res.json({ engine: "node", amount, score });
  }

  // 30% lỗi nhanh → Retry + CircuitBreaker
  if (x < 0.7) {
    return res.status(500).json({ error: "fast failure" });
  }

  // 30% OK nhanh → Success path
  const score = Math.min(100, Math.round(amount / 10));
  return res.json({ engine: "node", amount, score });
});

app.listen(4000, () => console.log("Node risk service: http://localhost:4000"));
