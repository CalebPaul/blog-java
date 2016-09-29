import org.sql2o.*;
import java.sql.Timestamp;

public abstract class Comment {
  public int id;
  public String title;
  public String body;
  public Timestamp comment_date;
  public int parent_id;
  public String type;

  public int getId(){
    return id;
  }

  public int getParentId(){
    return parent_id;
  }

  public String getTitle(){
    return title;
  }

  public String getBody(){
    return body;
  }

  public Timestamp getCommentDate(){
    return comment_date;
  }

  @Override
  public boolean equals(Object otherComment){
    if (!(otherComment instanceof Comment)) {
      return false;
    } else {
      Comment newComment = (Comment) otherComment;
      return this.title.equals(newComment.title) &&
             this.id == newComment.id &&
             this.body.equals(newComment.body) &&
             this.type.equals(newComment.type);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO comments (title, body, comment_date, parent_id, type) VALUES (:title, :body, now(), :parent_id, :type)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("title", this.title)
        .addParameter("body", this.body)
        .addParameter("parent_id", this.parent_id)
        .addParameter("type", this.type)
        .executeUpdate()
        .getKey();
    }
  }

  public void update(String newBody) {
    this.body = newBody;
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE comments SET body=:body WHERE id=:id";
      con.createQuery(sql).addParameter("id", this.id).addParameter("body", this.body).executeUpdate();
    }
  }
}
