import java.nio.file.Paths;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector3f;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;
import org.jsfml.window.VideoMode;
//////////////////////////////////////////////////////////////////////////////////////////////
//
//  Sprite3d EXAMPLE
//
//  by Hapax (http://github.com/Hapaxia)
//
//
//    Key controls:
//
//  Escape          Quit
//  Space           Pause/unpause
//  Backspace       Reset clock
//  - =             Adjust depth
//  [ ]             Adjust mesh density
//  , .             Adjust subdivision level
//  Tab             Toggle textures
//  Return          Toggle Dynamic Subdivision (default range is 1 to 3)
//  B               Toggle back face correction (flip)
//  F8              Resets mesh to minimal (two triangles) and turns off Dynamic Subdivision
//  F9              Toggle visibility of Sprite3d's bounds
//  F10             Toggle visibility of standard sprite's bounds
//  F11             Toggle visibility of Sprite3d
//  F12             Toggle visibility of standard sprite
//
//  Please note that this example makes use of C++11 features
//
//////////////////////////////////////////////////////////////////////////////////////////////
class visible{
  public boolean sprite3d, sprite, sprite3dBounds, spriteBounds;
  public visible(boolean b1, boolean b2, boolean b3, boolean b4){
    sprite3d=b1;
    sprite=b2;
    sprite3dBounds=b3;
    spriteBounds=b4;
  }
}
public class main{
  
