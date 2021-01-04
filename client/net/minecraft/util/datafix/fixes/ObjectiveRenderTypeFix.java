package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
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
      Type var1 = DSL.named(References.OBJECTIVE.typeName(), DSL.remainderType());
      if (!Objects.equals(var1, this.getInputSchema().getType(References.OBJECTIVE))) {
         throw new IllegalStateException("Objective type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("ObjectiveRenderTypeFix", var1, (var0) -> {
            return (var0x) -> {
               return var0x.mapSecond((var0) -> {
                  Optional var1 = var0.get("RenderType").asString();
                  if (!var1.isPresent()) {
                     String var2 = var0.get("CriteriaName").asString("");
                     ObjectiveCriteria.RenderType var3 = getRenderType(var2);
                     return var0.set("RenderType", var0.createString(var3.getId()));
                  } else {
                     return var0;
                  }
               });
            };
         });
      }
   }
}
