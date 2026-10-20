package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.JukeboxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;

public class JukeboxBlockEntity extends BlockEntity implements ContainerSingleItem.BlockContainerSingleItem {
   public static final String SONG_ITEM_TAG_ID = "RecordItem";
   public static final String TICKS_SINCE_SONG_STARTED_TAG_ID = "ticks_since_song_started";
   public static final String PLAYLIST_POSITION = "playlist_position";
   public static final int DATA_PLAYLIST_POS = 0;
   public static final int NUM_DATA = 1;
   public static final int NO_PLAYLIST_POS = -1;
   public final ContainerData dataAccess = new ContainerData() {
      {
         Objects.requireNonNull(JukeboxBlockEntity.this);
      }

      public int get(final int dataId) {
         return dataId == 0 ? JukeboxBlockEntity.this.playlistPosition.orElse(-1) : 0;
      }

      public void set(final int dataId, final int value) {
         if (dataId == 0) {
            JukeboxBlockEntity.this.playlistPosition = OptionalInt.of(value);
         }

      }

      public int getCount() {
         return 1;
      }
   };
   private ItemStack item;
   private OptionalInt playlistPosition;
   private final JukeboxSongPlayer jukeboxSongPlayer;

   public JukeboxBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityTypes.JUKEBOX, worldPosition, blockState);
      this.item = ItemStack.EMPTY;
      this.playlistPosition = OptionalInt.empty();
      this.jukeboxSongPlayer = new JukeboxSongPlayer(this::onSongChanged, this.getBlockPos(), this::shouldLoop);
   }

   public JukeboxSongPlayer getSongPlayer() {
      return this.jukeboxSongPlayer;
   }

   public void onSongChanged() {
      if (!this.jukeboxSongPlayer.isPlaying() && this.shouldLoop()) {
         this.tryToAdvancePlaylist(true);
      }

      this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
      this.setChanged();
   }

   private void notifyItemChangedInJukebox(final boolean wasInserted) {
      if (this.level != null && this.level.getBlockState(this.getBlockPos()) == this.getBlockState()) {
         this.level.setBlock(this.getBlockPos(), (BlockState)this.getBlockState().setValue(JukeboxBlock.HAS_RECORD, wasInserted), 2);
         this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
      }
   }

   public void popOutTheItem() {
      if (this.level != null && !this.level.isClientSide()) {
         BlockPos pos = this.getBlockPos();
         ItemStack itemBeforePoppingOut = this.getTheItem();
         if (!itemBeforePoppingOut.isEmpty()) {
            this.removeTheItem();
            Vec3 itemPos = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandomXZ(this.level.getRandom(), 0.7F);
            ItemStack itemStack = itemBeforePoppingOut.copy();
            ItemEntity entity = new ItemEntity(this.level, itemPos.x(), itemPos.y(), itemPos.z(), itemStack);
            entity.setDefaultPickUpDelay();
            this.level.addFreshEntity(entity);
            this.onSongChanged();
         }
      }
   }

   public static void tickWithRecord(final Level level, final BlockPos blockPos, final BlockState blockState, final JukeboxBlockEntity jukebox) {
      jukebox.jukeboxSongPlayer.tick(level, blockState);
   }

   public static void tickWithoutRecord(final Level level, final BlockPos blockPos, final BlockState blockState, final JukeboxBlockEntity jukebox) {
      jukebox.tryToAdvancePlaylist(false);
   }

   public int getComparatorOutput() {
      return (Integer)JukeboxSong.fromStack(this.item).map(Holder::value).map(JukeboxSong::comparatorOutput).orElse(0);
   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      ItemStack newItem = (ItemStack)input.read("RecordItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
      if (!this.item.isEmpty() && !ItemStack.isSameItemSameComponents(newItem, this.item)) {
         this.jukeboxSongPlayer.stop(this.level, this.getBlockState());
      }

      this.item = newItem;
      input.getLong("ticks_since_song_started").ifPresent((ticksSinceSongStarted) -> JukeboxSong.fromStack(this.item).ifPresent((song) -> this.jukeboxSongPlayer.setSongWithoutPlaying(song, ticksSinceSongStarted)));
      Optional<Integer> newPlaylistPosition = input.getInt("playlist_position");
      this.playlistPosition = newPlaylistPosition.isPresent() ? OptionalInt.of((Integer)newPlaylistPosition.get()) : OptionalInt.empty();
   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      if (!this.getTheItem().isEmpty()) {
         output.store("RecordItem", ItemStack.CODEC, this.getTheItem());
      }

      if (this.jukeboxSongPlayer.getSong() != null) {
         output.putLong("ticks_since_song_started", this.jukeboxSongPlayer.getTicksSinceSongStarted());
      }

      if (this.playlistPosition.isPresent()) {
         output.putInt("playlist_position", this.playlistPosition.getAsInt());
      }

   }

   public ItemStack getTheItem() {
      return this.item;
   }

   public ItemStack splitTheItem(final int count) {
      ItemStack retrievedItem = this.item;
      this.setTheItem(ItemStack.EMPTY);
      return retrievedItem;
   }

   public void setTheItem(final ItemStack itemStack) {
      this.item = itemStack;
      boolean itemWasInserted = !this.item.isEmpty();
      Optional<Holder<JukeboxSong>> maybeSong = JukeboxSong.fromStack(this.item);
      this.notifyItemChangedInJukebox(itemWasInserted);
      if (itemWasInserted && maybeSong.isPresent()) {
         this.jukeboxSongPlayer.play(this.level, (Holder)maybeSong.get());
      } else {
         this.jukeboxSongPlayer.stop(this.level, this.getBlockState());
      }

   }

   public void setRemoved() {
      super.setRemoved();
      this.level.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
      this.level.levelEvent(1011, this.getBlockPos(), 0);
   }

   public int getMaxStackSize() {
      return 1;
   }

   public BlockEntity getContainerBlockEntity() {
      return this;
   }

   public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
      return isMusicDisc(itemStack) && this.getItem(slot).isEmpty();
   }

   public boolean canTakeItem(final Container into, final int slot, final ItemStack itemStack) {
      return into.hasAnyMatching(ItemStack::isEmpty);
   }

   public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
      this.popOutTheItem();
   }

   @VisibleForTesting
   public void setSongItemWithoutPlaying(final ItemStack itemStack) {
      this.item = itemStack;
      JukeboxSong.fromStack(itemStack).ifPresent((song) -> this.jukeboxSongPlayer.setSongWithoutPlaying(song, 0L));
      this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
      this.setChanged();
   }

   @VisibleForTesting
   public void tryForcePlaySong() {
      JukeboxSong.fromStack(this.getTheItem()).ifPresent((song) -> this.jukeboxSongPlayer.play(this.level, song));
   }

   private boolean shouldLoop() {
      return isMusicDisc(this.item) && isInPlaylistMode(this.level, this.worldPosition);
   }

   public static boolean isInPlaylistMode(final LevelReader level, final BlockPos pos) {
      BlockPos above = pos.above();
      BlockState blockAbove = level.getBlockState(above);
      BlockEntity entityAbove = level.getBlockEntity(above);
      return blockAbove.is(BlockTags.JUKEBOX_CONTAINERS) && entityAbove instanceof Container && (!(blockAbove.getBlock() instanceof ChestBlock) || ChestBlock.getBlockType(blockAbove) == DoubleBlockCombiner.BlockType.SINGLE);
   }

   private static boolean isMusicDisc(final ItemStack item) {
      return item.has(DataComponents.JUKEBOX_PLAYABLE);
   }

   public static OptionalInt findNextPlaylistPosition(final Container container, final int startPosition) {
      int startIndex = Math.max(-1, startPosition);

      for(int offset = 1; offset <= container.getContainerSize(); ++offset) {
         int slot = (startIndex + offset) % container.getContainerSize();
         if (isMusicDisc(container.getItem(slot))) {
            return OptionalInt.of(slot);
         }
      }

      return OptionalInt.empty();
   }

   private void replayCurrentSong() {
      this.setTheItem(this.item);
   }

   private void advancePlaylist(final Container container, final int newPosition) {
      ItemStack recordItem = container.getItem(newPosition);
      container.setItem(newPosition, this.getTheItem());
      this.setTheItem(recordItem);
   }

   private void tryToAdvancePlaylist(final boolean replayCurrentSongOtherwise) {
      BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.above());
      if (blockEntity instanceof Container container) {
         this.playlistPosition = findNextPlaylistPosition(container, this.playlistPosition.orElse(-1));
         this.playlistPosition.ifPresentOrElse((newPosition) -> this.advancePlaylist(container, newPosition), replayCurrentSongOtherwise ? this::replayCurrentSong : () -> {
         });
      }

   }

   public AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Container container) {
      return new JukeboxMenu(containerId, inventory, container, this.dataAccess);
   }
}
