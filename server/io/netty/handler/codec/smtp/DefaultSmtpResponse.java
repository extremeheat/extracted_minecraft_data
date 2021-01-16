package io.netty.handler.codec.smtp;

import java.util.Collections;
import java.util.List;

public final class DefaultSmtpResponse implements SmtpResponse {
   private final int code;
   private final List<CharSequence> details;

   public DefaultSmtpResponse(int var1) {
      this(var1, (List)null);
   }

   public DefaultSmtpResponse(int var1, CharSequence... var2) {
      this(var1, SmtpUtils.toUnmodifiableList(var2));
   }

   DefaultSmtpResponse(int var1, List<CharSequence> var2) {
      super();
      if (var1 >= 100 && var1 <= 599) {
         this.code = var1;
         if (var2 == null) {
            this.details = Collections.emptyList();
         } else {
            this.details = Collections.unmodifiableList(var2);
         }

      } else {
         throw new IllegalArgumentException("code must be 100 <= code <= 599");
      }
   }

   public int code() {
      return this.code;
   }

   public List<CharSequence> details() {
      return this.details;
   }

   public int hashCode() {
      return this.code * 31 + this.details.hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultSmtpResponse)) {
         return false;
      } else if (var1 == this) {
         return true;
      } else {
         DefaultSmtpResponse var2 = (DefaultSmtpResponse)var1;
         return this.code() == var2.code() && this.details().equals(var2.details());
      }
   }

   public String toString() {
      return "DefaultSmtpResponse{code=" + this.code + ", details=" + this.details + '}';
   }
}
