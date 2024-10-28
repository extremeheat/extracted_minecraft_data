package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

public record SoundEvent(ResourceLocation location, Optional<Float> fixedRange) {
   public static final Codec<SoundEvent> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ResourceLocation.CODEC.fieldOf("sound_id").forGetter(SoundEvent::location), Codec.FLOAT.lenientOptionalFieldOf("range").forGetter(SoundEvent::fixedRange)).apply(var0, SoundEvent::create);
   });
   public static final Codec<Holder<SoundEvent>> CODEC;
   public static final StreamCodec<ByteBuf, SoundEvent> DIRECT_STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SoundEvent>> STREAM_CODEC;

   public SoundEvent(ResourceLocation var1, Optional<Float> var2) {
      super();
      this.location = var1;
      this.fixedRange = var2;
   }

   private static SoundEvent create(ResourceLocation var0, Optional<Float> var1) {
      return (SoundEvent)var1.map((var1x) -> {
         return createFixedRangeEvent(var0, var1x);
      }).orElseGet(() -> {
         return createVariableRangeEvent(var0);
      });
   }

   public static SoundEvent createVariableRangeEvent(ResourceLocation var0) {
      return new SoundEvent(var0, Optional.empty());
   }

   public static SoundEvent createFixedRangeEvent(ResourceLocation var0, float var1) {
      return new SoundEvent(var0, Optional.of(var1));
   }

   public float getRange(float var1) {
      return (Float)this.fixedRange.orElse(var1 > 1.0F ? 16.0F * var1 : 16.0F);
   }

   public ResourceLocation location() {
      return this.location;
   }

   public Optional<Float> fixedRange() {
      return this.fixedRange;
   }

   static {
      CODEC = RegistryFileCodec.create(Registries.SOUND_EVENT, DIRECT_CODEC);
      DIRECT_STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, SoundEvent::location, ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), SoundEvent::fixedRange, SoundEvent::create);
      STREAM_CODEC = ByteBufCodecs.holder(Registries.SOUND_EVENT, DIRECT_STREAM_CODEC);
   }
}
