package foo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @PostMapping("/greeting")
    public Greeting greeting(@RequestBody Greeting greeting) {
        Greeting responseGreeting = new Greeting();
        responseGreeting.setId(greeting.getId());
        responseGreeting.setContent("Hi " + greeting.getContent());

        return responseGreeting;
    }

}
