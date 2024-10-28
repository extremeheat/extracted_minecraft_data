package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import java.util.Objects;

public class EntityRedundantChanceTagsFix extends DataFix {
   private static final Codec<List<Float>> FLOAT_LIST_CODEC;

   public EntityRedundantChanceTagsFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(References.ENTITY), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            if (isZeroList(var0x.get("HandDropChances"), 2)) {
               var0x = var0x.remove("HandDropChances");
            }

            if (isZeroList(var0x.get("ArmorDropChances"), 4)) {
               var0x = var0x.remove("ArmorDropChances");
            }

            return var0x;
         });
      });
   }

   private static boolean isZeroList(OptionalDynamic<?> var0, int var1) {
      Codec var10001 = FLOAT_LIST_CODEC;
      Objects.requireNonNull(var10001);
      return (Boolean)var0.flatMap(var10001::parse).map((var1x) -> {
         return var1x.size() == var1 && var1x.stream().allMatch((var0) -> {
            return var0 == 0.0F;
         });
      }).result().orElse(false);
   }

   static {
      FLOAT_LIST_CODEC = Codec.FLOAT.listOf();
   }
}
