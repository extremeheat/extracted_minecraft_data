package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Locale;

public final class NettyRuntime {
   private static final NettyRuntime.AvailableProcessorsHolder holder = new NettyRuntime.AvailableProcessorsHolder();

   public static void setAvailableProcessors(int var0) {
      holder.setAvailableProcessors(var0);
   }

   public static int availableProcessors() {
      return holder.availableProcessors();
   }

   private NettyRuntime() {
      super();
   }

   static class AvailableProcessorsHolder {
      private int availableProcessors;

      AvailableProcessorsHolder() {
         super();
      }

      synchronized void setAvailableProcessors(int var1) {
         ObjectUtil.checkPositive(var1, "availableProcessors");
         if (this.availableProcessors != 0) {
            String var2 = String.format(Locale.ROOT, "availableProcessors is already set to [%d], rejecting [%d]", this.availableProcessors, var1);
            throw new IllegalStateException(var2);
         } else {
            this.availableProcessors = var1;
         }
      }

      @SuppressForbidden(
         reason = "to obtain default number of available processors"
      )
      synchronized int availableProcessors() {
         if (this.availableProcessors == 0) {
            int var1 = SystemPropertyUtil.getInt("io.netty.availableProcessors", Runtime.getRuntime().availableProcessors());
            this.setAvailableProcessors(var1);
         }

         return this.availableProcessors;
      }
   }
}
