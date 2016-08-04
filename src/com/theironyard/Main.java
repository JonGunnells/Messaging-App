package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.HashMap;


public class Main {


    static HashMap<String, User> userMap = new HashMap<>();


    public static void main(String[] args) {
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {


                    Session session = request.session();
                    String username = session.attribute("username");
                    HashMap m = new HashMap();
                    if (username == null) {
                        return new ModelAndView(m, "index.html");
                    } else {
                        User user = userMap.get(username);
                        m.put("messages", user.messages);
                        m.put("name", user.name);
                        return new ModelAndView(m, "messages.html");
                    }

                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/create-user",
                (request, response) -> {
                    String username = request.queryParams("username");
                    String pass = request.queryParams("password");
                    if (username == null || pass == null) {
                        throw new Exception("Name or pass not sent");
                    }

                    User user = userMap.get(username);
                    if (user == null) {
                        user = new User(username);
                        userMap.put(username, user);
                    } else if (!pass.equals(user.password)) {
                        throw new Exception("Wrong password");
                    }

                    Session session = request.session();
                    session.attribute("username", username);


                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/create-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    if (username == null) {
                        throw new Exception("Not logged in");

                    }

                    String content = request.queryParams("message");
                    Message message = new Message(content);
                    User user = userMap.get(username);

                    user.messages.add(message);
                    response.redirect("/");
                    return "";
                }
        );


        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/delete-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    if (username == null) {
                        throw new Exception("Not logged in");

                    }

                    int id = Integer.valueOf(request.queryParams("id"));

                    User user = userMap.get(username);
                    if (id <= 0 || id > user.messages.size()) {
                        throw new Exception("Invalid ID");
                    }
                    user.messages.remove(id-1);

                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/edit-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    if (username == null) {
                        throw new Exception("Not logged in");

                    }

                    int id = Integer.valueOf(request.queryParams("editid"));
                    String edit = request.queryParams("edit");
                    Message message = new Message(edit);

                    User user = userMap.get(username);
                    if (id <= 0 || id > user.messages.size()) {
                        throw new Exception("Invalid ID");
                    }
                    user.messages.add(id-1, message);
                    user.messages.remove(id);

                    response.redirect("/");
                    return "";
                }
        );
    }
}