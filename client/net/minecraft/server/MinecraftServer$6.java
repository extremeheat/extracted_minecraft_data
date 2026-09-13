package net.minecraft.server;

import java.util.concurrent.Callable;

public class MinecraftServer$6 implements Callable {
   public MinecraftServer$6(MinecraftServer var1) {
      super();
      this.field_74270_a = var1;
   }

   public String call() {
      return MinecraftServer.access$100(this.field_74270_a).func_72394_k()
         + " / "
         + MinecraftServer.access$100(this.field_74270_a).func_72352_l()
         + "; "
         + MinecraftServer.access$100(this.field_74270_a).field_72404_b;
   }
}
