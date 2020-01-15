package com.ramostear.springbootfreemarker.util;


import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName DirectiveNameUtils
 * @Description TODO
 * @Author ramostear
 * @Date 2020/1/15 0015 19:52
 * @Version 1.0
 **/
public class DirectiveNameUtils {
    private DirectiveNameUtils(){}

    public static String humpToUnderline(String beanName){
        beanName = StringUtils.uncapitalize(beanName);
        char[] letters = beanName.toCharArray();
        StringBuffer sb = new StringBuffer();
        for(char letter:letters){
            if(Character.isUpperCase(letter)){
                sb.append("_"+letter+"");
            }else{
                sb.append(letter+"");
            }
        }
        return StringUtils.lowerCase(sb.toString());
    }
}
