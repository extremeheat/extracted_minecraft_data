package net.minecraft.world.level.gameevent;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityPositionSource implements PositionSource {
   public static final MapCodec<EntityPositionSource> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(UUIDUtil.CODEC.fieldOf("source_entity").forGetter(EntityPositionSource::getUuid), Codec.FLOAT.fieldOf("y_offset").orElse(0.0F).forGetter((var0x) -> {
         return var0x.yOffset;
      })).apply(var0, (var0x, var1) -> {
         return new EntityPositionSource(Either.right(Either.left(var0x)), var1);
      });
   });
   public static final StreamCodec<ByteBuf, EntityPositionSource> STREAM_CODEC;
   private Either<Entity, Either<UUID, Integer>> entityOrUuidOrId;
   private final float yOffset;

   public EntityPositionSource(Entity var1, float var2) {
      this(Either.left(var1), var2);
   }

   private EntityPositionSource(Either<Entity, Either<UUID, Integer>> var1, float var2) {
      super();
      this.entityOrUuidOrId = var1;
      this.yOffset = var2;
   }

   public Optional<Vec3> getPosition(Level var1) {
      if (this.entityOrUuidOrId.left().isEmpty()) {
         this.resolveEntity(var1);
      }

      return this.entityOrUuidOrId.left().map((var1x) -> {
         return var1x.position().add(0.0, (double)this.yOffset, 0.0);
      });
   }

   private void resolveEntity(Level var1) {
      ((Optional)this.entityOrUuidOrId.map(Optional::of, (var1x) -> {
         Function var10001 = (var1xx) -> {
            Entity var10000;
            if (var1 instanceof ServerLevel var2) {
               var10000 = var2.getEntity(var1xx);
            } else {
               var10000 = null;
            }

            return var10000;
         };
         Objects.requireNonNull(var1);
         return Optional.ofNullable((Entity)var1x.map(var10001, var1::getEntity));
      })).ifPresent((var1x) -> {
         this.entityOrUuidOrId = Either.left(var1x);
      });
   }

   private UUID getUuid() {
      return (UUID)this.entityOrUuidOrId.map(Entity::getUUID, (var0) -> {
         return (UUID)var0.map(Function.identity(), (var0x) -> {
            throw new RuntimeException("Unable to get entityId from uuid");
         });
      });
   }

   private int getId() {
      return (Integer)this.entityOrUuidOrId.map(Entity::getId, (var0) -> {
         return (Integer)var0.map((var0x) -> {
            throw new IllegalStateException("Unable to get entityId from uuid");
         }, Function.identity());
      });
   }

   public PositionSourceType<EntityPositionSource> getType() {
      return PositionSourceType.ENTITY;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, EntityPositionSource::getId, ByteBufCodecs.FLOAT, (var0) -> {
         return var0.yOffset;
      }, (var0, var1) -> {
         return new EntityPositionSource(Either.right(Either.right(var0)), var1);
      });
   }

   public static class Type implements PositionSourceType<EntityPositionSource> {
      public Type() {
         super();
      }

      public MapCodec<EntityPositionSource> codec() {
         return EntityPositionSource.CODEC;
      }

      public StreamCodec<ByteBuf, EntityPositionSource> streamCodec() {
         return EntityPositionSource.STREAM_CODEC;
      }
   }
}
