package org.apache.logging.log4j.core.config.plugins.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.logging.log4j.core.util.ReflectionUtil;

public final class ConstraintValidators {
   private ConstraintValidators() {
      super();
   }

   public static Collection<ConstraintValidator<?>> findValidators(Annotation... var0) {
      ArrayList var1 = new ArrayList();
      Annotation[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Annotation var5 = var2[var4];
         Class var6 = var5.annotationType();
         if (var6.isAnnotationPresent(Constraint.class)) {
            ConstraintValidator var7 = getValidator(var5, var6);
            if (var7 != null) {
               var1.add(var7);
            }
         }
      }

      return var1;
   }

   private static <A extends Annotation> ConstraintValidator<A> getValidator(A var0, Class<? extends A> var1) {
      Constraint var2 = (Constraint)var1.getAnnotation(Constraint.class);
      Class var3 = var2.value();
      if (var1.equals(getConstraintValidatorAnnotationType(var3))) {
         ConstraintValidator var4 = (ConstraintValidator)ReflectionUtil.instantiate(var3);
         var4.initialize(var0);
         return var4;
      } else {
         return null;
      }
   }

   private static Type getConstraintValidatorAnnotationType(Class<? extends ConstraintValidator<?>> var0) {
      Type[] var1 = var0.getGenericInterfaces();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Type var4 = var1[var3];
         if (var4 instanceof ParameterizedType) {
            ParameterizedType var5 = (ParameterizedType)var4;
            if (ConstraintValidator.class.equals(var5.getRawType())) {
               return var5.getActualTypeArguments()[0];
            }
         }
      }

      return Void.TYPE;
   }
}
