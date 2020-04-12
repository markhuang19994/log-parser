package com.example.logparser.handler

import org.springframework.core.io.ClassPathResource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/page")
class PageHandler {

    @GetMapping("/process")
    String processPage() {
        new ClassPathResource("/templates/user_process_view.html").inputStream.text
    }


}
