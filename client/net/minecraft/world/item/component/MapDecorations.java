package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.util.Util;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public record MapDecorations(Map<String, Entry> decorations) {
   public static final MapDecorations EMPTY = new MapDecorations(Map.of());
   public static final Codec<MapDecorations> CODEC;

   public MapDecorations {
      super();
   }

   public MapDecorations withDecoration(final String id, final Entry entry) {
      return new MapDecorations(Util.copyAndPut(this.decorations, id, entry));
   }

   static {
      CODEC = Codec.unboundedMap(Codec.STRING, MapDecorations.Entry.CODEC).xmap(MapDecorations::new, MapDecorations::decorations);
   }

   public static record Entry(Holder<MapDecorationType> type, double x, double z, float rotation) {
      public static final Codec<Entry> CODEC = RecordCodecBuilder.create((i) -> i.group(MapDecorationType.CODEC.fieldOf("type").forGetter(Entry::type), Codec.DOUBLE.fieldOf("x").forGetter(Entry::x), Codec.DOUBLE.fieldOf("z").forGetter(Entry::z), Codec.FLOAT.fieldOf("rotation").forGetter(Entry::rotation)).apply(i, Entry::new));

      public Entry {
         super();
      }
   }
}
