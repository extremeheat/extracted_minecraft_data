package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class IntegerArgument extends BrigadierWrapperArgument<Integer, IntegerArgumentType> {
   private static final Integer MIN = -2147483648;
   private static final Integer MAX = 2147483647;
   private static final MapCodec<IntegerArgument> MAP_CODEC;
   private static final StreamCodec<ByteBuf, IntegerArgument> STREAM_CODEC;
   public static final SerializableArgument.Type<IntegerArgument> TYPE;
   private final MinMaxBounds.Bounds<Integer> bounds;

   public IntegerArgument(final MinMaxBounds.Bounds<Integer> bounds) {
      super(IntegerArgumentType.integer((Integer)bounds.min().orElse(MIN), (Integer)bounds.max().orElse(MAX)));
      this.bounds = bounds;
   }

   public SerializableArgument.Type<IntegerArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Integer> integer(final MinMaxBounds.Bounds<Integer> bounds) {
      return (new IntegerArgument(bounds)).toBrigadier();
   }

   public static ArgumentType<Integer> integer() {
      return integer(MinMaxBounds.Bounds.any());
   }

   public static ArgumentType<Integer> integer(final int min) {
      return integer(MinMaxBounds.Bounds.atLeast(min));
   }

   public static ArgumentType<Integer> integer(final int min, final int max) {
      return integer(MinMaxBounds.Bounds.between(min, max));
   }

   public static int getInteger(final CommandContext<?> context, final String name) {
      return IntegerArgumentType.getInteger(context, name);
   }

   static {
      MAP_CODEC = MinMaxBounds.Bounds.createMapCodec(Codec.INT).xmap(IntegerArgument::new, (arg) -> arg.bounds);
      STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.INT).map(IntegerArgument::new, (arg) -> arg.bounds);
      TYPE = new SerializableArgument.Type<IntegerArgument>(MAP_CODEC, STREAM_CODEC);
   }
}
