# Java Galaga

A simple Galaga-style arcade game built with Java Swing.

## Features

- Resizable game window
- Player movement with arrow keys
- Player shooting with the space bar
- Enemy formation movement
- Score, lives, and stage UI
- Start, game over, and clear screens
- PNG sprite assets for the player, enemies, and lasers

## Project Structure

```text
assets/
  player_ship.png
  enemy_bug.png
  player_laser.png
  enemy_laser.png
src/
  galaga/
    Main.java
    GameFrame.java
    GamePanel.java
    GameState.java
    Sprite.java
    Player.java
    Enemy.java
    Bullet.java
```

## Run

Compile:

```bash
javac -d out src/galaga/*.java
```

Run:

```bash
java -cp out galaga.Main
```

Windows PowerShell example:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src/galaga/*.java
java -cp out galaga.Main
```
