package com.alienlab.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 橘 on 2017/5/18.
 */
@RestController
public class TestController {
    @RequestMapping(value="/test.do",method = RequestMethod.GET)
    public ResponseEntity test(){
        return ResponseEntity.ok().body("test");
    }
}
