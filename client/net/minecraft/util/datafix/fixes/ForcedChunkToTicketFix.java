package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class ForcedChunkToTicketFix extends DataFix {
   public ForcedChunkToTicketFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("ForcedChunkToTicketFix", this.getInputSchema().getType(References.SAVED_DATA_TICKETS), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.update("data", (var1) -> var1.renameAndFixField("Forced", "tickets", (var1x) -> var1x.createList(var1x.asLongStream().mapToObj((var1) -> var0x.emptyMap().set("type", var0x.createString("minecraft:forced")).set("level", var0x.createInt(31)).set("ticks_left", var0x.createLong(0L)).set("chunk_pos", var0x.createLong(var1))))))));
   }
}
