package net.minecraft.world.level.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class StoneButtonBlock extends ButtonBlock {
   protected StoneButtonBlock(Block.Properties var1) {
      super(false, var1);
   }

   protected SoundEvent getSound(boolean var1) {
      return var1 ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
   }
}
