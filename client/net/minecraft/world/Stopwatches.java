package net.minecraft.world;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.UnaryOperator;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

public class Stopwatches extends SavedData {
   private static final Codec<Stopwatches> CODEC;
   public static final SavedDataType<Stopwatches> TYPE;
   private final Map<ResourceLocation, Stopwatch> stopwatches = new Object2ObjectOpenHashMap();

   private Stopwatches() {
      super();
   }

   private static Stopwatches unpack(Map<ResourceLocation, Long> var0) {
      Stopwatches var1 = new Stopwatches();
      long var2 = currentTime();
      var0.forEach((var3, var4) -> var1.stopwatches.put(var3, new Stopwatch(var2, var4)));
      return var1;
   }

   private Map<ResourceLocation, Long> pack() {
      long var1 = currentTime();
      TreeMap var3 = new TreeMap();
      this.stopwatches.forEach((var3x, var4) -> var3.put(var3x, var4.elapsedMilliseconds(var1)));
      return var3;
   }

   public @Nullable Stopwatch get(ResourceLocation var1) {
      return (Stopwatch)this.stopwatches.get(var1);
   }

   public boolean add(ResourceLocation var1, Stopwatch var2) {
      if (this.stopwatches.putIfAbsent(var1, var2) == null) {
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public boolean update(ResourceLocation var1, UnaryOperator<Stopwatch> var2) {
      if (this.stopwatches.computeIfPresent(var1, (var1x, var2x) -> (Stopwatch)var2.apply(var2x)) != null) {
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public boolean remove(ResourceLocation var1) {
      boolean var2 = this.stopwatches.remove(var1) != null;
      if (var2) {
         this.setDirty();
      }

      return var2;
   }

   public boolean isDirty() {
      return super.isDirty() || !this.stopwatches.isEmpty();
   }

   public List<ResourceLocation> ids() {
      return List.copyOf(this.stopwatches.keySet());
   }

   public static long currentTime() {
      return Util.getMillis();
   }

   static {
      CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.LONG).fieldOf("stopwatches").codec().xmap(Stopwatches::unpack, Stopwatches::pack);
      TYPE = new SavedDataType<Stopwatches>("stopwatches", Stopwatches::new, CODEC, DataFixTypes.SAVED_DATA_STOPWATCHES);
   }
}
