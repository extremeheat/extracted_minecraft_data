package net.minecraft.world.waypoints;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.Logger;

public abstract class TrackedWaypoint implements Waypoint {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final StreamCodec<ByteBuf, TrackedWaypoint> STREAM_CODEC = StreamCodec.<ByteBuf, TrackedWaypoint>ofMember(TrackedWaypoint::write, TrackedWaypoint::read);
   protected final Either<UUID, String> identifier;
   private final Waypoint.Icon icon;
   private final Type type;

   private TrackedWaypoint(final Either<UUID, String> identifier, final Waypoint.Icon icon, final Type type) {
      super();
      this.identifier = identifier;
      this.icon = icon;
      this.type = type;
   }

   public Either<UUID, String> id() {
      return this.identifier;
   }

   public abstract void update(final TrackedWaypoint other);

   public void write(final ByteBuf buf) {
      FriendlyByteBuf byteBuf = new FriendlyByteBuf(buf);
      byteBuf.writeEither(this.identifier, UUIDUtil.STREAM_CODEC, FriendlyByteBuf::writeUtf);
      Waypoint.Icon.STREAM_CODEC.encode(byteBuf, this.icon);
      byteBuf.writeEnum(this.type);
      this.writeContents(buf);
   }

   public abstract void writeContents(final ByteBuf buf);

   private static TrackedWaypoint read(final ByteBuf buf) {
      FriendlyByteBuf byteBuf = new FriendlyByteBuf(buf);
      Either<UUID, String> identifier = byteBuf.<UUID, String>readEither(UUIDUtil.STREAM_CODEC, FriendlyByteBuf::readUtf);
      Waypoint.Icon icon = (Waypoint.Icon)Waypoint.Icon.STREAM_CODEC.decode(byteBuf);
      Type type = (Type)byteBuf.readEnum(Type.class);
      return (TrackedWaypoint)type.constructor.apply(identifier, icon, byteBuf);
   }

   public static TrackedWaypoint setPosition(final UUID identifier, final Waypoint.Icon icon, final Vec3i position) {
      return new Vec3iWaypoint(identifier, icon, position);
   }

   public static TrackedWaypoint setChunk(final UUID identifier, final Waypoint.Icon icon, final ChunkPos chunk) {
      return new ChunkWaypoint(identifier, icon, chunk);
   }

   public static TrackedWaypoint setAzimuth(final UUID identifier, final Waypoint.Icon icon, final float angle) {
      return new AzimuthWaypoint(identifier, icon, angle);
   }

   public static TrackedWaypoint empty(final UUID identifier) {
      return new EmptyWaypoint(identifier);
   }

   public abstract double yawAngleToCamera(final Level level, final Camera camera, final PartialTickSupplier partialTickSupplier);

   public abstract PitchDirection pitchDirectionToCamera(Level level, Projector projector, final PartialTickSupplier partialTickSupplier);

   public abstract double distanceSquared(final Entity fromEntity);

   public Waypoint.Icon icon() {
      return this.icon;
   }

   public static enum PitchDirection {
      NONE,
      UP,
      DOWN;

      private PitchDirection() {
      }

      // $FF: synthetic method
      private static PitchDirection[] $values() {
         return new PitchDirection[]{NONE, UP, DOWN};
      }
   }

   private static enum Type {
      EMPTY(EmptyWaypoint::new),
      VEC3I(Vec3iWaypoint::new),
      CHUNK(ChunkWaypoint::new),
      AZIMUTH(AzimuthWaypoint::new);

      private final TriFunction<Either<UUID, String>, Waypoint.Icon, FriendlyByteBuf, TrackedWaypoint> constructor;

      private Type(final TriFunction<Either<UUID, String>, Waypoint.Icon, FriendlyByteBuf, TrackedWaypoint> constructor) {
         this.constructor = constructor;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{EMPTY, VEC3I, CHUNK, AZIMUTH};
      }
   }

   private static class EmptyWaypoint extends TrackedWaypoint {
      private EmptyWaypoint(final Either<UUID, String> identifier, final Waypoint.Icon icon, final FriendlyByteBuf byteBuf) {
         super(identifier, icon, TrackedWaypoint.Type.EMPTY);
      }

