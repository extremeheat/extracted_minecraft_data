package net.minecraft.server;

public class MinecraftServer$3 extends Thread {
   public MinecraftServer$3(MinecraftServer var1, String var2) {
      super(var2);
      this.field_73716_a = var1;
   }

   @Override
   public void run() {
      this.field_73716_a.run();
   }
}
