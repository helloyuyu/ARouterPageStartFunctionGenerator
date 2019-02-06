package com.helloyuyu.plugin.arouternavigatefunctiongenerator.utils;

import com.helloyuyu.plugin.arouternavigatefunctiongenerator.Constants;
import com.helloyuyu.plugin.arouternavigatefunctiongenerator.model.AutowiredAnnotationParseResult;
import com.helloyuyu.plugin.arouternavigatefunctiongenerator.model.RouteAnnotationParseResult;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析的工具类
 *
 * @author xjs
 */
public class ParseUtils {


    /**
     * 解析class上标注的 Route 注解 返回解析后的模型
     *
     * @param psiClass 需要解析的目标 class
     * @return 如果没有标注 Route 注解返回 null
     */
    @Nullable
    public static RouteAnnotationParseResult parseRouteAnnotation(@NotNull PsiClass psiClass) {

        PsiAnnotation psiAnnotation = PsiUtils.findAnnotationByQualifiedName(psiClass,
                Constants.AROUTER_ROUTE_ANNOTATION_QUALIFIED_NAME);

        if (psiAnnotation != null) {
            PsiAnnotationMemberValue pathValue = psiAnnotation.findAttributeValue(
                    Constants.AROUTER_ROUTE_ANNOTATION_PARAM_PATH);
            PsiAnnotationMemberValue groupValue = psiAnnotation.findAttributeValue(
                    Constants.AROUTER_ROUTE_ANNOTATION_PARAM_GROUP);
            if (pathValue instanceof PsiExpression && groupValue instanceof PsiExpression) {
                String path = PsiUtils.getAnnotationMemberConstantValue((PsiExpression) pathValue, "");
                String group = PsiUtils.getAnnotationMemberConstantValue((PsiExpression) groupValue, "");
                return new RouteAnnotationParseResult(path, group);
            } else {
                PLogger.getInstance().logE("unknow type:" + pathValue == null ? "null" : pathValue.getClass().getCanonicalName());
            }
        }
        return null;
    }

    /**
     * 获取被Autowired 注解所标注的字段 会忽略掉实现 IProvider的服务提供类的 字段
     *
     * @param psiClass 目标类
     * @return 返回被Autowired 注解所标注并且不是IProvider实现类的字段
     */
    public static List<PsiField> findTheFieldWithAutowiredAnnotation(PsiClass psiClass) {
        PsiField[] psiFields = psiClass.getAllFields();
        List<PsiField> psiFieldList = new ArrayList<>();
        for (PsiField psiField : psiFields) {
            PsiAnnotation psiAnnotation = PsiUtils.findAnnotationByQualifiedName(psiField,
                    Constants.AROUTER_AUTOWIRED_ANNOTATION_QUALIFIED_NAME);
            if (psiAnnotation != null && !isFieldImplIProvider(psiField)) {
                psiFieldList.add(psiField);
            }
        }
        return psiFieldList;
    }


    /**
     * 获取字段上 Autowired 注解的信息
     *
     * @param psiField 目标字段
     * @return 返回 Autowired 注解的解析结果 或则 null 如果没有注解上Autowired
     */
    @Nullable
    public static AutowiredAnnotationParseResult parseAutowiredAnnotation(PsiField psiField) throws NullPointerException {

        PsiAnnotation psiAnnotation = PsiUtils.findAnnotationByQualifiedName(psiField,
                Constants.AROUTER_AUTOWIRED_ANNOTATION_QUALIFIED_NAME);
        if (psiAnnotation != null && !isFieldImplIProvider(psiField)) {
            PsiAnnotationMemberValue nameValue =
                    psiAnnotation.findAttributeValue(Constants.AROUTER_AUTOWIRED_ANNOTATION_PARAM_NAME);
            PsiAnnotationMemberValue requiredValue =
                    psiAnnotation.findAttributeValue(Constants.AROUTER_AUTOWIRED_ANNOTATION_PARAM_REQUIRED);

            if (nameValue instanceof PsiExpression && requiredValue instanceof PsiExpression) {
                String name = PsiUtils.getAnnotationMemberConstantValue((PsiExpression) nameValue, "");
                boolean required = PsiUtils.getAnnotationMemberConstantValue((PsiExpression) requiredValue, false);
                return new AutowiredAnnotationParseResult(name, required);
            }
        }
        return null;
    }

    /**
     * 字段类是否是实现了IProvider接口
     *
     * @param psiField 目标字段
     * @return true 实现了IProvider
     */
    private static boolean isFieldImplIProvider(PsiField psiField) {
        PsiClass psiClass = PsiUtils.getPsiFieldClass(psiField);
        if (psiClass == null) {
            return false;
        } else {
            PLogger.getInstance().logI("isFieldImplIProvider: " +
                    "psiField=" + psiField.getName() +
                    "psiClass=" + psiClass.getQualifiedName());
            return PsiUtils.isPsiClassSubTypeOf(psiClass, Constants.AROUTER_IPROVIDER_QUALIFIED_NAME);
        }

    }

}
