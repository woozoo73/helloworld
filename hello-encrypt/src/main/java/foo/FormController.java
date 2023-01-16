package foo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FormController {

    @PostMapping("/form")
    public Greeting greeting(@RequestParam("id") int id, @RequestParam("content") String content) {
        Greeting responseGreeting = new Greeting();
        responseGreeting.setId(id);
        responseGreeting.setContent("Hi " + content);

        return responseGreeting;
    }

}
