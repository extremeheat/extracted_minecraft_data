package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.nbt.NbtFormatException;

public class WorldGenSettingsDisallowOldCustomWorldsFix extends DataFix {
   public WorldGenSettingsDisallowOldCustomWorldsFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.WORLD_GEN_SETTINGS);
      OpticFinder var2 = var1.findField("dimensions");
      return this.fixTypeEverywhereTyped(
         "WorldGenSettingsDisallowOldCustomWorldsFix_" + this.getOutputSchema().getVersionKey(), var1, var1x -> var1x.updateTyped(var2, var0x -> {
               var0x.write().map(var0xx -> var0xx.getMapValues().map(var0xxx -> {
                     var0xxx.forEach((var0xxxx, var1xx) -> {
                        if (var1xx.get("type").asString().result().isEmpty()) {
                           throw new NbtFormatException("Unable load old custom worlds.");
                        }
                     });
                     return var0xxx;
                  }));
               return var0x;
            })
      );
   }
}
