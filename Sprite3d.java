import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector2f;
import java.util.ArrayList;
import org.jsfml.system.Vector3f;
import org.jsfml.graphics.Vertex;
import java.util.Collections;
import org.jsfml.graphics.IntRect;
import java.util.Vector;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.Transformable;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderStates;
public class Sprite3d extends Sprite implements Drawable, Transformable{
  private float m_depthToShallownessConversionNumerator;
  private float m_pitch;
  private float m_yaw;
  private float m_depth;
  private float m_shallowness;
  private float m_meshDensity;
  private boolean m_flipBack;
  private Texture m_pTexture;
  private Texture m_pBackTexture;
  private Vector2i m_textureOffset;
  private Vector2i m_backTextureOffset;
  private Vector2i m_size;
  private boolean m_useDynamicSubdivision;
  private int m_minSubdivision;
  private int m_maxSubdivision;
  private int m_subdivision;
  private int m_subdividedMeshDensity;
  private ArrayList<Vector3f> m_points;
  private ArrayList<Vector2f> m_transformedPoints;
  private Vector3f m_origin;
  private ArrayList<Vertex> m_vertices;
  private boolean m_isBackFacing;
  private ArrayList<Float> m_compactTransformMatrix;
  private Vector2f m_topLeft;
  private Vector2f m_topRight;
  private Vector2f m_bottomLeft;
  private Vector2f m_bottomRight;
  public Sprite3d(){
    super();
    m_depthToShallownessConversionNumerator=10000f;
    m_pitch=0f;
    m_yaw=0f;
    m_depth=10f;
    m_shallowness=m_depthToShallownessConversionNumerator/m_depth;
    m_meshDensity=0f;
    m_flipBack=false;
    m_pTexture=null;
    m_pBackTexture=null;
    m_textureOffset=new Vector2i(0, 0);
    m_backTextureOffset=new Vector2i(0, 0);
    m_size=new Vector2i(0, 0);
    m_useDynamicSubdivision=false;
    m_minSubdivision=1;
    m_maxSubdivision=4;
    m_subdivision=0;
    m_subdividedMeshDensity=0;
    m_points=new ArrayList<Vector3f>(4);    
    m_transformedPoints=new ArrayList<Vector2f>(4);
    m_origin=new Vector3f(0, 0, 0);
    m_vertices=new ArrayList<Vertex>(4);
    m_isBackFacing=false;
    m_compactTransformMatrix=new ArrayList<Float>(5);Collections.fill(m_compactTransformMatrix, new Float(0f));
    m_topLeft=new Vector2f(0, 0);
    m_topRight=new Vector2f(0, 0);
    m_bottomLeft=new Vector2f(0, 0);
    m_bottomRight=new Vector2f(0, 0);
  }
  public Sprite3d(Texture texture){
    this();
    setTexture(texture);
  }
  public Sprite3d(Texture texture, IntRect textureRect){
    this();
    setTexture(texture);
    setTextureRect(textureRect);
  }
  public Sprite3d(Texture texture, Texture backTexture){
    this();
    setTexture(texture);
    setBackTexture(backTexture);
  }

  public Sprite3d(Texture texture, IntRect textureRect, Texture backTexture, Vector2i backTextureOffset){
    this();
    setTexture(texture);
    setTextureRect(textureRect);
    setBackTexture(backTexture);
    setBackTextureOffset(backTextureOffset);
  }

  public Sprite3d(Sprite sprite){
    this();
    setTexture(sprite.getTexture());
    setTextureRect(sprite.getTextureRect());
    this.setColor(sprite.getColor());
    this.setOrigin(sprite.getOrigin());
    this.setPosition(sprite.getPosition());
    this.setRotation(sprite.getRotation());
    this.setScale(sprite.getScale());
  }  
  public Sprite getSprite(){
    IntRect textureRec=new IntRect(m_textureOffset, m_size);
    Sprite sprite=new Sprite(m_pTexture, this.getTextureRect());
    sprite.setColor(this.getColor());
    sprite.setOrigin(this.getOrigin());
    sprite.setPosition(this.getPosition());
    sprite.setRotation(this.getRotation());
    sprite.setScale(this.getScale());
    return sprite;
  }  

