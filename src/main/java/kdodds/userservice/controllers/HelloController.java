package kdodds.userservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello Controller class.
 */
@RestController
public class HelloController {

    /**
     * Simple test endpoint.
     *
     * @return String response
     */
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}
