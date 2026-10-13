package net.minecraft.world.entity.animal.pig;

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

public record PigSoundVariant(PigSoundSet adultSounds, PigSoundSet babySounds) implements AgeableSoundVariant<PigSoundSet> {
   public static final Codec<PigSoundVariant> DIRECT_CODEC;
   public static final Codec<Holder<PigSoundVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PigSoundVariant>> STREAM_CODEC;

   public PigSoundVariant {
      super();
   }

   static {
      DIRECT_CODEC = AgeableSoundVariant.codec(PigSoundVariant.PigSoundSet.CODEC, PigSoundVariant::new);
      CODEC = RegistryCodecs.holder(Registries.PIG_SOUND_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.PIG_SOUND_VARIANT);
   }

   public static record PigSoundSet(Optional<Holder<SoundEvent>> ambientSound, Optional<Holder<SoundEvent>> hurtSound, Optional<Holder<SoundEvent>> deathSound, Optional<Holder<SoundEvent>> stepSound, Optional<Holder<SoundEvent>> eatSound) {
      public static final Codec<PigSoundSet> CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(PigSoundSet::ambientSound), SoundEvent.CODEC.optionalFieldOf("hurt_sound").forGetter(PigSoundSet::hurtSound), SoundEvent.CODEC.optionalFieldOf("death_sound").forGetter(PigSoundSet::deathSound), SoundEvent.CODEC.optionalFieldOf("step_sound").forGetter(PigSoundSet::stepSound), SoundEvent.CODEC.optionalFieldOf("eat_sound").forGetter(PigSoundSet::eatSound)).apply(i, PigSoundSet::new));

      public PigSoundSet {
         super();
      }
   }
}
