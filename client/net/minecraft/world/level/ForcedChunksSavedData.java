package net.minecraft.world.level;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class ForcedChunksSavedData extends SavedData {
   public static final String FILE_ID = "chunks";
   private static final String TAG_FORCED = "Forced";
   private final LongSet chunks;

   private ForcedChunksSavedData(LongSet var1) {
      super();
      this.chunks = var1;
   }

   public ForcedChunksSavedData() {
      this(new LongOpenHashSet());
   }

   public static ForcedChunksSavedData load(CompoundTag var0) {
      return new ForcedChunksSavedData(new LongOpenHashSet(var0.getLongArray("Forced")));
   }

   @Override
   public CompoundTag save(CompoundTag var1) {
      var1.putLongArray("Forced", this.chunks.toLongArray());
      return var1;
   }

   public LongSet getChunks() {
      return this.chunks;
   }
}
