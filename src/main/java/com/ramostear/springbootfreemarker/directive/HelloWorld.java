package com.ramostear.springbootfreemarker.directive;

import com.ramostear.springbootfreemarker.directive.abs.AbstractTemplateDirective;
import com.ramostear.springbootfreemarker.directive.abs.DirectiveHandler;
import org.springframework.stereotype.Service;

/**
 * @ClassName HelloWorld
 * @Description TODO
 * @Author ramostear
 * @Date 2020/1/15 0015 19:58
 * @Version 1.0
 **/
@Service
public class HelloWorld extends AbstractTemplateDirective {

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        handler.put(RESULT,"Hello World").render();
    }
}
