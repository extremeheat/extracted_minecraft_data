package net.minecraft.client;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.stats.IStatStringFormat;

class Minecraft$2 implements IStatStringFormat {
   Minecraft$2(Minecraft var1) {
      super();
      this.field_74536_a = var1;
   }

   @Override
   public String func_74535_a(String var1) {
      try {
         return String.format(var1, GameSettings.func_74298_c(this.field_74536_a.field_71474_y.field_151445_Q.func_151463_i()));
      } catch (Exception var3) {
         return "Error: " + var3.getLocalizedMessage();
      }
   }
}
