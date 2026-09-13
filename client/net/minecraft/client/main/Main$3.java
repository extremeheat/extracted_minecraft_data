package net.minecraft.client.main;

import net.minecraft.client.Minecraft;

public final class Main$3 extends Thread {
   public Main$3(String var1) {
      super(var1);
   }

   @Override
   public void run() {
      Minecraft.func_71363_D();
   }
}
