package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult.PartialResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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
import org.slf4j.Logger;

public class TrialSpawnerBlockEntity extends BlockEntity implements Spawner, TrialSpawner.StateAccessor {
   private static final Logger LOGGER = LogUtils.getLogger();
   private TrialSpawner trialSpawner;

   public TrialSpawnerBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.TRIAL_SPAWNER, var1, var2);
      PlayerDetector var3 = PlayerDetector.NO_CREATIVE_PLAYERS;
      PlayerDetector.EntitySelector var4 = PlayerDetector.EntitySelector.SELECT_FROM_LEVEL;
      this.trialSpawner = new TrialSpawner(this, var3, var4);
   }

   @Override
   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      super.load(var1, var2);
      this.trialSpawner.codec().parse(NbtOps.INSTANCE, var1).resultOrPartial(LOGGER::error).ifPresent(var1x -> this.trialSpawner = var1x);
      if (this.level != null) {
         this.markUpdated();
      }
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      this.trialSpawner
         .codec()
         .encodeStart(NbtOps.INSTANCE, this.trialSpawner)
         .get()
         .ifLeft(var1x -> var1.merge((CompoundTag)var1x))
         .ifRight(var0 -> LOGGER.warn("Failed to encode TrialSpawner {}", var0.message()));
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.trialSpawner.getData().getUpdateTag(this.getBlockState().getValue(TrialSpawnerBlock.STATE));
   }

   @Override
   public boolean onlyOpCanSetNbt() {
      return true;
   }

   @Override
   public void setEntityId(EntityType<?> var1, RandomSource var2) {
      this.trialSpawner.getData().setEntityId(this.trialSpawner, var2, var1);
      this.setChanged();
   }

   public TrialSpawner getTrialSpawner() {
      return this.trialSpawner;
   }

   @Override
   public TrialSpawnerState getState() {
      return !this.getBlockState().hasProperty(BlockStateProperties.TRIAL_SPAWNER_STATE)
         ? TrialSpawnerState.INACTIVE
         : this.getBlockState().getValue(BlockStateProperties.TRIAL_SPAWNER_STATE);
   }

   @Override
   public void setState(Level var1, TrialSpawnerState var2) {
      this.setChanged();
      var1.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(BlockStateProperties.TRIAL_SPAWNER_STATE, var2));
   }

   @Override
   public void markUpdated() {
      this.setChanged();
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }
}
