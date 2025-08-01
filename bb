<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Brick Breaker</title>
  <style>
    body {
      margin: 0;
      background: #111;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      overflow: hidden;
      font-family: sans-serif;
      color: white;
    }
    canvas {
      background: #000;
      border: 3px solid #00ffcc;
    }
  </style>
</head>
<body>
  <canvas id="gameCanvas" width="800" height="600"></canvas>
  <script>
    const canvas = document.getElementById("gameCanvas");
    const ctx = canvas.getContext("2d");

    const paddle = {
      height: 20,
      width: 120,
      x: canvas.width / 2 - 60,
      speed: 7,
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

    let score = 0;

    const bricks = [];
    for (let c = 0; c < brick.columnCount; c++) {
      bricks[c] = [];
      for (let r = 0; r < brick.rowCount; r++) {
        bricks[c][r] = { x: 0, y: 0, visible: true };
      }
    }

    function drawPaddle() {
      ctx.fillStyle = "#00ffcc";
      ctx.fillRect(paddle.x, canvas.height - paddle.height - 10, paddle.width, paddle.height);
    }

    function drawBall() {
      ctx.beginPath();
      ctx.arc(ball.x, ball.y, ball.size, 0, Math.PI * 2);
      ctx.fillStyle = "#ff3366";
      ctx.fill();
      ctx.closePath();
    }

    function drawBricks() {
      bricks.forEach((column, c) => {
        column.forEach((b, r) => {
          if (b.visible) {
            const brickX = c * (brick.width + brick.padding) + brick.offsetLeft;
            const brickY = r * (brick.height + brick.padding) + brick.offsetTop;
            b.x = brickX;
            b.y = brickY;
            ctx.fillStyle = "#fff";
            ctx.fillRect(brickX, brickY, brick.width, brick.height);
          }
        });
      });
    }

    function drawScore() {
      ctx.font = "20px Arial";
      ctx.fillStyle = "#00ffcc";
      ctx.fillText(`Score: ${score}`, 10, 25);
    }

    function movePaddle() {
      paddle.x += paddle.dx;
      if (paddle.x < 0) paddle.x = 0;
      if (paddle.x + paddle.width > canvas.width) paddle.x = canvas.width - paddle.width;
    }

    function moveBall() {
      ball.x += ball.dx;
      ball.y += ball.dy;

      if (ball.x + ball.size > canvas.width || ball.x - ball.size < 0) {
        ball.dx *= -1;
      }

      if (ball.y - ball.size < 0) {
        ball.dy *= -1;
      }

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
        alert("Game Over! Final Score: " + score);
        document.location.reload();
      }
    }

    function draw() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      drawPaddle();
      drawBall();
      drawBricks();
      drawScore();
    }

    function update() {
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

    document.addEventListener("keydown", keyDown);
    document.addEventListener("keyup", keyUp);

    update();
  </script>
</body>
</html>