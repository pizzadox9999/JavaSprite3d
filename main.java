import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;
import org.jsfml.graphics.Texture;
import java.nio.file.Paths;
public class main{
  public main(){
    
    //prepare sprite
    Texture back=new Texture();
    Texture front=new Texture();
    try {
      back.loadFromFile(Paths.get("gfx/Cards/cardBack_blue3.png")); 
      front.loadFromFile(Paths.get("gfx/Cards/cardDiamondsA.png"));
      back.setSmooth(true);
      front.setSmooth(true);
    } catch(Exception e) {
      e.printStackTrace();
    } 
    Sprite3d sprite=new Sprite3d(front, back);
    sprite.move(sprite.getGlobalBounds().width/2, sprite.getGlobalBounds().height/2);
    /*sprite.setOrigin(sprite.getGlobalBounds().width/2, sprite.getGlobalBounds().height/2);*/
    sprite.setYaw(120);
    
    RenderWindow window=new RenderWindow();
    window.create(new VideoMode(800, 600), "test");
    window.setFramerateLimit(60);
    
    while (window.isOpen()) { 
      window.clear();
      window.draw(sprite);
      window.display();
      
      
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
