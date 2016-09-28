import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class ParentCommentTest {
  ParentComment testParentComment;
  ParentComment testParentComment2;

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Before
  public void setUp(){
    testParentComment = new ParentComment("cake sux", "lol", 1);
    testParentComment2 = new ParentComment("pie sux", "roflcopter", 2);
  }

  @Test
  public void object_instantiatesCorrectly_true() {
    assertTrue(testParentComment instanceof ParentComment);
  }

  @Test
  public void equals_returnsTrueIfPropertiesAreSame_true(){
    testParentComment2 = new ParentComment("cake sux", "lol", 1);
    assertTrue(testParentComment.equals(testParentComment2));
  }

  @Test
  public void save_insertsParentCommentIntoDatabase_ParentComment() {
    testParentComment.save();
    try(Connection con = DB.sql2o.open()){
      testParentComment2 = con.createQuery("SELECT * FROM comments WHERE title='cake sux'")
      .executeAndFetchFirst(ParentComment.class);
    }
    assertTrue(testParentComment2.equals(testParentComment));
  }

  @Test
  public void all_returnsAllInstancesOfComment_true() {
    testParentComment.save();
    testParentComment2.save();
    assertEquals(true, ParentComment.all().get(0).equals(testParentComment));
    assertEquals(true, ParentComment.all().get(1).equals(testParentComment2));
  }

  @Test
  public void save_assignsIdToParentComment() {
    testParentComment.save();
    testParentComment2 = ParentComment.all().get(0);
    assertEquals(testParentComment.getId(), testParentComment2.getId());
  }

  @Test
  public void find_returnsParentCommentWithSameId_secondParentComment() {
    testParentComment.save();
    testParentComment2.save();
    assertEquals(ParentComment.find(testParentComment2.getId()), testParentComment2);
  }


  @Test
  public void getChildComments_returnsAllChildComments_List() {
    testParentComment.save();
    ChildComment testChildComment = new ChildComment("diaf", "go home ur drunk", testParentComment.getId());
    testChildComment.save();
    List savedChildComments = testParentComment.getChildComments();
    assertEquals(savedChildComments.size(), 1);
  }
}
