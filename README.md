# BattleShip
Battleship is a war-themed board game for two players in which the opponents try to guess the location of their opponent's warships and sink them.

## Overview
The game is developed for p2p. The game board is 10x10 in size and there are 2 boards, one for the user and one for the enemy.

### Steps to run the Game
* Run Server.java
* Run Client.java

### How To Play The Game
1. Waits for connection after entering host, port and name.
2. Ships are placed on the opened board. Use mouse2 to place vertically.
3. Ships cannot be positioned on the board touching each other. The game starts after positioning is complete.
4. A square is selected from the enemy's board in turn. If there is a piece of the ship in the selected square, the selection continues, otherwise the turn passes to the opposite side.
5. The player who manages to locate all the other player’s ship is declared the winner of the game.

#### Ships
* *Battleship* (4) x1
* *Cruiser* (3) x2
* *Destroyer* (2) x3
* *Submarine* (1) x4

### Features
* Two-way text chat
* Option to rematch after game ends
