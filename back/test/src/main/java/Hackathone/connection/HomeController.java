package Hackathone.connection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @PostMapping(value="android")
    @ResponseBody
    public String androidResponse(@RequestBody User user) {

        System.out.println("Connection from Android");
        System.out.println("id: " + user.getId() + ", pw: " + user.getPassword());

        return "1";
    }
}
