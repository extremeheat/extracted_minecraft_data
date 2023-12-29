package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
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
         return this.fixTypeEverywhere(
            "BlockNameFlatteningFix",
            var3,
            var4,
            var0 -> var0x -> var0x.mapSecond(
                     var0xx -> (String)var0xx.map(
                           BlockStateData::upgradeBlock, var0xxx -> BlockStateData.upgradeBlock(NamespacedSchema.ensureNamespaced(var0xxx))
                        )
                  )
         );
      } else {
         throw new IllegalStateException("Expected and actual types don't match.");
      }
   }
}
