package net.minecraft.world.entity.animal.wolf;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;

public record WolfSoundVariant(WolfSoundSet adultSounds, WolfSoundSet babySounds) {
   public static final Codec<WolfSoundVariant> DIRECT_CODEC = getWolfSoundVariantCodec();
   public static final Codec<WolfSoundVariant> NETWORK_CODEC = getWolfSoundVariantCodec();
   public static final Codec<Holder<WolfSoundVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<WolfSoundVariant>> STREAM_CODEC;

   public WolfSoundVariant {
      super();
   }

   private static Codec<WolfSoundVariant> getWolfSoundVariantCodec() {
      return RecordCodecBuilder.create((i) -> i.group(WolfSoundVariant.WolfSoundSet.CODEC.fieldOf("adult_sounds").forGetter(WolfSoundVariant::adultSounds), WolfSoundVariant.WolfSoundSet.CODEC.fieldOf("baby_sounds").forGetter(WolfSoundVariant::babySounds)).apply(i, WolfSoundVariant::new));
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.WOLF_SOUND_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.WOLF_SOUND_VARIANT);
   }

   public static record WolfSoundSet(Holder<SoundEvent> ambientSound, Holder<SoundEvent> deathSound, Holder<SoundEvent> growlSound, Holder<SoundEvent> hurtSound, Holder<SoundEvent> pantSound, Holder<SoundEvent> whineSound, Holder<SoundEvent> stepSound) {
      public static final Codec<WolfSoundSet> CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.fieldOf("ambient_sound").forGetter(WolfSoundSet::ambientSound), SoundEvent.CODEC.fieldOf("death_sound").forGetter(WolfSoundSet::deathSound), SoundEvent.CODEC.fieldOf("growl_sound").forGetter(WolfSoundSet::growlSound), SoundEvent.CODEC.fieldOf("hurt_sound").forGetter(WolfSoundSet::hurtSound), SoundEvent.CODEC.fieldOf("pant_sound").forGetter(WolfSoundSet::pantSound), SoundEvent.CODEC.fieldOf("whine_sound").forGetter(WolfSoundSet::whineSound), SoundEvent.CODEC.fieldOf("step_sound").forGetter(WolfSoundSet::stepSound)).apply(i, WolfSoundSet::new));

      public WolfSoundSet {
         super();
      }
   }
}
