package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ObjectiveRenderTypeFix extends DataFix {
   public ObjectiveRenderTypeFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   private static ObjectiveCriteria.RenderType getRenderType(String var0) {
      return var0.equals("health") ? ObjectiveCriteria.RenderType.HEARTS : ObjectiveCriteria.RenderType.INTEGER;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.OBJECTIVE);
      return this.fixTypeEverywhereTyped("ObjectiveRenderTypeFix", var1, (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            Optional var1 = var0x.get("RenderType").asString().result();
            if (!var1.isPresent()) {
               String var2 = var0x.get("CriteriaName").asString("");
               ObjectiveCriteria.RenderType var3 = getRenderType(var2);
               return var0x.set("RenderType", var0x.createString(var3.getId()));
            } else {
               return var0x;
            }
         });
      });
   }
}
