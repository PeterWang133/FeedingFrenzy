import tester.*;
import java.util.Random;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;

// Draws the game with th fish and the player
class FishWorld extends World {
  Player player;
  ILoEnemy enemies;
  Time t;
  Score s;
  
  FishWorld(Player player, ILoEnemy enemies, Time t, Score s) {
    this.player = player; 
    this.enemies = enemies; 
    this.t = t;
    this.s = s;
  }

  /* TEMPLATE
  Fields:
    ... this.player ...   -- Player
    ... this.enemies ...  -- ILoEnemy
    ... this.t ...        -- Time
    ... this.s ...        -- Score
  Methods:
    ... this.makeScene() ...          -- WorldScene
    ... this.worldEnds() ...          -- WorldEnd
    ... this.onTick() ...             -- World
    ... this.onKeyEvent(String) ...   -- FishWorld
  Fields of Methods:
    ... this.enemies.draw(WorldScene, player) ...  -- WorldImage
    ... this.player.draw(WorldScene) ...  -- WorldScene
    ... this.t.draw(WorldScene) ...   -- WorldScene
    ... this.s.draw(WorldScene) ...   -- WorldScene
    ... this.enemies.gameOver(player) ...  -- boolean
    ... this.enemies.largest(player) ...  -- boolean
    ... this.s.update(player, ILoEnemy) ... -- Score
    ... this.player.evolve(int) ...   -- player
  */

  // Draw the world
  public WorldScene makeScene() {
    // make the colored background
    WorldScene scene = new WorldScene(600, 400).placeImageXY(
        new RectangleImage(600, 400, "solid", new Color(185, 245, 255)), 300, 200);

    // draw the enemies on the screen
    scene = this.enemies.draw(scene, this.player);
    
    // draw the player on the screen
    scene = this.player.draw(scene);

    // draw the passed time above the screen
    scene = this.t.draw(scene);

    // draw the score above the screen
    scene = this.s.draw(scene);
    return scene;
  }
  
