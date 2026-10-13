package net.minecraft.world.entity.animal.cow;

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

public record CowSoundVariant(Optional<Holder<SoundEvent>> ambientSound, Optional<Holder<SoundEvent>> hurtSound, Optional<Holder<SoundEvent>> deathSound, Optional<Holder<SoundEvent>> stepSound) {
   public static final Codec<CowSoundVariant> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(CowSoundVariant::ambientSound), SoundEvent.CODEC.optionalFieldOf("hurt_sound").forGetter(CowSoundVariant::hurtSound), SoundEvent.CODEC.optionalFieldOf("death_sound").forGetter(CowSoundVariant::deathSound), SoundEvent.CODEC.optionalFieldOf("step_sound").forGetter(CowSoundVariant::stepSound)).apply(i, CowSoundVariant::new));
   public static final Codec<Holder<CowSoundVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<CowSoundVariant>> STREAM_CODEC;

   public CowSoundVariant {
      super();
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.COW_SOUND_VARIANT);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.COW_SOUND_VARIANT);
   }
}