  public void setTextureRect(IntRect textureRectangle){
    m_textureOffset = new Vector2i(textureRectangle.left, textureRectangle.top);
    m_backTextureOffset = m_textureOffset;
    m_size = new Vector2i(textureRectangle.width, textureRectangle.height);
    createPointGrid();
    updateTransformedPoints();
    updateVertices();
    updateGlobalCorners();
  }
  public void setTexture(Texture texture){
    setTexture(texture, false, false);
  }
  public void setTexture(Texture texture, boolean resetRect, boolean resetBackOffset){
    if (m_pTexture == null || resetRect){
      m_textureOffset = new Vector2i(0, 0);
      Vector2i tempTextureSize=texture.getSize();
      m_size = new Vector2i((int)tempTextureSize.x, (int)tempTextureSize.y);
      createPointGrid();
      m_vertices=resize(m_vertices, getNumberOfVerticesNeededForCurrentSubdividedMeshDensity());//this.m_vertices.resize(this.getNumberOfVerticesNeededForCurrentSubdividedMeshDensity());
    }
    if (resetBackOffset){
      m_backTextureOffset = new Vector2i(0, 0);
    }
    m_pTexture = texture;
  } 
  private ArrayList<Vector2f> resizeVector2f(ArrayList<Vector2f> list, int resizeTo){
    ArrayList<Vector2f> returnList=new ArrayList<Vector2f>();
    for (int i=0; i<resizeTo; i++) {
      try {
        returnList.add(list.get(i));
      } catch(Exception e) {
        System.out.println("NOTIFY: indexoutofbounds: i: "+i);
      } finally {
        returnList.add(new Vector2f(0, 0));  
      }
    } 
    return returnList;
  }  
  private ArrayList<Vector3f> resizeVector3f(ArrayList<Vector3f> list, int resizeTo){
    ArrayList<Vector3f> returnList=new ArrayList<Vector3f>();
    for (int i=0; i<resizeTo; i++) {
      try {
        returnList.add(list.get(i));
      } catch(Exception e) {
        System.out.println("NOTIFY: indexoutofbounds: i: "+i);
      } finally {
        returnList.add(new Vector3f(0, 0, 0));  
      }
    } 
    return returnList;
  }
  private ArrayList<Vertex> resize(ArrayList<Vertex> list, int resizeTo){
    ArrayList<Vertex> returnList=new ArrayList<Vertex>();
    for (int i=0; i<resizeTo; i++) {
      try {
        returnList.add(list.get(i));
      } catch(Exception e) {
        System.out.println("NOTIFY: indexoutofbounds: i: "+i);
      } finally {
        returnList.add(new Vertex(new Vector2f(0, 0)));  
      }
    } 
    return returnList;
  } 
  public void setBackTexture(Texture t){
    this.setBackTexture(t, false);
  }
  public void setBackTexture(Texture texture, boolean resetOffset){
    m_pBackTexture = texture;
    if (m_pBackTexture == null || resetOffset)
      m_backTextureOffset = new Vector2i(0, 0);
  }
  public void setBackFlipEnabled(boolean flipBack){
    m_flipBack = flipBack;
  }
  public Texture getTexture() {
    return m_pTexture;
  }
  public Texture getBackTexture(){
    return m_pBackTexture;
  }
  public boolean getBackFlipEnabled(){
    return m_flipBack;
  }
  public Vector2i getTextureOffset(){
    return m_textureOffset;
  }
  public void setTextureOffset(Vector2i textureOffset){
    m_textureOffset = textureOffset;
  }  
  public Vector2i getBackTextureOffset(){
    return m_backTextureOffset;
  }
  public void setBackTextureOffset(Vector2i backTextureOffset){
    m_backTextureOffset = backTextureOffset;
  }
  public void setColor(Color color){
    for (int i=0; i<m_vertices.size(); i++) {
      Vertex v=m_vertices.get(i);  //Vertex(Vector2f position, Color color, Vector2f texCoords) 
      m_vertices.set(i, new Vertex(v.position, color, v.texCoords));
    } 
  }
  public Color getColor(){
    return m_vertices.get(0).color;
  }
  public float getPitch(){
    return m_pitch;
  }
  public float getYaw(){
    return m_yaw;
  }
  public float getRoll(){
    return this.getRotation();
  }
  public Vector3f getRotation3d(){
    return new Vector3f(m_pitch, m_yaw, this.getRotation());
  }  
  public void setPitch(float pitch){
    m_pitch = pitch;
    while (m_pitch > 180.f)
      m_pitch -= 360.f;
    while (m_pitch < -180.f)
      m_pitch += 360.f;
  }
  public void setYaw(float yaw){
    m_yaw = yaw;
    while (m_yaw > 180.f)
      m_yaw -= 360.f;
    while (m_yaw < -180.f)
      m_yaw += 360.f;
  }
  public void setRoll(float roll){
    this.setRotation(roll);
  }
  /*public void setRotation(float rotation){
    setRoll(rotation);
  }*/
  public void setRotation(Vector3f rotation){
    setRotation3d(rotation);
  }
  public void setRotation3d(Vector3f rotation){
    setPitch(rotation.x);
    setYaw(rotation.y);
    setRoll(rotation.z);
  }
  public float getMostExtremeAngle() {
    float pitch = Math.abs(m_pitch);
    if (pitch > 90.f)
      pitch = 180.f - pitch;
    float yaw = (float)Math.abs(m_yaw);
    if (yaw > 90.f)
      yaw = 180.f - yaw;
    return (float)Math.max(pitch, yaw);
  }
  public void setMeshDensity(int meshDensity){
    m_meshDensity = meshDensity;
    setSubdivision(m_subdivision);
  }
  public int getMeshDensity(){
    return (int)m_meshDensity;
  }
  int getSubdividedMeshDensity(){
    return m_subdividedMeshDensity;
  }
  public void reserveMeshDensity(int meshDensity){
    int numberOfPointsPerDimension = meshDensity + 2;
    m_points.ensureCapacity(numberOfPointsPerDimension * numberOfPointsPerDimension);
    m_transformedPoints.ensureCapacity(m_points.size());
    int currentSubdividedMeshDensity = m_subdividedMeshDensity;
    m_subdividedMeshDensity = meshDensity;
    m_vertices.ensureCapacity(getNumberOfVerticesNeededForCurrentSubdividedMeshDensity());
    m_subdividedMeshDensity = currentSubdividedMeshDensity;
  } 
  public void setDynamicSubdivisionEnabled(boolean enabled){
    m_useDynamicSubdivision = enabled;
  }
  
