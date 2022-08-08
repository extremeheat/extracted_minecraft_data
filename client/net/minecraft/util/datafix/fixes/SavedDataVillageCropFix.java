package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.stream.Stream;

public class SavedDataVillageCropFix extends DataFix {
   public SavedDataVillageCropFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(References.STRUCTURE_FEATURE), this.getOutputSchema().getType(References.STRUCTURE_FEATURE), this::fixTag);
   }

   private <T> Dynamic<T> fixTag(Dynamic<T> var1) {
      return var1.update("Children", SavedDataVillageCropFix::updateChildren);
   }

   private static <T> Dynamic<T> updateChildren(Dynamic<T> var0) {
      DataResult var10000 = var0.asStreamOpt().map(SavedDataVillageCropFix::updateChildren);
      Objects.requireNonNull(var0);
      return (Dynamic)var10000.map(var0::createList).result().orElse(var0);
   }

   private static Stream<? extends Dynamic<?>> updateChildren(Stream<? extends Dynamic<?>> var0) {
      return var0.map((var0x) -> {
         String var1 = var0x.get("id").asString("");
         if ("ViF".equals(var1)) {
            return updateSingleField(var0x);
         } else {
            return "ViDF".equals(var1) ? updateDoubleField(var0x) : var0x;
         }
      });
   }

   private static <T> Dynamic<T> updateSingleField(Dynamic<T> var0) {
      var0 = updateCrop(var0, "CA");
      return updateCrop(var0, "CB");
   }

   private static <T> Dynamic<T> updateDoubleField(Dynamic<T> var0) {
      var0 = updateCrop(var0, "CA");
      var0 = updateCrop(var0, "CB");
      var0 = updateCrop(var0, "CC");
      return updateCrop(var0, "CD");
   }

   private static <T> Dynamic<T> updateCrop(Dynamic<T> var0, String var1) {
      return var0.get(var1).asNumber().result().isPresent() ? var0.set(var1, BlockStateData.getTag(var0.get(var1).asInt(0) << 4)) : var0;
   }
}
