package net.minecraft.world;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.UnaryOperator;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

public class Stopwatches extends SavedData {
   private static final Codec<Stopwatches> CODEC;
   public static final SavedDataType<Stopwatches> TYPE;
   private final Map<Identifier, Stopwatch> stopwatches = new Object2ObjectOpenHashMap();

   private Stopwatches() {
      super();
   }

   private static Stopwatches unpack(final Map<Identifier, Long> stopwatches) {
      Stopwatches result = new Stopwatches();
      long currentTime = currentTime();
      stopwatches.forEach((id, accumulatedElapsedTime) -> result.stopwatches.put(id, new Stopwatch(currentTime, accumulatedElapsedTime)));
      return result;
   }

   private Map<Identifier, Long> pack() {
      long currentTime = currentTime();
      Map<Identifier, Long> result = new TreeMap();
      this.stopwatches.forEach((id, stopwatch) -> result.put(id, stopwatch.elapsedMilliseconds(currentTime)));
      return result;
   }

   public @Nullable Stopwatch get(final Identifier id) {
      return (Stopwatch)this.stopwatches.get(id);
   }

   public boolean add(final Identifier id, final Stopwatch stopwatch) {
      if (this.stopwatches.putIfAbsent(id, stopwatch) == null) {
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public boolean update(final Identifier id, final UnaryOperator<Stopwatch> update) {
      if (this.stopwatches.computeIfPresent(id, (key, value) -> (Stopwatch)update.apply(value)) != null) {
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public boolean remove(final Identifier id) {
      boolean removed = this.stopwatches.remove(id) != null;
      if (removed) {
         this.setDirty();
      }

      return removed;
   }

   public boolean isDirty() {
      return super.isDirty() || !this.stopwatches.isEmpty();
   }

   public List<Identifier> ids() {
      return List.copyOf(this.stopwatches.keySet());
   }

   public static long currentTime() {
      return Util.getMillis();
   }

   static {
      CODEC = Codec.unboundedMap(Identifier.CODEC, Codec.LONG).fieldOf("stopwatches").codec().xmap(Stopwatches::unpack, Stopwatches::pack);
      TYPE = new SavedDataType<Stopwatches>(Identifier.withDefaultNamespace("stopwatches"), Stopwatches::new, CODEC, DataFixTypes.SAVED_DATA_STOPWATCHES);
   }
}
