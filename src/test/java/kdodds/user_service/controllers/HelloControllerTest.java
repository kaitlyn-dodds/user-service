package kdodds.user_service.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HelloControllerTest {

    @Autowired
    private HelloController helloController;

    /**
     * Test the HelloController /ping endpoint returns a 200 status code.
     */
    @Test
    public void testPingEndpoint_PongResponse() {
        String response = helloController.ping();

        Assertions.assertNotNull(response);
        Assertions.assertEquals("pong", response);
    }

}
