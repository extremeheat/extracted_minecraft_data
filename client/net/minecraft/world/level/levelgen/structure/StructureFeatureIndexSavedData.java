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

   public static SavedDataType<StructureFeatureIndexSavedData> type(final String id) {
      return new SavedDataType<StructureFeatureIndexSavedData>(id, StructureFeatureIndexSavedData::new, CODEC, DataFixTypes.SAVED_DATA_STRUCTURE_FEATURE_INDICES);
   }

   private StructureFeatureIndexSavedData(final LongSet all, final LongSet remaining) {
      super();
      this.all = all;
      this.remaining = remaining;
   }

   public StructureFeatureIndexSavedData() {
      this(new LongOpenHashSet(), new LongOpenHashSet());
   }

   public void addIndex(final long chunkPosKey) {
      this.all.add(chunkPosKey);
      this.remaining.add(chunkPosKey);
      this.setDirty();
   }

   public boolean hasStartIndex(final long chunkPosKey) {
      return this.all.contains(chunkPosKey);
   }

   public boolean hasUnhandledIndex(final long chunkPosKey) {
      return this.remaining.contains(chunkPosKey);
   }

   public void removeIndex(final long chunkPosKey) {
      if (this.remaining.remove(chunkPosKey)) {
         this.setDirty();
      }

   }

   public LongSet getAll() {
      return this.all;
   }

   static {
      LONG_SET = Codec.LONG_STREAM.xmap(LongOpenHashSet::toSet, LongCollection::longStream);
      CODEC = RecordCodecBuilder.create((i) -> i.group(LONG_SET.fieldOf("All").forGetter((data) -> data.all), LONG_SET.fieldOf("Remaining").forGetter((data) -> data.remaining)).apply(i, StructureFeatureIndexSavedData::new));
   }
}
