package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;

public class JukeboxBlockEntity extends BlockEntity implements Clearable, ContainerSingleItem {
   private static final int SONG_END_PADDING = 20;
   private ItemStack item = ItemStack.EMPTY;
   private int ticksSinceLastEvent;
   private long tickCount;
   private long recordStartedTick;
   private boolean isPlaying;

   public JukeboxBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.JUKEBOX, var1, var2);
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("RecordItem", 10)) {
         this.item = ItemStack.of(var1.getCompound("RecordItem"));
      }

      this.isPlaying = var1.getBoolean("IsPlaying");
      this.recordStartedTick = var1.getLong("RecordStartTick");
      this.tickCount = var1.getLong("TickCount");
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      if (!this.getTheItem().isEmpty()) {
         var1.put("RecordItem", this.getTheItem().save(new CompoundTag()));
      }

      var1.putBoolean("IsPlaying", this.isPlaying);
      var1.putLong("RecordStartTick", this.recordStartedTick);
      var1.putLong("TickCount", this.tickCount);
   }

   public boolean isRecordPlaying() {
      return !this.getTheItem().isEmpty() && this.isPlaying;
   }

   private void setHasRecordBlockState(@Nullable Entity var1, boolean var2) {
      if (this.level.getBlockState(this.getBlockPos()) == this.getBlockState()) {
         this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(JukeboxBlock.HAS_RECORD, Boolean.valueOf(var2)), 2);
         this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(var1, this.getBlockState()));
      }
   }

   @VisibleForTesting
   public void startPlaying() {
      this.recordStartedTick = this.tickCount;
      this.isPlaying = true;
      this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
      this.level.levelEvent(null, 1010, this.getBlockPos(), Item.getId(this.getTheItem().getItem()));
      this.setChanged();
   }

   private void stopPlaying() {
      this.isPlaying = false;
      this.level.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
      this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
      this.level.levelEvent(1011, this.getBlockPos(), 0);
      this.setChanged();
   }

   private void tick(Level var1, BlockPos var2, BlockState var3) {
      ++this.ticksSinceLastEvent;
      if (this.isRecordPlaying()) {
         Item var5 = this.getTheItem().getItem();
         if (var5 instanceof RecordItem var4) {
            if (this.shouldRecordStopPlaying((RecordItem)var4)) {
               this.stopPlaying();
            } else if (this.shouldSendJukeboxPlayingEvent()) {
               this.ticksSinceLastEvent = 0;
               var1.gameEvent(GameEvent.JUKEBOX_PLAY, var2, GameEvent.Context.of(var3));
               this.spawnMusicParticles(var1, var2);
            }
         }
      }

      ++this.tickCount;
   }

   private boolean shouldRecordStopPlaying(RecordItem var1) {
      return this.tickCount >= this.recordStartedTick + (long)var1.getLengthInTicks() + 20L;
   }

   private boolean shouldSendJukeboxPlayingEvent() {
      return this.ticksSinceLastEvent >= 20;
   }

   @Override
   public ItemStack getTheItem() {
      return this.item;
   }

   @Override
   public ItemStack splitTheItem(int var1) {
      ItemStack var2 = this.item;
      this.item = ItemStack.EMPTY;
      if (!var2.isEmpty()) {
         this.setHasRecordBlockState(null, false);
         this.stopPlaying();
      }

      return var2;
   }

   @Override
   public void setTheItem(ItemStack var1) {
      if (var1.is(ItemTags.MUSIC_DISCS) && this.level != null) {
         this.item = var1;
         this.setHasRecordBlockState(null, true);
         this.startPlaying();
      } else if (var1.isEmpty()) {
         this.splitTheItem(1);
      }
   }

   @Override
   public int getMaxStackSize() {
      return 1;
   }

   @Override
   public BlockEntity getContainerBlockEntity() {
      return this;
   }

   @Override
   public boolean canPlaceItem(int var1, ItemStack var2) {
      return var2.is(ItemTags.MUSIC_DISCS) && this.getItem(var1).isEmpty();
   }

   @Override
   public boolean canTakeItem(Container var1, int var2, ItemStack var3) {
      return var1.hasAnyMatching(ItemStack::isEmpty);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void spawnMusicParticles(Level var1, BlockPos var2) {
      if (var1 instanceof ServerLevel var3) {
         Vec3 var4 = Vec3.atBottomCenterOf(var2).add(0.0, 1.2000000476837158, 0.0);
         float var5 = (float)var1.getRandom().nextInt(4) / 24.0F;
         var3.sendParticles(ParticleTypes.NOTE, var4.x(), var4.y(), var4.z(), 0, (double)var5, 0.0, 0.0, 1.0);
      }
   }

   public void popOutRecord() {
      if (this.level != null && !this.level.isClientSide) {
         BlockPos var1 = this.getBlockPos();
         ItemStack var2 = this.getTheItem();
         if (!var2.isEmpty()) {
            this.removeTheItem();
            Vec3 var3 = Vec3.atLowerCornerWithOffset(var1, 0.5, 1.01, 0.5).offsetRandom(this.level.random, 0.7F);
            ItemStack var4 = var2.copy();
            ItemEntity var5 = new ItemEntity(this.level, var3.x(), var3.y(), var3.z(), var4);
            var5.setDefaultPickUpDelay();
            this.level.addFreshEntity(var5);
         }
      }
   }

   public static void playRecordTick(Level var0, BlockPos var1, BlockState var2, JukeboxBlockEntity var3) {
      var3.tick(var0, var1, var2);
   }

   @VisibleForTesting
   public void setRecordWithoutPlaying(ItemStack var1) {
      this.item = var1;
      this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
      this.setChanged();
   }
}
