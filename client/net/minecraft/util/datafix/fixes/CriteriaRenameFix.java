package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;

public class CriteriaRenameFix extends DataFix {
   private final String name;
   private final String advancementId;
   private final UnaryOperator<String> conversions;

   public CriteriaRenameFix(Schema var1, String var2, String var3, UnaryOperator<String> var4) {
      super(var1, false);
      this.name = var2;
      this.advancementId = var3;
      this.conversions = var4;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(
         this.name, this.getInputSchema().getType(References.ADVANCEMENTS), var1 -> var1.update(DSL.remainderFinder(), this::fixAdvancements)
      );
   }

   private Dynamic<?> fixAdvancements(Dynamic<?> var1) {
      return var1.update(
         this.advancementId,
         var1x -> var1x.update(
               "criteria",
               var1xx -> var1xx.updateMapValues(
                     var1xxx -> var1xxx.mapFirst(
                           var1xxxx -> (Dynamic)DataFixUtils.orElse(
                                 var1xxxx.asString().map(var2 -> var1xxxx.createString(this.conversions.apply(var2))).result(), var1xxxx
                              )
                        )
                  )
            )
      );
   }
}