  public static void main(String[] args){
    // set up window
    RenderWindow window=new RenderWindow();
    window.create(new VideoMode(800, 600), "Sprite3d");
    window.setVerticalSyncEnabled(true);
    
    // load resources (textures and font)
    Texture texture=new Texture(), frontTexture=new Texture(), backTexture=new Texture();
    try {
      texture.loadFromFile(Paths.get("resources/Card Face - SFML.png"));
      frontTexture.loadFromFile(Paths.get("resources/Card Face - SFML.png"));
      backTexture.loadFromFile(Paths.get("resources/Card Back - SFML.png"));
    } catch(Exception e) {
      e.printStackTrace();
    }
    
    
    Font font=new Font();
    try {
      font.loadFromFile(Paths.get("resources/arial.ttf"));
    } catch(Exception e) {
      e.printStackTrace();
    } 
    
    // text headers
    Text sprite3dText=new Text("Sprite3d", font, 48);
    Text spriteText=new Text("Sprite", font, 48);
    sprite3dText.setOrigin(sprite3dText.getLocalBounds().left + sprite3dText.getLocalBounds().width / 2.f, 0.f);
    spriteText.setOrigin(spriteText.getLocalBounds().left + spriteText.getLocalBounds().width / 2.f, 0.f);
    sprite3dText.setPosition(new Vector2f(window.getSize().x * 0.25f, 0.f));
    spriteText.setPosition(new Vector2f(window.getSize().x * 0.75f, 0.f));
    
    // text feedback
    Text feedbackText=new Text("FPS:\nVertices:\nSubdivision Level:\nSubdivided Mesh Density:\nMesh Density:\nDynamic Subdivision enabled:\nMost Extreme Angle:\nDepth:", font, 16);
    feedbackText.setOrigin(feedbackText.getLocalBounds().left, feedbackText.getLocalBounds().top + feedbackText.getLocalBounds().height);
    feedbackText.setPosition(new Vector2f(2.f, window.getSize().y - 1.f));
    
    // sprite3d
    Sprite3d sprite3d=new Sprite3d(frontTexture, backTexture);
    sprite3d.setOrigin(new Vector2f(sprite3d.getLocalBounds().width/ 2.f, sprite3d.getLocalBounds().height/ 2.f) );
    sprite3d.setPosition(new Vector2f(window.getSize().x * 0.25f, window.getSize().y / 2.f));
    sprite3d.setBackFlipEnabled(true);
    //sprite3d.setMeshDensity(3); // = 5x5 points = 4x4 quads
    //sprite3d.setNumberOfPoints(25); // 5x5
    //sprite3d.setNumberOfQuads(16); // 4x4
    //sprite3d.setDynamicSubdivisionRange(3, 0); // max: 32x32 quads (4 subdivided 3 times is 32), min: 4x4 quads (zero subdivisions)
    
    // sprite
    //sf::Sprite sprite(sprite3d.getSprite());
    Sprite sprite=new Sprite(texture);
    sprite.setOrigin(new Vector2f(sprite.getLocalBounds().width / 2.f, sprite.getLocalBounds().height / 2.f));
    sprite.setPosition(new Vector2f(window.getSize().x * 0.75f, window.getSize().y / 2.f));
    
    // bounds rectangles
    RectangleShape boundsSprite3d=new RectangleShape();
    boundsSprite3d.setFillColor(Color.TRANSPARENT);
    boundsSprite3d.setOutlineColor(Color.RED);
    boundsSprite3d.setOutlineThickness(1.f);
    RectangleShape boundsSprite = new RectangleShape();//boundsSprite3d;
    boundsSprite.setFillColor(Color.TRANSPARENT);
    boundsSprite.setOutlineColor(Color.RED);
    boundsSprite.setOutlineThickness(1.f);
    // clock and pause
    Clock clock=new Clock();
    boolean isPaused=true;
    float time=0f;
    
    // visibility flags
    visible isVisible=new visible(true, true, false, false);
    
    while (window.isOpen()){
      for (Event event : window.pollEvents()) {
        switch (event.type) {
          case  CLOSED: 
            window.close();
            break;
          case KEY_PRESSED:
            KeyEvent keyEvent=event.asKeyEvent();
            switch (keyEvent.key) {
              case  ESCAPE: 
                window.close();
                break;
              case SPACE:
                isPaused = !isPaused;
                clock.restart();
                break;
              case BACKSPACE:
                time = 0.f;
                clock.restart();
                break;
              case NUM0:
                sprite3d.setDepth(sprite3d.getDepth() + 1.f);
                break;
              case DASH:
                sprite3d.setDepth(sprite3d.getDepth() - 1.f);
              case NUM9:
                sprite3d.setMeshDensity(sprite3d.getMeshDensity() + 1);
                break;
              case NUM8:
                if (sprite3d.getMeshDensity() > 0)
                  sprite3d.setMeshDensity(sprite3d.getMeshDensity() - 1);
                break;
              case PERIOD:
                sprite3d.setSubdivision(sprite3d.getSubdivision() + 1);
                break;
              case COMMA:
                if (sprite3d.getSubdivision() > 0)
                  sprite3d.setSubdivision(sprite3d.getSubdivision() - 1);
                break;
              case RETURN:
                sprite3d.setDynamicSubdivisionEnabled(!sprite3d.getDynamicSubdivisionEnabled());
                break;
              case B:
                sprite3d.setBackFlipEnabled(!sprite3d.getBackFlipEnabled());
                break;
              case F9:
                isVisible.sprite3dBounds = !isVisible.sprite3dBounds;
                break;
              case F10:
                isVisible.spriteBounds = !isVisible.spriteBounds;
                break;    
              case F11:
                isVisible.sprite3d = !isVisible.sprite3d;
                break;
              case F12:
                isVisible.sprite = !isVisible.sprite;
                break;   
              case F8:
                sprite3d.setDynamicSubdivisionEnabled(false);
                sprite3d.minimalMesh();
                break;  
              case TAB:
                if (sprite3d.getTexture() != frontTexture){
                  sprite3d.setTexture(frontTexture);
                  sprite3d.setBackTexture(backTexture);
                }else{
                  sprite3d.setTexture(new Texture());
                  sprite3d.setBackTexture(new Texture());
                }
                break;
            } 
            break;
        } 
      }
      // update time
      float frameTime = 1.f;
      if (!isPaused){
        frameTime = clock.restart().asSeconds();
        time += frameTime;
      }
      // update sprites
      //sprite3d.setRotation(new Vector3f( time * 97.f, time * 42.f, time * 51.f ));
      //sprite.setScale({ cos(time * 97.f * 3.141592653f / 180.f), cos(time * 42.f * 3.141592653f / 180.f) });
      sprite.setScale(new Vector2f((float)Math.cos(time * 42 * 3.141592653f / 180.f), (float)Math.cos(time * 97 * 3.141592653f / 180.f) ));
      sprite.setRotation(time * 51.f);
      
      // other sprite3d rotations for testing
      //sprite3d.setRotation(time * 60.f);
      
      sprite3d.setPitch(time * 60.f);
      sprite3d.setYaw(time * 40.f);
      
      //sprite3d.setYaw(time * 180.f);
      
      // update bounds rectangle
      boundsSprite3d.setPosition(new Vector2f(sprite3d.getGlobalBounds().left, sprite3d.getGlobalBounds().top));
      boundsSprite3d.setSize(new Vector2f(sprite3d.getGlobalBounds().width, sprite3d.getGlobalBounds().height));
      boundsSprite.setPosition(new Vector2f(sprite.getGlobalBounds().left, sprite.getGlobalBounds().top));
      boundsSprite.setSize(new Vector2f(sprite.getGlobalBounds().width, sprite.getGlobalBounds().height));
      // update depth text
      int subdividedMeshDensity = sprite3d.getMeshDensity();
      for (int i = 0; i < sprite3d.getSubdivision(); ++i)
        subdividedMeshDensity = subdividedMeshDensity * 2 + 1;
      int numberOfVerticesInSubdividedMesh = (subdividedMeshDensity * 2 + 5) * subdividedMeshDensity + 4;
      feedbackText.setString(
      "FPS: " + (1f / frameTime) +
      "\nVertices: " + numberOfVerticesInSubdividedMesh + " (" + (numberOfVerticesInSubdividedMesh - 2) + " triangles)" +
      "\nSubdivision Level: " + sprite3d.getSubdivision() +
      "\nSubdivided Mesh Density: " + sprite3d.getSubdividedMeshDensity() + " (" + (sprite3d.getSubdividedMeshDensity() + 2) + "x" + (sprite3d.getSubdividedMeshDensity() + 2) + " = " + ((sprite3d.getSubdividedMeshDensity() + 2) * (sprite3d.getSubdividedMeshDensity() + 2)) + " points)" +
      "\nMesh Density: " + (sprite3d.getMeshDensity()) + " (" + (sprite3d.getMeshDensity() + 2) + "x" + (sprite3d.getMeshDensity() + 2) + " = " + ((sprite3d.getMeshDensity() + 2) * (sprite3d.getMeshDensity() + 2)) + " points)" +
      "\nDynamic Subdivision enabled: " + (sprite3d.getDynamicSubdivisionEnabled() ? "true" : "false") +
      "\nMost Extreme Angle: " + (sprite3d.getMostExtremeAngle()) +
      "\nDepth: " + (sprite3d.getDepth()));
      // update display
      window.clear(new Color(64, 64, 64));
      if (isVisible.sprite)
        window.draw(sprite);
      if (isVisible.sprite3d)
        window.draw(sprite3d);
      if (isVisible.spriteBounds)
        window.draw(boundsSprite);
      if (isVisible.sprite3dBounds)
        window.draw(boundsSprite3d);
        
      window.draw(spriteText);
      window.draw(sprite3dText);
      window.draw(feedbackText);
      window.display();
    }
  }
}








