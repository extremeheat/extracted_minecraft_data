package net.minecraft.world.entity.animal.chicken;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;

public record ChickenSoundVariant(ChickenSoundSet adultSounds, ChickenSoundSet babySounds) {
   public static final Codec<ChickenSoundVariant> DIRECT_CODEC = codec();
   public static final Codec<ChickenSoundVariant> NETWORK_CODEC = codec();
   public static final Codec<Holder<ChickenSoundVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ChickenSoundVariant>> STREAM_CODEC;

   public ChickenSoundVariant {
      super();
   }

   private static Codec<ChickenSoundVariant> codec() {
      return RecordCodecBuilder.create((i) -> i.group(ChickenSoundVariant.ChickenSoundSet.CODEC.fieldOf("adult_sounds").forGetter(ChickenSoundVariant::adultSounds), ChickenSoundVariant.ChickenSoundSet.CODEC.fieldOf("baby_sounds").forGetter(ChickenSoundVariant::babySounds)).apply(i, ChickenSoundVariant::new));
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.CHICKEN_SOUND_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.CHICKEN_SOUND_VARIANT);
   }

   public static record ChickenSoundSet(Holder<SoundEvent> ambientSound, Holder<SoundEvent> hurtSound, Holder<SoundEvent> deathSound, Holder<SoundEvent> stepSound) {
      private static final Codec<ChickenSoundSet> CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.fieldOf("ambient_sound").forGetter(ChickenSoundSet::ambientSound), SoundEvent.CODEC.fieldOf("hurt_sound").forGetter(ChickenSoundSet::hurtSound), SoundEvent.CODEC.fieldOf("death_sound").forGetter(ChickenSoundSet::deathSound), SoundEvent.CODEC.fieldOf("step_sound").forGetter(ChickenSoundSet::stepSound)).apply(i, ChickenSoundSet::new));

      public ChickenSoundSet {
         super();
      }
   }
}
