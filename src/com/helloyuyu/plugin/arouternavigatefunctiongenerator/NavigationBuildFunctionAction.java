package com.helloyuyu.plugin.arouternavigatefunctiongenerator;

import com.helloyuyu.plugin.arouternavigatefunctiongenerator.model.RouteAnnotationParseResult;
import com.helloyuyu.plugin.arouternavigatefunctiongenerator.utils.PLogger;
import com.helloyuyu.plugin.arouternavigatefunctiongenerator.utils.ParseUtils;
import com.helloyuyu.plugin.arouternavigatefunctiongenerator.utils.PsiUtils;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;

import java.lang.management.PlatformLoggingMXBean;
import java.util.List;

/**
 * 跳转方法构建的Action
 *
 * @author xjs
 */
public class NavigationBuildFunctionAction extends BaseGenerateAction {

    public NavigationBuildFunctionAction() {
        super(null);
    }

    public NavigationBuildFunctionAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(DataKeys.EDITOR);
        PsiFile psiFile = e.getData(DataKeys.PSI_FILE);

        PsiClass psiClass = getTargetClass(editor, psiFile);
        parseJavaClass(psiClass, psiFile);
    }


    private void parseJavaClass(PsiClass psiClass, PsiFile psiFile) {
        List<PsiField> psiFieldList = ParseUtils.findTheFieldWithAutowiredAnnotation(psiClass);
        PsiAnnotation psiAnnotation = PsiUtils.findAnnotationByQualifiedName(psiClass,
                Constants.AROUTER_ROUTE_ANNOTATION_QUALIFIED_NAME);
        try {
            WriteCommandAction.runWriteCommandAction(psiFile.getProject(),
                    new NavigationBuildFunctionWriteRunnable2(psiFile, psiClass, psiAnnotation, psiFieldList));
        } catch (Throwable throwable) {
            PLogger.getInstance().logE("Generate navigation function error", throwable);
        }
    }

    @Override
    protected boolean isValidForClass(PsiClass targetClass) {
        return super.isValidForClass(targetClass)
                && ParseUtils.parseRouteAnnotation(targetClass) != null;
    }
}
