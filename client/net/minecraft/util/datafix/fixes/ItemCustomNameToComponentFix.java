package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;

public class ItemCustomNameToComponentFix extends DataFix {
   public ItemCustomNameToComponentFix(Schema var1) {
      super(var1, false);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      Type var2 = this.getInputSchema().getType(References.TEXT_COMPONENT);
      OpticFinder var3 = var1.findField("tag");
      OpticFinder var4 = var3.type().findField("display");
      OpticFinder var5 = var4.type().findField("Name");
      OpticFinder var6 = DSL.typeFinder(var2);
      return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", var1, (var4x) -> var4x.updateTyped(var3, (var3x) -> var3x.updateTyped(var4, (var2) -> var2.updateTyped(var5, (var1) -> var1.update(var6, (var0) -> var0.mapSecond(LegacyComponentDataFixUtils::createTextComponentJson))))));
   }
}
