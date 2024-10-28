package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.ComponentDataFixUtils;

public class ItemCustomNameToComponentFix extends DataFix {
   public ItemCustomNameToComponentFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   private Dynamic<?> fixTag(Dynamic<?> var1) {
      Optional var2 = var1.get("display").result();
      if (var2.isPresent()) {
         Dynamic var3 = (Dynamic)var2.get();
         Optional var4 = var3.get("Name").asString().result();
         if (var4.isPresent()) {
            var3 = var3.set("Name", ComponentDataFixUtils.createPlainTextComponent(var3.getOps(), (String)var4.get()));
         }

         return var1.set("display", var3);
      } else {
         return var1;
      }
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", var1, (var2x) -> {
         return var2x.updateTyped(var2, (var1) -> {
            return var1.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }
}
