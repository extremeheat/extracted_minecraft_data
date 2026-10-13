package net.minecraft.world.entity.animal.chicken;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.animal.AgeableSoundVariant;

public record ChickenSoundVariant(ChickenSoundSet adultSounds, ChickenSoundSet babySounds) implements AgeableSoundVariant<ChickenSoundSet> {
   public static final Codec<ChickenSoundVariant> DIRECT_CODEC;
   public static final Codec<Holder<ChickenSoundVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ChickenSoundVariant>> STREAM_CODEC;

   public ChickenSoundVariant {
      super();
   }

   static {
      DIRECT_CODEC = AgeableSoundVariant.codec(ChickenSoundVariant.ChickenSoundSet.CODEC, ChickenSoundVariant::new);
      CODEC = RegistryCodecs.holder(Registries.CHICKEN_SOUND_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.CHICKEN_SOUND_VARIANT);
   }

   public static record ChickenSoundSet(Optional<Holder<SoundEvent>> ambientSound, Optional<Holder<SoundEvent>> hurtSound, Optional<Holder<SoundEvent>> deathSound, Optional<Holder<SoundEvent>> stepSound) {
      private static final Codec<ChickenSoundSet> CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(ChickenSoundSet::ambientSound), SoundEvent.CODEC.optionalFieldOf("hurt_sound").forGetter(ChickenSoundSet::hurtSound), SoundEvent.CODEC.optionalFieldOf("death_sound").forGetter(ChickenSoundSet::deathSound), SoundEvent.CODEC.optionalFieldOf("step_sound").forGetter(ChickenSoundSet::stepSound)).apply(i, ChickenSoundSet::new));

      public ChickenSoundSet {
         super();
      }
   }
}