//this little demo by pizza_dox_9999
/*import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;
import org.jsfml.graphics.Texture;
import java.nio.file.Paths;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.Color;
import org.jsfml.window.event.KeyEvent;
public class main{
  public main(){
    /*float pitch=sprite.getPitch();
      float yaw=sprite.getYaw();
      float roll=sprite.getRoll();
      sprite=new Sprite3d(front, back);
      //sprite.setOrigin(sprite.getGlobalBounds().width/2, sprite.getGlobalBounds().height/2); // this line destroys it
      sprite.move(150, 150);
      //sprite.setPitch(pitch+5);
      sprite.setYaw(yaw-1);
      //sprite.setRoll(roll+2);*/ 
    /*
    
    
    
    
    
    //prepare sprite
    Texture back=new Texture();
    Texture front=new Texture();
    try {
      back.loadFromFile(Paths.get("resources/Card Face - SFML.png")); 
      front.loadFromFile(Paths.get("resources/Card Back - SFML.png"));
      back.setSmooth(true);
      front.setSmooth(true);
    } catch(Exception e) {
      e.printStackTrace();
    } 
    Sprite3d sprite=new Sprite3d(front, back);
    sprite.move(sprite.getGlobalBounds().width/2, sprite.getGlobalBounds().height/2);
    
    sprite.setOrigin(sprite.getGlobalBounds().width/2, sprite.getGlobalBounds().height/2); 
    sprite.setYaw(30);
    sprite.setPitch(20);
    
    RenderWindow window=new RenderWindow();
    window.create(new VideoMode(800, 600), "test");
    window.setFramerateLimit(60);
    
    while (window.isOpen()) { 
      window.clear();
      window.draw(sprite);
      window.display();
      sprite.setYaw(sprite.getYaw()+1);
      sprite.setRoll(sprite.getRoll()+1);
      System.out.println(sprite.getRoll());
      //sprite.move(1, 1);
     
      //sprite.setOrigin(sprite.getGlobalBounds().width/2, sprite.getGlobalBounds().height/2);
      //sprite.setPosition(200, 200);
      
      //sprite.setYaw(sprite.getYaw()+1);
      
      for (Event event : window.pollEvents()) {
        switch (event.type) {
          case  CLOSED: 
            window.close();
            break;  
        } 
      } 
    } 
  }
  public static void main(String[] args) {
    new main();
  }
}

  */
