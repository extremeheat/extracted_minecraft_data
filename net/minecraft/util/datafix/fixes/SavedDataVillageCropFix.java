package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.stream.Stream;

public class SavedDataVillageCropFix extends DataFix {
   public SavedDataVillageCropFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(References.STRUCTURE_FEATURE), this.getOutputSchema().getType(References.STRUCTURE_FEATURE), this::fixTag);
   }

   private Dynamic fixTag(Dynamic var1) {
      return var1.update("Children", SavedDataVillageCropFix::updateChildren);
   }

   private static Dynamic updateChildren(Dynamic var0) {
      Optional var10000 = var0.asStreamOpt().map(SavedDataVillageCropFix::updateChildren);
      var0.getClass();
      return (Dynamic)var10000.map(var0::createList).orElse(var0);
   }

   private static Stream updateChildren(Stream var0) {
      return var0.map((var0x) -> {
         String var1 = var0x.get("id").asString("");
         if ("ViF".equals(var1)) {
            return updateSingleField(var0x);
         } else {
            return "ViDF".equals(var1) ? updateDoubleField(var0x) : var0x;
         }
      });
   }

   private static Dynamic updateSingleField(Dynamic var0) {
      var0 = updateCrop(var0, "CA");
      return updateCrop(var0, "CB");
   }

   private static Dynamic updateDoubleField(Dynamic var0) {
      var0 = updateCrop(var0, "CA");
      var0 = updateCrop(var0, "CB");
      var0 = updateCrop(var0, "CC");
      return updateCrop(var0, "CD");
   }

   private static Dynamic updateCrop(Dynamic var0, String var1) {
      return var0.get(var1).asNumber().isPresent() ? var0.set(var1, BlockStateData.getTag(var0.get(var1).asInt(0) << 4)) : var0;
   }
}
