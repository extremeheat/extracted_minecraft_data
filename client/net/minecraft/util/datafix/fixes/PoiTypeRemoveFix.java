package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PoiTypeRemoveFix extends AbstractPoiSectionFix {
   private final Predicate<String> typesToKeep;

   public PoiTypeRemoveFix(Schema var1, String var2, Predicate<String> var3) {
      super(var1, var2);
      this.typesToKeep = var3.negate();
   }

   protected <T> Stream<Dynamic<T>> processRecords(Stream<Dynamic<T>> var1) {
      return var1.filter(this::shouldKeepRecord);
   }

   private <T> boolean shouldKeepRecord(Dynamic<T> var1) {
      return var1.get("type").asString().result().filter(this.typesToKeep).isPresent();
   }
}
