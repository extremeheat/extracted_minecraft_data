package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public interface Saddleable {
   boolean isSaddleable();

   void equipSaddle(ItemStack var1, @Nullable SoundSource var2);

   default SoundEvent getSaddleSoundEvent() {
      return SoundEvents.HORSE_SADDLE;
   }

   boolean isSaddled();
}
