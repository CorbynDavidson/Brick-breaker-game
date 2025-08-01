# Brick-breaker-game
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Easy Brick Breaker</title>
  <style>
    canvas {
      background: #111;
      display: block;
      margin: 20px auto;
      border: 2px solid #fff;
    }
    #startBtn {
      display: block;
      margin: 0 auto;
      padding: 12px 24px;
      font-size: 18px;
      background-color: #00cc66;
      border: none;
      color: white;
      border-radius: 5px;
      cursor: pointer;
    }
  </style>
</head>
<body>

<button id="startBtn">Start Game</button>
<canvas id="gameCanvas" width="480" height="320"></canvas>

<script>
const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");
const startBtn = document.getElementById("startBtn");

let gameStarted = false;

let x = canvas.width / 2;
let y = canvas.height - 30;
let dx = 1.5;
let dy = -1.5;
const ballRadius = 8;

const paddleHeight = 10;
const paddleWidth = 100;
let paddleX = (canvas.width - paddleWidth) / 2;

let rightPressed = false;
let leftPressed = false;

const brickRowCount = 5;
const brickColumnCount = 7;
const brickWidth = 60;
const brickHeight = 15;
const brickPadding = 10;
const brickOffsetTop = 30;
const brickOffsetLeft = 25;

let score = 0;
let lives = 3;

const bricks = [];
for(let c = 0; c < brickColumnCount; c++) {
  bricks[c] = [];
  for(let r = 0; r < brickRowCount; r++) {
    bricks[c][r] = { x: 0, y: 0, status: 1 };
  }
}

document.addEventListener("keydown", e => {
  if (e.key === "Right" || e.key === "ArrowRight") rightPressed = true;
  else if (e.key === "Left" || e.key === "ArrowLeft") leftPressed = true;
});

document.addEventListener("keyup", e => {
  if (e.key === "Right" || e.key === "ArrowRight") rightPressed = false;
  else if (e.key === "Left" || e.key === "ArrowLeft") leftPressed = false;
});

startBtn.onclick = function () {
  gameStarted = true;
  startBtn.style.display = "none";
  draw(); // start game loop
};

function drawBall() {
  ctx.beginPath();
  ctx.arc(x, y, ballRadius, 0, Math.PI * 2);
  ctx.fillStyle = "#00FF99";
  ctx.fill();
  ctx.closePath();
}

function drawPaddle() {
  ctx.beginPath();
  ctx.rect(paddleX, canvas.height - paddleHeight, paddleWidth, paddleHeight);
  ctx.fillStyle = "#0095DD";
  ctx.fill();
  ctx.closePath();
}

function drawBricks() {
  for(let c = 0; c < brickColumnCount; c++) {
    for(let r = 0; r < brickRowCount; r++) {
      if(bricks[c][r].status === 1) {
        const brickX = (c * (brickWidth + brickPadding)) + brickOffsetLeft;
        const brickY = (r * (brickHeight + brickPadding)) + brickOffsetTop;
        bricks[c][r].x = brickX;
        bricks[c][r].y = brickY;
        ctx.beginPath();
        ctx.rect(brickX, brickY, brickWidth, brickHeight);
        ctx.fillStyle = "#FF4444";
        ctx.fill();
        ctx.closePath();
      }
    }
  }
}

function drawScore() {
  ctx.font = "16px Arial";
  ctx.fillStyle = "#fff";
  ctx.fillText("Score: " + score, 8, 20);
}

function drawLives() {
  ctx.font = "16px Arial";
  ctx.fillStyle = "#fff";
  ctx.fillText("Lives: " + lives, canvas.width - 70, 20);
}

function collisionDetection() {
  for(let c = 0; c < brickColumnCount; c++) {
    for(let r = 0; r < brickRowCount; r++) {
      const b = bricks[c][r];
      if(b.status === 1) {
        if(x > b.x && x < b.x + brickWidth &&
           y > b.y && y < b.y + brickHeight) {
          dy = -dy;
          b.status = 0;
          score++;
          if(score === brickRowCount * brickColumnCount) {
            alert("🎉 YOU WIN!");
            document.location.reload();
          }
        }
      }
    }
  }
}

function draw() {
  if (!gameStarted) return;

  ctx.clearRect(0, 0, canvas.width, canvas.height);
  drawBricks();
  drawBall();
  drawPaddle();
  drawScore();
  drawLives();
  collisionDetection();

  if(x + dx > canvas.width - ballRadius || x + dx < ballRadius) dx = -dx;
  if(y + dy < ballRadius) dy = -dy;
  else if(y + dy > canvas.height - ballRadius) {
    if(x > paddleX && x < paddleX + paddleWidth) {
      dy = -dy;
    } else {
      lives--;
      if (!lives) {
        alert("💀 GAME OVER");
        document.location.reload();
      } else {
        x = canvas.width / 2;
        y = canvas.height - 30;
        dx = 1.5;
        dy = -1.5;
        paddleX = (canvas.width - paddleWidth) / 2;
      }
    }
  }

  x += dx;
  y += dy;

  if(rightPressed && paddleX < canvas.width - paddleWidth) paddleX += 5;
  else if(leftPressed && paddleX > 0) paddleX -= 5;

  requestAnimationFrame(draw);
}
</script>
</body>
</html>