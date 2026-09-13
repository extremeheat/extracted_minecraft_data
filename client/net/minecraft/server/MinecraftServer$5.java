package net.minecraft.server;

import java.util.concurrent.Callable;

public class MinecraftServer$5 implements Callable {
   public MinecraftServer$5(MinecraftServer var1) {
      super();
      this.field_74272_a = var1;
   }

   public String call() {
      byte var1 = 0;
      int var2 = 56 * var1;
      int var3 = var2 / 1024 / 1024;
      byte var4 = 0;
      int var5 = 56 * var4;
      int var6 = var5 / 1024 / 1024;
      return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; " + var6 + " MB) used";
   }
}
