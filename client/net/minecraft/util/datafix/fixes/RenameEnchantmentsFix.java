package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.function.Function;

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
      return this.fixTypeEverywhereTyped(this.name, var1, var2x -> var2x.updateTyped(var2, var1xx -> var1xx.update(DSL.remainderFinder(), this::fixTag)));
   }

   private Dynamic<?> fixTag(Dynamic<?> var1) {
      var1 = this.fixEnchantmentList(var1, "Enchantments");
      return this.fixEnchantmentList(var1, "StoredEnchantments");
   }

   private Dynamic<?> fixEnchantmentList(Dynamic<?> var1, String var2) {
      return var1.update(
         var2,
         var1x -> (Dynamic)var1x.asStreamOpt()
               .map(
                  var1xx -> var1xx.map(
                        var1xxx -> var1xxx.update(
                              "id",
                              var2x -> (Dynamic)var2x.asString()
                                    .map(var2xx -> var1xxx.createString(this.renames.getOrDefault(var2xx, var2xx)))
                                    .mapOrElse(Function.identity(), var1xxxxx -> var2x)
                           )
                     )
               )
               .map(var1x::createList)
               .mapOrElse(Function.identity(), var1xx -> var1x)
      );
   }
}
