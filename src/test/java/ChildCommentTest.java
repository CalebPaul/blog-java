import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class ChildCommentTest {
  ChildComment testChildComment;
  ChildComment testChildComment2;

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Before
  public void setUp(){
    testChildComment = new ChildComment("cake sux", "lol", 1);
    testChildComment2 = new ChildComment("pie sux", "roflcopter", 2);
  }

  @Test
  public void object_instantiatesCorrectly_true() {
    assertTrue(testChildComment instanceof ChildComment);
  }

  @Test
  public void equals_returnsTrueIfPropertiesAreSame_true(){
    testChildComment2 = new ChildComment("cake sux", "lol", 1);
    assertTrue(testChildComment.equals(testChildComment2));
  }

  @Test
  public void save_insertsChildCommentIntoDatabase_ChildComment() {
    testChildComment.save();
    try(Connection con = DB.sql2o.open()){
      testChildComment2 = con.createQuery("SELECT * FROM comments WHERE title='cake sux'")
      .executeAndFetchFirst(ChildComment.class);
    }
    assertTrue(testChildComment2.equals(testChildComment));
  }

  @Test
  public void all_returnsAllInstancesOfComment_true() {
    testChildComment.save();
    testChildComment2.save();
    assertEquals(true, ChildComment.all().get(0).equals(testChildComment));
    assertEquals(true, ChildComment.all().get(1).equals(testChildComment2));
  }

  @Test
  public void save_assignsIdToChildComment() {
    testChildComment.save();
    testChildComment2 = ChildComment.all().get(0);
    assertEquals(testChildComment.getId(), testChildComment2.getId());
  }

  @Test
  public void find_returnsChildCommentWithSameId_secondChildComment() {
    testChildComment.save();
    testChildComment2.save();
    assertEquals(ChildComment.find(testChildComment2.getId()), testChildComment2);
  }

  @Test
  public void delete_deletesChildComment_True() {
    testChildComment.save();
    testChildComment.delete();
    assertEquals(0, ChildComment.all().size());
  }

  @Test
  public void update_updatesChildComment_True() {
    testChildComment.save();
    testChildComment.update("lol @ u");
    ChildComment newComment = ChildComment.find(testChildComment.getId());
    assertEquals("lol @ u", newComment.getBody());
  }
}
