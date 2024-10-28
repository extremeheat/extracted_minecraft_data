package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;

public class AdvancementsRenameFix extends DataFix {
   private final String name;
   private final Function<String, String> renamer;

   public AdvancementsRenameFix(Schema var1, boolean var2, String var3, Function<String, String> var4) {
      super(var1, var2);
      this.name = var3;
      this.renamer = var4;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(References.ADVANCEMENTS), (var1) -> {
         return var1.update(DSL.remainderFinder(), (var1x) -> {
            return var1x.updateMapValues((var2) -> {
               String var3 = ((Dynamic)var2.getFirst()).asString("");
               return var2.mapFirst((var3x) -> {
                  return var1x.createString((String)this.renamer.apply(var3));
               });
            });
         });
      });
   }
}
