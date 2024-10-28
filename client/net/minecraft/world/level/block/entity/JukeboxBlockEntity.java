package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;

public class JukeboxBlockEntity extends BlockEntity implements Clearable, ContainerSingleItem.BlockContainerSingleItem {
   public static final String SONG_ITEM_TAG_ID = "RecordItem";
   public static final String TICKS_SINCE_SONG_STARTED_TAG_ID = "ticks_since_song_started";
   private ItemStack item;
   private final JukeboxSongPlayer jukeboxSongPlayer;

   public JukeboxBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.JUKEBOX, var1, var2);
      this.item = ItemStack.EMPTY;
      this.jukeboxSongPlayer = new JukeboxSongPlayer(this::onSongChanged, this.getBlockPos());
   }

   public JukeboxSongPlayer getSongPlayer() {
      return this.jukeboxSongPlayer;
   }

   public void onSongChanged() {
      this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
      this.setChanged();
   }

   private void notifyItemChangedInJukebox(boolean var1) {
      if (this.level != null && this.level.getBlockState(this.getBlockPos()) == this.getBlockState()) {
         this.level.setBlock(this.getBlockPos(), (BlockState)this.getBlockState().setValue(JukeboxBlock.HAS_RECORD, var1), 2);
         this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
      }
   }

   public void popOutTheItem() {
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

   public static void tick(Level var0, BlockPos var1, BlockState var2, JukeboxBlockEntity var3) {
      var3.jukeboxSongPlayer.tick(var0, var2);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      if (var1.contains("RecordItem", 10)) {
         this.item = (ItemStack)ItemStack.parse(var2, var1.getCompound("RecordItem")).orElse(ItemStack.EMPTY);
      } else {
         this.item = ItemStack.EMPTY;
      }

      if (var1.contains("ticks_since_song_started", 4)) {
         JukeboxSong.fromStack(var2, this.item).ifPresent((var2x) -> {
            this.jukeboxSongPlayer.setSongWithoutPlaying(var2x, var1.getLong("ticks_since_song_started"));
         });
      }

   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      if (!this.getTheItem().isEmpty()) {
         var1.put("RecordItem", this.getTheItem().save(var2));
      }

      if (this.jukeboxSongPlayer.getSong() != null) {
         var1.putLong("ticks_since_song_started", this.jukeboxSongPlayer.getTicksSinceSongStarted());
      }

   }

   public ItemStack getTheItem() {
      return this.item;
   }

   public ItemStack splitTheItem(int var1) {
      ItemStack var2 = this.item;
      this.setTheItem(ItemStack.EMPTY);
      return var2;
   }

   public void setTheItem(ItemStack var1) {
      this.item = var1;
      boolean var2 = !this.item.isEmpty();
      Optional var3 = JukeboxSong.fromStack(this.level.registryAccess(), this.item);
      this.notifyItemChangedInJukebox(var2);
      if (var2 && var3.isPresent()) {
         this.jukeboxSongPlayer.play(this.level, (Holder)var3.get());
      } else {
         this.jukeboxSongPlayer.stop(this.level, this.getBlockState());
      }

   }

   public int getMaxStackSize() {
      return 1;
   }

   public BlockEntity getContainerBlockEntity() {
      return this;
   }

   public boolean canPlaceItem(int var1, ItemStack var2) {
      return var2.has(DataComponents.JUKEBOX_PLAYABLE) && this.getItem(var1).isEmpty();
   }

   public boolean canTakeItem(Container var1, int var2, ItemStack var3) {
      return var1.hasAnyMatching(ItemStack::isEmpty);
   }

   @VisibleForTesting
   public void setSongItemWithoutPlaying(ItemStack var1) {
      this.item = var1;
      JukeboxSong.fromStack(this.level.registryAccess(), var1).ifPresent((var1x) -> {
         this.jukeboxSongPlayer.setSongWithoutPlaying(var1x, 0L);
      });
      this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
      this.setChanged();
   }

   @VisibleForTesting
   public void tryForcePlaySong() {
      JukeboxSong.fromStack(this.level.registryAccess(), this.getTheItem()).ifPresent((var1) -> {
         this.jukeboxSongPlayer.play(this.level, var1);
      });
   }
}
