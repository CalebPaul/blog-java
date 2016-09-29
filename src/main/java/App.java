import java.util.HashMap;
import java.util.Map;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    //Home Page
    get("/", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("tags", Tag.all());
      model.put("template", "templates/index.vtl");
      model.put("posts", Post.all());
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //New Post
    post("/", (request, response) -> {
      Post post = new Post(request.queryParams("title"), request.queryParams("content"));
      post.save();
      String tagArrayString = request.queryParams("tags");
      String[] tagArray = request.queryParams("tags").split(",");
      for(String tagString : tagArray){
        Tag tag = Tag.findByName(tagString.trim());
        if(tag == null){
          tag = new Tag(tagString.trim());
          tag.save();
        }
        post.addTag(tag);
      }
      response.redirect("/");
      return null;
    });

    //New Post Form
    get("posts/new", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/post-form.vtl");
      model.put("posts", Post.all());
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //View Specific Post
    get("posts/:id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Post post = Post.find(Integer.parseInt(request.params("id")));
      model.put("post", post);
      model.put("template", "templates/post.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //Add comment to specific post
    post("posts/:id", (request, response) -> {
      int postId = Integer.parseInt(request.params("id"));
      ParentComment comment = new ParentComment(request.queryParams("title"), request.queryParams("body"), postId);
      comment.save();
      String urlString = "/posts/" + postId;
      response.redirect(urlString);
      return null;
    });

    //Delete post
    post("posts/:id/delete", (request, response) -> {
      Post post = Post.find(Integer.parseInt(request.params("id")));
      post.delete();
      response.redirect("/");
      return null;
    });

    //Show specific Tag and its posts
    get("tags/:id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Tag tag = Tag.find(Integer.parseInt(request.params("id")));
      model.put("tag", tag);
      model.put("template", "templates/tag.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //Show and add replies/subcomments
    get("posts/:post_id/comments/:comment_id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Post post = Post.find(Integer.parseInt(request.params("post_id")));
      ParentComment parentComment = ParentComment.find(Integer.parseInt(request.params("comment_id")));
      model.put("comment", parentComment);
      model.put("post", post);
      model.put("template", "templates/comment.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //Show and add replies/subcomments
    post("posts/:post_id/comments/:comment_id/reply", (request, response) -> {
      Post post = Post.find(Integer.parseInt(request.params("post_id")));
      ParentComment parentComment = ParentComment.find(Integer.parseInt(request.params("comment_id")));
      ChildComment childComment = new ChildComment(request.queryParams("title"), request.queryParams("body"), parentComment.getId());
      childComment.save();
      String urlString = "/posts/" + post.getId() + "/comments/" + parentComment.getId();
      response.redirect(urlString);
      return null;
    });

    //Delete tag
    post("tags/:id/delete", (request, response) -> {
      Tag tag = Tag.find(Integer.parseInt(request.params("id")));
      tag.delete();
      response.redirect("/");
      return null;
    });
  }
}
