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
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;
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
      OpticFinder var7 = var5.type().findField("minecraft:item_name");
      OpticFinder var8 = DSL.typeFinder(this.getInputSchema().getType(References.TEXT_COMPONENT));
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("Ominous Banner block entity common rarity to uncommon rarity fix", var1, (var5x) -> {
         Object var6 = ((Pair)var5x.get(var3.finder())).getFirst();
         return var6.equals("minecraft:banner") ? this.fix(var5x, var5, var7, var8) : var5x;
      }), this.fixTypeEverywhereTyped("Ominous Banner item stack common rarity to uncommon rarity fix", var2, (var5x) -> {
         String var6x = (String)var5x.getOptional(var4).map(Pair::getSecond).orElse("");
         return var6x.equals("minecraft:white_banner") ? this.fix(var5x, var6, var7, var8) : var5x;
      }));
   }

   private Typed<?> fix(Typed<?> var1, OpticFinder<?> var2, OpticFinder<?> var3, OpticFinder<Pair<String, String>> var4) {
      return var1.updateTyped(var2, (var2x) -> {
         boolean var3x = var2x.getOptionalTyped(var3).flatMap((var1) -> var1.getOptional(var4)).map(Pair::getSecond).flatMap(LegacyComponentDataFixUtils::extractTranslationString).filter((var0) -> var0.equals("block.minecraft.ominous_banner")).isPresent();
         return var3x ? var2x.updateTyped(var3, (var1) -> var1.set(var4, Pair.of(References.TEXT_COMPONENT.typeName(), LegacyComponentDataFixUtils.createTranslatableComponentJson("block.minecraft.ominous_banner")))).update(DSL.remainderFinder(), (var0) -> var0.set("minecraft:rarity", var0.createString("uncommon"))) : var2x;
      });
   }
}
