package org.articlesblog.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Header")
@Slf4j
@RequiredArgsConstructor
@Controller
public class HeaderController {

    @GetMapping("/about")
    @Operation(summary = "О нас")
    public String about(Model model) {
        model.addAttribute("title", "О нас");
        log.info("Открыли страницу \"О нас\"");
        return "about";
    }
}
