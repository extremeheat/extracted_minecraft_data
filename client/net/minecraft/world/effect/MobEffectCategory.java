package net.minecraft.world.effect;

import net.minecraft.ChatFormatting;

public enum MobEffectCategory {
   BENEFICIAL(ChatFormatting.BLUE),
   HARMFUL(ChatFormatting.RED),
   NEUTRAL(ChatFormatting.BLUE);

   private final ChatFormatting tooltipFormatting;

   private MobEffectCategory(final ChatFormatting tooltipFormatting) {
      this.tooltipFormatting = tooltipFormatting;
   }

   public ChatFormatting getTooltipFormatting() {
      return this.tooltipFormatting;
   }

   // $FF: synthetic method
   private static MobEffectCategory[] $values() {
      return new MobEffectCategory[]{BENEFICIAL, HARMFUL, NEUTRAL};
   }
}