  // end the game 
  public WorldEnd worldEnds() {
    WorldImage gameOver = new TextImage("Game Over !", 40, Color.black);
    WorldImage winning = new TextImage("You've won the game!", 40, Color.black);
    WorldScene endscene = this.makeScene().placeImageXY(gameOver, 300, 200);
    WorldScene winscene = this.makeScene().placeImageXY(winning, 300, 200);
    
    // when the player is eaten by a larger enemy
    if (this.enemies.gameOver(this.player)) {
      return new WorldEnd(true, endscene);
    }
    // or when the player gets the largest in the screen
    else if (this.enemies.largest(this.player)) {
      return new WorldEnd(true, winscene);
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // Update the game scene at every tick of the clock
  public World onTick() {
    // update the score every frame
    this.s = this.s.update(this.player, this.enemies);
    
    // increase "countAfterAcceleration"
    this.player.caa += 1; 

    // evloving the player depending on how many or how large enemies it ate
    this.player = this.player.evolve(this.s.eatenNum);

    // reconstruct the enemy list deleting the eaten enemies
    this.enemies = this.enemies.eaten(this.player);
    
    // Limit the number of enemy displayed on screen to be 20
    if (this.enemies.count() <= 20) {
    	
      Enemy newE = new Enemy(this.player.level, Color.magenta);
      this.enemies = this.enemies.addNew(newE);
    }
    return new FishWorld(this.player.move(), this.enemies.move(this.player), this.t.update(),
      this.s);
  }
  
//Update the game scene at every tick of the clock
// Used for testing purpose
 public World onTickForTesting() {
   // update the score every frame
   this.s = this.s.update(this.player, this.enemies);
   
   // increase "countAfterAcceleration"
   this.player.caa += 1; 

   // evloving the player depending on how many or how large enemies it ate
   this.player = this.player.evolve(this.s.eatenNum);

   // reconstruct the enemy list deleting the eaten enemies
   this.enemies = this.enemies.eaten(this.player);
   
   // Limit the number of enemy displayed on screen to be 20
   if (this.enemies.count() <= 20) {
   	
     Enemy newE = new Enemy(this.player.level, Color.magenta, 200, 200, 3, "left");
     this.enemies = this.enemies.addNew(newE);
   }
   return new FishWorld(this.player.move(), this.enemies.move(this.player), this.t.update(),
     this.s);
 }
  
  // change the player's direction using arrow keys
  public FishWorld onKeyEvent(String key) {
    if (key.equals("left") || key.equals("right") || key.equals("up") || key.equals("down")) {
      return new FishWorld(new Player(this.player.level, this.player.c, this.player.x, 
        this.player.y, this.player.speed, key.toString(), 0), this.enemies, this.t, this.s);
    }
    else {
      return this;
    }
  }
}

// To represent an enemy fish
interface ILoEnemy {

  // draw the enemies in this list one by one 
  WorldScene draw(WorldScene acc, Player p);

  // move the enemies in this list one by one 
  ILoEnemy move(Player p);
  
  // count the number of the enemies
  int count();
  
  // produce a new enemy with a level which is decided based on the level of the player
  ILoEnemy addNew(Enemy e);
  
  // reproduce the list of enemies deleting the enemies eaten by the player
  ILoEnemy eaten(Player p);
  
  // judge whether the player can be eaten by one of the enemy or not
  boolean gameOver(Player p);
  
  // calculate the sum of the levels of enemies eaten by the player
  int eatScore(Player p);
  
  // count the number of enemies eaten by the player
  int fishEaten(Player p);
  
  // to judge whether the player is larger than any of enemies or not
  boolean largest(Player p);
}

// Represents an empty list of enemy fish
class MtLoEnemy implements ILoEnemy {

  // draw the enemies in this list one by one 
  public WorldScene draw(WorldScene acc, Player p) {
    return acc;
  }

  //move the enemies in this list one by one 
  public ILoEnemy move(Player p) {
    return this;
  }
  
  // count the number of the enemies
  public int count() {
    return 0;
  }
  
  //Add a new enemy to the list
  public ILoEnemy addNew(Enemy e) {
    return new ConsLoEnemy(e, this);
  }
  
  // reproduce the list of enemies deleting the enemies eaten by the player
  public ILoEnemy eaten(Player p) {
    return this;
  }
  
  // judge whether the player can be eaten by one of the enemy or not
  public boolean gameOver(Player p) {
    return false;
  }
  
  // calculate the sum of the levels of enemies eaten by the player
  public int eatScore(Player p) {
    return 0;
  }
  
  // count the number of enemies eaten by the player
  public int fishEaten(Player p) {
    return 0;
  }

  // to judge whether the player is larger than any of enemies or not
  public boolean largest(Player p) {
    return true;
  }
  
}

// To represent a non-empty list of enemy fish
class ConsLoEnemy implements ILoEnemy {
  Enemy first;
  ILoEnemy rest;

  ConsLoEnemy(Enemy first, ILoEnemy rest) {
    this.first = first;
    this.rest = rest;
  }

  /* TEMPLATE
  Fields:
    ... this.first ...     -- Enemy
    ... this.rest ...      -- ILoEnemy
    
  Methods:
    ... this.draw(WorldScene, Player) ...   -- WorldScene
    ... this.move(Player) ...               -- ILoEnemy
    ... this.count() ...                    -- int
    ... this.addNew(Player) ...             -- ILoEnemy
    ... this.eaten(Player) ...              -- ILoEnemy
    ... this.gameOver(Player) ...           -- boolean
    ... this.eatScore(Player) ...           -- int
    ... this.fishEaten(Player) ...          -- int
    ... this.largest(Player) ...            -- boolean
  
  Methods for fields:
    ... this.first.eaten(Player) ...    -- boolean
    ... this.first.draw(Player) ...     -- WorldScene
    ... this.first.move(Player) ...     -- Enemy
    ... this.first.eaten(Player) ...    -- boolean

    ... this.rest.draw(WorldScene, Player) ...  -- WorldScene
    ... this.rest.move(Player) ...              -- ILoEnemy
    ... this.rest.count() ...                   -- int
    ... this.rest.eaten(Player) ...             -- ILoEnemy
    ... this.rest.gameOver(Player) ...          -- boolean
    ... this.rest.eatScore(Player) ...          -- int
    ... this.rest.fishEaten(Player) ...         -- int
    ... this.rest.largest(Player) ...           -- boolean
  */

  //draw the enemies in this list one by one 
  public WorldScene draw(WorldScene acc, Player p) {
    return this.rest.draw(this.first.draw(acc, p), p);
  }

  //move the enemies in this list one by one
  public ILoEnemy move(Player p) {
    return new ConsLoEnemy(this.first.move(p), this.rest.move(p));
  }
  
  //count the number of the enemy fish in the list
  public int count() {
    return 1 + this.rest.count();
  }
  
  //Add a new enemy to the list
  public ILoEnemy addNew(Enemy e) {    
    return new ConsLoEnemy(e, this);
  }
  
  // reproduce the list of enemies deleting the enemies eaten by the player
  public ILoEnemy eaten(Player p) {
    if (this.first.eaten(p)) {
      return this.rest.eaten(p);
    }
    else {
      return new ConsLoEnemy(this.first, this.rest.eaten(p));
    }
  }
  
  // judge whether the player can be eaten by one of the enemy or not
  public boolean gameOver(Player p) {
    if (this.first.eaten(p) && this.first.level > p.level) {
      return true;
    }
    else {
      return this.rest.gameOver(p);
    }
  }

  // calculate the sum of the levels of enemies eaten by the player
  public int eatScore(Player p) {  
    if (this.first.eaten(p)) {
      return this.first.level + this.rest.eatScore(p);
    }
    else {
      return this.rest.eatScore(p);
    }
  }
  // count the number of enemies eaten by the player
  public int fishEaten(Player p) {  
    if (this.first.eaten(p)) {
      return 1 + this.rest.fishEaten(p);
    }
    else {
      return this.rest.fishEaten(p);
    }
  }
  
  // to judge whether the player is larger than any of enemies or not
  public boolean largest(Player p) {
    return this.first.level < p.level && this.rest.largest(p);
  }  
}

// to represent a class of a player fish
class Player {
  int level;
  Color c;
  int x;
  int y;
  int speed;
  String direction;
  // countAfterAcceleration
  int caa;

  Player(int level, Color c, int x, int y, int speed, String direction, int caa) {
    this.level = level;
    this.c = c;
    this.x = x;
    this.y = y;
    this.speed = speed;
    this.direction = direction;
    this.caa = caa;
  }

  /* TEMPLATE
  Fields:
    ... this.level ...      -- int
    ... this.c ...          -- Color
    ... this.x ...          -- int
    ... this.y ...          -- int
    ... this.speed ...      -- int
    ... this.direction ...  -- String
    ... this.caa ...        -- int
  Methods:
    ... this.draw(WorldScene) ...  -- WorldScene
    ... this.move() ...            -- Player
    ... this.evolve(int) ...       -- Player
  */

  // Draw the fish onto the WorldScene
  public WorldScene draw(WorldScene acc) {
    // set the size depending on its level
    int size = (int) (10 + this.level * 1.7);
    WorldImage ellipse = new EllipseImage(2 * size, (int) (1.6 * size), "solid", this.c);
    WorldImage triangle = new EquilateralTriangleImage((int) (1.6 * size), "solid", this.c);
    if (this.x < 0) {
      this.x = 600;
    }
    else if (this.x > 600) {
      this.x = 0;
    }
    if (this.direction.equals("right")) {
      triangle = new RotateImage(triangle, 90);
      return acc.placeImageXY(ellipse, this.x, this.y).placeImageXY(triangle, this.x - size, this.y);
    }
    else if (this.direction.equals("up")) {
      ellipse = new RotateImage(ellipse, 90);
      return acc.placeImageXY(ellipse, this.x, this.y).placeImageXY(triangle, this.x, this.y + size);
    } 
    else if (this.direction.equals("down")) {
      ellipse = new RotateImage(ellipse, 90);
      triangle = new RotateImage(triangle, 180);
      return acc.placeImageXY(ellipse, this.x, this.y).placeImageXY(triangle, this.x, this.y - size);
    } else {
      triangle = new RotateImage(triangle, -90);
      return acc.placeImageXY(ellipse, this.x, this.y).placeImageXY(triangle, this.x + size, this.y);
    }
  }

  // Move the player's location based on keyboard input
  public Player move() {
    // distance to move
    int dis = 0;
    // decid the distance to move based on how many frames has passed after pushing an arrow key
    if (this.speed - Math.floorDiv(this.caa, 5) > 0) {
      dis = this.speed - Math.floorDiv(this.caa, 5);
    }
    // move the player depending on the current direction by the distance
    switch (this.direction) {
      case "right":
        return new Player(this.level, this.c, this.x + dis,
            this.y, this.speed, this.direction, this.caa);
      case "left":
        return new Player(this.level, this.c, this.x - dis,
            this.y, this.speed, this.direction, this.caa);
      case "up":
        if (0 < this.y - dis) {
          return new Player(this.level, this.c, this.x, this.y - dis,
              this.speed, this.direction, this.caa);
        }
        else {
          return this;
        }
      case "down":
        if (this.y + dis < 400) {
          return new Player(this.level, this.c, this.x, this.y + dis, 
              this.speed, this.direction, this.caa);
        }
        else {
          return this;
        }
      default:
        return new Player(this.level, this.c, this.x, this.y, this.speed, this.direction, this.caa);
    }
  }
  
  // evolve the player based on how many enemies it has eaten
  public Player evolve(int eaten) {
    return new Player(Math.floorDiv(eaten + 10, 10), this.c, this.x, 
        this.y, this.speed, this.direction, this.caa);
  }
  
}

// to represent a class of enemy fish
class Enemy {
  int level;
  Color c;
  int x;
  int y;
  int speed;
  String dir;
  Random rand;
  
  Enemy(int player_level, Color c) {
    // Enemy level is randomly generated
    this.rand = new Random();
    
    int possibility = rand.nextInt(100);
    this.level = rand.nextInt(4) + 1;
    
    if (possibility >= 0 && possibility <= 39) {
      this.level = player_level;
    }
    else if (possibility >= 40 && possibility <= 69 && player_level > 1 && player_level < 5) {
      this.level = player_level + 1;
    }
    else if (possibility >= 70 && possibility <= 89 && player_level > 1 && player_level < 4) {
      this.level = player_level + 2;
    }
    else if (possibility >= 90 && possibility <= 100 && player_level > 1 && player_level < 3) {
      this.level = player_level + 3;
    }
    
    this.c = c;
	    
    int r = rand.nextInt(100);
	    
    if (r >= 50) {
      this.dir = "right";
      this.x = 0;
    }
    else {
      this.dir = "left";
      this.x = 600;
    }
	    
    this.y = rand.nextInt(400);
    this.speed = rand.nextInt(4) + 1;
    }
  
  Enemy(int level, Color c, String dir) {
    this.rand = new Random();
    this.level = level;
    this.c = c;
    if (dir.equals("right")) {
      this.x = 0;
    }
    else {
      this.x = 600;
    }
    this.y = rand.nextInt(400);
    this.speed = rand.nextInt(4) + 1;
    this.dir = dir;
  }

  Enemy(int level, Color c, int x, int y, int speed, String dir) {
    this.rand = new Random();
    this.level = level;
    this.c = c;
    this.x = x;
    this.y = y;
    this.speed = speed;
    this.dir = dir;
  }

  /* TEMPLATE
  Fields:
    ... this.level ...  -- int
    ... this.c ...      -- Color
    ... this.x ...      -- int
    ... this.y ...      -- int
    ... this.dir ...    -- String
    ... this.rand ...   -- Random
  Methods:
  ... this.draw(WorldScene, Player) ...          -- WorldScene
  ... this.move(Player) ...          -- Enemy
  ... this.approaching(Player) ...   -- boolean
  ... this.eaten(Player) ...         -- boolean
  */

  // Draws the enemy
  public WorldScene draw(WorldScene acc, Player p) {
    int size = (int) (10 + this.level * 1.5);
    // draw the enemy which has higher level than player's level using darker color
    if (this.approaching(p)) {
      this.c = new Color(106, 13, 173);
    }
    else if (this.level > p.level) {
      this.c = new Color(200, 0, 200);
    } 
    else if (this.level == p.level) {
      this.c = new Color(240, 100, 240);
    }
    else {
      this.c = Color.magenta;
    }
    WorldImage ellipse = new EllipseImage(2 * size, (int) (1.6 * size), "solid", this.c);
    WorldImage triangle = new EquilateralTriangleImage((int) (1.6 * size), "solid", this.c);
    if (this.dir.equals("right")) {
      triangle = new RotateImage(triangle, 90);
      return acc.placeImageXY(ellipse, this.x, this.y).placeImageXY(triangle, this.x - size, this.y);
    } else {
      triangle = new RotateImage(triangle, -90);
      return acc.placeImageXY(ellipse, this.x, this.y).placeImageXY(triangle, this.x + size, this.y);
    }
  }

  // Moves the enemy fish based its attributed speed
  public Enemy move(Player p) {
    // let the larger enemy chase the player
    if (this.level > p.level) {
      if (Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2) < Math.pow(25 * this.level, 2)) {
        if (p.x < this.x) {
          this.dir = "left";
          this.x -= 2;
        } else {
          this.dir = "right";
          this.x += 2;
        }
        if (p.y < this.y) {
          this.y -= 1;
        } else {
          this.y += 1;
        }
        return new Enemy(this.level, this.c, this.x, this.y, this.speed, this.dir);
      }
    }
    
    if (this.x > 600 && this.dir.equals("right")) {
      this.y = (this.y + 33) % 400;
      this.x = 1;
    }
    else if (this.x < 0 && this.dir.equals("left")) {
      this.y = (this.y + 33) % 400;
      this.x = 599;
    }
    
    if (this.dir.equals("right")) {
      return new Enemy(this.level, this.c, this.x + this.speed, this.y, this.speed, this.dir);
    }
    else {
      return new Enemy(this.level, this.c, this.x - this.speed, this.y, this.speed, this.dir);
    }
  }
  
  // decide whether this turns to "CHASING MODE" or not
  public boolean approaching(Player p) {
    return Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2) < Math.pow(25 * this.level, 2)
        && this.level > p.level;
  }
  
