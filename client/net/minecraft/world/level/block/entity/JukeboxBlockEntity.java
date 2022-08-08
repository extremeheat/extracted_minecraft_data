package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class JukeboxBlockEntity extends BlockEntity implements Clearable {
   private ItemStack record;
   private int ticksSinceLastEvent;
   private long tickCount;
   private long recordStartedTick;
   private boolean isPlaying;

   public JukeboxBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.JUKEBOX, var1, var2);
      this.record = ItemStack.EMPTY;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("RecordItem", 10)) {
         this.setRecord(ItemStack.of(var1.getCompound("RecordItem")));
      }

      this.isPlaying = var1.getBoolean("IsPlaying");
      this.recordStartedTick = var1.getLong("RecordStartTick");
      this.tickCount = var1.getLong("TickCount");
   }

   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      if (!this.getRecord().isEmpty()) {
         var1.put("RecordItem", this.getRecord().save(new CompoundTag()));
      }

      var1.putBoolean("IsPlaying", this.isPlaying);
      var1.putLong("RecordStartTick", this.recordStartedTick);
      var1.putLong("TickCount", this.tickCount);
   }

   public ItemStack getRecord() {
      return this.record;
   }

   public void setRecord(ItemStack var1) {
      this.record = var1;
      this.setChanged();
   }

   public void playRecord() {
      this.recordStartedTick = this.tickCount;
      this.isPlaying = true;
   }

   public void clearContent() {
      this.setRecord(ItemStack.EMPTY);
      this.isPlaying = false;
   }

   public static void playRecordTick(Level var0, BlockPos var1, BlockState var2, JukeboxBlockEntity var3) {
      ++var3.ticksSinceLastEvent;
      if (recordIsPlaying(var2, var3)) {
         Item var5 = var3.getRecord().getItem();
         if (var5 instanceof RecordItem) {
            RecordItem var4 = (RecordItem)var5;
            if (recordShouldStopPlaying(var3, var4)) {
               var0.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, var1, GameEvent.Context.of(var2));
               var3.isPlaying = false;
            } else if (shouldSendJukeboxPlayingEvent(var3)) {
               var3.ticksSinceLastEvent = 0;
               var0.gameEvent(GameEvent.JUKEBOX_PLAY, var1, GameEvent.Context.of(var2));
            }
         }
      }

      ++var3.tickCount;
   }

   private static boolean recordIsPlaying(BlockState var0, JukeboxBlockEntity var1) {
      return (Boolean)var0.getValue(JukeboxBlock.HAS_RECORD) && var1.isPlaying;
   }

   private static boolean recordShouldStopPlaying(JukeboxBlockEntity var0, RecordItem var1) {
      return var0.tickCount >= var0.recordStartedTick + (long)var1.getLengthInTicks();
   }

   private static boolean shouldSendJukeboxPlayingEvent(JukeboxBlockEntity var0) {
      return var0.ticksSinceLastEvent >= 20;
   }
}