      private EmptyWaypoint(final UUID identifier) {
         super(Either.left(identifier), Waypoint.Icon.NULL, TrackedWaypoint.Type.EMPTY);
      }

      public void update(final TrackedWaypoint other) {
      }

      public void writeContents(final ByteBuf buf) {
      }

      public double yawAngleToCamera(final Level level, final Camera camera, final PartialTickSupplier partialTickSupplier) {
         return 0.0 / 0.0;
      }

      public PitchDirection pitchDirectionToCamera(final Level level, final Projector projector, final PartialTickSupplier partialTickSupplier) {
         return TrackedWaypoint.PitchDirection.NONE;
      }

      public double distanceSquared(final Entity fromEntity) {
         return 1.0 / 0.0;
      }
   }

   private static class Vec3iWaypoint extends TrackedWaypoint {
      private Vec3i vector;

      public Vec3iWaypoint(final UUID identifier, final Waypoint.Icon icon, final Vec3i vector) {
         super(Either.left(identifier), icon, TrackedWaypoint.Type.VEC3I);
         this.vector = vector;
      }

      public Vec3iWaypoint(final Either<UUID, String> identifier, final Waypoint.Icon icon, final FriendlyByteBuf byteBuf) {
         super(identifier, icon, TrackedWaypoint.Type.VEC3I);
         this.vector = new Vec3i(byteBuf.readVarInt(), byteBuf.readVarInt(), byteBuf.readVarInt());
      }

      public void update(final TrackedWaypoint other) {
         if (other instanceof Vec3iWaypoint vec3iWaypoint) {
            this.vector = vec3iWaypoint.vector;
         } else {
            TrackedWaypoint.LOGGER.warn("Unsupported Waypoint update operation: {}", other.getClass());
         }

      }

      public void writeContents(final ByteBuf buf) {
         VarInt.write(buf, this.vector.getX());
         VarInt.write(buf, this.vector.getY());
         VarInt.write(buf, this.vector.getZ());
      }

      private Vec3 position(final Level level, final PartialTickSupplier partialTick) {
         Optional var10000 = this.identifier.left();
         Objects.requireNonNull(level);
         return (Vec3)var10000.map(level::getEntity).map((e) -> e.blockPosition().distManhattan(this.vector) > 3 ? null : e.getEyePosition(partialTick.apply(e))).orElseGet(() -> Vec3.atCenterOf(this.vector));
      }

      public double yawAngleToCamera(final Level level, final Camera camera, final PartialTickSupplier partialTickSupplier) {
         Vec3 direction = camera.position().subtract(this.position(level, partialTickSupplier)).rotateClockwise90();
         float waypointAngle = (float)Mth.atan2(direction.z(), direction.x()) * 57.295776F;
         return (double)Mth.degreesDifference(camera.yaw(), waypointAngle);
      }

      public PitchDirection pitchDirectionToCamera(final Level level, final Projector projector, final PartialTickSupplier partialTickSupplier) {
         Vec3 pointOnScreen = projector.projectPointToScreen(this.position(level, partialTickSupplier));
         boolean isBehindCamera = pointOnScreen.z > 1.0;
         double yInFrontOfCamera = isBehindCamera ? -pointOnScreen.y : pointOnScreen.y;
         if (yInFrontOfCamera < -1.0) {
            return TrackedWaypoint.PitchDirection.DOWN;
         } else if (yInFrontOfCamera > 1.0) {
            return TrackedWaypoint.PitchDirection.UP;
         } else {
            if (isBehindCamera) {
               if (pointOnScreen.y > 0.0) {
                  return TrackedWaypoint.PitchDirection.UP;
               }

               if (pointOnScreen.y < 0.0) {
                  return TrackedWaypoint.PitchDirection.DOWN;
               }
            }

            return TrackedWaypoint.PitchDirection.NONE;
         }
      }

      public double distanceSquared(final Entity fromEntity) {
         return fromEntity.distanceToSqr(Vec3.atCenterOf(this.vector));
      }
   }

   private static class ChunkWaypoint extends TrackedWaypoint {
      private ChunkPos chunkPos;

      public ChunkWaypoint(final UUID identifier, final Waypoint.Icon icon, final ChunkPos chunkPos) {
         super(Either.left(identifier), icon, TrackedWaypoint.Type.CHUNK);
         this.chunkPos = chunkPos;
      }

