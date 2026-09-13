package net.minecraft.client;

class Minecraft$1 extends Thread {
   Minecraft$1(Minecraft var1, String var2) {
      super(var2);
      this.field_74532_a = var1;
   }

   @Override
   public void run() {
      while(this.field_74532_a.field_71425_J) {
         try {
            Thread.sleep(2147483647L);
         } catch (InterruptedException var2) {
         }
      }
   }
}
