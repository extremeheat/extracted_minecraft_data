package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class LegacyWorldBorderFix extends DataFix {
   public LegacyWorldBorderFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("LegacyWorldBorderFix", this.getInputSchema().getType(References.LEVEL), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> {
            Dynamic var1 = var0x.emptyMap().set("center_x", var0x.createDouble(var0x.get("BorderCenterX").asDouble(0.0))).set("center_z", var0x.createDouble(var0x.get("BorderCenterZ").asDouble(0.0))).set("size", var0x.createDouble(var0x.get("BorderSize").asDouble(5.9999968E7))).set("lerp_time", var0x.createLong(var0x.get("BorderSizeLerpTime").asLong(0L))).set("lerp_target", var0x.createDouble(var0x.get("BorderSizeLerpTarget").asDouble(0.0))).set("safe_zone", var0x.createDouble(var0x.get("BorderSafeZone").asDouble(5.0))).set("damage_per_block", var0x.createDouble(var0x.get("BorderDamagePerBlock").asDouble(0.2))).set("warning_blocks", var0x.createInt(var0x.get("BorderWarningBlocks").asInt(5))).set("warning_time", var0x.createInt(var0x.get("BorderWarningTime").asInt(15)));
            var0x = var0x.remove("BorderCenterX").remove("BorderCenterZ").remove("BorderSize").remove("BorderSizeLerpTime").remove("BorderSizeLerpTarget").remove("BorderSafeZone").remove("BorderDamagePerBlock").remove("BorderWarningBlocks").remove("BorderWarningTime");
            return var0x.set("world_border", var1);
         }));
   }
}
