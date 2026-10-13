package net.minecraft.world.entity.animal.wolf;

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

public record WolfSoundVariant(WolfSoundSet adultSounds, WolfSoundSet babySounds) implements AgeableSoundVariant<WolfSoundSet> {
   public static final Codec<WolfSoundVariant> DIRECT_CODEC;
   public static final Codec<Holder<WolfSoundVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<WolfSoundVariant>> STREAM_CODEC;

   public WolfSoundVariant {
      super();
   }

   static {
      DIRECT_CODEC = AgeableSoundVariant.codec(WolfSoundVariant.WolfSoundSet.CODEC, WolfSoundVariant::new);
      CODEC = RegistryCodecs.holder(Registries.WOLF_SOUND_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.WOLF_SOUND_VARIANT);
   }

   public static record WolfSoundSet(Optional<Holder<SoundEvent>> ambientSound, Optional<Holder<SoundEvent>> deathSound, Optional<Holder<SoundEvent>> growlSound, Optional<Holder<SoundEvent>> hurtSound, Optional<Holder<SoundEvent>> pantSound, Optional<Holder<SoundEvent>> whineSound, Optional<Holder<SoundEvent>> stepSound) {
      public static final Codec<WolfSoundSet> CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(WolfSoundSet::ambientSound), SoundEvent.CODEC.optionalFieldOf("death_sound").forGetter(WolfSoundSet::deathSound), SoundEvent.CODEC.optionalFieldOf("growl_sound").forGetter(WolfSoundSet::growlSound), SoundEvent.CODEC.optionalFieldOf("hurt_sound").forGetter(WolfSoundSet::hurtSound), SoundEvent.CODEC.optionalFieldOf("pant_sound").forGetter(WolfSoundSet::pantSound), SoundEvent.CODEC.optionalFieldOf("whine_sound").forGetter(WolfSoundSet::whineSound), SoundEvent.CODEC.optionalFieldOf("step_sound").forGetter(WolfSoundSet::stepSound)).apply(i, WolfSoundSet::new));

      public WolfSoundSet {
         super();
      }
   }
}
