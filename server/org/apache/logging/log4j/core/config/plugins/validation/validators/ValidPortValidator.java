package org.apache.logging.log4j.core.config.plugins.validation.validators;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidator;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidPort;
import org.apache.logging.log4j.status.StatusLogger;

public class ValidPortValidator implements ConstraintValidator<ValidPort> {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private ValidPort annotation;

   public ValidPortValidator() {
      super();
   }

   public void initialize(ValidPort var1) {
      this.annotation = var1;
   }

   public boolean isValid(String var1, Object var2) {
      if (var2 instanceof CharSequence) {
         return this.isValid(var1, TypeConverters.convert(var2.toString(), Integer.class, -1));
      } else if (!Integer.class.isInstance(var2)) {
         LOGGER.error(this.annotation.message());
         return false;
      } else {
         int var3 = (Integer)var2;
         if (var3 >= 0 && var3 <= 65535) {
            return true;
         } else {
            LOGGER.error(this.annotation.message());
            return false;
         }
      }
   }
}
