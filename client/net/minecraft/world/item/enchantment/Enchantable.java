package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record Enchantable(int value) {
   public static final Codec<Enchantable> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(ExtraCodecs.POSITIVE_INT.fieldOf("value").forGetter(Enchantable::value)).apply(var0, Enchantable::new)
   );
   public static final StreamCodec<ByteBuf, Enchantable> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Enchantable::value, Enchantable::new);

   public Enchantable(int value) {
      super();
      if (value <= 0) {
         throw new IllegalArgumentException("Enchantment value must be positive, but was " + value);
      } else {
         this.value = value;
      }
   }
}
