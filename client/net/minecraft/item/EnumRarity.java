package net.minecraft.item;

import net.minecraft.util.EnumChatFormatting;

public enum EnumRarity {
   COMMON(EnumChatFormatting.WHITE, "Common"),
   UNCOMMON(EnumChatFormatting.YELLOW, "Uncommon"),
   RARE(EnumChatFormatting.AQUA, "Rare"),
   EPIC(EnumChatFormatting.LIGHT_PURPLE, "Epic");

   public final EnumChatFormatting field_77937_e;
   public final String field_77934_f;

   private EnumRarity(EnumChatFormatting var3, String var4) {
      this.field_77937_e = var3;
      this.field_77934_f = var4;
   }
}
