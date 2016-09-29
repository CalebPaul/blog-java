import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;
import java.sql.Timestamp;

//TODO: front end

//TODO: remove tag from post (w/o delete)
//TODO: remove post from tag? (some sort of admin action thingy?)
//TODO: search methods?
//TODO: alternate method for sorting posts by comment number

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

  public void addTag(Tag tag) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO posts_tags (post_id, tag_id) VALUES (:post_id, :tag_id)";
      con.createQuery(sql)
      .addParameter("post_id", this.getId())
      .addParameter("tag_id", tag.getId())
      .executeUpdate();
    }
  }

  public List<Tag> getTags() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT tags.* FROM posts " +
      "JOIN posts_tags ON (posts.id = posts_tags.post_id) " +
      "JOIN tags ON (posts_tags.tag_id = tags.id) "+
      "WHERE posts.id = :id";
      return con.createQuery(joinQuery)
        .addParameter("id", this.getId())
        .executeAndFetch(Tag.class);
    }
  }

  public List<ParentComment> getComments() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT * FROM comments WHERE parent_id=:id AND type='parent' ORDER BY comment_date";
      return con.createQuery(joinQuery)
        .addParameter("id", this.getId())
        .executeAndFetch(ParentComment.class);
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()){
      String sql = "DELETE FROM posts WHERE id=:id";
      con.createQuery(sql)
         .addParameter("id", this.getId())
         .executeUpdate();
      String joinDeleteQuery = "DELETE FROM posts_tags WHERE post_id=:post_id";
      con.createQuery(joinDeleteQuery)
         .addParameter("post_id", this.getId())
         .executeUpdate();
      String selectAllComments = "SELECT id FROM comments WHERE parent_id = :id AND type='parent'";
      //String selectAllComments = "SELECT t2.id FROM comments t1 JOIN comments t2 ON t1.id=t2.parent_id WHERE t1.parent_id = :id AND t1.type='parent' OR t2.type='child'";
      List<Integer> commentIds = con.createQuery(selectAllComments).addParameter("id", id).executeAndFetch(Integer.class);
      for(Integer commentId : commentIds){
        String deleteString = "DELETE FROM comments WHERE id=:id";
        String deleteChildString = "DELETE FROM comments WHERE parent_id=:id AND type='child'";
        con.createQuery(deleteString).addParameter("id", commentId).executeUpdate();
        con.createQuery(deleteChildString).addParameter("id", commentId).executeUpdate();
      }
    }
  }

  public void update(String newContent) {
    this.content = newContent;
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE posts SET content=:content WHERE id=:id";
      con.createQuery(sql).addParameter("id", this.id).addParameter("content", this.content).executeUpdate();
    }
  }
}
