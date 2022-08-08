package net.minecraft.world.item;

import net.minecraft.ChatFormatting;

public enum Rarity {
   COMMON(ChatFormatting.WHITE),
   UNCOMMON(ChatFormatting.YELLOW),
   RARE(ChatFormatting.AQUA),
   EPIC(ChatFormatting.LIGHT_PURPLE);

   public final ChatFormatting color;

   private Rarity(ChatFormatting var3) {
      this.color = var3;
   }

   // $FF: synthetic method
   private static Rarity[] $values() {
      return new Rarity[]{COMMON, UNCOMMON, RARE, EPIC};
   }
}
