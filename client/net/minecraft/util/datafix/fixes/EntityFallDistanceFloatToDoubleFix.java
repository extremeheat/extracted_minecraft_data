package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class EntityFallDistanceFloatToDoubleFix extends DataFix {
   private final DSL.TypeReference type;

   public EntityFallDistanceFloatToDoubleFix(Schema var1, DSL.TypeReference var2) {
      super(var1, false);
      this.type = var2;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityFallDistanceFloatToDoubleFixFor" + this.type.typeName(), this.getOutputSchema().getType(this.type), EntityFallDistanceFloatToDoubleFix::fixEntity);
   }

   private static Typed<?> fixEntity(Typed<?> var0) {
      return var0.update(DSL.remainderFinder(), (var0x) -> var0x.renameAndFixField("FallDistance", "fall_distance", (var0) -> var0.createDouble((double)var0.asFloat(0.0F))));
   }
}
