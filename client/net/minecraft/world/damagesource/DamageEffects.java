package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;

public enum DamageEffects implements StringRepresentable {
   HURT("hurt", SoundEvents.PLAYER_HURT),
   THORNS("thorns", SoundEvents.PLAYER_HURT),
   DROWNING("drowning", SoundEvents.PLAYER_HURT_DROWN),
   BURNING("burning", SoundEvents.PLAYER_HURT_ON_FIRE),
   POKING("poking", SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH),
   FREEZING("freezing", SoundEvents.PLAYER_HURT_FREEZE);

   public static final Codec<DamageEffects> CODEC = StringRepresentable.<DamageEffects>fromEnum(DamageEffects::values);
   private final String id;
   private final SoundEvent sound;

   private DamageEffects(final String id, final SoundEvent sound) {
      this.id = id;
      this.sound = sound;
   }

   public String getSerializedName() {
      return this.id;
   }

   public SoundEvent sound() {
      return this.sound;
   }

   // $FF: synthetic method
   private static DamageEffects[] $values() {
      return new DamageEffects[]{HURT, THORNS, DROWNING, BURNING, POKING, FREEZING};
   }
}
