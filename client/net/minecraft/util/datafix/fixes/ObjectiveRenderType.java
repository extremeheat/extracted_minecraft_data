package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.util.datafix.TypeReferences;

public class ObjectiveRenderType extends DataFix {
   public ObjectiveRenderType(Schema var1, boolean var2) {
      super(var1, var2);
   }

   private static ScoreCriteria.RenderType func_211858_a(String var0) {
      return var0.equals("health") ? ScoreCriteria.RenderType.HEARTS : ScoreCriteria.RenderType.INTEGER;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(TypeReferences.field_211873_t.typeName(), DSL.remainderType());
      if (!Objects.equals(var1, this.getInputSchema().getType(TypeReferences.field_211873_t))) {
         throw new IllegalStateException("Objective type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("ObjectiveRenderTypeFix", var1, (var0) -> {
            return (var0x) -> {
               return var0x.mapSecond((var0) -> {
                  Optional var1 = var0.get("RenderType").flatMap(Dynamic::getStringValue);
                  if (!var1.isPresent()) {
                     String var2 = var0.getString("CriteriaName");
                     ScoreCriteria.RenderType var3 = func_211858_a(var2);
                     return var0.set("RenderType", var0.createString(var3.func_211838_a()));
                  } else {
                     return var0;
                  }
               });
            };
         });
      }
   }
}
