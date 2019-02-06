package com.helloyuyu.plugin.arouternavigatefunctiongenerator.utils;

import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * utils
 *
 * @author xjs
 */
public class PsiUtils {

    /**
     * {@link PsiClass} 是否继承或实现 {@code superQualifiedName} 接口或类
     *
     * @param psiClass           子类
     * @param superQualifiedName 父类名称
     * @return true 为是子类 反之
     */
    public static boolean isPsiClassSubTypeOf(PsiClass psiClass,
                                              String superQualifiedName) {
        if (psiClass == null) {
            return false;
        }

        if (Objects.equals(psiClass.getQualifiedName(), superQualifiedName)) {
            return true;
        } else {
            PsiClass[] supers = psiClass.getSupers();
            for (PsiClass aSuper : supers) {
                if (isPsiClassSubTypeOf(aSuper, superQualifiedName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public static PsiAnnotation findAnnotationByQualifiedName(@NotNull PsiModifierListOwner modifierListOwner,
                                                              @NotNull String annotationQualifiedName) {
        for (PsiAnnotation psiAnnotation : modifierListOwner.getAnnotations()) {
            if (annotationEquals(psiAnnotation, annotationQualifiedName)) {
                return psiAnnotation;
            }
        }
        return null;
    }

    public static boolean annotationEquals(@NotNull JvmAnnotation annotation,
                                           @NotNull String annotationQualifiedName) {
        return Objects.equals(annotation.getQualifiedName(), annotationQualifiedName);
    }


    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationMemberConstantValue(PsiExpression psiExpression, T defaultValue) {
            PLogger.getInstance().logI("getAnnotationMemberConstantValue toString: " + psiExpression.toString());
            PLogger.getInstance().logI("getAnnotationMemberConstantValue getText: " + psiExpression.getText());
        try {
            PsiConstantEvaluationHelper evaluationHelper = JavaPsiFacade.getInstance(psiExpression.getProject()).getConstantEvaluationHelper();
            return (T) evaluationHelper.computeConstantExpression(psiExpression);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static boolean isJvmClassPublic(JvmClass jvmClass) {
        for (JvmModifier jvmModifier : jvmClass.getModifiers()) {
            if (jvmModifier == JvmModifier.PUBLIC) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static PsiClass getPsiFieldClass(@NotNull PsiField psiField) {
        PLogger.getInstance().logI("getPsiFieldClass= psiField:" + psiField.toString());
        PsiType psiType = psiField.getType();
        PLogger.getInstance().logI("getPsiFieldClass= psiType:" + psiType.toString());
        if (psiType instanceof PsiClassType) {
            PsiClass psiClass = ((PsiClassType) psiType).resolve();
            if (psiClass != null) {
                return psiClass;
            }
        } else if (psiType instanceof PsiArrayType) {
            PsiArrayType psiArrayType = (PsiArrayType) psiType;
            PsiType psiType1 = psiArrayType.getComponentType();
        } else if (psiType instanceof PsiPrimitiveType) {
            PsiPrimitiveType psiPrimitiveType = (PsiPrimitiveType) psiType;
            PsiClassType psiClassType = psiPrimitiveType.getBoxedType(psiField);
        }

        return null;
    }

    public static String getTypeName(@NotNull PsiParameter psiParameter) {
        return psiParameter.getType().getPresentableText();
    }

    public static void get(PsiField field) {
        PsiType psiType = field.getType();
        if (psiType instanceof PsiClassType) {
            PsiClass psiClass = ((PsiClassType) psiType).resolve();

        } else if (psiType instanceof PsiArrayType) {
            PsiArrayType psiArrayType = (PsiArrayType) psiType;
            PsiType psiType1 = psiArrayType.getComponentType();
            PLogger.getInstance().logI("generateMethodParamsText: array=" + psiArrayType.toString());
            PLogger.getInstance().logI("generateMethodParamsText: array=" + psiArrayType.getCanonicalText());

        } else if (psiType instanceof PsiPrimitiveType) {
            PsiPrimitiveType psiPrimitiveType = (PsiPrimitiveType) psiType;
            psiPrimitiveType.getName();

            PsiClassType psiClassType = psiPrimitiveType.getBoxedType(field);
        }
    }

}
