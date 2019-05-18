package jp.ivan.swadeshness;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LanguagesUpdateController {

    @RequestMapping("/")
    public String index() {
        return "Groups were retrieved!";
    }
}
