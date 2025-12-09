package net.minecraft.world.level.block.entity;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TrialSpawnerBlockEntity extends BlockEntity implements Spawner, TrialSpawner.StateAccessor {
   private final TrialSpawner trialSpawner = this.createDefaultSpawner();

   public TrialSpawnerBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.TRIAL_SPAWNER, var1, var2);
   }

   private TrialSpawner createDefaultSpawner() {
      PlayerDetector var1 = SharedConstants.DEBUG_TRIAL_SPAWNER_DETECTS_SHEEP_AS_PLAYERS ? PlayerDetector.SHEEP : PlayerDetector.NO_CREATIVE_PLAYERS;
      PlayerDetector.EntitySelector var2 = PlayerDetector.EntitySelector.SELECT_FROM_LEVEL;
      return new TrialSpawner(TrialSpawner.FullConfig.DEFAULT, this, var1, var2);
   }

   protected void loadAdditional(ValueInput var1) {
      super.loadAdditional(var1);
      this.trialSpawner.load(var1);
      if (this.level != null) {
         this.markUpdated();
      }

   }

   protected void saveAdditional(ValueOutput var1) {
      super.saveAdditional(var1);
      this.trialSpawner.store(var1);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.trialSpawner.getStateData().getUpdateTag((TrialSpawnerState)this.getBlockState().getValue(TrialSpawnerBlock.STATE));
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
