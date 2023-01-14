package foo;

import org.springframework.web.bind.annotation.*;

@RestController
public class GreetingController {

    @PostMapping("/greeting")
    public Greeting greeting(@RequestBody Greeting greeting) {
        Greeting responseGreeting = new Greeting();
        responseGreeting.setId(greeting.getId());
        responseGreeting.setContent("Hi " + greeting.getContent());

        return responseGreeting;
    }

    @GetMapping("/greeting")
    public Greeting greeting() {
        Greeting responseGreeting = new Greeting();
        responseGreeting.setId(0);
        responseGreeting.setContent("Hi all");

        return responseGreeting;
    }

    @GetMapping("/nothing")
    public void nothing() {
    }

    @GetMapping("/path-and-param/{path}")
    public Greeting pathAndParam(@PathVariable String path, @RequestParam String param) {
        Greeting responseGreeting = new Greeting();
        responseGreeting.setId(0);
        responseGreeting.setContent("Your path is " + path + ", and param is " + param);

        return responseGreeting;
    }

}
