package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class MapBannerBlockPosFormatFix extends DataFix {
   public MapBannerBlockPosFormatFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.SAVED_DATA_MAP_DATA);
      OpticFinder var2 = var1.findField("data");
      OpticFinder var3 = var2.type().findField("banners");
      OpticFinder var4 = DSL.typeFinder(((List.ListType)var3.type()).getElement());
      return this.fixTypeEverywhereTyped("MapBannerBlockPosFormatFix", var1, (var3x) -> var3x.updateTyped(var2, (var2x) -> var2x.updateTyped(var3, (var1) -> var1.updateTyped(var4, (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.update("Pos", ExtraDataFixUtils::fixBlockPos))))));
   }
}
