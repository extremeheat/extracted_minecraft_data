package net.minecraft.world.level.levelgen.structure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class StructureFeatureIndexSavedData extends SavedData {
   private LongSet all = new LongOpenHashSet();
   private LongSet remaining = new LongOpenHashSet();

   public StructureFeatureIndexSavedData(String var1) {
      super(var1);
   }

   public void load(CompoundTag var1) {
      this.all = new LongOpenHashSet(var1.getLongArray("All"));
      this.remaining = new LongOpenHashSet(var1.getLongArray("Remaining"));
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
