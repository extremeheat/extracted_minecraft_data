package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;

public class WrittenBookPagesStrictJsonFix extends ItemStackTagFix {
   public WrittenBookPagesStrictJsonFix(Schema var1) {
      super(var1, "WrittenBookPagesStrictJsonFix", (var0) -> var0.equals("minecraft:written_book"));
   }

   protected Typed<?> fixItemStackTag(Typed<?> var1) {
      Type var2 = this.getInputSchema().getType(References.TEXT_COMPONENT);
      Type var3 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var4 = var3.findField("tag");
      OpticFinder var5 = var4.type().findField("pages");
      OpticFinder var6 = DSL.typeFinder(var2);
      return var1.updateTyped(var5, (var1x) -> var1x.update(var6, (var0) -> var0.mapSecond(LegacyComponentDataFixUtils::rewriteFromLenient)));
   }
}
