package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.ResolvableNumber;

public record Compostable(ResolvableNumber layers) {
   public static final Codec<Compostable> CODEC = RecordCodecBuilder.create((i) -> i.group(ResolvableNumber.CODEC.fieldOf("layers").forGetter(Compostable::layers)).apply(i, Compostable::new));
   public static final StreamCodec<ByteBuf, Compostable> STREAM_CODEC;

   public Compostable(final ResourceKey<NumberProvider> layers) {
      this(ResolvableNumber.fromKey(layers));
   }

   public Compostable {
      super();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResolvableNumber.STREAM_CODEC, Compostable::layers, Compostable::new);
   }
}
