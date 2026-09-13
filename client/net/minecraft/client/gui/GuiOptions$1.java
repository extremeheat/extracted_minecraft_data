package net.minecraft.client.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;

class GuiOptions$1 extends GuiButton {
   GuiOptions$1(GuiOptions var1, int var2, int var3, int var4, int var5, int var6, String var7) {
      super(var2, var3, var4, var5, var6, var7);
      this.field_146130_o = var1;
   }

   @Override
   public void func_146113_a(SoundHandler var1) {
      SoundEventAccessorComposite var2 = var1.func_147686_a(
         SoundCategory.ANIMALS, SoundCategory.BLOCKS, SoundCategory.MOBS, SoundCategory.PLAYERS, SoundCategory.WEATHER
      );
      if (var2 != null) {
         var1.func_147682_a(PositionedSoundRecord.func_147674_a(var2.func_148729_c(), 0.5F));
      }
   }
}
