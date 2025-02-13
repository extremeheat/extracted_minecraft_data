package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.stream.IntStream;

public class ChunkTicketUnpackPosFix extends DataFix {
   private static final long CHUNK_COORD_BITS = 32L;
   private static final long CHUNK_COORD_MASK = 4294967295L;

   public ChunkTicketUnpackPosFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("ChunkTicketUnpackPosFix", this.getInputSchema().getType(References.SAVED_DATA_TICKETS), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.update("data", (var0) -> var0.update("tickets", (var0x) -> var0x.createList(var0x.asStream().map((var0) -> var0.update("chunk_pos", (var0x) -> {
                        long var1 = var0x.asLong(0L);
                        int var3 = (int)(var1 & 4294967295L);
                        int var4 = (int)(var1 >>> 32 & 4294967295L);
                        return var0x.createIntList(IntStream.of(new int[]{var3, var4}));
                     })))))));
   }
}
