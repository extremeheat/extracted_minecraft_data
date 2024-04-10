package net.minecraft.world.effect;

import net.minecraft.ChatFormatting;

public enum MobEffectCategory {
   BENEFICIAL(ChatFormatting.BLUE),
   HARMFUL(ChatFormatting.RED),
   NEUTRAL(ChatFormatting.BLUE);

   private final ChatFormatting tooltipFormatting;

   private MobEffectCategory(final ChatFormatting param3) {
      this.tooltipFormatting = nullxx;
   }

   public ChatFormatting getTooltipFormatting() {
      return this.tooltipFormatting;
   }
}
