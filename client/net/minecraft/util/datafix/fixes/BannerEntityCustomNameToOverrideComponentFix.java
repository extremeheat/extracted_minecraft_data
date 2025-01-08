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
import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;

public class BannerEntityCustomNameToOverrideComponentFix extends DataFix {
   public BannerEntityCustomNameToOverrideComponentFix(Schema var1) {
      super(var1, false);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.BLOCK_ENTITY);
      TaggedChoice.TaggedChoiceType var2 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
      OpticFinder var3 = var1.findField("CustomName");
      OpticFinder var4 = DSL.typeFinder(this.getInputSchema().getType(References.TEXT_COMPONENT));
      return this.fixTypeEverywhereTyped("Banner entity custom_name to item_name component fix", var1, (var4x) -> {
         Object var5 = ((Pair)var4x.get(var2.finder())).getFirst();
         return var5.equals("minecraft:banner") ? this.fix(var4x, var4, var3) : var4x;
      });
   }

   private Typed<?> fix(Typed<?> var1, OpticFinder<Pair<String, String>> var2, OpticFinder<?> var3) {
      Optional var4 = var1.getOptionalTyped(var3).flatMap((var1x) -> var1x.getOptional(var2).map(Pair::getSecond));
      boolean var5 = var4.flatMap(LegacyComponentDataFixUtils::extractTranslationString).filter((var0) -> var0.equals("block.minecraft.ominous_banner")).isPresent();
      return var5 ? Util.writeAndReadTypedOrThrow(var1, var1.getType(), (var1x) -> {
         Dynamic var2 = var1x.createMap(Map.of(var1x.createString("minecraft:item_name"), var1x.createString((String)var4.get()), var1x.createString("minecraft:hide_additional_tooltip"), var1x.emptyMap()));
         return var1x.set("components", var2).remove("CustomName");
      }) : var1;
   }
}
