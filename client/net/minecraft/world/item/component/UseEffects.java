package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record UseEffects(boolean canSprint, float speedMultiplier) {
   public static final UseEffects DEFAULT = new UseEffects(false, 0.2F);
   public static final Codec<UseEffects> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.optionalFieldOf("can_sprint", DEFAULT.canSprint).forGetter(UseEffects::canSprint), Codec.floatRange(0.0F, 1.0F).optionalFieldOf("speed_multiplier", DEFAULT.speedMultiplier).forGetter(UseEffects::speedMultiplier)).apply(var0, UseEffects::new));
   public static final StreamCodec<ByteBuf, UseEffects> STREAM_CODEC;

   public UseEffects(boolean var1, float var2) {
      super();
      this.canSprint = var1;
      this.speedMultiplier = var2;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, UseEffects::canSprint, ByteBufCodecs.FLOAT, UseEffects::speedMultiplier, UseEffects::new);
   }
}