  public void setDynamicSubdivisionRange(int maximum, int minimum){
    if (maximum < minimum){
      int temp;
      temp = maximum;
      maximum = minimum;
      minimum = temp;
    }
    m_maxSubdivision = maximum;
    m_minSubdivision = minimum;
    reserveMeshDensity(m_maxSubdivision);
  }
  public boolean getDynamicSubdivisionEnabled(){
    return m_useDynamicSubdivision;
  }
  public void setSubdivision(int subdivision){
    m_subdivision = subdivision;
    m_subdividedMeshDensity = (int)m_meshDensity;
    for (int i = 0; i < m_subdivision; ++i)
      m_subdividedMeshDensity = m_subdividedMeshDensity * 2 + 1;
    createPointGrid();
    m_vertices=resize(m_vertices, getNumberOfVerticesNeededForCurrentSubdividedMeshDensity());//m_vertices.resize(getNumberOfVerticesNeededForCurrentSubdividedMeshDensity());
  }
  public int getSubdivision(){
    return m_subdivision;
  }
  public void setNumberOfPoints(int numberOfPoints){
    int root = (int)(Math.sqrt(numberOfPoints));
    if (root > 2)
      setMeshDensity(root - 2);
    else
      setMeshDensity(0);
  }
  
  public void setNumberOfQuads(int numberOfQuads){
    int root = (int)(Math.sqrt(numberOfQuads));
    if (root > 1)
      setMeshDensity(root - 1);
    else
      setMeshDensity(0);
  }
  
  public void minimalMesh(){
    m_meshDensity = 0;
    setSubdivision(0);
  }
  
  public FloatRect getLocalBounds(){
    return new FloatRect(new Vector2f(0.f, 0.f), new Vector2f(abs(m_size)));
  }
  
