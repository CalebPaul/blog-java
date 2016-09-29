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
      model.put("template", "templates/index.vtl");
      model.put("posts", Post.all());
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //New Post
    post("/", (request, response) -> {
      Post post = new Post(request.queryParams("title"), request.queryParams("content"));
      post.save();
      String[] tagArray = request.queryParams("tags").split(",");
      for(String tagString : tagArray){
        Tag tag = new Tag(tagString.trim());
        tag.save();
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
  }
}
