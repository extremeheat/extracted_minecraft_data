package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class DoubleArgument extends BrigadierWrapperArgument<Double, DoubleArgumentType> {
   private static final Double MIN = -1.7976931348623157E308;
   private static final Double MAX = 1.7976931348623157E308;
   private static final MapCodec<DoubleArgument> MAP_CODEC;
   private static final StreamCodec<ByteBuf, DoubleArgument> STREAM_CODEC;
   public static final SerializableArgument.Type<DoubleArgument> TYPE;
   private final MinMaxBounds.Bounds<Double> bounds;

   private DoubleArgument(final MinMaxBounds.Bounds<Double> bounds) {
      super(DoubleArgumentType.doubleArg((Double)bounds.min().orElse(MIN), (Double)bounds.max().orElse(MAX)));
      this.bounds = bounds;
   }

   public SerializableArgument.Type<DoubleArgument> type() {
      return TYPE;
   }

   private static ArgumentType<Double> doubleArg(final MinMaxBounds.Bounds<Double> bounds) {
      return (new DoubleArgument(bounds)).toBrigadier();
   }

   public static ArgumentType<Double> doubleArg() {
      return doubleArg(MinMaxBounds.Bounds.any());
   }

   public static ArgumentType<Double> doubleArg(final double min) {
      return doubleArg(MinMaxBounds.Bounds.atLeast(min));
   }

   public static ArgumentType<Double> doubleArg(final double min, final double max) {
      return doubleArg(MinMaxBounds.Bounds.between(min, max));
   }

   public static double getDouble(final CommandContext<?> context, final String name) {
      return DoubleArgumentType.getDouble(context, name);
   }

   static {
      MAP_CODEC = MinMaxBounds.Bounds.createMapCodec(Codec.DOUBLE).xmap(DoubleArgument::new, (arg) -> arg.bounds);
      STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.DOUBLE).map(DoubleArgument::new, (arg) -> arg.bounds);
      TYPE = new SerializableArgument.Type<DoubleArgument>(MAP_CODEC, STREAM_CODEC);
   }
}