  public FloatRect getGlobalBounds(){
    updateTransformedPoints();
    updateGlobalCorners();
    float minX = min(m_topLeft.x, min(m_topRight.x, min(m_bottomLeft.x, m_bottomRight.x)));
    float maxX = max(m_topLeft.x, max(m_topRight.x, max(m_bottomLeft.x, m_bottomRight.x)));
    float minY = min(m_topLeft.y, min(m_topRight.y, min(m_bottomLeft.y, m_bottomRight.y)));
    float maxY = max(m_topLeft.y, max(m_topRight.y, max(m_bottomLeft.y, m_bottomRight.y)));
    return new FloatRect(new Vector2f(minX, minY), new Vector2f(maxX - minX, maxY - minY));
  }
  
  public void setDepth(float depth){
    m_depth = depth;
    m_shallowness = m_depthToShallownessConversionNumerator / ((m_depth > -0.000001f && m_depth < 0.000001f) ? 0.000001f : m_depth); // avoid division by zero here but don't change m_depth from being zero
  }
  
  public float getDepth(){
    return m_depth;
  }
  
  public void draw(RenderTarget target, RenderStates states){
    if (m_pTexture != null){
      updateTransformedPoints();
      updateVertices();
      states=new RenderStates(states.blendMode, this.getTransform(), states.texture, states.shader);//RenderStates(BlendMode blendMode, Transform transform, ConstTexture texture, ConstShader shader) 
      //states.transform = this.getTransform();
      
      if (m_isBackFacing && m_pBackTexture != null)
        //states.texture = m_pBackTexture;
        states=new RenderStates(states.blendMode, states.transform, m_pBackTexture, states.shader);//RenderStates(BlendMode blendMode, Transform transform, ConstTexture texture, ConstShader shader) 
      else
        //states.texture = m_pTexture;
        states=new RenderStates(states.blendMode, states.transform, m_pTexture, states.shader);//RenderStates(BlendMode blendMode, Transform transform, ConstTexture texture, ConstShader shader) 
      
      //java doesnt need apperently the size target.draw(m_vertices.get(0), m_vertices.size(), PrimitiveType.TRIANGLE_STRIP, states);
      //convert m_vertices to an array
      
      Vertex[] array_m_vertices=new Vertex[m_vertices.size()];
      array_m_vertices=m_vertices.toArray(array_m_vertices);
      
      target.draw(array_m_vertices, PrimitiveType.TRIANGLE_STRIP, states);
      
    }
  }
  
  public void updateTransformedPoints(){
    if (m_useDynamicSubdivision)
      setSubdivision((int)((m_maxSubdivision - m_minSubdivision) * getMostExtremeAngle() / 90.f + m_minSubdivision));
    
    m_origin = new Vector3f(getOrigin().x, getOrigin().y, 0.f);
    float radiansFromDegreesMultiplier = 0.0174532925f; // pi / 180;
    float pitchInRadians = m_pitch * radiansFromDegreesMultiplier;
    float yawInRadians = m_yaw * radiansFromDegreesMultiplier;
    
    float cosPitch = (float)Math.cos(pitchInRadians);
    float sinPitch = (float)Math.sin(pitchInRadians);
    float cosYaw = (float)Math.cos(yawInRadians);
    float sinYaw = (float)Math.sin(yawInRadians);
    
    /*******************************************************
    *          Pitch and Yaw combined matrix               *
    *                                                      *
    *  cosYaw,  sinPitch * sinYaw, -cosPitch * sinYaw, 0,  *
    *  0,       cosPitch,           sinPitch,          0,  *
    *  sinYaw, -sinPitch * cosYaw,  cosPitch * cosYaw, 0,  *
    *  0,       0,                  0,                 1   *
    *******************************************************/
    
    //m_compactTransformMatrix = { cosYaw, sinYaw, sinPitch * sinYaw, cosPitch, -sinPitch * cosYaw }; // only the five used elements
    //put it in the arrayllist
    m_compactTransformMatrix.add(cosYaw);
    m_compactTransformMatrix.add(sinYaw);
    m_compactTransformMatrix.add(sinPitch * sinYaw);
    m_compactTransformMatrix.add(cosPitch);
    m_compactTransformMatrix.add(-sinPitch * cosYaw );
    
    for (int v = 0; v < m_points.size(); ++v){
      Vector3f point = m_points.get(v);
      
      //point -= m_origin;
      point=new Vector3f(
      point.x-m_origin.x,
      point.y-m_origin.y,
      point.z-m_origin.z);
      
      point = new Vector3f(
      m_compactTransformMatrix.get(0) * point.x + m_compactTransformMatrix.get(2) * point.y,
      m_compactTransformMatrix.get(3) * point.y,
      m_compactTransformMatrix.get(1) * point.x + m_compactTransformMatrix.get(4) * point.y
      ); // apply rotations
      float depth=m_shallowness / (m_shallowness + point.z); // apply depth
      point=new Vector3f(//point *= m_shallowness / (m_shallowness + point.z); // apply depth
      point.x*depth,
      point.y*depth,
      point.z*depth
      );
      point=new Vector3f( //point += m_origin;
      point.x+m_origin.x,
      point.y+m_origin.y,
      point.z+m_origin.z
      );
      
      
      m_transformedPoints.set(v, new Vector2f(point.x, point.y));
    }
    
    updateGlobalCorners();
    
    m_isBackFacing = false;
    if (m_pitch < -90.f || m_pitch > 90.f)
      m_isBackFacing = true;
    if (m_yaw < -90.f || m_yaw > 90.f)
      m_isBackFacing = !m_isBackFacing;
  }
  
