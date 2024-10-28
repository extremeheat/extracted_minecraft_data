package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class RenameEnchantmentsFix extends DataFix {
   final String name;
   final Map<String, String> renames;

   public RenameEnchantmentsFix(Schema var1, String var2, Map<String, String> var3) {
      super(var1, false);
      this.name = var2;
      this.renames = var3;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped(this.name, var1, (var2x) -> {
         return var2x.updateTyped(var2, (var1) -> {
            return var1.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }

   private Dynamic<?> fixTag(Dynamic<?> var1) {
      var1 = this.fixEnchantmentList(var1, "Enchantments");
      var1 = this.fixEnchantmentList(var1, "StoredEnchantments");
      return var1;
   }

   private Dynamic<?> fixEnchantmentList(Dynamic<?> var1, String var2) {
      return var1.update(var2, (var1x) -> {
         DataResult var10000 = var1x.asStreamOpt().map((var1) -> {
            return var1.map((var1x) -> {
               return var1x.update("id", (var2) -> {
                  return (Dynamic)var2.asString().map((var2x) -> {
                     return var1x.createString((String)this.renames.getOrDefault(NamespacedSchema.ensureNamespaced(var2x), var2x));
                  }).mapOrElse(Function.identity(), (var1) -> {
                     return var2;
                  });
               });
            });
         });
         Objects.requireNonNull(var1x);
         return (Dynamic)var10000.map(var1x::createList).mapOrElse(Function.identity(), (var1) -> {
            return var1x;
         });
      });
   }
}
