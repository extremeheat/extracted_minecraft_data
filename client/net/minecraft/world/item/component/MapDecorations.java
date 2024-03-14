package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

public record MapDecorations(Map<String, MapDecorations.Entry> c) {
   private final Map<String, MapDecorations.Entry> decorations;
   public static final MapDecorations EMPTY = new MapDecorations(Map.of());
   public static final Codec<MapDecorations> CODEC = Codec.unboundedMap(Codec.STRING, MapDecorations.Entry.CODEC)
      .xmap(MapDecorations::new, MapDecorations::decorations);

   public MapDecorations(Map<String, MapDecorations.Entry> var1) {
      super();
      this.decorations = var1;
   }

   public MapDecorations withDecoration(String var1, MapDecorations.Entry var2) {
      return new MapDecorations(Util.copyAndPut(this.decorations, var1, var2));
   }

   public static record Entry(MapDecoration.Type b, double c, double d, float e) {
      private final MapDecoration.Type type;
      private final double x;
      private final double z;
      private final float rotation;
      public static final Codec<MapDecorations.Entry> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  MapDecoration.Type.CODEC.fieldOf("type").forGetter(MapDecorations.Entry::type),
                  Codec.DOUBLE.fieldOf("x").forGetter(MapDecorations.Entry::x),
                  Codec.DOUBLE.fieldOf("z").forGetter(MapDecorations.Entry::z),
                  Codec.FLOAT.fieldOf("rotation").forGetter(MapDecorations.Entry::rotation)
               )
               .apply(var0, MapDecorations.Entry::new)
      );

      public Entry(MapDecoration.Type var1, double var2, double var4, float var6) {
         super();
         this.type = var1;
         this.x = var2;
         this.z = var4;
         this.rotation = var6;
      }
   }
}
