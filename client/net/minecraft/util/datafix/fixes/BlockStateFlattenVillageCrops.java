package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class BlockStateFlattenVillageCrops extends DataFix {
   public BlockStateFlattenVillageCrops(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(TypeReferences.field_211303_s), this.getOutputSchema().getType(TypeReferences.field_211303_s), this::func_209677_a);
   }

   private <T> Dynamic<T> func_209677_a(Dynamic<T> var1) {
      return var1.update("Children", BlockStateFlattenVillageCrops::func_210590_b);
   }

   private static <T> Dynamic<T> func_210590_b(Dynamic<T> var0) {
      Optional var10000 = var0.getStream().map(BlockStateFlattenVillageCrops::func_210586_a);
      var0.getClass();
      return (Dynamic)var10000.map(var0::createList).orElse(var0);
   }

   private static Stream<? extends Dynamic<?>> func_210586_a(Stream<? extends Dynamic<?>> var0) {
      return var0.map((var0x) -> {
         String var1 = var0x.getString("id");
         if ("ViF".equals(var1)) {
            return func_210588_c(var0x);
         } else {
            return "ViDF".equals(var1) ? func_210589_d(var0x) : var0x;
         }
      });
   }

   private static <T> Dynamic<T> func_210588_c(Dynamic<T> var0) {
      var0 = func_209676_a(var0, "CA");
      return func_209676_a(var0, "CB");
   }

   private static <T> Dynamic<T> func_210589_d(Dynamic<T> var0) {
      var0 = func_209676_a(var0, "CA");
      var0 = func_209676_a(var0, "CB");
      var0 = func_209676_a(var0, "CC");
      return func_209676_a(var0, "CD");
   }

   private static <T> Dynamic<T> func_209676_a(Dynamic<T> var0, String var1) {
      return var0.get(var1).flatMap(Dynamic::getNumberValue).isPresent() ? var0.set(var1, BlockStateFlatteningMap.func_210049_b(var0.getInt(var1) << 4)) : var0;
   }
}
