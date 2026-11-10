package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class LongArgument extends BrigadierWrapperArgument<Long, LongArgumentType> {
   private static final Long MIN = -9223372036854775808L;
   private static final Long MAX = 9223372036854775807L;
   private static final MapCodec<LongArgument> MAP_CODEC;
   private static final StreamCodec<ByteBuf, LongArgument> STREAM_CODEC;
   public static final SerializableArgument.Type<LongArgument> TYPE;
   private final MinMaxBounds.Bounds<Long> bounds;

   private LongArgument(final MinMaxBounds.Bounds<Long> bounds) {
      super(LongArgumentType.longArg((Long)bounds.min().orElse(MIN), (Long)bounds.max().orElse(MAX)));
      this.bounds = bounds;
   }

   public SerializableArgument.Type<LongArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Long> longArg(final MinMaxBounds.Bounds<Long> bounds) {
      return (new LongArgument(bounds)).toBrigadier();
   }

   public static ArgumentType<Long> longArg() {
      return longArg(MinMaxBounds.Bounds.any());
   }

   public static ArgumentType<Long> longArg(final long min) {
      return longArg(MinMaxBounds.Bounds.atLeast(min));
   }

   public static ArgumentType<Long> longArg(final long min, final long max) {
      return longArg(MinMaxBounds.Bounds.between(min, max));
   }

   public static long getLong(final CommandContext<?> context, final String name) {
      return LongArgumentType.getLong(context, name);
   }

   static {
      MAP_CODEC = MinMaxBounds.Bounds.createMapCodec(Codec.LONG).xmap(LongArgument::new, (arg) -> arg.bounds);
      STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.LONG).map(LongArgument::new, (arg) -> arg.bounds);
      TYPE = new SerializableArgument.Type<LongArgument>(MAP_CODEC, STREAM_CODEC);
   }
}
