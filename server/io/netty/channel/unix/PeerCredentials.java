package io.netty.channel.unix;

import io.netty.util.internal.EmptyArrays;

public final class PeerCredentials {
   private final int pid;
   private final int uid;
   private final int[] gids;

   PeerCredentials(int var1, int var2, int... var3) {
      super();
      this.pid = var1;
      this.uid = var2;
      this.gids = var3 == null ? EmptyArrays.EMPTY_INTS : var3;
   }

   public int pid() {
      return this.pid;
   }

   public int uid() {
      return this.uid;
   }

   public int[] gids() {
      return (int[])this.gids.clone();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append("UserCredentials[pid=").append(this.pid).append("; uid=").append(this.uid).append("; gids=[");
      if (this.gids.length > 0) {
         var1.append(this.gids[0]);

         for(int var2 = 1; var2 < this.gids.length; ++var2) {
            var1.append(", ").append(this.gids[var2]);
         }
      }

      var1.append(']');
      return var1.toString();
   }
}
