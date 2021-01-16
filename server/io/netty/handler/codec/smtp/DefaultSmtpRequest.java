package io.netty.handler.codec.smtp;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;

public final class DefaultSmtpRequest implements SmtpRequest {
   private final SmtpCommand command;
   private final List<CharSequence> parameters;

   public DefaultSmtpRequest(SmtpCommand var1) {
      super();
      this.command = (SmtpCommand)ObjectUtil.checkNotNull(var1, "command");
      this.parameters = Collections.emptyList();
   }

   public DefaultSmtpRequest(SmtpCommand var1, CharSequence... var2) {
      super();
      this.command = (SmtpCommand)ObjectUtil.checkNotNull(var1, "command");
      this.parameters = SmtpUtils.toUnmodifiableList(var2);
   }

   public DefaultSmtpRequest(CharSequence var1, CharSequence... var2) {
      this(SmtpCommand.valueOf(var1), var2);
   }

   DefaultSmtpRequest(SmtpCommand var1, List<CharSequence> var2) {
      super();
      this.command = (SmtpCommand)ObjectUtil.checkNotNull(var1, "command");
      this.parameters = var2 != null ? Collections.unmodifiableList(var2) : Collections.emptyList();
   }

   public SmtpCommand command() {
      return this.command;
   }

   public List<CharSequence> parameters() {
      return this.parameters;
   }

   public int hashCode() {
      return this.command.hashCode() * 31 + this.parameters.hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultSmtpRequest)) {
         return false;
      } else if (var1 == this) {
         return true;
      } else {
         DefaultSmtpRequest var2 = (DefaultSmtpRequest)var1;
         return this.command().equals(var2.command()) && this.parameters().equals(var2.parameters());
      }
   }

   public String toString() {
      return "DefaultSmtpRequest{command=" + this.command + ", parameters=" + this.parameters + '}';
   }
}
