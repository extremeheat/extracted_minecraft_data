package org.apache.logging.log4j.core.appender;

import java.nio.charset.StandardCharsets;

public class TlsSyslogFrame {
   private final String message;
   private final int byteLength;

   public TlsSyslogFrame(String var1) {
      super();
      this.message = var1;
      byte[] var2 = var1.getBytes(StandardCharsets.UTF_8);
      this.byteLength = var2.length;
   }

   public String getMessage() {
      return this.message;
   }

   public String toString() {
      return Integer.toString(this.byteLength) + ' ' + this.message;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.message == null ? 0 : this.message.hashCode());
      return var3;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof TlsSyslogFrame)) {
         return false;
      } else {
         TlsSyslogFrame var2 = (TlsSyslogFrame)var1;
         if (this.message == null) {
            if (var2.message != null) {
               return false;
            }
         } else if (!this.message.equals(var2.message)) {
            return false;
         }

         return true;
      }
   }
}
