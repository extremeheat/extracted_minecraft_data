package net.minecraft.util.random;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public record Weighted<T>(T value, int weight) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public Weighted(T var1, int var2) {
      super();
      if (var2 < 0) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Weight should be >= 0"));
      } else {
         if (var2 == 0 && SharedConstants.IS_RUNNING_IN_IDE) {
            LOGGER.warn("Found 0 weight, make sure this is intentional!");
         }

         this.value = var1;
         this.weight = var2;
      }
   }

   public static <E> Codec<Weighted<E>> codec(Codec<E> var0) {
      return codec(var0.fieldOf("data"));
   }

   public static <E> Codec<Weighted<E>> codec(MapCodec<E> var0) {
      return RecordCodecBuilder.create((var1) -> var1.group(var0.forGetter(Weighted::value), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(Weighted::weight)).apply(var1, Weighted::new));
   }

   public static <B extends ByteBuf, T> StreamCodec<B, Weighted<T>> streamCodec(StreamCodec<B, T> var0) {
      return StreamCodec.composite(var0, Weighted::value, ByteBufCodecs.VAR_INT, Weighted::weight, Weighted::new);
   }

   public <U> Weighted<U> map(Function<T, U> var1) {
      return new Weighted<U>(var1.apply(this.value()), this.weight);
   }
}
