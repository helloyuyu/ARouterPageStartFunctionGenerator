package com.helloyuyu.plugin.arouternavigatefunctiongenerator.model;

/**
 * ARouter  Route 注解的解析结果
 * 当前插件应用中只需要拿路径 'path'
 * 和组 'group' 就够了
 * @author xjs
 */
public class RouteAnnotationParseResult {

    private String path;

    private String group;

    public RouteAnnotationParseResult(String path, String group) {
        this.path = path;
        this.group = group;
    }

    @Override
    public String toString() {
        return "path=" + path + ",group=" + group;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
