package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class RemapChunkStatusFix extends DataFix {
   private final String name;
   private final UnaryOperator<String> mapper;

   public RemapChunkStatusFix(Schema var1, String var2, UnaryOperator<String> var3) {
      super(var1, false);
      this.name = var2;
      this.mapper = var3;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(
         this.name,
         this.getInputSchema().getType(References.CHUNK),
         var1 -> var1.update(
               DSL.remainderFinder(),
               var1x -> var1x.update("Status", this::fixStatus).update("below_zero_retrogen", var1xx -> var1xx.update("target_status", this::fixStatus))
            )
      );
   }

   private <T> Dynamic<T> fixStatus(Dynamic<T> var1) {
      Optional var2 = var1.asString().result().map(NamespacedSchema::ensureNamespaced).map(this.mapper).map(var1::createString);
      return (Dynamic<T>)DataFixUtils.orElse(var2, var1);
   }
}
