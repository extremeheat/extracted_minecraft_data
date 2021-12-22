package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntityPositionSource implements PositionSource {
   public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.fieldOf("source_entity_id").forGetter((var0x) -> {
         return var0x.sourceEntityId;
      })).apply(var0, EntityPositionSource::new);
   });
   final int sourceEntityId;
   private Optional<Entity> sourceEntity = Optional.empty();

   public EntityPositionSource(int var1) {
      super();
      this.sourceEntityId = var1;
   }

   public Optional<BlockPos> getPosition(Level var1) {
      if (!this.sourceEntity.isPresent()) {
         this.sourceEntity = Optional.ofNullable(var1.getEntity(this.sourceEntityId));
      }

      return this.sourceEntity.map(Entity::blockPosition);
   }

   public PositionSourceType<?> getType() {
      return PositionSourceType.ENTITY;
   }

   public static class Type implements PositionSourceType<EntityPositionSource> {
      public Type() {
         super();
      }

      public EntityPositionSource read(FriendlyByteBuf var1) {
         return new EntityPositionSource(var1.readVarInt());
      }

      public void write(FriendlyByteBuf var1, EntityPositionSource var2) {
         var1.writeVarInt(var2.sourceEntityId);
      }

      public Codec<EntityPositionSource> codec() {
         return EntityPositionSource.CODEC;
      }

      // $FF: synthetic method
      public PositionSource read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   }
}
