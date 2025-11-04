package net.minecraft;

import org.apache.commons.lang3.StringEscapeUtils;

public class IdentifierException extends RuntimeException {
   public IdentifierException(String var1) {
      super(StringEscapeUtils.escapeJava(var1));
   }

   public IdentifierException(String var1, Throwable var2) {
      super(StringEscapeUtils.escapeJava(var1), var2);
   }
}
