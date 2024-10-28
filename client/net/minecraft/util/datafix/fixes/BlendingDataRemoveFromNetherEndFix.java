package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;

public class BlendingDataRemoveFromNetherEndFix extends DataFix {
   public BlendingDataRemoveFromNetherEndFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(References.CHUNK);
      return this.fixTypeEverywhereTyped("BlendingDataRemoveFromNetherEndFix", var1, (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            return updateChunkTag(var0x, var0x.get("__context"));
         });
      });
   }

   private static Dynamic<?> updateChunkTag(Dynamic<?> var0, OptionalDynamic<?> var1) {
      boolean var2 = "minecraft:overworld".equals(var1.get("dimension").asString().result().orElse(""));
      return var2 ? var0 : var0.remove("blending_data");
   }
}