  // to judge whether this enemy is enough close to be eaten based on the distance
  public boolean eaten(Player p) {
    int limitDis = 10 + (int) (this.level * 1.5);
    return Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2) < Math.pow(limitDis, 2);
  }
}

// to represent a class of time
class Time {
  int hour;
  int minute;
  int second;
  int frame;
  
  Time(int h, int m, int s, int f) {
    this.hour = h;
    this.minute = m;
    this.second = s;
    this.frame = f;
  }
  
  /* TEMPLATE
  Fields:
    ... this.hour ...    -- int
    ... this.minute ...  -- int
    ... this.second ...  -- int
    ... this.frame ...   -- int
  Methods:
  ... this.update() ...          -- Time
  ... this.draw(WorldScene) ...  -- WorldScene
  */

  // adjust them properly
  public Time update() {
    if (this.frame % 17 == 0 && this.frame != 0) {
      this.second += 1;
    }
    if (this.second == 59) {
      this.second = 0;
      this.minute += 1;
    }
    if (this.minute == 59) {
      this.minute = 0;
      this.hour += 1;
    }
    return new Time(this.hour, this.minute, this.second, this.frame + 1);
  }
  
  // draw the time at the top of the screen
  public WorldScene draw(WorldScene acc) {
    String t = "Time: " + this.hour + ":" + this.minute + ":" + this.second;
    return acc.placeImageXY(new TextImage(t, 20, Color.black), 250, 20);
  }
}

