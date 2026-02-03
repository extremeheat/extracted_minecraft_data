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

   public EntityRedundantChanceTagsFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(References.ENTITY), (input) -> input.update(DSL.remainderFinder(), (tag) -> {
            if (isZeroList(tag.get("HandDropChances"), 2)) {
               tag = tag.remove("HandDropChances");
            }

            if (isZeroList(tag.get("ArmorDropChances"), 4)) {
               tag = tag.remove("ArmorDropChances");
            }

            return tag;
         }));
   }

   private static boolean isZeroList(final OptionalDynamic<?> element, final int size) {
      Codec var10001 = FLOAT_LIST_CODEC;
      Objects.requireNonNull(var10001);
      return (Boolean)element.flatMap(var10001::parse).map((floats) -> floats.size() == size && floats.stream().allMatch((f) -> f == 0.0F)).result().orElse(false);
   }

   static {
      FLOAT_LIST_CODEC = Codec.FLOAT.listOf();
   }
}
