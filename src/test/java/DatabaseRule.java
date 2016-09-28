import org.junit.rules.ExternalResource;
import org.sql2o.*;

public class DatabaseRule extends ExternalResource {

  @Override
  protected void before() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/blog_test", null, null);
  }

  @Override
  protected void after() {
    try(Connection con = DB.sql2o.open()) {
      String deleteTableQuery = "DELETE FROM tags *;";
      String deletePostsQuery = "DELETE FROM posts *;";
      String deletePostTagsQuery = "DELETE FROM posts_tags *;";
      String deleteCommentsQuery = "DELETE FROM comments *;";
      con.createQuery(deleteTableQuery).executeUpdate();
      con.createQuery(deletePostTagsQuery).executeUpdate();
      con.createQuery(deletePostsQuery).executeUpdate();
      con.createQuery(deleteCommentsQuery).executeUpdate();
    }
  }

}
