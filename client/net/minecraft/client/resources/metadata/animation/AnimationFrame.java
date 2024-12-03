package net.minecraft.client.resources.metadata.animation;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.ExtraCodecs;

public record AnimationFrame(int index, Optional<Integer> time) {
   public static final Codec<AnimationFrame> FULL_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("index").forGetter(AnimationFrame::index), ExtraCodecs.POSITIVE_INT.optionalFieldOf("time").forGetter(AnimationFrame::time)).apply(var0, AnimationFrame::new));
   public static final Codec<AnimationFrame> CODEC;

   public AnimationFrame(int var1) {
      this(var1, Optional.empty());
   }

   public AnimationFrame(int var1, Optional<Integer> var2) {
      super();
      this.index = var1;
      this.time = var2;
   }

   public int timeOr(int var1) {
      return (Integer)this.time.orElse(var1);
   }

   static {
      CODEC = Codec.either(ExtraCodecs.NON_NEGATIVE_INT, FULL_CODEC).xmap((var0) -> (AnimationFrame)var0.map(AnimationFrame::new, (var0x) -> var0x), (var0) -> var0.time.isPresent() ? Either.right(var0) : Either.left(var0.index));
   }
}
