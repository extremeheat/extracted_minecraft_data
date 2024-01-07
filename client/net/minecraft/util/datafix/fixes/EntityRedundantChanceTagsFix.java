package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;

public class EntityRedundantChanceTagsFix extends DataFix {
   private static final Codec<List<Float>> FLOAT_LIST_CODEC = Codec.FLOAT.listOf();

   public EntityRedundantChanceTagsFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(
         "EntityRedundantChanceTagsFix", this.getInputSchema().getType(References.ENTITY), var0 -> var0.update(DSL.remainderFinder(), var0x -> {
               if (isZeroList(var0x.get("HandDropChances"), 2)) {
                  var0x = var0x.remove("HandDropChances");
               }
   
               if (isZeroList(var0x.get("ArmorDropChances"), 4)) {
                  var0x = var0x.remove("ArmorDropChances");
               }
   
               return var0x;
            })
      );
   }

   private static boolean isZeroList(OptionalDynamic<?> var0, int var1) {
      return var0.flatMap(FLOAT_LIST_CODEC::parse)
         .map(var1x -> var1x.size() == var1 && var1x.stream().allMatch(var0xx -> var0xx == 0.0F))
         .result()
         .orElse(false);
   }
}
