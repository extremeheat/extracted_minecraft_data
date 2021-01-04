package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlockEntity extends BlockEntity implements TickableBlockEntity {
   private final BaseSpawner spawner = new BaseSpawner() {
      public void broadcastEvent(int var1) {
         SpawnerBlockEntity.this.level.blockEvent(SpawnerBlockEntity.this.worldPosition, Blocks.SPAWNER, var1, 0);
      }

      public Level getLevel() {
         return SpawnerBlockEntity.this.level;
      }

      public BlockPos getPos() {
         return SpawnerBlockEntity.this.worldPosition;
      }

      public void setNextSpawnData(SpawnData var1) {
         super.setNextSpawnData(var1);
         if (this.getLevel() != null) {
            BlockState var2 = this.getLevel().getBlockState(this.getPos());
            this.getLevel().sendBlockUpdated(SpawnerBlockEntity.this.worldPosition, var2, var2, 4);
         }

      }
   };

   public SpawnerBlockEntity() {
      super(BlockEntityType.MOB_SPAWNER);
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.spawner.load(var1);
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      this.spawner.save(var1);
      return var1;
   }

   public void tick() {
      this.spawner.tick();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      CompoundTag var1 = this.save(new CompoundTag());
      var1.remove("SpawnPotentials");
      return var1;
   }

   public boolean triggerEvent(int var1, int var2) {
      return this.spawner.onEventTriggered(var1) ? true : super.triggerEvent(var1, var2);
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public BaseSpawner getSpawner() {
      return this.spawner;
   }
}
