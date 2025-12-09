package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Set;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.ExtraDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class SaddleEquipmentSlotFix extends DataFix {
   private static final Set<String> ENTITIES_WITH_SADDLE_ITEM = Set.of("minecraft:horse", "minecraft:skeleton_horse", "minecraft:zombie_horse", "minecraft:donkey", "minecraft:mule", "minecraft:camel", "minecraft:llama", "minecraft:trader_llama");
   private static final Set<String> ENTITIES_WITH_SADDLE_FLAG = Set.of("minecraft:pig", "minecraft:strider");
   private static final String SADDLE_FLAG = "Saddle";
   private static final String NEW_SADDLE = "saddle";

   public SaddleEquipmentSlotFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      TaggedChoice.TaggedChoiceType var1 = this.getInputSchema().findChoiceType(References.ENTITY);
      OpticFinder var2 = DSL.typeFinder(var1);
      Type var3 = this.getInputSchema().getType(References.ENTITY);
      Type var4 = this.getOutputSchema().getType(References.ENTITY);
      Type var5 = ExtraDataFixUtils.patchSubType(var3, var3, var4);
      return this.fixTypeEverywhereTyped("SaddleEquipmentSlotFix", var3, var4, (var3x) -> {
         String var4x = (String)var3x.getOptional(var2).map(Pair::getFirst).map(NamespacedSchema::ensureNamespaced).orElse("");
         Typed var5x = ExtraDataFixUtils.cast(var5, var3x);
         if (ENTITIES_WITH_SADDLE_ITEM.contains(var4x)) {
            return Util.writeAndReadTypedOrThrow(var5x, var4, SaddleEquipmentSlotFix::fixEntityWithSaddleItem);
         } else {
            return ENTITIES_WITH_SADDLE_FLAG.contains(var4x) ? Util.writeAndReadTypedOrThrow(var5x, var4, SaddleEquipmentSlotFix::fixEntityWithSaddleFlag) : ExtraDataFixUtils.cast(var4, var3x);
         }
      });
   }

   private static Dynamic<?> fixEntityWithSaddleItem(Dynamic<?> var0) {
      return var0.get("SaddleItem").result().isEmpty() ? var0 : fixDropChances(var0.renameField("SaddleItem", "saddle"));
   }

   private static Dynamic<?> fixEntityWithSaddleFlag(Dynamic<?> var0) {
      boolean var1 = var0.get("Saddle").asBoolean(false);
      var0 = var0.remove("Saddle");
      if (!var1) {
         return var0;
      } else {
         Dynamic var2 = var0.emptyMap().set("id", var0.createString("minecraft:saddle")).set("count", var0.createInt(1));
         return fixDropChances(var0.set("saddle", var2));
      }
   }

   private static Dynamic<?> fixDropChances(Dynamic<?> var0) {
      Dynamic var1 = var0.get("drop_chances").orElseEmptyMap().set("saddle", var0.createFloat(2.0F));
      return var0.set("drop_chances", var1);
   }
}
