package net.minecraft.world.level;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class ForcedChunksSavedData extends SavedData {
   private LongSet chunks = new LongOpenHashSet();

   public ForcedChunksSavedData() {
      super("chunks");
   }

   public void load(CompoundTag var1) {
      this.chunks = new LongOpenHashSet(var1.getLongArray("Forced"));
   }

   public CompoundTag save(CompoundTag var1) {
      var1.putLongArray("Forced", this.chunks.toLongArray());
      return var1;
   }

   public LongSet getChunks() {
      return this.chunks;
   }
}
