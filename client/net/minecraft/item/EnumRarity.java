package net.minecraft.item;

import net.minecraft.util.text.TextFormatting;

public enum EnumRarity {
   COMMON(TextFormatting.WHITE),
   UNCOMMON(TextFormatting.YELLOW),
   RARE(TextFormatting.AQUA),
   EPIC(TextFormatting.LIGHT_PURPLE);

   public final TextFormatting field_77937_e;

   private EnumRarity(TextFormatting var3) {
      this.field_77937_e = var3;
   }
}
