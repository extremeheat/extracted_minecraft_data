package net.minecraft.server;

import net.minecraft.util.IProgressUpdate;

public class MinecraftServer$1 implements IProgressUpdate {
   private long field_96245_b;

   public MinecraftServer$1(MinecraftServer var1) {
      super();
      this.field_74267_a = var1;
      this.field_96245_b = MinecraftServer.func_130071_aq();
   }

   @Override
   public void func_73720_a(String var1) {
   }

   @Override
   public void func_73721_b(String var1) {
   }

   @Override
   public void func_73718_a(int var1) {
      if (MinecraftServer.func_130071_aq() - this.field_96245_b >= 1000L) {
         this.field_96245_b = MinecraftServer.func_130071_aq();
         MinecraftServer.access$000().info("Converting... " + var1 + "%");
      }
   }

   @Override
   public void func_146586_a() {
   }

   @Override
   public void func_73719_c(String var1) {
   }
}
