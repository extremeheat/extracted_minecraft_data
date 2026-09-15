package net.minecraft.world.entity;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.phys.Vec3;

public sealed interface PositionPath {
   StreamCodec<ByteBuf, PositionPath> STREAM_CODEC = PositionPath.Type.STREAM_CODEC.dispatch(PositionPath::type, Type::streamCodec);

   Vec3 endPosition();

   Type type();

   static PositionPath of(final Vec3 position) {
      return new Linear(position);
   }

   static PositionPath stepped(final List<PositionStep> steps) {
      return new Stepped(steps);
   }

   public static record Linear(Vec3 endPosition) implements PositionPath {
      public static final StreamCodec<ByteBuf, Linear> STREAM_CODEC;

      public Linear {
         super();
      }

      public Type type() {
         return PositionPath.Type.LINEAR;
      }

      static {
         STREAM_CODEC = Vec3.STREAM_CODEC.map(Linear::new, Linear::endPosition);
      }
   }

   public static record Stepped(Vec3 endPosition, List<PositionStep> steps) implements PositionPath {
      public static final StreamCodec<ByteBuf, Stepped> STREAM_CODEC;

      public Stepped(final List<PositionStep> steps) {
         this(((PositionStep)steps.getLast()).position(), steps);
      }

      public Stepped {
         super();
      }

      public Type type() {
         return PositionPath.Type.STEPPED;
      }

      static {
         STREAM_CODEC = PositionStep.STREAM_CODEC.apply(ByteBufCodecs.list()).map(Stepped::new, Stepped::steps);
      }
   }

   public static enum Type {
      LINEAR(PositionPath.Linear.STREAM_CODEC),
      STEPPED(PositionPath.Stepped.STREAM_CODEC);

      public static final IntFunction<Type> BY_ID = ByIdMap.<Type>continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
      private final StreamCodec<ByteBuf, ? extends PositionPath> streamCodec;

      private Type(final StreamCodec<ByteBuf, ? extends PositionPath> streamCodec) {
         this.streamCodec = streamCodec;
      }

      public StreamCodec<ByteBuf, ? extends PositionPath> streamCodec() {
         return this.streamCodec;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{LINEAR, STEPPED};
      }
   }
}