      public ChunkWaypoint(final Either<UUID, String> identifier, final Waypoint.Icon icon, final FriendlyByteBuf byteBuf) {
         super(identifier, icon, TrackedWaypoint.Type.CHUNK);
         this.chunkPos = new ChunkPos(byteBuf.readVarInt(), byteBuf.readVarInt());
      }

      public void update(final TrackedWaypoint other) {
         if (other instanceof ChunkWaypoint chunkWaypoint) {
            this.chunkPos = chunkWaypoint.chunkPos;
         } else {
            TrackedWaypoint.LOGGER.warn("Unsupported Waypoint update operation: {}", other.getClass());
         }

      }

      public void writeContents(final ByteBuf buf) {
         VarInt.write(buf, this.chunkPos.x());
         VarInt.write(buf, this.chunkPos.z());
      }

      private Vec3 position(final double positionY) {
         return Vec3.atCenterOf(this.chunkPos.getMiddleBlockPosition((int)positionY));
      }

      public double yawAngleToCamera(final Level level, final Camera camera, final PartialTickSupplier partialTickSupplier) {
         Vec3 cameraPosition = camera.position();
         Vec3 direction = cameraPosition.subtract(this.position(cameraPosition.y())).rotateClockwise90();
         float waypointAngle = (float)Mth.atan2(direction.z(), direction.x()) * 57.295776F;
         return (double)Mth.degreesDifference(camera.yaw(), waypointAngle);
      }

      public PitchDirection pitchDirectionToCamera(final Level level, final Projector projector, final PartialTickSupplier partialTickSupplier) {
         double onScreenHorizon = projector.projectHorizonToScreen();
         if (onScreenHorizon < -1.0) {
            return TrackedWaypoint.PitchDirection.DOWN;
         } else {
            return onScreenHorizon > 1.0 ? TrackedWaypoint.PitchDirection.UP : TrackedWaypoint.PitchDirection.NONE;
         }
      }

      public double distanceSquared(final Entity fromEntity) {
         return fromEntity.distanceToSqr(Vec3.atCenterOf(this.chunkPos.getMiddleBlockPosition(fromEntity.getBlockY())));
      }
   }

   private static class AzimuthWaypoint extends TrackedWaypoint {
      private float angle;

      public AzimuthWaypoint(final UUID identifier, final Waypoint.Icon icon, final float angle) {
         super(Either.left(identifier), icon, TrackedWaypoint.Type.AZIMUTH);
         this.angle = angle;
      }

      public AzimuthWaypoint(final Either<UUID, String> identifier, final Waypoint.Icon icon, final FriendlyByteBuf byteBuf) {
         super(identifier, icon, TrackedWaypoint.Type.AZIMUTH);
         this.angle = byteBuf.readFloat();
      }

      public void update(final TrackedWaypoint other) {
         if (other instanceof AzimuthWaypoint azimuthWaypoint) {
            this.angle = azimuthWaypoint.angle;
         } else {
            TrackedWaypoint.LOGGER.warn("Unsupported Waypoint update operation: {}", other.getClass());
         }

      }

      public void writeContents(final ByteBuf buf) {
         buf.writeFloat(this.angle);
      }

      public double yawAngleToCamera(final Level level, final Camera camera, final PartialTickSupplier partialTickSupplier) {
         return (double)Mth.degreesDifference(camera.yaw(), this.angle * 57.295776F);
      }

      public PitchDirection pitchDirectionToCamera(final Level level, final Projector projector, final PartialTickSupplier partialTickSupplier) {
         double horizon = projector.projectHorizonToScreen();
         if (horizon < -1.0) {
            return TrackedWaypoint.PitchDirection.DOWN;
         } else {
            return horizon > 1.0 ? TrackedWaypoint.PitchDirection.UP : TrackedWaypoint.PitchDirection.NONE;
         }
      }

      public double distanceSquared(final Entity fromEntity) {
         return 1.0 / 0.0;
      }
   }

   public interface Camera {
      float yaw();

      Vec3 position();
   }

   public interface Projector {
      Vec3 projectPointToScreen(final Vec3 point);

      double projectHorizonToScreen();
   }
}