// to represent information of score and how many enemies you ate
class Score {
  int score;
  int eatenNum;
  
  Score(int score, int eatenNum) {
    this.score = score;
    this.eatenNum = eatenNum;
  }

  /* TEMPLATE
  Fields:
    ... this.score ...     -- int
    ... this.eatenNum ...  -- int
  Methods:
    ... this.update(Player, ILoEnemy) ...   -- Score
    ... this.draw(WorldScene) ...           -- WorldScene
  */
  
  // update the score and the number
  public Score update(Player p, ILoEnemy enemies) {
    return new Score(this.score + enemies.eatScore(p), this.eatenNum + enemies.fishEaten(p));
  }
  
  // draw the score at the top of the screen
  public WorldScene draw(WorldScene acc) {
    String s = "Score: " + this.score;
    return acc.placeImageXY(new TextImage(s, 20, Color.black), 400, 20);
  }
}


//Examples and tests for the program
class ExamplesFishGame {

Player player1;
Player player2;
Player player3;
Player player4;
Player player5;
Player player6;
Player player7;
Player player8;
Player player9;

Enemy enemy1;
Enemy enemy2;
Enemy enemy3;
Enemy enemy4;
Enemy enemy5;
Enemy enemy6;

ILoEnemy enemies1;
ILoEnemy enemies2;
ILoEnemy enemies3;
ILoEnemy enemies4;
ILoEnemy enemies5;
ILoEnemy enemies6;
ILoEnemy enemies7;
ILoEnemy enemies8;

FishWorld f1;
FishWorld f2;
FishWorld f3;
FishWorld f4;
FishWorld f5;
FishWorld f6;

Time t1;
Time t2;
Time t3;
Time t4;

Score s1;
Score s2;
Score s3;

Color higherLevel;
Color lowerLevel;
Color sameLevel;
Color background;

void initData() {
this.player1 = new Player(1, Color.red, 300, 200, 5, "none", 0);
this.player2 = new Player(2, Color.red, 300, 200, 5, "right", 0);
this.player3 = new Player(2, Color.red, 300, 200, 5, "left", 0);
this.player4 = new Player(2, Color.red, 300, 200, 5, "up", 0);
this.player5 = new Player(2, Color.red, 300, 200, 5, "down", 0);

this.player6 = new Player(2, Color.red, -10, 300, 5, "left", 0);
this.player7 = new Player(3, Color.red, 699, 200, 5, "left", 0);
this.player8 = new Player(3, Color.red, 200, 600, 5, "left", 0);
this.player9 = new Player(3, Color.red, 100, -600, 5, "left", 0);


this.enemy1 = new Enemy(1, Color.magenta, 200, 200, 4, "left");
this.enemy2 = new Enemy(1, Color.magenta, 300, 200, 4, "left");
this.enemy3 = new Enemy(3, Color.magenta, 300, 200, 4, "right");
this.enemy4 = new Enemy(3, Color.magenta, 200, 200, 4, "right");

this.enemy5 = new Enemy(3, Color.magenta, -40, 200, 4, "left");
this.enemy6 = new Enemy(3, Color.magenta, 699, 200, 4, "right");

this.enemies1 = new ConsLoEnemy(enemy1, new MtLoEnemy());
this.enemies2 = new ConsLoEnemy(enemy2, enemies1);
this.enemies3 = new ConsLoEnemy(enemy3, enemies1);
// For testing empty case
this.enemies4 = new MtLoEnemy();

this.enemies5 = new ConsLoEnemy(enemy5, 
                      new ConsLoEnemy(enemy2,
                        new ConsLoEnemy(enemy1,
                          new ConsLoEnemy(enemy3,
                            new ConsLoEnemy(enemy4,
                              enemies3)))));
this.enemies6 = new ConsLoEnemy(enemy4, 
                      new ConsLoEnemy(enemy2,
                        new ConsLoEnemy(enemy1,
                          new ConsLoEnemy(enemy3,
                            new ConsLoEnemy(enemy2,
                              enemies5)))));
this.enemies7 = new ConsLoEnemy(enemy5, 
                      new ConsLoEnemy(enemy2,
                        new ConsLoEnemy(enemy1,
                          new ConsLoEnemy(enemy3,
                            enemies6))));
this.enemies8 = new ConsLoEnemy(enemy5, 
                      new ConsLoEnemy(enemy2,
                        new ConsLoEnemy(enemy1,
                          new ConsLoEnemy(enemy3,
                            new ConsLoEnemy(enemy6,
                              enemies7)))));

this.f1 = new FishWorld(this.player1, this.enemies1, this.t1, this.s1);
this.f2 = new FishWorld(this.player2, this.enemies1, this.t1, this.s1);
this.f3 = new FishWorld(this.player2, this.enemies8, this.t3, this.s2);
this.f4 = new FishWorld(this.player4, this.enemies2, this.t3, this.s3);
this.f5 = new FishWorld(this.player2, this.enemies3, this.t3, this.s2);
// For testing empty case
this.f6 = new FishWorld(this.player3, this.enemies4, this.t1, this.s1);


this.t1 = new Time(0, 0, 0, 0);
this.t2 = new Time(0, 0, 0, 17);
this.t3 = new Time(0, 0, 59, 59);
this.t4 = new Time(0, 59, 58, 60);

this.s1 = new Score(0, 0);
this.s2 = new Score(9, 9);
this.s3 = new Score(100, 59);

this.higherLevel = new Color(200, 0, 200);
this.sameLevel = new Color(240, 100, 240);
this.lowerLevel = Color.magenta;
this.background = new Color(185, 245, 255);
}

WorldScene w1 = new WorldScene(600, 400);

void testmakeScene(Tester t) {
  this.initData();
  t.checkExpect(this.f1.makeScene(),
    this.w1.placeImageXY(new RectangleImage(600, 400, "solid", this.background), 300, 200).placeImageXY(
      new EllipseImage(22, 17, "solid", this.sameLevel), 200, 200).placeImageXY(
        new RotateImage(new EquilateralTriangleImage(17, "solid", this.sameLevel), -90), 211, 200).placeImageXY(
          new EllipseImage(22, 17, "solid", Color.red), 300, 200).placeImageXY(
            new RotateImage(new EquilateralTriangleImage(17, "solid", Color.red), -90), 311, 200).placeImageXY(
              new TextImage("Time: 0:0:0", 20, Color.black), 250, 20).placeImageXY(
                new TextImage("Score: 0", 20, Color.black), 400, 20));
}

//Test the method that determines if the player has won or lost the game
void testworldEnds(Tester t) {
	
	WorldImage gameOver = new TextImage("Game Over !", 40, Color.black);
    WorldImage winning = new TextImage("You've won the game!", 40, Color.black);
    
  this.initData();
  t.checkExpect(this.f1.worldEnds(), new WorldEnd(false, this.f1.makeScene()));
  t.checkExpect(this.f2.worldEnds(), new WorldEnd(true, 
    this.w1.placeImageXY(new RectangleImage(600, 400, "solid", this.background), 300, 200).placeImageXY(
      new EllipseImage(22, 17, "solid", Color.magenta), 200, 200).placeImageXY(
        new RotateImage(new EquilateralTriangleImage(17, "solid", Color.magenta), -90), 211, 200).placeImageXY(
          new EllipseImage(26, 20, "solid", Color.red), 300, 200).placeImageXY(
            new RotateImage(new EquilateralTriangleImage(20, "solid", Color.red), 90), 287, 200).placeImageXY(
              new TextImage("Time: 0:0:0", 20, Color.black), 250, 20).placeImageXY(
                new TextImage("Score: 0", 20, Color.black), 400, 20).placeImageXY(
                  winning, 300, 200)));
  
  this.initData();
  t.checkExpect(this.f5.worldEnds(), new WorldEnd(true, 
    this.w1.placeImageXY(new RectangleImage(600, 400, "solid", this.background), 300, 200).placeImageXY(
      new EllipseImage(28, 22, "solid", new Color(106, 13, 173)), 300, 200).placeImageXY(
        new RotateImage(new EquilateralTriangleImage(22, "solid", new Color(106, 13, 173)), 90), 286, 200).placeImageXY(
          new EllipseImage(22, 17, "solid", Color.magenta), 200, 200).placeImageXY(
            new RotateImage(new EquilateralTriangleImage(17, "solid", Color.magenta), -90), 211, 200).placeImageXY(
              new EllipseImage(26, 20, "solid", Color.red), 300, 200).placeImageXY(
                new RotateImage(new EquilateralTriangleImage(20, "solid", Color.red), 90), 287, 200).placeImageXY(
                  new TextImage("Time: 0:0:59", 20, Color.black), 250, 20).placeImageXY(
                    new TextImage("Score: 9", 20, Color.black), 400, 20).placeImageXY(
                      gameOver, 300, 200)));
}

// Test the method that updates the world after every tick of the clock
 void testonTick(Tester t) {
  this.initData();
  this.f1.onTickForTesting();
  t.checkExpect(this.f1.player, this.player1);
  t.checkExpect(this.f1.enemies,
    new ConsLoEnemy(new Enemy(1, Color.magenta, 200, 200, 3, "left"),
      new ConsLoEnemy(new Enemy(1, Color.magenta, 200, 200, 4, "left"), 
        new MtLoEnemy())));
  t.checkExpect(this.f1.t, new Time(0, 0, 0, 0));
  t.checkExpect(this.f1.s, new Score(0, 0));
  
  this.initData();
  this.f2.onTickForTesting();
  t.checkExpect(this.f2.player, 
    new Player(1, Color.red, 300, 200, 5, "right", 1));
  t.checkExpect(this.f2.enemies,
    new ConsLoEnemy(new Enemy(1, Color.magenta, 200, 200, 3, "left"),
      new ConsLoEnemy(new Enemy(1, Color.magenta, 200, 200, 4, "left"), 
        new MtLoEnemy())));
  t.checkExpect(this.f2.t, new Time(0, 0, 0, 0));
  t.checkExpect(this.f2.s, new Score(0, 0));
  
  this.initData();
  this.f4.onTickForTesting();
  t.checkExpect(this.f4.player, 
    new Player(7, Color.red, 300, 200, 5, "up", 1));
  t.checkExpect(this.f4.enemies,
    new ConsLoEnemy(new Enemy(7, Color.magenta, 200, 200, 3, "left"),
      new ConsLoEnemy(new Enemy(1, Color.magenta, 200, 200, 4, "left"), 
        new MtLoEnemy())));
  t.checkExpect(this.f4.t, new Time(0, 1, 0, 59));
  t.checkExpect(this.f4.s, new Score(101, 60));
}

// Test the key event for the user input during gameplay
boolean testonKeyEvent(Tester t) {
  this.initData();
  return t.checkExpect(this.f1.onKeyEvent("h"), this.f1)
  && t.checkExpect(this.f1.onKeyEvent("up"), 
    new FishWorld(new Player(1, Color.red, 300, 200, 5, "up", 0), 
      this.enemies1, this.f1.t, this.f1.s))
  && t.checkExpect(this.f1.onKeyEvent("down"),
    new FishWorld(new Player(1, Color.red, 300, 200, 5, "down", 0), 
      this.enemies1, this.f1.t, this.f1.s))
  && t.checkExpect(this.f1.onKeyEvent("left"),
    new FishWorld(new Player(1, Color.red, 300, 200, 5, "left", 0), 
      this.enemies1, this.f1.t, this.f1.s))
  && t.checkExpect(this.f1.onKeyEvent("right"),
    new FishWorld(new Player(1, Color.red, 300, 200, 5, "right", 0), 
      this.enemies1, this.f1.t, this.f1.s))
  && t.checkExpect(this.f1.onKeyEvent(""),
    new FishWorld(new Player(1, Color.red, 300, 200, 5, "none", 0), 
      this.enemies1, this.f1.t, this.f1.s));
}

// To test the method for drawing the list of enemy fish
boolean testILoEnemyDraw(Tester t) {
  this.initData();
  return t.checkExpect(this.enemies1.draw(w1, player1),
    this.w1.placeImageXY(new EllipseImage(
      22, 17, "solid", this.sameLevel), 200, 200).placeImageXY(
      new RotateImage(new EquilateralTriangleImage(17, "solid", this.sameLevel), -90), 211, 200))
    && t.checkExpect(this.enemies2.draw(w1, player1),
      this.w1.placeImageXY(new EllipseImage(
      22, 17, "solid", this.sameLevel), 300, 200).placeImageXY(
      new RotateImage(new EquilateralTriangleImage(17, "solid", this.sameLevel), -90), 311, 200).placeImageXY(new EllipseImage(
      22, 17, "solid", this.sameLevel), 200, 200).placeImageXY(
      new RotateImage(new EquilateralTriangleImage(17, "solid", this.sameLevel), -90), 211, 200))
    && t.checkExpect(this.enemies4.draw(w1, player1), w1);
}

// To test the method of adding new fish to the list of enemy fish
boolean testILoEnemyAddNew(Tester t) {
  this.initData();
  return t.checkExpect(this.enemies1.addNew(this.enemy2), this.enemies2)
    && t.checkExpect(this.enemies4.addNew(enemy1), this.enemies1);
}

//test the method move for ILoEnemy
boolean testILoEnemyMove(Tester t) {
  this.initData();
  return t.checkExpect(this.enemies2.move(this.player1), 
    new ConsLoEnemy(new Enemy(1, this.enemy2.c, 296, 200, 4, "left"), 
      new ConsLoEnemy(new Enemy(1, this.enemy1.c, 196, 200, 4, "left"), new MtLoEnemy())))
    && t.checkExpect(this.enemies4.move(this.player5), new MtLoEnemy());
}

// test the method that checks if the player has eaten any of the fish
// in a list. If so, that fish will be removed from the list
boolean testILoEnemyeaten(Tester t) {
  this.initData();
  return t.checkExpect(this.enemies1.eaten(this.player1), this.enemies1)
    && t.checkExpect(this.enemies2.eaten(this.player1), this.enemies1)
    && t.checkExpect(this.enemies4.eaten(this.player6), this.enemies4);
}

// test the method that counts the length of the list of enemies
boolean testCount(Tester t) {
  this.initData();
  return t.checkExpect(this.enemies4.count(), 0)
    && t.checkExpect(this.enemies3.count(), 2)
    && t.checkExpect(this.enemies8.count(), 21);
}

// test the method that determines if the game is over
// Game ends when the player is being eaten by a higher level enemy
boolean testgameOver(Tester t) {
  this.initData();
  return t.checkExpect(this.enemies1.gameOver(this.player1), false)
    && t.checkExpect(this.enemies6.gameOver(this.player2), true)
    && t.checkExpect(this.enemies1.gameOver(this.player4), false)
    && t.checkExpect(this.enemies4.gameOver(this.player2), false);
}

// test the method that records the score based on the size and level
// that the fish has been eaten by the player
boolean testeatScore(Tester t) {
  this.initData();
  return t.checkExpect(this.enemies5.eatScore(this.player1), 7)
    && t.checkExpect(this.enemies6.eatScore(this.player3), 12)
    && t.checkExpect(this.enemies1.eatScore(this.player1), 0)
    && t.checkExpect(this.enemies2.eatScore(this.player1), 1)
    && t.checkExpect(this.enemies4.eatScore(this.player2), 0);
}

// Test the method that counts how many fish the player has eaten
boolean testfishEaten(Tester t) {
  this.initData();
  return t.checkExpect(this.enemies5.fishEaten(this.player1), 3)
    && t.checkExpect(this.enemies6.fishEaten(this.player3), 6)
    && t.checkExpect(this.enemies1.fishEaten(this.player1), 0)
    && t.checkExpect(this.enemies2.fishEaten(this.player1), 1)
    && t.checkExpect(this.enemies4.fishEaten(this.player6), 0);
}

// test the method that determines whether the player is the largest fish
boolean testlargest(Tester t) {
  this.initData();
  return t.checkExpect(this.enemies1.largest(player3), true)
    && t.checkExpect(this.enemies3.largest(this.player1), false)
    && t.checkExpect(this.enemies1.largest(this.player1), false)
    && t.checkExpect(this.enemies4.largest(this.player8), true);
}

// test the method for drawing the player
boolean testDrawPlayer(Tester t) {
  this.initData();
  return t.checkExpect(this.player1.draw(this.w1), 
    w1.placeImageXY(new EllipseImage(22, 17, "solid", Color.red), 300, 200).placeImageXY(
      new RotateImage(new EquilateralTriangleImage(17, "solid", Color.red), -90), 311, 200))
  && t.checkExpect(this.player2.draw(this.w1),
    w1.placeImageXY(new EllipseImage(26, 20, "solid", Color.red), 300, 200).placeImageXY(
      new RotateImage(new EquilateralTriangleImage(20, "solid", Color.red), 90), 287, 200))
  && t.checkExpect(this.player3.draw(this.w1),
    w1.placeImageXY(new EllipseImage(26, 20, "solid", Color.red), 300, 200).placeImageXY(
      new RotateImage(new EquilateralTriangleImage(20, "solid", Color.red), -90), 313, 200))
  && t.checkExpect(this.player4.draw(this.w1),
    w1.placeImageXY(new RotateImage(new EllipseImage(26, 20, "solid", Color.red), 90), 300, 200).placeImageXY(
      new EquilateralTriangleImage(20, "solid", Color.red), 300, 213))
  && t.checkExpect(this.player5.draw(this.w1),
    w1.placeImageXY(new RotateImage(new EllipseImage(26, 20, "solid", Color.red), 90), 300, 200).placeImageXY(
      new RotateImage(new EquilateralTriangleImage(20, "solid", Color.red), 180), 300, 187))
  && t.checkExpect(this.player6.draw(w1), 
    this.w1.placeImageXY(new EllipseImage(26, 20, "solid", Color.red), 600, 300).placeImageXY(
      new RotateImage(new EquilateralTriangleImage(20, "solid", Color.red), -90), 613, 300));
}

//test the method move for Player
boolean testPlayerMove(Tester t) {
  this.initData();
  return t.checkExpect(this.player2.move(), 
      new Player(2, Color.red, 305, 200, 5, "right", 0))
    && t.checkExpect(this.player3.move(),
      new Player(2, Color.red, 295, 200, 5, "left", 0))
    && t.checkExpect(this.player4.move(),
      new Player(2, Color.red, 300, 195, 5, "up", 0))
    && t.checkExpect(this.player5.move(), 
      new Player(2, Color.red, 300, 205, 5, "down", 0))
    && t.checkExpect(this .player1.move(), this.player1);
}

// test the method that changes the size of the Player
// based on the number of fish eaten
boolean testevolve(Tester t) {
  this.initData();
  return t.checkExpect(this.player1.evolve(0), this.player1)
    && t.checkExpect(this.player1.evolve(10), new Player(2, Color.red, 300, 200, 5, "none", 0));
}

//test the method draw for Enemy
boolean testEnemyDraw(Tester t) {
  this.initData();
  return t.checkExpect(this.enemy1.draw(this.w1, this.player1),
    new WorldScene(600, 400).placeImageXY(new EllipseImage(
      22, 17, "solid", this.sameLevel), 200, 200).placeImageXY(
    new RotateImage(new EquilateralTriangleImage(17, "solid", this.sameLevel), -90), 211, 200))
  && t.checkExpect(this.enemy2.draw(this.w1, this.player6), 
    new WorldScene(600, 400).placeImageXY(new EllipseImage(
      22, 17, "solid", this.lowerLevel), 300, 200).placeImageXY(
    new RotateImage(new EquilateralTriangleImage(17, "solid", this.lowerLevel), -90), 311, 200))
  && t.checkExpect(this.enemy4.draw(this.w1, this.player4),
    new WorldScene(600, 400).placeImageXY(new EllipseImage(
      28, 22, "solid", this.higherLevel), 200, 200).placeImageXY(
    new RotateImage(new EquilateralTriangleImage(22, "solid", this.higherLevel), 90), 186, 200))
  && t.checkExpect(this.enemy3.draw(this.w1, this.player3),
    new WorldScene(600, 400).placeImageXY(new EllipseImage(
      28, 22, "solid", new Color(106, 13, 173)), 300, 200).placeImageXY(
        new RotateImage(new EquilateralTriangleImage(22, "solid", new Color(106, 13, 173)), 90), 286, 200));
}

//test the method move for Enemy
boolean testmoveEnemy(Tester t) {
  this.initData();
  return t.checkExpect(this.enemy1.move(this.player1), new Enemy(1, this.enemy1.c, 196, 200, 4, "left"))
    && t.checkExpect(this.enemy4.move(this.player2), new Enemy(3, this.enemy4.c, 204, 200, 4, "right"));
}

// test the method to determine if the enemy should chase the player
// when approaching the player within a distance
boolean testapproaching(Tester t) {
  this.initData();
  return t.checkExpect(this.enemy1.approaching(this.player2), false)
    && t.checkExpect(this.enemy3.approaching(this.player1), true)
    && t.checkExpect(this.enemy2.approaching(this.player1), false);
}

// test the method to determine the distance between the enemy and the player
boolean testEnemyeaten(Tester t) {
  this.initData();
  return t.checkExpect(this.enemy1.eaten(player2), false)
    && t.checkExpect(this.enemy2.eaten(player1), true);
}

// test the method for updating time
boolean testUpdate(Tester t) {
  this.initData();
  return t.checkExpect(this.t1.update(), new Time(0, 0, 0, 1))
    && t.checkExpect(this.t2.update(), new Time(0, 0, 1, 18))
    && t.checkExpect(this.t3.update(), new Time(0, 1, 0, 60))
    && t.checkExpect(this.t4.update(), new Time(1, 0, 58, 61));
}

// test the method to draw the time
boolean testTimeDraw(Tester t) {
  this.initData();
  return t.checkExpect(this.t1.draw(this.w1), w1.placeImageXY(
      new TextImage("Time: 0:0:0", 20, Color.black), 250, 20))
    && t.checkExpect(this.t2.draw(this.w1), w1.placeImageXY(
      new TextImage("Time: 0:0:0", 20, Color.black), 250, 20))
    && t.checkExpect(this.t3.draw(this.w1), w1.placeImageXY(
    	    new TextImage("Time: 0:0:59", 20, Color.black), 250, 20));
}

// test the method to update the score
boolean testScoreupdate(Tester t) {
  this.initData();
  return t.checkExpect(this.s1.update(player1, enemies3), new Score(3, 1))
    && t.checkExpect(this.s3.update(player3, enemies2), new Score(101, 60))
    && t.checkExpect(this.s2.update(player1, enemies4), this.s2);
}

// test the method to draw the score
boolean testScoreDraw(Tester t) {
  this.initData();
  return t.checkExpect(this.s1.draw(w1), w1.placeImageXY(
    new TextImage("Score: 0", 20, Color.black), 400, 20))
    && t.checkExpect(this.s2.draw(w1), w1.placeImageXY(
    	    new TextImage("Score: 9", 20, Color.black), 400, 20))
    && t.checkExpect(this.s3.draw(w1), w1.placeImageXY(
    	    new TextImage("Score: 100", 20, Color.black), 400, 20));
}

/*
boolean testBigBang(Tester t) {
    Time time = new Time(0, 0, 0, 0);
    Score score = new Score(0, 0);
    FishWorld world = new FishWorld(player1, new MtLoEnemy(), time, score);
    int worldWidth = 600;
    int worldHeight = 400;
    double tickRate = 0.05;
    return t.checkExpect(world.bigBang(worldWidth, worldHeight, tickRate), true);
  }
  */
}


public class FishGame{
  public static void main(String[] args) {
    Player player = new Player(1, Color.red, 300, 200, 5, "none", 0);
    ILoEnemy enemies = new ConsLoEnemy(new Enemy(1, 
        Color.magenta, 58, 239, 5, "left"), new MtLoEnemy());
    Time t = new Time(0, 0, 0, 0);
    Score s = new Score(0, 0);
    FishWorld world = new FishWorld(player, enemies, t, s);
    int worldWidth = 600;
    int worldHeight = 400;
    double tickRate = 0.05;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }
}