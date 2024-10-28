package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class BlockNameFlatteningFix extends DataFix {
   public BlockNameFlatteningFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.BLOCK_NAME);
      Type var2 = this.getOutputSchema().getType(References.BLOCK_NAME);
      Type var3 = DSL.named(References.BLOCK_NAME.typeName(), DSL.or(DSL.intType(), NamespacedSchema.namespacedString()));
      Type var4 = DSL.named(References.BLOCK_NAME.typeName(), NamespacedSchema.namespacedString());
      if (Objects.equals(var1, var3) && Objects.equals(var2, var4)) {
         return this.fixTypeEverywhere("BlockNameFlatteningFix", var3, var4, (var0) -> {
            return (var0x) -> {
               return var0x.mapSecond((var0) -> {
                  return (String)var0.map(BlockStateData::upgradeBlock, (var0x) -> {
                     return BlockStateData.upgradeBlock(NamespacedSchema.ensureNamespaced(var0x));
                  });
               });
            };
         });
      } else {
         throw new IllegalStateException("Expected and actual types don't match.");
      }
   }
}
