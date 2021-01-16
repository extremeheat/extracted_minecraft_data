package io.netty.handler.codec.string;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public final class LineSeparator {
   public static final LineSeparator DEFAULT;
   public static final LineSeparator UNIX;
   public static final LineSeparator WINDOWS;
   private final String value;

   public LineSeparator(String var1) {
      super();
      this.value = (String)ObjectUtil.checkNotNull(var1, "lineSeparator");
   }

   public String value() {
      return this.value;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LineSeparator)) {
         return false;
      } else {
         LineSeparator var2 = (LineSeparator)var1;
         return this.value != null ? this.value.equals(var2.value) : var2.value == null;
      }
   }

   public int hashCode() {
      return this.value != null ? this.value.hashCode() : 0;
   }

   public String toString() {
      return ByteBufUtil.hexDump(this.value.getBytes(CharsetUtil.UTF_8));
   }

   static {
      DEFAULT = new LineSeparator(StringUtil.NEWLINE);
      UNIX = new LineSeparator("\n");
      WINDOWS = new LineSeparator("\r\n");
   }
}
