package net.minecraft.world.entity;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public interface Saddleable {
   Holder<SoundEvent> DEFAULT_EQUIP_SOUND = SoundEvents.HORSE_SADDLE;

   Holder<SoundEvent> getSaddleEquipSound();
}
