package io.netty.handler.codec.smtp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class SmtpUtils {
   static List<CharSequence> toUnmodifiableList(CharSequence... var0) {
      return var0 != null && var0.length != 0 ? Collections.unmodifiableList(Arrays.asList(var0)) : Collections.emptyList();
   }

   private SmtpUtils() {
      super();
   }
}
