package com.helloyuyu.plugin.arouternavigatefunctiongenerator;

import com.helloyuyu.plugin.arouternavigatefunctiongenerator.model.AutowiredAnnotationParseResult;
import com.helloyuyu.plugin.arouternavigatefunctiongenerator.model.RouteAnnotationParseResult;
import com.helloyuyu.plugin.arouternavigatefunctiongenerator.utils.ParseUtils;
import com.helloyuyu.plugin.arouternavigatefunctiongenerator.utils.PsiUtils;
import com.helloyuyu.plugin.arouternavigatefunctiongenerator.utils.Utils;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ARouter 页面跳转的方法生成写入
 *
 * @author xjs
 */
public class NavigationBuildFunctionWriteRunnable2 implements Runnable {

    private PsiFile mPsiFile;
    private PsiClass mPsiClass;
    private PsiAnnotation mRouteAnnotation;
    private List<PsiField> mAutowiredFieldList;
    private PsiElementFactory mFactory;

    public NavigationBuildFunctionWriteRunnable2(PsiFile psiFile, PsiClass psiClass,
                                                 PsiAnnotation routerAnnotation,
                                                 List<PsiField> autowiredFieldList) {
        mPsiFile = psiFile;
        mPsiClass = psiClass;
        mRouteAnnotation = routerAnnotation;
        mAutowiredFieldList = autowiredFieldList;
        mFactory = JavaPsiFacade.getElementFactory(psiFile.getProject());
    }

    @Override
    public void run() {
        generateNavigationMethod();
    }

    private void generateNavigationMethod() {

        String methodStringBuilder = "public static void " + Constants.NAVIGATE_FUNCTION_NAME +
                "(" + buildMethodParametersText() + ")" +
                "{" + buildMethodStatementText() + "}";
        PsiMethod psiMethod = mFactory.createMethodFromText(methodStringBuilder, mPsiClass);
        mPsiClass.add(psiMethod);


        // reformat class
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mPsiClass.getProject());
        styleManager.optimizeImports(mPsiFile);
        styleManager.shortenClassReferences(mPsiClass);
        new ReformatCodeProcessor(mPsiClass.getProject(),
                mPsiClass.getContainingFile(), null, false)
                .runWithoutProgress();
    }

    /**
     * 构建方法的参数文本
     * 如（String string,int i）
     *
     * @return 参数文本
     */
    private String buildMethodParametersText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mAutowiredFieldList.size(); i++) {
            PsiField psiField = mAutowiredFieldList.get(i);
            String parameterName = getPsiFiledParameterName(psiField);
            String parameterType = psiField.getType().getCanonicalText();

            sb.append(parameterType).append(" ").append(parameterName);

            if (i != mAutowiredFieldList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 获取变量作为参数的命名
     *
     * @param psiField 变量
     * @return 参数命名
     */
    private static String getPsiFiledParameterName(PsiField psiField) {
        String parameterName = psiField.getName();
        //如果字段命名为 mUserId 会被替换为 userId
        parameterName = replaceStartWith_m_FieldName(parameterName);
        return parameterName;
    }

    /**
     * 替换 类似于 mUserId这样的为userId
     *
     * @param fieldName 字段名称
     * @return 替换后的名称
     */
    private static String replaceStartWith_m_FieldName(String fieldName) {
        if (Utils.isStartWithM(fieldName)) {
            return Utils.firstCharacterToLow(fieldName.substring(1));
        }
        return fieldName;
    }

    /**
     * 构建方法体
     *
     * @return 方法体构建文本
     */
    private String buildMethodStatementText() {
        StringBuilder statementSb = new StringBuilder();

        PsiAnnotationMemberValue pathValue = mRouteAnnotation.findAttributeValue(
                Constants.AROUTER_ROUTE_ANNOTATION_PARAM_PATH);
        PsiAnnotationMemberValue groupValue = mRouteAnnotation.findAttributeValue(
                Constants.AROUTER_ROUTE_ANNOTATION_PARAM_GROUP);

        if (pathValue instanceof PsiLiteralExpression) {//直接是字符串
            String path = PsiUtils.getAnnotationMemberConstantValue((PsiLiteralExpression) pathValue, "");
            statementSb.append("ARouter.getInstance().build(\"").append(path).append("\"");
        } else if (pathValue instanceof PsiExpression) {//常量
            String path = pathValue.getText();
            statementSb.append("ARouter.getInstance().build(").append(path);
        }

        if (groupValue instanceof PsiLiteralExpression) {
            String group = PsiUtils.getAnnotationMemberConstantValue((PsiLiteralExpression) groupValue, "");
            if (!StringUtils.isEmpty(group)) {
                statementSb.append(",\"").append(group).append("\"");
            }
        } else if (groupValue instanceof PsiExpression) {
            String group = groupValue.getText();
            statementSb.append(",").append(group);
        }
        statementSb.append(")");
        if (!mAutowiredFieldList.isEmpty()) {
            statementSb.append("\n");
            for (int i = 0; i < mAutowiredFieldList.size(); i++) {
                PsiField psiField = mAutowiredFieldList.get(i);

                String withType;
                PsiType psiType = psiField.getType();
                if (psiType instanceof PsiPrimitiveType) {//是否为基本类型
                    withType = Utils.firstCharacterToUp(((PsiPrimitiveType) psiType).getName());
                } else if (psiType instanceof PsiClassType) {
                    //是否是Parcelable 类型
                    if (PsiUtils.isPsiClassSubTypeOf(((PsiClassType) psiType).resolve(),
                            Constants.ANDROID_OS_PARCELABLE_CLASS_NAME)) {
                        withType = "Parcelable";
                    } else if (PsiUtils.isPsiClassSubTypeOf(((PsiClassType) psiType).resolve(),
                            Constants.JAVA_STRING_CLASS_NAME)) {//是否是String类型
                        withType = "String";
                    } else {
                        withType = "Object";
                    }
                } else {
                    withType = "Object";
                }
                statementSb.append(".with").append(withType);

                PsiAnnotation psiAnnotation = PsiUtils.findAnnotationByQualifiedName(psiField,
                        Constants.AROUTER_AUTOWIRED_ANNOTATION_QUALIFIED_NAME);
                PsiAnnotationMemberValue nameValue = Objects.requireNonNull(psiAnnotation)
                        .findAttributeValue(Constants.AROUTER_AUTOWIRED_ANNOTATION_PARAM_NAME);
                if (nameValue instanceof PsiLiteralExpression) {
                    String key = PsiUtils.getAnnotationMemberConstantValue((PsiLiteralExpression) nameValue, "");
                    if (StringUtils.isEmpty(key)) {
                        key = psiField.getName();
                    }
                    statementSb.append("(\"").append(key).append("\"");
                } else if (nameValue instanceof PsiExpression) {
                    String key = nameValue.getText();
                    statementSb.append("(").append(key);
                }

                String parameterName = getPsiFiledParameterName(psiField);
                statementSb.append(",").append(parameterName).append(")").append("\n");
            }
        }

        statementSb.append(".navigation();");

        return statementSb.toString();
    }
}
