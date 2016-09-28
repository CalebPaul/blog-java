import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class Tag {
  private int id;
  private String tag_name;

  public Tag (String tag_name){
    this.tag_name = tag_name;
  }

  public int getId(){
    return id;
  }

  public String getName() {
    return tag_name;
  }

  @Override
  public boolean equals(Object otherTag){
    if (!(otherTag instanceof Tag)) {
      return false;
    } else {
      Tag newTag = (Tag) otherTag;
      return this.id == newTag.id && this.tag_name.equals(newTag.tag_name);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO tags (tag_name) VALUES (:tag_name)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("tag_name", this.tag_name)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Tag> all() {
    String sql = "SELECT * FROM tags";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Tag.class);
    }
  }

  public static Tag find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM tags where id=:id";
      Tag object = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Tag.class);
      return object;
    }
  }
}
