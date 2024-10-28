package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class MapBannerBlockPosFormatFix extends DataFix {
   public MapBannerBlockPosFormatFix(Schema var1) {
      super(var1, false);
   }

   private static <T> Dynamic<T> fixMapSavedData(Dynamic<T> var0) {
      return var0.update("banners", (var0x) -> {
         return var0x.createList(var0x.asStream().map((var0) -> {
            return var0.update("Pos", ExtraDataFixUtils::fixBlockPos);
         }));
      });
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("MapBannerBlockPosFormatFix", this.getInputSchema().getType(References.SAVED_DATA_MAP_DATA), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            return var0x.update("data", MapBannerBlockPosFormatFix::fixMapSavedData);
         });
      });
   }
}
