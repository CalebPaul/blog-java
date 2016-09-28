import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;
import java.sql.Timestamp;

public class Post {
  private int id;
  private String title;
  private String content;
  private Timestamp date_posted;

  public Post(String title, String content) {
    this.title = title;
    this.content = content;
  }

  public int getId(){
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  @Override
  public boolean equals(Object otherPost){
    if (!(otherPost instanceof Post)) {
      return false;
    } else {
      Post newPost = (Post) otherPost;
      return this.id  == newPost.id &&
             this.title.equals(newPost.title) &&
             this.content.equals(newPost.content);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO posts (title, content, date_posted) VALUES (:title, :content, now())";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("title", this.title)
        .addParameter("content", this.content)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Post> all() {
    String sql = "SELECT * FROM posts";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Post.class);
    }
  }

  public static Post find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM posts where id=:id";
      Post object = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Post.class);
      return object;
    }
  }
}
