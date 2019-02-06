package com.helloyuyu.plugin.arouternavigatefunctiongenerator.model;

/**
 * ARouter  Autowired 注解的解析结果
 *
 * @author xjs
 */
public class AutowiredAnnotationParseResult {

    private String name;

    private Boolean required;

    public AutowiredAnnotationParseResult(String name, Boolean required) {
        this.name = name;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