  void updateVertices() {
    Vector2i currentTextureOffset = m_textureOffset;
    if (m_isBackFacing)
      currentTextureOffset = m_backTextureOffset;
    
    // create a mesh (triangle strip) from the points
    for (int v = 0; v < m_vertices.size(); ++v){
      int pointIndex = getPointIndexForVertexIndex(v, false);
      int texturePointIndex = getPointIndexForVertexIndex(v, m_isBackFacing && m_flipBack);
      
      // update vertex
      m_vertices.set(v, new Vertex(m_transformedPoints.get(pointIndex), m_vertices.get(v).color, new Vector2f((m_points.get(texturePointIndex).x * (m_size.x < 0 ? -1 : 1)) + currentTextureOffset.x, (m_points.get(texturePointIndex).y * (m_size.y < 0 ? -1 : 1)) + currentTextureOffset.y)));//Vertex(Vector2f position, Color color, Vector2f texCoords) 
      
      /*m_vertices.get(v).position = m_transformedPoints.get(pointIndex);
      m_vertices.get(v).texCoords.x = (m_points.get(texturePointIndex).x * (m_size.x < 0 ? -1 : 1)) + currentTextureOffset.x;
      m_vertices.get(v).texCoords.y = (m_points.get(texturePointIndex).y * (m_size.y < 0 ? -1 : 1)) + currentTextureOffset.y; */
      //t1
    }
  }
  
  public void updateGlobalCorners(){
    m_topLeft = this.getTransform().transformPoint(m_transformedPoints.get(0));
    Vector2f vec_m_transformedPoints=m_transformedPoints.get(0);
    
    //m_topRight = this.getTransform().transformPoint((m_transformedPoints.get(0) + m_subdividedMeshDensity + 1));
    m_topRight = this.getTransform().transformPoint(new Vector2f(
    vec_m_transformedPoints.x+m_subdividedMeshDensity+1,
    vec_m_transformedPoints.y+m_subdividedMeshDensity+1
    )   );
    
    vec_m_transformedPoints=m_transformedPoints.get(m_transformedPoints.size()-1);
    
    //m_bottomLeft = this.getTransform().transformPoint((m_transformedPoints.get(m_transformedPoints.size()-1) - m_subdividedMeshDensity - 2)); // end() - (m_subdividedMeshDensity + 1) - 1
    m_bottomLeft = this.getTransform().transformPoint(new Vector2f(
    vec_m_transformedPoints.x-m_subdividedMeshDensity-2,
    vec_m_transformedPoints.y-m_subdividedMeshDensity-2
    )); // end() - (m_subdividedMeshDensity + 1) - 1
    m_bottomRight = this.getTransform().transformPoint(m_transformedPoints.get(m_transformedPoints.size()-1));
  }
  
