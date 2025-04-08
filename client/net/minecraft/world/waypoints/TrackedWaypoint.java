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
   static final Logger LOGGER = LogUtils.getLogger();
   public static StreamCodec<ByteBuf, TrackedWaypoint> STREAM_CODEC = StreamCodec.<ByteBuf, TrackedWaypoint>ofMember(TrackedWaypoint::write, TrackedWaypoint::read);
   protected final Either<UUID, String> identifier;
   private final Waypoint.Icon icon;
   private final Type type;

   TrackedWaypoint(Either<UUID, String> var1, Waypoint.Icon var2, Type var3) {
      super();
      this.identifier = var1;
      this.icon = var2;
      this.type = var3;
   }

   public Either<UUID, String> id() {
      return this.identifier;
   }

   public abstract void update(TrackedWaypoint var1);

   public void write(ByteBuf var1) {
      FriendlyByteBuf var2 = new FriendlyByteBuf(var1);
      var2.writeEither(this.identifier, UUIDUtil.STREAM_CODEC, FriendlyByteBuf::writeUtf);
      Waypoint.Icon.STREAM_CODEC.encode(var2, this.icon);
      var2.writeEnum(this.type);
      this.writeContents(var1);
   }

   public abstract void writeContents(ByteBuf var1);

   private static TrackedWaypoint read(ByteBuf var0) {
      FriendlyByteBuf var1 = new FriendlyByteBuf(var0);
      Either var2 = var1.readEither(UUIDUtil.STREAM_CODEC, FriendlyByteBuf::readUtf);
      Waypoint.Icon var3 = (Waypoint.Icon)Waypoint.Icon.STREAM_CODEC.decode(var1);
      Type var4 = (Type)var1.readEnum(Type.class);
      return (TrackedWaypoint)var4.constructor.apply(var2, var3, var1);
   }

   public static TrackedWaypoint setPosition(UUID var0, Waypoint.Icon var1, Vec3i var2) {
      return new Vec3iWaypoint(var0, var1, var2);
   }

   public static TrackedWaypoint setChunk(UUID var0, Waypoint.Icon var1, ChunkPos var2) {
      return new ChunkWaypoint(var0, var1, var2);
   }

   public static TrackedWaypoint setAzimuth(UUID var0, Waypoint.Icon var1, float var2) {
      return new AzimuthWaypoint(var0, var1, var2);
   }

   public static TrackedWaypoint empty(UUID var0) {
      return new EmptyWaypoint(var0);
   }

   public abstract double yawAngleToEntity(Entity var1);

   public abstract int pitchDirectionToEntity(Entity var1, Projector var2);

   public abstract double distanceSquared(Entity var1);

   public Waypoint.Icon icon() {
      return this.icon;
   }

   static enum Type {
      EMPTY(EmptyWaypoint::new),
      VEC3I(Vec3iWaypoint::new),
      CHUNK(ChunkWaypoint::new),
      AZIMUTH(AzimuthWaypoint::new);

      final TriFunction<Either<UUID, String>, Waypoint.Icon, FriendlyByteBuf, TrackedWaypoint> constructor;

      private Type(final TriFunction<Either<UUID, String>, Waypoint.Icon, FriendlyByteBuf, TrackedWaypoint> var3) {
         this.constructor = var3;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{EMPTY, VEC3I, CHUNK, AZIMUTH};
      }
   }

   static class EmptyWaypoint extends TrackedWaypoint {
      private EmptyWaypoint(Either<UUID, String> var1, Waypoint.Icon var2, FriendlyByteBuf var3) {
         super(var1, var2, TrackedWaypoint.Type.EMPTY);
      }

      EmptyWaypoint(UUID var1) {
         super(Either.left(var1), Waypoint.Icon.NULL, TrackedWaypoint.Type.EMPTY);
      }

      public void update(TrackedWaypoint var1) {
      }

      public void writeContents(ByteBuf var1) {
      }

      public double yawAngleToEntity(Entity var1) {
         return 0.0 / 0.0;
      }

      public int pitchDirectionToEntity(Entity var1, Projector var2) {
         return 0;
      }

      public double distanceSquared(Entity var1) {
         return 1.0 / 0.0;
      }
   }

   static class Vec3iWaypoint extends TrackedWaypoint {
      private Vec3i vector;

      public Vec3iWaypoint(UUID var1, Waypoint.Icon var2, Vec3i var3) {
         super(Either.left(var1), var2, TrackedWaypoint.Type.VEC3I);
         this.vector = var3;
      }

      public Vec3iWaypoint(Either<UUID, String> var1, Waypoint.Icon var2, FriendlyByteBuf var3) {
         super(var1, var2, TrackedWaypoint.Type.VEC3I);
         this.vector = new Vec3i(var3.readVarInt(), var3.readVarInt(), var3.readVarInt());
      }

      public void update(TrackedWaypoint var1) {
         if (var1 instanceof Vec3iWaypoint var2) {
            this.vector = var2.vector;
         } else {
            TrackedWaypoint.LOGGER.warn("Unsupported Waypoint update operation: {}", var1.getClass());
         }

      }

      public void writeContents(ByteBuf var1) {
         VarInt.write(var1, this.vector.getX());
         VarInt.write(var1, this.vector.getY());
         VarInt.write(var1, this.vector.getZ());
      }

      private Vec3 position(Level var1) {
         Optional var10000 = this.identifier.left();
         Objects.requireNonNull(var1);
         return (Vec3)var10000.map(var1::getEntity).map((var1x) -> var1x.blockPosition().distManhattan(this.vector) > 3 ? null : var1x.getEyePosition()).orElseGet(() -> Vec3.atCenterOf(this.vector));
      }

      public double yawAngleToEntity(Entity var1) {
         Vec3 var2 = var1.getEyePosition().subtract(this.position(var1.level())).rotateClockwise90();
         float var3 = Mth.wrapDegrees(var1.getYRot());
         float var4 = (float)Mth.atan2(var2.z(), var2.x()) * 57.295776F;
         return (double)Mth.degreesDifference(var3, var4);
      }

      public int pitchDirectionToEntity(Entity var1, Projector var2) {
         Vec3 var3 = var2.projectPointToScreen(this.position(var1.level()));
         boolean var4 = var3.z > 1.0;
         double var5 = var4 ? -var3.y : var3.y;
         if (var5 < -1.0) {
            return -1;
         } else if (var5 > 1.0) {
            return 1;
         } else {
            return var4 ? Mth.sign(var3.y) : 0;
         }
      }

      public double distanceSquared(Entity var1) {
         return var1.distanceToSqr(Vec3.atCenterOf(this.vector));
      }
   }

   static class ChunkWaypoint extends TrackedWaypoint {
      private ChunkPos chunkPos;

      public ChunkWaypoint(UUID var1, Waypoint.Icon var2, ChunkPos var3) {
         super(Either.left(var1), var2, TrackedWaypoint.Type.CHUNK);
         this.chunkPos = var3;
      }

      public ChunkWaypoint(Either<UUID, String> var1, Waypoint.Icon var2, FriendlyByteBuf var3) {
         super(var1, var2, TrackedWaypoint.Type.CHUNK);
         this.chunkPos = new ChunkPos(var3.readVarInt(), var3.readVarInt());
      }

      public void update(TrackedWaypoint var1) {
         if (var1 instanceof ChunkWaypoint var2) {
            this.chunkPos = var2.chunkPos;
         } else {
            TrackedWaypoint.LOGGER.warn("Unsupported Waypoint update operation: {}", var1.getClass());
         }

      }

      public void writeContents(ByteBuf var1) {
         VarInt.write(var1, this.chunkPos.x);
         VarInt.write(var1, this.chunkPos.z);
      }

      private Vec3 position(Entity var1) {
         return Vec3.atCenterOf(this.chunkPos.getMiddleBlockPosition(var1.getBlockY()));
      }

      public double yawAngleToEntity(Entity var1) {
         Vec3 var2 = var1.getEyePosition().subtract(this.position(var1)).rotateClockwise90();
         float var3 = Mth.wrapDegrees(var1.getYRot());
         float var4 = (float)Mth.atan2(var2.z(), var2.x()) * 57.295776F;
         return (double)Mth.degreesDifference(var3, var4);
      }

      public int pitchDirectionToEntity(Entity var1, Projector var2) {
         double var3 = var2.projectHorizonToScreen();
         if (var3 < -1.0) {
            return -1;
         } else {
            return var3 > 1.0 ? 1 : 0;
         }
      }

      public double distanceSquared(Entity var1) {
         return var1.distanceToSqr(Vec3.atCenterOf(this.chunkPos.getMiddleBlockPosition(var1.getBlockY())));
      }
   }

   static class AzimuthWaypoint extends TrackedWaypoint {
      private float angle;

      public AzimuthWaypoint(UUID var1, Waypoint.Icon var2, float var3) {
         super(Either.left(var1), var2, TrackedWaypoint.Type.AZIMUTH);
         this.angle = var3;
      }

      public AzimuthWaypoint(Either<UUID, String> var1, Waypoint.Icon var2, FriendlyByteBuf var3) {
         super(var1, var2, TrackedWaypoint.Type.AZIMUTH);
         this.angle = var3.readFloat();
      }

      public void update(TrackedWaypoint var1) {
         if (var1 instanceof AzimuthWaypoint var2) {
            this.angle = var2.angle;
         } else {
            TrackedWaypoint.LOGGER.warn("Unsupported Waypoint update operation: {}", var1.getClass());
         }

      }

      public void writeContents(ByteBuf var1) {
         var1.writeFloat(this.angle);
      }

      public double yawAngleToEntity(Entity var1) {
         return (double)Mth.degreesDifference(Mth.wrapDegrees(var1.getYRot()), this.angle * 57.295776F);
      }

      public int pitchDirectionToEntity(Entity var1, Projector var2) {
         double var3 = var2.projectHorizonToScreen();
         if (var3 < -1.0) {
            return -1;
         } else {
            return var3 > 1.0 ? 1 : 0;
         }
      }

      public double distanceSquared(Entity var1) {
         return 1.0 / 0.0;
      }
   }

   public interface Projector {
      Vec3 projectPointToScreen(Vec3 var1);

      double projectHorizonToScreen();
   }
}
