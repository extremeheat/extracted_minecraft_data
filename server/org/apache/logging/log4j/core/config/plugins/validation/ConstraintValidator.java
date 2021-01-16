package org.apache.logging.log4j.core.config.plugins.validation;

import java.lang.annotation.Annotation;

public interface ConstraintValidator<A extends Annotation> {
   void initialize(A var1);

   boolean isValid(String var1, Object var2);
}
