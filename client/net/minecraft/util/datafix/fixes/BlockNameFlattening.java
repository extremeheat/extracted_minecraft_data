package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class BlockNameFlattening extends DataFix {
   public BlockNameFlattening(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211300_p);
      Type var2 = this.getOutputSchema().getType(TypeReferences.field_211300_p);
      Type var3 = DSL.named(TypeReferences.field_211300_p.typeName(), DSL.or(DSL.intType(), DSL.namespacedString()));
      Type var4 = DSL.named(TypeReferences.field_211300_p.typeName(), DSL.namespacedString());
      if (Objects.equals(var1, var3) && Objects.equals(var2, var4)) {
         return this.fixTypeEverywhere("BlockNameFlatteningFix", var3, var4, (var0) -> {
            return (var0x) -> {
               return var0x.mapSecond((var0) -> {
                  return (String)var0.map(BlockStateFlatteningMap::func_207215_a, (var0x) -> {
                     return BlockStateFlatteningMap.func_199198_a(NamespacedSchema.func_206477_f(var0x));
                  });
               });
            };
         });
      } else {
         throw new IllegalStateException("Expected and actual types don't match.");
      }
   }
}
