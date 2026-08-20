package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.ResolvableNumber;

public record BrewingFuel(ResolvableNumber uses, ResolvableNumber speedMultiplier) {
   public static final Codec<BrewingFuel> CODEC = RecordCodecBuilder.create((i) -> i.group(ResolvableNumber.CODEC.fieldOf("uses").forGetter(BrewingFuel::uses), ResolvableNumber.CODEC.fieldOf("speed_multiplier").forGetter(BrewingFuel::speedMultiplier)).apply(i, BrewingFuel::new));
   public static final StreamCodec<ByteBuf, BrewingFuel> STREAM_CODEC;

   public BrewingFuel(final ResourceKey<NumberProvider> uses, final ResourceKey<NumberProvider> speedMultiplier) {
      this(ResolvableNumber.fromKey(uses), ResolvableNumber.fromKey(speedMultiplier));
   }

   public BrewingFuel {
      super();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResolvableNumber.STREAM_CODEC, BrewingFuel::uses, ResolvableNumber.STREAM_CODEC, BrewingFuel::speedMultiplier, BrewingFuel::new);
   }
}
