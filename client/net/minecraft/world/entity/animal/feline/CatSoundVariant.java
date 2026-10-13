package net.minecraft.world.entity.animal.feline;

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

public record CatSoundVariant(CatSoundSet adultSounds, CatSoundSet babySounds) implements AgeableSoundVariant<CatSoundSet> {
   public static final Codec<CatSoundVariant> DIRECT_CODEC;
   public static final Codec<Holder<CatSoundVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<CatSoundVariant>> STREAM_CODEC;

   public CatSoundVariant {
      super();
   }

   static {
      DIRECT_CODEC = AgeableSoundVariant.codec(CatSoundVariant.CatSoundSet.CODEC, CatSoundVariant::new);
      CODEC = RegistryCodecs.holder(Registries.CAT_SOUND_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.CAT_SOUND_VARIANT);
   }

   public static record CatSoundSet(Optional<Holder<SoundEvent>> ambientSound, Optional<Holder<SoundEvent>> strayAmbientSound, Optional<Holder<SoundEvent>> hissSound, Optional<Holder<SoundEvent>> hurtSound, Optional<Holder<SoundEvent>> deathSound, Optional<Holder<SoundEvent>> eatSound, Optional<Holder<SoundEvent>> begForFoodSound, Optional<Holder<SoundEvent>> purrSound, Optional<Holder<SoundEvent>> purreowSound) {
      private static final Codec<CatSoundSet> CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(CatSoundSet::ambientSound), SoundEvent.CODEC.optionalFieldOf("stray_ambient_sound").forGetter(CatSoundSet::strayAmbientSound), SoundEvent.CODEC.optionalFieldOf("hiss_sound").forGetter(CatSoundSet::hissSound), SoundEvent.CODEC.optionalFieldOf("hurt_sound").forGetter(CatSoundSet::hurtSound), SoundEvent.CODEC.optionalFieldOf("death_sound").forGetter(CatSoundSet::deathSound), SoundEvent.CODEC.optionalFieldOf("eat_sound").forGetter(CatSoundSet::eatSound), SoundEvent.CODEC.optionalFieldOf("beg_for_food_sound").forGetter(CatSoundSet::begForFoodSound), SoundEvent.CODEC.optionalFieldOf("purr_sound").forGetter(CatSoundSet::purrSound), SoundEvent.CODEC.optionalFieldOf("purreow_sound").forGetter(CatSoundSet::purreowSound)).apply(i, CatSoundSet::new));

      public CatSoundSet {
         super();
      }
   }
}
