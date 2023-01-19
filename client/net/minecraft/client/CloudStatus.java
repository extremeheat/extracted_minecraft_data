package net.minecraft.client;

import net.minecraft.util.OptionEnum;

public enum CloudStatus implements OptionEnum {
   OFF(0, "options.off"),
   FAST(1, "options.clouds.fast"),
   FANCY(2, "options.clouds.fancy");

   private final int id;
   private final String key;

   private CloudStatus(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   @Override
   public int getId() {
      return this.id;
   }

   @Override
   public String getKey() {
      return this.key;
   }
}
