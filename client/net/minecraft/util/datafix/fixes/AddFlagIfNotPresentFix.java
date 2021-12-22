package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class AddFlagIfNotPresentFix extends DataFix {
   private final String name;
   private final boolean flagValue;
   private final String flagKey;
   private final TypeReference typeReference;

   public AddFlagIfNotPresentFix(Schema var1, TypeReference var2, String var3, boolean var4) {
      super(var1, true);
      this.flagValue = var4;
      this.flagKey = var3;
      String var10001 = this.flagKey;
      this.name = "AddFlagIfNotPresentFix_" + var10001 + "=" + this.flagValue + " for " + var1.getVersionKey();
      this.typeReference = var2;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(this.typeReference);
      return this.fixTypeEverywhereTyped(this.name, var1, (var1x) -> {
         return var1x.update(DSL.remainderFinder(), (var1) -> {
            return var1.set(this.flagKey, (Dynamic)DataFixUtils.orElseGet(var1.get(this.flagKey).result(), () -> {
               return var1.createBoolean(this.flagValue);
            }));
         });
      });
   }
}
