package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public record MapDecorations(Map<String, MapDecorations.Entry> decorations) {
   public static final MapDecorations EMPTY = new MapDecorations(Map.of());
   public static final Codec<MapDecorations> CODEC = Codec.unboundedMap(Codec.STRING, MapDecorations.Entry.CODEC)
      .xmap(MapDecorations::new, MapDecorations::decorations);

   public MapDecorations(Map<String, MapDecorations.Entry> decorations) {
      super();
      this.decorations = decorations;
   }

   public MapDecorations withDecoration(String var1, MapDecorations.Entry var2) {
      return new MapDecorations(Util.copyAndPut(this.decorations, var1, var2));
   }

   public static record Entry(Holder<MapDecorationType> type, double x, double z, float rotation) {
      public static final Codec<MapDecorations.Entry> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  MapDecorationType.CODEC.fieldOf("type").forGetter(MapDecorations.Entry::type),
                  Codec.DOUBLE.fieldOf("x").forGetter(MapDecorations.Entry::x),
                  Codec.DOUBLE.fieldOf("z").forGetter(MapDecorations.Entry::z),
                  Codec.FLOAT.fieldOf("rotation").forGetter(MapDecorations.Entry::rotation)
               )
               .apply(var0, MapDecorations.Entry::new)
      );

      public Entry(Holder<MapDecorationType> type, double x, double z, float rotation) {
         super();
         this.type = type;
         this.x = x;
         this.z = z;
         this.rotation = rotation;
      }
   }
}
