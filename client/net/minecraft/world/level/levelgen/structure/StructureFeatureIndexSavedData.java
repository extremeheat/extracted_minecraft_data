package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class StructureFeatureIndexSavedData extends SavedData {
   private final LongSet all;
   private final LongSet remaining;
   private static final Codec<LongSet> LONG_SET;
   public static final Codec<StructureFeatureIndexSavedData> CODEC;

   public static SavedDataType<StructureFeatureIndexSavedData> type(String var0) {
      return new SavedDataType<StructureFeatureIndexSavedData>(var0, StructureFeatureIndexSavedData::new, CODEC, DataFixTypes.SAVED_DATA_STRUCTURE_FEATURE_INDICES);
   }

   private StructureFeatureIndexSavedData(LongSet var1, LongSet var2) {
      super();
      this.all = var1;
      this.remaining = var2;
   }

   public StructureFeatureIndexSavedData() {
      this(new LongOpenHashSet(), new LongOpenHashSet());
   }

   public void addIndex(long var1) {
      this.all.add(var1);
      this.remaining.add(var1);
      this.setDirty();
   }

   public boolean hasStartIndex(long var1) {
      return this.all.contains(var1);
   }

   public boolean hasUnhandledIndex(long var1) {
      return this.remaining.contains(var1);
   }

   public void removeIndex(long var1) {
      if (this.remaining.remove(var1)) {
         this.setDirty();
      }

   }

   public LongSet getAll() {
      return this.all;
   }

   static {
      LONG_SET = Codec.LONG_STREAM.xmap(LongOpenHashSet::toSet, LongCollection::longStream);
      CODEC = RecordCodecBuilder.create((var0) -> var0.group(LONG_SET.fieldOf("All").forGetter((var0x) -> var0x.all), LONG_SET.fieldOf("Remaining").forGetter((var0x) -> var0x.remaining)).apply(var0, StructureFeatureIndexSavedData::new));
   }
}