  public void createPointGrid(){
    Vector2f leftTop=new Vector2f(0.f, 0.f);
    Vector2f rightBottom=new Vector2f(abs(m_size));
    
    int numberOfPointsPerDimension = m_subdividedMeshDensity + 2;
    
    // create a grid of points
    m_points=resizeVector3f(m_points, numberOfPointsPerDimension * numberOfPointsPerDimension);//m_points.resize(numberOfPointsPerDimension * numberOfPointsPerDimension);
    for (int y = 0; y < numberOfPointsPerDimension; ++y){
      for (int x = 0; x < numberOfPointsPerDimension; ++x){
        m_points.set(y * numberOfPointsPerDimension + x, new Vector3f(
        linearInterpolation(leftTop.x, rightBottom.x, (float)(x) / (numberOfPointsPerDimension - 1)),
        linearInterpolation(leftTop.y, rightBottom.y, (float)(y) / (numberOfPointsPerDimension - 1)),
        0.f
        ));
        
        /*m_points.get(y * numberOfPointsPerDimension + x).x = linearInterpolation(leftTop.x, rightBottom.x, (float)(x) / (numberOfPointsPerDimension - 1));
        m_points.get(y * numberOfPointsPerDimension + x).y = linearInterpolation(leftTop.y, rightBottom.y, (float)(y) / (numberOfPointsPerDimension - 1));
        m_points.get(y * numberOfPointsPerDimension + x).z = 0.f;*/
      }
    }
    
    m_transformedPoints=resizeVector2f(m_transformedPoints, m_points.size());//m_transformedPoints.resize(m_points.size());
  }
  
  public int getPointIndexForVertexIndex(int vertexIndex, boolean invertPointX){
    int numberOfPointsPerDimension = m_subdividedMeshDensity + 2;
    int numberOfVerticesPerRow = numberOfPointsPerDimension * 2 - 1;
    
    boolean isOddRow = ((vertexIndex / numberOfVerticesPerRow) % 2) == 1;
    int pointX = (vertexIndex % numberOfVerticesPerRow) / 2;
    if (isOddRow)
      pointX = numberOfPointsPerDimension - pointX - 1;
    if (invertPointX)
      pointX = numberOfPointsPerDimension - pointX - 1;
    int pointY = (vertexIndex / numberOfVerticesPerRow) + ((vertexIndex % numberOfVerticesPerRow) % 2);
    
    return pointY * numberOfPointsPerDimension + pointX;
  }
  
  int getNumberOfVerticesNeededForCurrentSubdividedMeshDensity(){
    //const unsigned int numberOfPointsPerDimension = m_meshDensity + 2;
    //const unsigned int numberOfVerticesPerRow = numberOfPointsPerDimension * 2 - 1;
    //return numberOfVerticesPerRow * (numberOfPointsPerDimension - 1) + 1;
    /*
    = v * (p - 1) + 1
    
    v = p * 2 - 1
    
    = (p * 2 - 1) * (p - 1) + 1
    
    = (2p - 1)(p - 1) + 1
    
    p = m + 2
    
    = (2(m + 2) - 1)(m + 2 - 1) + 1
    
    = (2m + 4 - 1)(m + 1) + 1
    
    = (2m + 3)(m + 1) + 1
    
    = (2m² + 3m + 2m + 3) + 1
    
    = 2m² + 5m + 4
    
    = m(2m + 5) + 4
    */
    return (m_subdividedMeshDensity * 2 + 5) * m_subdividedMeshDensity + 4;
  }
  
  public float linearInterpolation(float from, float to, float alpha){
    return from * (1.f - alpha) + to * alpha;
  }
  
  public float mod(float numerator, float denominator){
    // avoid division by zero (if more accuracy is required, only offset the divided denominator, still use the actual denominator to multiply back as zero multiplication is fine)
    if (denominator > -0.000001f && denominator < 0.000001f)
      denominator = 0.000001f;
    
    return numerator - ((int)(numerator / denominator) * denominator);
  }
  
  public float min(float a, float b){
    return (a < b) ? a : b;
  }
  
  public float max(float a, float b){
    return (a > b) ? a : b;
  }
  
  public Vector2i abs(Vector2i vector){
    return new Vector2i((int)Math.abs(vector.x), (int)Math.abs(vector.y));
  }   
}
