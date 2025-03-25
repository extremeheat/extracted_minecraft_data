package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import org.slf4j.Logger;

public class LevelUUIDFix extends AbstractUUIDFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public LevelUUIDFix(Schema var1) {
      super(var1, References.LEVEL);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(this.typeReference);
      OpticFinder var2 = var1.findField("CustomBossEvents");
      OpticFinder var3 = DSL.typeFinder(DSL.and(DSL.optional(DSL.field("Name", this.getInputSchema().getTypeRaw(References.TEXT_COMPONENT))), DSL.remainderType()));
      return this.fixTypeEverywhereTyped("LevelUUIDFix", var1, (var3x) -> var3x.update(DSL.remainderFinder(), (var1) -> {
            var1 = this.updateDragonFight(var1);
            var1 = this.updateWanderingTrader(var1);
            return var1;
         }).updateTyped(var2, (var2x) -> var2x.updateTyped(var3, (var1) -> var1.update(DSL.remainderFinder(), this::updateCustomBossEvent))));
   }

   private Dynamic<?> updateWanderingTrader(Dynamic<?> var1) {
      return (Dynamic)replaceUUIDString(var1, "WanderingTraderId", "WanderingTraderId").orElse(var1);
   }

   private Dynamic<?> updateDragonFight(Dynamic<?> var1) {
      return var1.update("DimensionData", (var0) -> var0.updateMapValues((var0x) -> var0x.mapSecond((var0) -> var0.update("DragonFight", (var0x) -> (Dynamic)replaceUUIDLeastMost(var0x, "DragonUUID", "Dragon").orElse(var0x)))));
   }

   private Dynamic<?> updateCustomBossEvent(Dynamic<?> var1) {
      return var1.update("Players", (var1x) -> var1.createList(var1x.asStream().map((var0) -> (Dynamic)createUUIDFromML(var0).orElseGet(() -> {
               LOGGER.warn("CustomBossEvents contains invalid UUIDs.");
               return var0;
            }))));
   }
}
