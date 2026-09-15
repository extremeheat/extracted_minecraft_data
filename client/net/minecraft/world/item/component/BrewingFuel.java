package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

public record BrewingFuel(ResolvableInt uses, ResolvableFloat speedMultiplier) {
   public static final Codec<BrewingFuel> CODEC = RecordCodecBuilder.create((i) -> i.group(ResolvableInt.CODEC.fieldOf("uses").forGetter(BrewingFuel::uses), ResolvableFloat.CODEC.fieldOf("speed_multiplier").forGetter(BrewingFuel::speedMultiplier)).apply(i, BrewingFuel::new));
   public static final StreamCodec<ByteBuf, BrewingFuel> STREAM_CODEC;

   public BrewingFuel(final ResourceKey<ContextIntProvider> uses, final ResourceKey<ContextFloatProvider> speedMultiplier) {
      this(ResolvableInt.fromKey(uses), ResolvableFloat.fromKey(speedMultiplier));
   }

   public BrewingFuel {
      super();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResolvableInt.STREAM_CODEC, BrewingFuel::uses, ResolvableFloat.STREAM_CODEC, BrewingFuel::speedMultiplier, BrewingFuel::new);
   }
}
