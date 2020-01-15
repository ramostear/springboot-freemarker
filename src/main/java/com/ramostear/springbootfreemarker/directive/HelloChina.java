package com.ramostear.springbootfreemarker.directive;

import com.ramostear.springbootfreemarker.directive.abs.AbstractTemplateDirective;
import com.ramostear.springbootfreemarker.directive.abs.DirectiveHandler;
import org.springframework.stereotype.Service;

/**
 * @ClassName HelloChina
 * @Description TODO
 * @Author ramostear
 * @Date 2020/1/15 0015 20:07
 * @Version 1.0
 **/
@Service
public class HelloChina extends AbstractTemplateDirective {
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String title = handler.getString("title","我爱你中国");
        handler.put(RESULT,title).render();
    }
}
