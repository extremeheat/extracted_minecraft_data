package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class LevelLegacyWorldGenSettingsFix extends DataFix {
   private static final String WORLD_GEN_SETTINGS = "WorldGenSettings";
   private static final List<String> OLD_SETTINGS_KEYS = List.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");

   public LevelLegacyWorldGenSettingsFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("LevelLegacyWorldGenSettingsFix", this.getInputSchema().getType(References.LEVEL), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            Dynamic var1 = var0x.get("WorldGenSettings").orElseEmptyMap();
            Iterator var2 = OLD_SETTINGS_KEYS.iterator();

            while(var2.hasNext()) {
               String var3 = (String)var2.next();
               Optional var4 = var0x.get(var3).result();
               if (var4.isPresent()) {
                  var0x = var0x.remove(var3);
                  var1 = var1.set(var3, (Dynamic)var4.get());
               }
            }

            return var0x.set("WorldGenSettings", var1);
         });
      });
   }
}
