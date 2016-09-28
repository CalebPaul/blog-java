import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class TagTest{
  Tag testTag;
  Tag testTag2;

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Before
  public void setUp(){
    testTag = new Tag("cake");
    testTag2 = new Tag("pie");
  }

  @Test
  public void object_instantiatesCorrectly_true() {
    assertTrue(testTag instanceof Tag);
  }

  @Test
  public void equals_returnsTrueIfPropertiesAreSame_true(){
    testTag2 = new Tag("cake");
    assertTrue(testTag.equals(testTag2));
  }

  @Test
  public void save_insertsTagIntoDatabase_Tag() {
    testTag.save();
    try(Connection con = DB.sql2o.open()){
      testTag2 = con.createQuery("SELECT * FROM tags WHERE tag_name='cake'")
      .executeAndFetchFirst(Tag.class);
    }
    assertTrue(testTag2.equals(testTag));
  }

  @Test
  public void all_returnsAllInstancesOfPerson_true() {
    testTag.save();
    testTag2.save();
    assertEquals(true, Tag.all().get(0).equals(testTag));
    assertEquals(true, Tag.all().get(1).equals(testTag2));
  }

  @Test
  public void save_assignsIdToTag() {
    testTag.save();
    testTag2 = Tag.all().get(0);
    assertEquals(testTag.getId(), testTag2.getId());
  }

  @Test
  public void find_returnsTagWithSameId_secondTag() {
    testTag.save();
    testTag2.save();
    assertEquals(Tag.find(testTag2.getId()), testTag2);
  }

  @Test
  public void getPosts_returnsAllPosts_List() {
    Post testPost = new Post("cake rules", "pie drools");
    testPost.save();
    testTag.save();
    testPost.addTag(testTag);
    List savedPosts = testTag.getPosts();
    assertEquals(savedPosts.size(), 1);
  }
}
