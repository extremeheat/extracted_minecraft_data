package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class MapIndex extends SavedData {
   private static final int NO_MAP_ID = -1;
   public static final Codec<MapIndex> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.INT.optionalFieldOf("map", -1).forGetter((var0x) -> var0x.lastMapId)).apply(var0, MapIndex::new));
   public static final SavedDataType<MapIndex> TYPE;
   private int lastMapId;

   public MapIndex() {
      this(-1);
   }

   public MapIndex(int var1) {
      super();
      this.lastMapId = var1;
   }

   public MapId getNextMapId() {
      MapId var1 = new MapId(++this.lastMapId);
      this.setDirty();
      return var1;
   }

   static {
      TYPE = new SavedDataType<MapIndex>("idcounts", MapIndex::new, CODEC, DataFixTypes.SAVED_DATA_MAP_INDEX);
   }
}
