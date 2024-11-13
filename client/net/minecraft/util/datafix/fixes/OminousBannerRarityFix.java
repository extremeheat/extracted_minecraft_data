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
import net.minecraft.util.datafix.ComponentDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class OminousBannerRarityFix extends DataFix {
   public OminousBannerRarityFix(Schema var1) {
      super(var1, false);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.BLOCK_ENTITY);
      Type var2 = this.getInputSchema().getType(References.ITEM_STACK);
      TaggedChoice.TaggedChoiceType var3 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
      OpticFinder var4 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var5 = var1.findField("components");
      OpticFinder var6 = var2.findField("components");
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("Ominous Banner block entity common rarity to uncommon rarity fix", var1, (var3x) -> {
         Object var4 = ((Pair)var3x.get(var3.finder())).getFirst();
         return var4.equals("minecraft:banner") ? this.fix(var3x, var5) : var3x;
      }), this.fixTypeEverywhereTyped("Ominous Banner item stack common rarity to uncommon rarity fix", var2, (var3x) -> {
         String var4x = (String)var3x.getOptional(var4).map(Pair::getSecond).orElse("");
         return var4x.equals("minecraft:white_banner") ? this.fix(var3x, var6) : var3x;
      }));
   }

   private Typed<?> fix(Typed<?> var1, OpticFinder<?> var2) {
      return var1.updateTyped(var2, (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> {
            boolean var1 = var0x.get("minecraft:item_name").asString().result().flatMap(ComponentDataFixUtils::extractTranslationString).filter((var0) -> var0.equals("block.minecraft.ominous_banner")).isPresent();
            return var1 ? var0x.set("minecraft:rarity", var0x.createString("uncommon")).set("minecraft:item_name", ComponentDataFixUtils.createTranslatableComponent(var0x.getOps(), "block.minecraft.ominous_banner")) : var0x;
         }));
   }
}
