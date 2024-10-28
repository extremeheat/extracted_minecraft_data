package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlockEntity extends BlockEntity implements Spawner {
   private final BaseSpawner spawner = new BaseSpawner(this) {
      public void broadcastEvent(Level var1, BlockPos var2, int var3) {
         var1.blockEvent(var2, Blocks.SPAWNER, var3, 0);
      }

      public void setNextSpawnData(@Nullable Level var1, BlockPos var2, SpawnData var3) {
         super.setNextSpawnData(var1, var2, var3);
         if (var1 != null) {
            BlockState var4 = var1.getBlockState(var2);
            var1.sendBlockUpdated(var2, var4, var4, 4);
         }

      }
   };

   public SpawnerBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.MOB_SPAWNER, var1, var2);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.spawner.load(this.level, this.worldPosition, var1);
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      this.spawner.save(var1);
   }

   public static void clientTick(Level var0, BlockPos var1, BlockState var2, SpawnerBlockEntity var3) {
      var3.spawner.clientTick(var0, var1);
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, SpawnerBlockEntity var3) {
      var3.spawner.serverTick((ServerLevel)var0, var1);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      CompoundTag var2 = this.saveCustomOnly(var1);
      var2.remove("SpawnPotentials");
      return var2;
   }

   public boolean triggerEvent(int var1, int var2) {
      return this.spawner.onEventTriggered(this.level, var1) ? true : super.triggerEvent(var1, var2);
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public void setEntityId(EntityType<?> var1, RandomSource var2) {
      this.spawner.setEntityId(var1, this.level, var2, this.worldPosition);
      this.setChanged();
   }

   public BaseSpawner getSpawner() {
      return this.spawner;
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
