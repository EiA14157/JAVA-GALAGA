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
    GameConfig.java
    GameAssets.java
    GameRenderer.java
    GameSession.java
    EnemyFormationFactory.java
    InputState.java
    GameState.java
    Sprite.java
    Player.java
    Enemy.java
    Bullet.java
```

## File Responsibilities

- `Main.java`: Starts the Swing application on the event dispatch thread.
- `GameFrame.java`: Creates the window and hosts the game panel.
- `GamePanel.java`: Connects Swing timer ticks, keyboard input, and rendering.
- `GameConfig.java`: Stores shared gameplay and layout constants.
- `GameAssets.java`: Loads and exposes sprite assets.
- `GameRenderer.java`: Draws the current game state to the screen.
- `GameSession.java`: Owns gameplay state and update rules such as movement, collisions, score, and stage flow.
- `EnemyFormationFactory.java`: Builds the enemy layout for each stage.
- `InputState.java`: Tracks which control keys are currently held down.
- `GameState.java`: Defines the high-level screen/game states.
- `Sprite.java`: Base rectangle-backed entity for collision and position data.
- `Player.java`: Player ship behavior and lives.
- `Enemy.java`: Enemy position and row metadata.
- `Bullet.java`: Bullet movement and bounds checks.

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
