package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class GlowInkSacItem extends Item implements SignApplicator {
   public GlowInkSacItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public boolean tryApplyToSign(Level var1, SignBlockEntity var2, boolean var3, Player var4) {
      if (var2.updateText(var0 -> var0.setHasGlowingText(true), var3)) {
         var1.playSound(null, var2.getBlockPos(), SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }
}
