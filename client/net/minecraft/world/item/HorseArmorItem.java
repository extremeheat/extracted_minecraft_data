package net.minecraft.world.item;

import net.minecraft.resources.ResourceLocation;

public class HorseArmorItem extends Item {
   private static final String TEX_FOLDER = "textures/entity/horse/";
   private final int protection;
   private final String texture;

   public HorseArmorItem(int var1, String var2, Item.Properties var3) {
      super(var3);
      this.protection = var1;
      this.texture = "textures/entity/horse/armor/horse_armor_" + var2 + ".png";
   }

   public ResourceLocation getTexture() {
      return new ResourceLocation(this.texture);
   }

   public int getProtection() {
      return this.protection;
   }
}
