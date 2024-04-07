package net.minecraft;

import org.apache.commons.lang3.StringEscapeUtils;

public class ResourceLocationException extends RuntimeException {
   public ResourceLocationException(String var1) {
      super(StringEscapeUtils.escapeJava(var1));
   }

   public ResourceLocationException(String var1, Throwable var2) {
      super(StringEscapeUtils.escapeJava(var1), var2);
   }
}
