package foo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @PostMapping("/greeting")
    public Greeting greeting(@RequestBody Greeting greeting) {
        return greeting;
    }

}
