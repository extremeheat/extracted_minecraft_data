package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class PlayerRespawnDataFix extends DataFix {
   public PlayerRespawnDataFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("PlayerRespawnDataFix", this.getInputSchema().getType(References.PLAYER), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.update("respawn", (var0) -> var0.set("dimension", var0.createString(var0.get("dimension").asString("minecraft:overworld"))).set("yaw", var0.createFloat(var0.get("angle").asFloat(0.0F))).set("pitch", var0.createFloat(0.0F)).remove("angle"))));
   }
}
