package org.apache.logging.log4j.core.config.plugins.validation.validators;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidator;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.util.Assert;
import org.apache.logging.log4j.status.StatusLogger;

public class RequiredValidator implements ConstraintValidator<Required> {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private Required annotation;

   public RequiredValidator() {
      super();
   }

   public void initialize(Required var1) {
      this.annotation = var1;
   }

   public boolean isValid(String var1, Object var2) {
      return Assert.isNonEmpty(var2) || this.err(var1);
   }

   private boolean err(String var1) {
      LOGGER.error(this.annotation.message() + ": " + var1);
      return false;
   }
}
