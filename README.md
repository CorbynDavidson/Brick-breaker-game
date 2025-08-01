<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Brick Breaker: Jigsaw Edition</title>
  <style>
    * { box-sizing: border-box; }
    body {
      margin: 0;
      font-family: 'Courier New', monospace;
      background: radial-gradient(circle, #000000, #111);
      color: #ff1a1a;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100vh;
      overflow: hidden;
    }

    canvas {
      border: 4px solid #ff1a1a;
      background: #000;
      touch-action: none;
    }

    #hud {
      position: absolute;
      top: 10px;
      left: 20px;
      font-size: 18px;
      color: #ff1a1a;
      z-index: 1;
      text-shadow: 0 0 10px #ff1a1a;
    }

    #overlay {
      position: absolute;
      top: 0; left: 0;
      width: 100%; height: 100%;
      background: rgba(0,0,0,0.92);
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      z-index: 2;
      color: #ff1a1a;
      text-align: center;
    }

    #overlay h1 {
      font-size: 2.5rem;
      margin-bottom: 20px;
      text-shadow: 0 0 10px #ff1a1a;
    }

    #overlay button {
      padding: 10px 20px;
      font-size: 18px;
      background: #ff1a1a;
      color: black;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      box-shadow: 0 0 10px #ff1a1a;
    }

    #overlay button:hover {
      background: #ff3333;
    }
  </style>
</head>
<body>

  <div id="hud">Score: 0 | Lives: 3</div>

  <div id="overlay">
    <h1>“Do you want to play a game?”</h1>
    <button onclick="startGame()">Start</button>
  </div>

  <canvas id="gameCanvas" width="800" height="600"></canvas>

  <script>
    const canvas = document.getElementById("gameCanvas");
    const ctx = canvas.getContext("2d");
    const hud = document.getElementById("hud");
    const overlay = document.getElementById("overlay");

    let score = 0;
    let lives = 3;
    let gameRunning = false;

    const paddle = {
      height: 20,
      width: 120,
      x: canvas.width / 2 - 60,
      speed: 10,
      dx: 0
    };

    const ball = {
      x: canvas.width / 2,
      y: canvas.height - 40,
      size: 10,
      speed: 5,
      dx: 5,
      dy: -5
    };

    const brick = {
      rowCount: 5,
      columnCount: 9,
      width: 70,
      height: 20,
      padding: 10,
      offsetTop: 40,
      offsetLeft: 35
    };

    const bricks = [];
    for (let c = 0; c < brick.columnCount; c++) {
      bricks[c] = [];
      for (let r = 0; r < brick.rowCount; r++) {
        bricks[c][r] = { x: 0, y: 0, visible: true };
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
      ctx.shadowBlur = 10;
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

    function keyDown(e) {
      if (e.key === "ArrowRight" || e.key === "d") {
        paddle.dx = paddle.speed;
      } else if (e.key === "ArrowLeft" || e.key === "a") {
        paddle.dx = -paddle.speed;
      }
    }

    function keyUp(e) {
      if (
        e.key === "ArrowRight" ||
        e.key === "ArrowLeft" ||
        e.key === "a" ||
        e.key === "d"
      ) {
        paddle.dx = 0;
      }
    }

    canvas.addEventListener("touchmove", function (e) {
      const touchX = e.touches[0].clientX - canvas.getBoundingClientRect().left;
      paddle.x = touchX - paddle.width / 2;
      if (paddle.x < 0) paddle.x = 0;
      if (paddle.x + paddle.width > canvas.width) paddle.x = canvas.width - paddle.width;
    });

    function startGame() {
      overlay.style.display = "none";
      score = 0;
      lives = 3;
      gameRunning = true;
      resetBall();
      update();
    }

    function gameOver() {
      gameRunning = false;
      overlay.querySelector("h1").textContent = "Game Over. You failed the test.";
      overlay.querySelector("button").textContent = "Try Again";
      overlay.style.display = "flex";
    }

    document.addEventListener("keydown", keyDown);
    document.addEventListener("keyup", keyUp);
  </script>
</body>
</html>