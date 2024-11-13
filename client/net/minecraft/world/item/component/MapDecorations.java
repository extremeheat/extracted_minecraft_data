package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public record MapDecorations(Map<String, Entry> decorations) {
   public static final MapDecorations EMPTY = new MapDecorations(Map.of());
   public static final Codec<MapDecorations> CODEC;

   public MapDecorations(Map<String, Entry> var1) {
      super();
      this.decorations = var1;
   }

   public MapDecorations withDecoration(String var1, Entry var2) {
      return new MapDecorations(Util.copyAndPut(this.decorations, var1, var2));
   }

   static {
      CODEC = Codec.unboundedMap(Codec.STRING, MapDecorations.Entry.CODEC).xmap(MapDecorations::new, MapDecorations::decorations);
   }

   public static record Entry(Holder<MapDecorationType> type, double x, double z, float rotation) {
      public static final Codec<Entry> CODEC = RecordCodecBuilder.create((var0) -> var0.group(MapDecorationType.CODEC.fieldOf("type").forGetter(Entry::type), Codec.DOUBLE.fieldOf("x").forGetter(Entry::x), Codec.DOUBLE.fieldOf("z").forGetter(Entry::z), Codec.FLOAT.fieldOf("rotation").forGetter(Entry::rotation)).apply(var0, Entry::new));

      public Entry(Holder<MapDecorationType> var1, double var2, double var4, float var6) {
         super();
         this.type = var1;
         this.x = var2;
         this.z = var4;
         this.rotation = var6;
      }
   }
}
