package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class WorldBorderWarningTimeFix extends DataFix {
   public WorldBorderWarningTimeFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.writeFixAndRead("WorldBorderWarningTimeFix", this.getInputSchema().getType(References.SAVED_DATA_WORLD_BORDER), this.getOutputSchema().getType(References.SAVED_DATA_WORLD_BORDER), (var0) -> var0.update("data", (var0x) -> var0x.update("warning_time", (var1) -> var0x.createInt(var1.asInt(15) * 20))));
   }
}
