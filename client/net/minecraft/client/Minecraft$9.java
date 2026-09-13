package net.minecraft.client;

import java.util.concurrent.Callable;

class Minecraft$9 implements Callable {
   Minecraft$9(Minecraft var1) {
      super();
      this.field_82887_a = var1;
   }

   public String call() {
      String var1 = ClientBrandRetriever.getClientModName();
      if (!var1.equals("vanilla")) {
         return "Definitely; Client brand changed to '" + var1 + "'";
      } else {
         return Minecraft.class.getSigners() == null
            ? "Very likely; Jar signature invalidated"
            : "Probably not. Jar signature remains and client brand is untouched.";
      }
   }
}
