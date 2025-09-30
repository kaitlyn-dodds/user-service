package kdodds.user_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        List<String> superLongNameOfThisArraySoWeCanTestLineWrapping = List.of(
            "this is another long string",
            "what are you gonna do about it?"
        );
        return "pong";
    }

}
