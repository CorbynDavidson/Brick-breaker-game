<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no"/>
  <title>Jigsaw Brick Breaker</title>
  <style>
    html, body {
      margin: 0;
      padding: 0;
      font-family: 'Courier New', monospace;
      background: radial-gradient(circle, #000000, #111);
      color: #ff1a1a;
      height: 100%;
      overflow-x: hidden;
      touch-action: none;
    }

    #hud {
      font-size: 16px;
      color: #ff1a1a;
      padding: 10px 15px;
      text-shadow: 0 0 8px #ff1a1a;
      text-align: center;
    }

    #overlay {
      position: fixed;
      top: 0; left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0,0,0,0.92);
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      z-index: 10;
      color: #ff1a1a;
      text-align: center;
      padding: 20px;
    }

    #overlay h1 {
      font-size: 1.8rem;
      margin-bottom: 20px;
      text-shadow: 0 0 10px #ff1a1a;
    }

    #overlay button {
      padding: 12px 24px;
      font-size: 18px;
      background: #ff1a1a;
      color: black;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      box-shadow: 0 0 10px #ff1a1a;
    }

    canvas {
      display: block;
      width: 100vw;
      height: auto;
      border: 4px solid #ff1a1a;
      background: #000;
      margin: 0 auto 50px;
    }
  </style>
</head>
<body>

  <div id="hud">Score: 0 | Lives: 3</div>
  <div id="overlay">
    <h1>“Do you want to play a game?”</h1>
    <button onclick="startGame()">Start</button>
  </div>

  <canvas id="gameCanvas" width="360" height="600"></canvas>

  <script>
    const canvas = document.getElementById("gameCanvas");
    const ctx = canvas.getContext("2d");
    const hud = document.getElementById("hud");
    const overlay = document.getElementById("overlay");

    let score = 0;
    let lives = 3;
    let gameRunning = false;

    const paddle = {
      height: 12,
      width: 80,
      x: canvas.width / 2 - 40,
      speed: 7,
      dx: 0
    };

    const ball = {
      x: canvas.width / 2,
      y: canvas.height - 60,
      size: 5,
      speed: 4,
      dx: 4,
      dy: -4
    };

    const brick = {
      rowCount: 6,
      columnCount: 10,
      width: 30,
      height: 12,
      padding: 5,
      offsetTop: 40,
      offsetLeft: 15
    };

    let bricks = [];

    function initBricks() {
      bricks = [];
      for (let c = 0; c < brick.columnCount; c++) {
        bricks[c] = [];
        for (let r = 0; r < brick.rowCount; r++) {
          bricks[c][r] = { x: 0, y: 0, visible: true };
        }
      }
    }

    function resetBall() {
      ball.x = canvas.width / 2;
      ball.y = canvas.height - 60;
      ball.dx = ball.speed * (Math.random() > 0.5 ? 1 : -1);
      ball.dy = -ball.speed;
    }

    function drawPaddle() {
      ctx.fillStyle = "#ff1a1a";
      ctx.fillRect(paddle.x, canvas.height - paddle.height - 10, paddle.width, paddle.height);
    }

    function drawBall() {
      ctx.beginPath();
      ctx.arc(ball.x, ball.y, ball.size, 0, Math.PI * 2);
      ctx.fillStyle = "#ff0000";
      ctx.shadowColor = "#ff0000";
      ctx.shadowBlur = 8;
      ctx.fill();
      ctx.closePath();
      ctx.shadowBlur = 0;
    }

    function drawBricks() {
      bricks.forEach((column, c) => {
        column.forEach((b, r) => {
          if (b.visible) {
            const brickX = c * (brick.width + brick.padding) + brick.offsetLeft;
            const brickY = r * (brick.height + brick.padding) + brick.offsetTop;
            b.x = brickX;
            b.y = brickY;
            ctx.fillStyle = "#990000";
            ctx.fillRect(brickX, brickY, brick.width, brick.height);
            ctx.strokeStyle = "#ff1a1a";
            ctx.strokeRect(brickX, brickY, brick.width, brick.height);
          }
        });
      });
    }

    function drawHUD() {
      hud.textContent = `Score: ${score} | Lives: ${lives}`;
    }

    function movePaddle() {
      paddle.x += paddle.dx;
      if (paddle.x < 0) paddle.x = 0;
      if (paddle.x + paddle.width > canvas.width) paddle.x = canvas.width - paddle.width;
    }

    function moveBall() {
      ball.x += ball.dx;
      ball.y += ball.dy;

      if (ball.x + ball.size > canvas.width || ball.x - ball.size < 0) ball.dx *= -1;
      if (ball.y - ball.size < 0) ball.dy *= -1;

      if (
        ball.x > paddle.x &&
        ball.x < paddle.x + paddle.width &&
        ball.y + ball.size > canvas.height - paddle.height - 10
      ) {
        ball.dy = -ball.speed;
      }

      bricks.forEach(column => {
        column.forEach(b => {
          if (b.visible) {
            if (
              ball.x > b.x &&
              ball.x < b.x + brick.width &&
              ball.y - ball.size < b.y + brick.height &&
              ball.y + ball.size > b.y
            ) {
              ball.dy *= -1;
              b.visible = false;
              score++;
            }
          }
        });
      });

      if (ball.y + ball.size > canvas.height) {
        lives--;
        if (lives > 0) {
          resetBall();
        } else {
          gameOver();
        }
      }
    }

    function draw() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      drawPaddle();
      drawBall();
      drawBricks();
      drawHUD();
    }

    function update() {
      if (!gameRunning) return;
      movePaddle();
      moveBall();
      draw();
      requestAnimationFrame(update);
    }

    function gameOver() {
      gameRunning = false;
      overlay.querySelector("h1").textContent = "Game Over. You failed the test.";
      overlay.querySelector("button").textContent = "Try Again";
      overlay.style.display = "flex";
    }

    function startGame() {
      initBricks();
      score = 0;
      lives = 3;
      resetBall();
      paddle.x = canvas.width / 2 - paddle.width / 2;
      overlay.style.display = "none";
      gameRunning = true;
      update();
    }

    document.addEventListener("keydown", e => {
      if (e.key === "ArrowRight" || e.key === "d") paddle.dx = paddle.speed;
      else if (e.key === "ArrowLeft" || e.key === "a") paddle.dx = -paddle.speed;
    });

    document.addEventListener("keyup", e => {
      if (["ArrowRight", "ArrowLeft", "a", "d"].includes(e.key)) paddle.dx = 0;
    });

    canvas.addEventListener("touchmove", e => {
      const rect = canvas.getBoundingClientRect();
      const touchX = e.touches[0].clientX - rect.left;
      paddle.x = touchX - paddle.width / 2;
      if (paddle.x < 0) paddle.x = 0;
      if (paddle.x + paddle.width > canvas.width) paddle.x = canvas.width - paddle.width;
    });
  </script>
</body>
</html>