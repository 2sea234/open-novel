package com.kxhy.novel.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("user")
@RestController
@Log4j2
@RequiredArgsConstructor // 自动生成构造方法 (自动注入UserService)
@Tag(name = "用户管理", description = "用户管理接口")
public class UserController {

    @PostMapping("/bookshelf")
    public String bookshelf() {
        return "用户书架";
    }

}
