package net.minecraft.world.entity.livingblock;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface Target {
   Target NONE = new Target() {
      public @Nullable Vec3 resolvePosition(final Level level) {
         return null;
      }

      public double distance() {
         return 0.0;
      }

      public Type type() {
         return Target.Type.NONE;
      }
   };
   StreamCodec<ByteBuf, Target> STREAM_CODEC = Target.Type.STREAM_CODEC.dispatch(Target::type, Type::streamCodec);

   @Nullable Vec3 resolvePosition(Level level);

   double distance();

   default boolean clearWhenNear() {
      return true;
   }

   Type type();

   static Target near(final Vec3 position, final double distance) {
      return new PositionTarget(position, distance);
   }

   static Target exactlyAt(final Vec3 position) {
      return near(position, 1.0E-6);
   }

   static Target nearEntity(final @Nullable Entity entity, final double distance) {
      return (Target)(entity == null ? NONE : new EntityTarget(EntityReference.of(entity), distance, true));
   }

   static Target followingEntity(final @Nullable Entity entity, final double distance) {
      return (Target)(entity == null ? NONE : new EntityTarget(EntityReference.of(entity), distance, false));
   }

   static Target exactlyAtEntity(final @Nullable Entity entity) {
      return nearEntity(entity, 1.0E-6);
   }

   public static record PositionTarget(Vec3 position, double distance) implements Target {
      public static final StreamCodec<ByteBuf, PositionTarget> STREAM_CODEC;

      public PositionTarget {
         super();
      }

      public Vec3 resolvePosition(final Level level) {
         return this.position;
      }

      public Type type() {
         return Target.Type.POSITION;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, PositionTarget::position, ByteBufCodecs.DOUBLE, PositionTarget::distance, PositionTarget::new);
      }
   }

   public static record EntityTarget(EntityReference<Entity> entityReference, double distance, boolean clearWhenNear) implements Target {
      public static final StreamCodec<ByteBuf, EntityTarget> STREAM_CODEC;

      public EntityTarget {
         super();
      }

      public @Nullable Vec3 resolvePosition(final Level level) {
         Entity entity = (Entity)this.entityReference.getEntity(level, Entity.class);
         return entity != null ? entity.position() : null;
      }

      public Type type() {
         return Target.Type.ENTITY;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(EntityReference.streamCodec(), EntityTarget::entityReference, ByteBufCodecs.DOUBLE, EntityTarget::distance, ByteBufCodecs.BOOL, EntityTarget::clearWhenNear, EntityTarget::new);
      }
   }

   public static enum Type {
      NONE(StreamCodec.unit(Target.NONE)),
      POSITION(Target.PositionTarget.STREAM_CODEC),
      ENTITY(Target.EntityTarget.STREAM_CODEC);

      public static final IntFunction<Type> BY_ID = ByIdMap.<Type>continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
      private final StreamCodec<ByteBuf, ? extends Target> streamCodec;

      private Type(final StreamCodec<ByteBuf, ? extends Target> streamCodec) {
         this.streamCodec = streamCodec;
      }

      public StreamCodec<ByteBuf, ? extends Target> streamCodec() {
         return this.streamCodec;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{NONE, POSITION, ENTITY};
      }
   }
}
