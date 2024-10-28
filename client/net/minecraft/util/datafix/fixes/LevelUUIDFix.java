package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import org.slf4j.Logger;

public class LevelUUIDFix extends AbstractUUIDFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public LevelUUIDFix(Schema var1) {
      super(var1, References.LEVEL);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("LevelUUIDFix", this.getInputSchema().getType(this.typeReference), (var1) -> {
         return var1.updateTyped(DSL.remainderFinder(), (var1x) -> {
            return var1x.update(DSL.remainderFinder(), (var1) -> {
               var1 = this.updateCustomBossEvents(var1);
               var1 = this.updateDragonFight(var1);
               var1 = this.updateWanderingTrader(var1);
               return var1;
            });
         });
      });
   }

   private Dynamic<?> updateWanderingTrader(Dynamic<?> var1) {
      return (Dynamic)replaceUUIDString(var1, "WanderingTraderId", "WanderingTraderId").orElse(var1);
   }

   private Dynamic<?> updateDragonFight(Dynamic<?> var1) {
      return var1.update("DimensionData", (var0) -> {
         return var0.updateMapValues((var0x) -> {
            return var0x.mapSecond((var0) -> {
               return var0.update("DragonFight", (var0x) -> {
                  return (Dynamic)replaceUUIDLeastMost(var0x, "DragonUUID", "Dragon").orElse(var0x);
               });
            });
         });
      });
   }

   private Dynamic<?> updateCustomBossEvents(Dynamic<?> var1) {
      return var1.update("CustomBossEvents", (var0) -> {
         return var0.updateMapValues((var0x) -> {
            return var0x.mapSecond((var0) -> {
               return var0.update("Players", (var1) -> {
                  return var0.createList(var1.asStream().map((var0x) -> {
                     return (Dynamic)createUUIDFromML(var0x).orElseGet(() -> {
                        LOGGER.warn("CustomBossEvents contains invalid UUIDs.");
                        return var0x;
                     });
                  }));
               });
            });
         });
      });
   }
}
