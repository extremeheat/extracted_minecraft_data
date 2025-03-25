package net.minecraft.world.level.block.entity;

import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TrialSpawnerBlockEntity extends BlockEntity implements Spawner, TrialSpawner.StateAccessor {
   private TrialSpawner trialSpawner = this.createDefaultSpawner();

   public TrialSpawnerBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.TRIAL_SPAWNER, var1, var2);
   }

   private TrialSpawner createDefaultSpawner() {
      PlayerDetector var1 = PlayerDetector.NO_CREATIVE_PLAYERS;
      PlayerDetector.EntitySelector var2 = PlayerDetector.EntitySelector.SELECT_FROM_LEVEL;
      return new TrialSpawner(this, var1, var2);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.trialSpawner = (TrialSpawner)var1.read((MapCodec)this.trialSpawner.codec(), var2.createSerializationContext(NbtOps.INSTANCE)).orElseGet(this::createDefaultSpawner);
      if (this.level != null) {
         this.markUpdated();
      }

   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.store((MapCodec)this.trialSpawner.codec(), var2.createSerializationContext(NbtOps.INSTANCE), this.trialSpawner);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.trialSpawner.getData().getUpdateTag((TrialSpawnerState)this.getBlockState().getValue(TrialSpawnerBlock.STATE));
   }

   public void setEntityId(EntityType<?> var1, RandomSource var2) {
      if (this.level == null) {
         Util.logAndPauseIfInIde("Expected non-null level");
      } else {
         this.trialSpawner.overrideEntityToSpawn(var1, this.level);
         this.setChanged();
      }
   }

   public TrialSpawner getTrialSpawner() {
      return this.trialSpawner;
   }

   public TrialSpawnerState getState() {
      return !this.getBlockState().hasProperty(BlockStateProperties.TRIAL_SPAWNER_STATE) ? TrialSpawnerState.INACTIVE : (TrialSpawnerState)this.getBlockState().getValue(BlockStateProperties.TRIAL_SPAWNER_STATE);
   }

   public void setState(Level var1, TrialSpawnerState var2) {
      this.setChanged();
      var1.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue(BlockStateProperties.TRIAL_SPAWNER_STATE, var2));
   }

   public void markUpdated() {
      this.setChanged();
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }

   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
