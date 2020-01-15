package com.ramostear.springbootfreemarker.directive.abs;

import com.ramostear.springbootfreemarker.util.DirectiveConst;
import com.ramostear.springbootfreemarker.util.DirectiveNameUtils;
import freemarker.core.Environment;
import freemarker.template.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName AbstractTemplateDirective
 * @Description TODO
 * @Author ramostear
 * @Date 2020/1/15 0015 20:01
 * @Version 1.0
 **/
@Service
@Slf4j
public abstract class AbstractTemplateDirective extends ApplicationObjectSupport implements TemplateDirectiveModel {
    @Autowired
    FreeMarkerConfigurer freeMarkerConfigurer;

    protected static final String RESULT = "result";

    protected static final String RESULTS = "results";

    @PostConstruct
    public void configuration() throws TemplateModelException {
        String className = this.getClass().getName();
        className = className.substring(className.lastIndexOf(".")+1);
        String beanName = StringUtils.uncapitalize(className);
        String tagName = DirectiveConst.DIRECTIVE_PREFIX+ DirectiveNameUtils.humpToUnderline(beanName);
        log.info("directive name :[{}]",tagName);
        freeMarkerConfigurer.getConfiguration()
                .setSharedVariable(tagName,this.getApplicationContext().getBean(beanName));
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        try {
            execute(new DirectiveHandler(env, params, loopVars, body));
        }catch (Exception ex){
            try {
                throw ex;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    abstract public void execute(DirectiveHandler handler) throws Exception;
}
