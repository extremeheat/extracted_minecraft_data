package net.minecraft.world.level.levelgen.structure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class StructureFeatureIndexSavedData extends SavedData {
   private static final String TAG_REMAINING_INDEXES = "Remaining";
   private static final String TAG_All_INDEXES = "All";
   private final LongSet all;
   private final LongSet remaining;

   private StructureFeatureIndexSavedData(LongSet var1, LongSet var2) {
      super();
      this.all = var1;
      this.remaining = var2;
   }

   public StructureFeatureIndexSavedData() {
      this(new LongOpenHashSet(), new LongOpenHashSet());
   }

   public static StructureFeatureIndexSavedData load(CompoundTag var0) {
      return new StructureFeatureIndexSavedData(new LongOpenHashSet(var0.getLongArray("All")), new LongOpenHashSet(var0.getLongArray("Remaining")));
   }

   public CompoundTag save(CompoundTag var1) {
      var1.putLongArray("All", this.all.toLongArray());
      var1.putLongArray("Remaining", this.remaining.toLongArray());
      return var1;
   }

   public void addIndex(long var1) {
      this.all.add(var1);
      this.remaining.add(var1);
   }

   public boolean hasStartIndex(long var1) {
      return this.all.contains(var1);
   }

   public boolean hasUnhandledIndex(long var1) {
      return this.remaining.contains(var1);
   }

   public void removeIndex(long var1) {
      this.remaining.remove(var1);
   }

   public LongSet getAll() {
      return this.all;
   }
}
