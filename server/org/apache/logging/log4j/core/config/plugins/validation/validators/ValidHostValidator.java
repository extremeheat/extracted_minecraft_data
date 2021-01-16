package org.apache.logging.log4j.core.config.plugins.validation.validators;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidator;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidHost;
import org.apache.logging.log4j.status.StatusLogger;

public class ValidHostValidator implements ConstraintValidator<ValidHost> {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private ValidHost annotation;

   public ValidHostValidator() {
      super();
   }

   public void initialize(ValidHost var1) {
      this.annotation = var1;
   }

   public boolean isValid(String var1, Object var2) {
      if (var2 == null) {
         LOGGER.error(this.annotation.message());
         return false;
      } else if (var2 instanceof InetAddress) {
         return true;
      } else {
         try {
            InetAddress.getByName(var2.toString());
            return true;
         } catch (UnknownHostException var4) {
            LOGGER.error((String)this.annotation.message(), (Throwable)var4);
            return false;
         }
      }
   }
}
