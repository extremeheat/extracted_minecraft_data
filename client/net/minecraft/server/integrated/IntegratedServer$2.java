package net.minecraft.server.integrated;

import java.util.concurrent.Callable;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;

class IntegratedServer$2 implements Callable {
   IntegratedServer$2(IntegratedServer var1) {
      super();
      this.field_76972_a = var1;
   }

   public String call() {
      String var1 = ClientBrandRetriever.getClientModName();
      if (!var1.equals("vanilla")) {
         return "Definitely; Client brand changed to '" + var1 + "'";
      } else {
         var1 = this.field_76972_a.getServerModName();
         if (!var1.equals("vanilla")) {
            return "Definitely; Server brand changed to '" + var1 + "'";
         } else {
            return Minecraft.class.getSigners() == null
               ? "Very likely; Jar signature invalidated"
               : "Probably not. Jar signature remains and both client + server brands are untouched.";
         }
      }
   }
}
