package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record BubbleColumnUpParticleOptions(float popChance) implements ParticleOptions {
   public static final BubbleColumnUpParticleOptions HALF_POP = new BubbleColumnUpParticleOptions(0.5F);
   public static final MapCodec<BubbleColumnUpParticleOptions> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.floatRange(0.0F, 1.0F).optionalFieldOf("pop_chance", 0.0F).forGetter(BubbleColumnUpParticleOptions::popChance)).apply(i, BubbleColumnUpParticleOptions::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, BubbleColumnUpParticleOptions> STREAM_CODEC;

   public BubbleColumnUpParticleOptions {
      super();
   }

   public ParticleType<?> getType() {
      return ParticleTypes.BUBBLE_COLUMN_UP;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, BubbleColumnUpParticleOptions::popChance, BubbleColumnUpParticleOptions::new);
   }
}
