import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;
import java.sql.Timestamp;

public class ChildComment extends Comment {
  public static final String TYPE_CHILD = "child";

  public ChildComment(String title, String body, int parent_id){
    this.title = title;
    this.body = body;
    this.parent_id = parent_id;
    this.type = TYPE_CHILD;
  }



  public static List<ChildComment> all() {
    String sql = "SELECT * FROM comments WHERE type = 'child'";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).throwOnMappingFailure(false).executeAndFetch(ChildComment.class);
    }
  }

  public static ChildComment find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM comments where id=:id";
      ChildComment object = con.createQuery(sql)
        .addParameter("id", id)
        .throwOnMappingFailure(false)
        .executeAndFetchFirst(ChildComment.class);
      return object;
    }
  }
}
