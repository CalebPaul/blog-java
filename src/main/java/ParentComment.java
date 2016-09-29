import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;
import java.sql.Timestamp;

public class ParentComment extends Comment {
  public static final String TYPE_PARENT = "parent";

  public ParentComment(String title, String body, int parent_id){
    this.title = title;
    this.body = body;
    this.parent_id = parent_id;
    this.type = TYPE_PARENT;
  }

  public static List<ParentComment> all() {
    String sql = "SELECT * FROM comments WHERE type='parent'";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).throwOnMappingFailure(false).executeAndFetch(ParentComment.class);
    }
  }

  public static ParentComment find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM comments where id=:id";
      ParentComment object = con.createQuery(sql)
        .addParameter("id", id)
        .throwOnMappingFailure(false)
        .executeAndFetchFirst(ParentComment.class);
      return object;
    }
  }

  public List<ChildComment> getChildComments() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT * FROM comments WHERE parent_id=:id AND type='child' ORDER BY comment_date";
      return con.createQuery(joinQuery)
        .addParameter("id", this.getId())
        .executeAndFetch(ChildComment.class);
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM comments WHERE id=:id";
      String deleteChild = "DELETE FROM comments WHERE parent_id=:id AND type = 'child'";
      con.createQuery(sql).addParameter("id", this.id).executeUpdate();
      con.createQuery(deleteChild).addParameter("id", this.id).executeUpdate();
    }
  }


}
