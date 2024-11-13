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
import com.mojang.serialization.OptionalDynamic;
import java.util.Map;
import net.minecraft.util.datafix.ComponentDataFixUtils;

public class BannerEntityCustomNameToOverrideComponentFix extends DataFix {
   public BannerEntityCustomNameToOverrideComponentFix(Schema var1) {
      super(var1, false);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.BLOCK_ENTITY);
      TaggedChoice.TaggedChoiceType var2 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
      OpticFinder var3 = var1.findField("components");
      return this.fixTypeEverywhereTyped("Banner entity custom_name to item_name component fix", var1, (var3x) -> {
         Object var4 = ((Pair)var3x.get(var2.finder())).getFirst();
         return var4.equals("minecraft:banner") ? this.fix(var3x, var3) : var3x;
      });
   }

   private Typed<?> fix(Typed<?> var1, OpticFinder<?> var2) {
      Dynamic var3 = (Dynamic)var1.getOptional(DSL.remainderFinder()).orElseThrow();
      OptionalDynamic var4 = var3.get("CustomName");
      boolean var5 = var4.asString().result().flatMap(ComponentDataFixUtils::extractTranslationString).filter((var0) -> var0.equals("block.minecraft.ominous_banner")).isPresent();
      if (var5) {
         Typed var6 = var1.getOrCreateTyped(var2).update(DSL.remainderFinder(), (var1x) -> var1x.set("minecraft:item_name", (Dynamic)var4.result().get()).set("minecraft:hide_additional_tooltip", var1x.createMap(Map.of())));
         return var1.set(var2, var6).set(DSL.remainderFinder(), var3.remove("CustomName"));
      } else {
         return var1;
      }
   }
}
