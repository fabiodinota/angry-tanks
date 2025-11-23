# Angry Tanks

**Angry Tanks** is a 2D turn-based artillery game, drawing inspiration from classic titles like *Worms* and *Scorched Earth*. Developed with JavaFX and JBox2D, the project features dynamic, destructible terrain and realistic physics-based combat.

## Key Features

* **Destructible Terrain**: The landscape is dynamically modified by explosions, using the **JTS Topology Suite** for polygon geometry and an **Ear Clipping Algorithm** for robust decomposition and reconstruction of the terrain.
* **Physics-Based Combat**: Utilizes the **JBox2D** engine for authentic projectile trajectories, rigid-body tank simulation, and raycasting for stable terrain alignment.
* **Turn-Based Gameplay**: Supports a two-player, turn-based combat loop where players alternate between moving their tank, adjusting the cannon's angle, and firing a projectile.
* **Leaderboard Integration**: Features a persistent high-score system connected to a **PostgreSQL** database to track user wins, scores, and trophies.

## Technologies Used

* **Game Framework**: JavaFX (UI/Rendering)
* **Physics Engine**: JBox2D (2D rigid-body simulation)
* **Geometry Library**: JTS Topology Suite (Polygon manipulation for destruction)
* **Build Tool**: Apache Maven
* **Database**: PostgreSQL (via JDBC for Leaderboard and user data persistence)

## Design & Documentation

The following resources provide detailed insight into the game's design and underlying architecture:

* **UML Design (Draw.io)**: [https://drive.google.com/file/d/1TlaOtGQ3LkVtJuaFS3dDNZdQx7PmK020/view?usp=sharing](https://drive.google.com/file/d/1TlaOtGQ3LkVtJuaFS3dDNZdQx7PmK020/view?usp=sharing)
* **FIGMA UI DESIGN**: [https://www.figma.com/design/KweG9KFmPG0l4qZ2HuOM1V/Untitled?node-id=0-1&t=Cw7YIFfEaWLBBzJZ-1](https://www.figma.com/design/KweG9KFmPG0l4qZ2HuOM1V/Untitled?node-id=0-1&t=Cw7YIFfEaWLBBzJZ-1)
* **Physics Engine Documentation (Box2D C++)**: [https://box2d.org/documentation/hello.htm](https://box2d.org/documentation/hello.htm)
* **Ear Clipping Visualization**: [https://www.youtube.com/watch?v=ox9IMJLB92o](https://www.youtube.com/watch?v=ox9IMJLB92o)
