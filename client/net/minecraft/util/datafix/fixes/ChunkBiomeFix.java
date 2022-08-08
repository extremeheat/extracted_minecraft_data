package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class ChunkBiomeFix extends DataFix {
   public ChunkBiomeFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder var2 = var1.findField("Level");
      return this.fixTypeEverywhereTyped("Leaves fix", var1, (var1x) -> {
         return var1x.updateTyped(var2, (var0) -> {
            return var0.update(DSL.remainderFinder(), (var0x) -> {
               Optional var1 = var0x.get("Biomes").asIntStreamOpt().result();
               if (var1.isEmpty()) {
                  return var0x;
               } else {
                  int[] var2 = ((IntStream)var1.get()).toArray();
                  if (var2.length != 256) {
                     return var0x;
                  } else {
                     int[] var3 = new int[1024];

                     int var4;
                     for(var4 = 0; var4 < 4; ++var4) {
                        for(int var5 = 0; var5 < 4; ++var5) {
                           int var6 = (var5 << 2) + 2;
                           int var7 = (var4 << 2) + 2;
                           int var8 = var7 << 4 | var6;
                           var3[var4 << 2 | var5] = var2[var8];
                        }
                     }

                     for(var4 = 1; var4 < 64; ++var4) {
                        System.arraycopy(var3, 0, var3, var4 * 16, 16);
                     }

                     return var0x.set("Biomes", var0x.createIntList(Arrays.stream(var3)));
                  }
               }
            });
         });
      });
   }
}
