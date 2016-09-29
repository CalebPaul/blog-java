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

  public static Tag findByName(String tag_name) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM tags where tag_name=:tag_name";
      Tag object = con.createQuery(sql)
        .addParameter("tag_name", tag_name)
        .executeAndFetchFirst(Tag.class);
      return object;
    }
  }

  public void update(String newName) {
    this.tag_name = newName;
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE comments SET tag_name=:tag_name WHERE id=:id";
      con.createQuery(sql).addParameter("id", this.id).addParameter("tag_name", this.tag_name).executeUpdate();
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM tags WHERE id=:id";
      String deleteChild = "DELETE FROM posts_tags WHERE tag_id=:id";
      con.createQuery(sql).addParameter("id", this.id).executeUpdate();
      con.createQuery(deleteChild).addParameter("id", this.id).executeUpdate();
    }
  }

  public List<Post> getPosts() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT posts.* FROM posts " +
      "JOIN posts_tags ON (posts.id = posts_tags.post_id) " +
      "JOIN tags ON (posts_tags.tag_id = tags.id) "+
      "WHERE tags.id = :id";
      return con.createQuery(joinQuery)
        .addParameter("id", this.getId())
        .executeAndFetch(Post.class);
    }
  }
}
