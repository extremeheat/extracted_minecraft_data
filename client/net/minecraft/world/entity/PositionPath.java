package net.minecraft.world.entity;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public sealed interface PositionPath {
   StreamCodec<ByteBuf, PositionPath> STREAM_CODEC = PositionPath.Type.STREAM_CODEC.dispatch(PositionPath::type, Type::streamCodec);

   Vec3 endPosition();

   Type type();

   static PositionPath of(final Vec3 position) {
      return new Linear(position);
   }

   static PositionPath stepped(final List<PositionStep> steps, final boolean startImmediately) {
      return new Stepped(steps, startImmediately);
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

   public static record Stepped(Vec3 endPosition, List<PositionStep> steps, boolean startImmediately) implements PositionPath {
      public static final StreamCodec<ByteBuf, Stepped> STREAM_CODEC;

      public Stepped(final List<PositionStep> steps, final boolean startImmediately) {
         this(((PositionStep)steps.getLast()).position(), steps, startImmediately);
      }

      public Stepped {
         super();
      }

      public Type type() {
         return PositionPath.Type.STEPPED;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(PositionStep.STREAM_CODEC.apply(ByteBufCodecs.list()), Stepped::steps, ByteBufCodecs.BOOL, Stepped::startImmediately, Stepped::new);
      }
   }

   public static enum Type {
      LINEAR(0, PositionPath.Linear.STREAM_CODEC),
      STEPPED(1, PositionPath.Stepped.STREAM_CODEC);

      public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.enumCodec(Type.class, Type::getId);
      private final int id;
      private final StreamCodec<ByteBuf, ? extends PositionPath> streamCodec;

      private Type(final int id, final StreamCodec<ByteBuf, ? extends PositionPath> streamCodec) {
         this.id = id;
         this.streamCodec = streamCodec;
      }

      public int getId() {
         return this.id;
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
