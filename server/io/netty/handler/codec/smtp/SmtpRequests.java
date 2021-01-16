package io.netty.handler.codec.smtp;

import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;

public final class SmtpRequests {
   private static final SmtpRequest DATA;
   private static final SmtpRequest NOOP;
   private static final SmtpRequest RSET;
   private static final SmtpRequest HELP_NO_ARG;
   private static final SmtpRequest QUIT;
   private static final AsciiString FROM_NULL_SENDER;

   public static SmtpRequest helo(CharSequence var0) {
      return new DefaultSmtpRequest(SmtpCommand.HELO, new CharSequence[]{var0});
   }

   public static SmtpRequest ehlo(CharSequence var0) {
      return new DefaultSmtpRequest(SmtpCommand.EHLO, new CharSequence[]{var0});
   }

   public static SmtpRequest noop() {
      return NOOP;
   }

   public static SmtpRequest data() {
      return DATA;
   }

   public static SmtpRequest rset() {
      return RSET;
   }

   public static SmtpRequest help(String var0) {
      return (SmtpRequest)(var0 == null ? HELP_NO_ARG : new DefaultSmtpRequest(SmtpCommand.HELP, new CharSequence[]{var0}));
   }

   public static SmtpRequest quit() {
      return QUIT;
   }

   public static SmtpRequest mail(CharSequence var0, CharSequence... var1) {
      if (var1 != null && var1.length != 0) {
         ArrayList var2 = new ArrayList(var1.length + 1);
         var2.add(var0 != null ? "FROM:<" + var0 + '>' : FROM_NULL_SENDER);
         CharSequence[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CharSequence var6 = var3[var5];
            var2.add(var6);
         }

         return new DefaultSmtpRequest(SmtpCommand.MAIL, var2);
      } else {
         return new DefaultSmtpRequest(SmtpCommand.MAIL, new CharSequence[]{(CharSequence)(var0 != null ? "FROM:<" + var0 + '>' : FROM_NULL_SENDER)});
      }
   }

   public static SmtpRequest rcpt(CharSequence var0, CharSequence... var1) {
      ObjectUtil.checkNotNull(var0, "recipient");
      if (var1 != null && var1.length != 0) {
         ArrayList var2 = new ArrayList(var1.length + 1);
         var2.add("TO:<" + var0 + '>');
         CharSequence[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CharSequence var6 = var3[var5];
            var2.add(var6);
         }

         return new DefaultSmtpRequest(SmtpCommand.RCPT, var2);
      } else {
         return new DefaultSmtpRequest(SmtpCommand.RCPT, new CharSequence[]{"TO:<" + var0 + '>'});
      }
   }

   public static SmtpRequest expn(CharSequence var0) {
      return new DefaultSmtpRequest(SmtpCommand.EXPN, new CharSequence[]{(CharSequence)ObjectUtil.checkNotNull(var0, "mailingList")});
   }

   public static SmtpRequest vrfy(CharSequence var0) {
      return new DefaultSmtpRequest(SmtpCommand.VRFY, new CharSequence[]{(CharSequence)ObjectUtil.checkNotNull(var0, "user")});
   }

   private SmtpRequests() {
      super();
   }

   static {
      DATA = new DefaultSmtpRequest(SmtpCommand.DATA);
      NOOP = new DefaultSmtpRequest(SmtpCommand.NOOP);
      RSET = new DefaultSmtpRequest(SmtpCommand.RSET);
      HELP_NO_ARG = new DefaultSmtpRequest(SmtpCommand.HELP);
      QUIT = new DefaultSmtpRequest(SmtpCommand.QUIT);
      FROM_NULL_SENDER = AsciiString.cached("FROM:<>");
   }
}
