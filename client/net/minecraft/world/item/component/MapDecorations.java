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

   public MapDecorations(Map<String, Entry> decorations) {
      super();
      this.decorations = decorations;
   }

   public MapDecorations withDecoration(String var1, Entry var2) {
      return new MapDecorations(Util.copyAndPut(this.decorations, var1, var2));
   }

   public Map<String, Entry> decorations() {
      return this.decorations;
   }

   static {
      CODEC = Codec.unboundedMap(Codec.STRING, MapDecorations.Entry.CODEC).xmap(MapDecorations::new, MapDecorations::decorations);
   }

   public static record Entry(Holder<MapDecorationType> type, double x, double z, float rotation) {
      public static final Codec<Entry> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(MapDecorationType.CODEC.fieldOf("type").forGetter(Entry::type), Codec.DOUBLE.fieldOf("x").forGetter(Entry::x), Codec.DOUBLE.fieldOf("z").forGetter(Entry::z), Codec.FLOAT.fieldOf("rotation").forGetter(Entry::rotation)).apply(var0, Entry::new);
      });

      public Entry(Holder<MapDecorationType> type, double x, double z, float rotation) {
         super();
         this.type = type;
         this.x = x;
         this.z = z;
         this.rotation = rotation;
      }

      public Holder<MapDecorationType> type() {
         return this.type;
      }

      public double x() {
         return this.x;
      }

      public double z() {
         return this.z;
      }

      public float rotation() {
         return this.rotation;
      }
   }
}
