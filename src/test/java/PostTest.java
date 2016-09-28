import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class PostTest {
  Post testPost;
  Post testPost2;

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Before
  public void setUp(){
    testPost = new Post("cake rules", "pie drools");
    testPost2 = new Post("pie rules", "cake drools");
  }

  @Test
  public void object_instantiatesCorrectly_true() {
    assertTrue(testPost instanceof Post);
  }

  @Test
  public void equals_returnsTrueIfPropertiesAreSame_true(){
    testPost2 = new Post("cake rules", "pie drools");
    assertTrue(testPost.equals(testPost2));
  }

  @Test
  public void save_insertsPostIntoDatabase_Post() {
    testPost.save();
    try(Connection con = DB.sql2o.open()){
      testPost2 = con.createQuery("SELECT * FROM posts WHERE title='cake rules'")
      .executeAndFetchFirst(Post.class);
    }
    assertTrue(testPost2.equals(testPost));
  }

  @Test
  public void all_returnsAllInstancesOfPerson_true() {
    testPost.save();
    testPost2.save();
    assertEquals(true, Post.all().get(0).equals(testPost));
    assertEquals(true, Post.all().get(1).equals(testPost2));
  }

  @Test
  public void save_assignsIdToPost() {
    testPost.save();
    testPost2 = Post.all().get(0);
    assertEquals(testPost.getId(), testPost2.getId());
  }

  @Test
  public void find_returnsPostWithSameId_secondPost() {
    testPost.save();
    testPost2.save();
    assertEquals(Post.find(testPost2.getId()), testPost2);
  }
}
