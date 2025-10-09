package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.SwingAnimationType;

public record SwingAnimation(SwingAnimationType type, int duration) {
   public static final SwingAnimation DEFAULT;
   public static final Codec<SwingAnimation> CODEC;
   public static final StreamCodec<ByteBuf, SwingAnimation> STREAM_CODEC;

   public SwingAnimation(SwingAnimationType var1, int var2) {
      super();
      this.type = var1;
      this.duration = var2;
   }

   static {
      DEFAULT = new SwingAnimation(SwingAnimationType.WHACK, 6);
      CODEC = RecordCodecBuilder.create((var0) -> var0.group(SwingAnimationType.CODEC.optionalFieldOf("type", DEFAULT.type).forGetter(SwingAnimation::type), ExtraCodecs.POSITIVE_INT.optionalFieldOf("duration", DEFAULT.duration).forGetter(SwingAnimation::duration)).apply(var0, SwingAnimation::new));
      STREAM_CODEC = StreamCodec.composite(SwingAnimationType.STREAM_CODEC, SwingAnimation::type, ByteBufCodecs.VAR_INT, SwingAnimation::duration, SwingAnimation::new);
   }
}
