package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.DoubleUnaryOperator;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntityAttributeBaseValueFix extends NamedEntityFix {
   private final String attributeId;
   private final DoubleUnaryOperator valueFixer;

   public EntityAttributeBaseValueFix(Schema var1, String var2, String var3, String var4, DoubleUnaryOperator var5) {
      super(var1, false, var2, References.ENTITY, var3);
      this.attributeId = var4;
      this.valueFixer = var5;
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixValue);
   }

   private Dynamic<?> fixValue(Dynamic<?> var1) {
      return var1.update("attributes", (var2) -> var1.createList(var2.asStream().map((var1x) -> {
            String var2 = NamespacedSchema.ensureNamespaced(var1x.get("id").asString(""));
            if (!var2.equals(this.attributeId)) {
               return var1x;
            } else {
               double var3 = var1x.get("base").asDouble(0.0);
               return var1x.set("base", var1x.createDouble(this.valueFixer.applyAsDouble(var3)));
            }
         })));
   }
}
