package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class EquippableAssetRenameFix extends DataFix {
   public EquippableAssetRenameFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.DATA_COMPONENTS);
      OpticFinder var2 = var1.findField("minecraft:equippable");
      return this.fixTypeEverywhereTyped("equippable asset rename fix", var1, (var1x) -> {
         return var1x.updateTyped(var2, (var0) -> {
            return var0.update(DSL.remainderFinder(), (var0x) -> {
               return var0x.renameField("model", "asset_id");
            });
         });
      });
   }
}
