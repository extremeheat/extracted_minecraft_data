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
      return this.fixTypeEverywhereTyped("Leaves fix", var1, var1x -> var1x.updateTyped(var2, var0x -> var0x.update(DSL.remainderFinder(), var0xx -> {
               Optional var1xx = var0xx.get("Biomes").asIntStreamOpt().result();
               if (var1xx.isEmpty()) {
                  return var0xx;
               } else {
                  int[] var2x = ((IntStream)var1xx.get()).toArray();
                  if (var2x.length != 256) {
                     return var0xx;
                  } else {
                     int[] var3 = new int[1024];

                     for (int var4 = 0; var4 < 4; var4++) {
                        for (int var5 = 0; var5 < 4; var5++) {
                           int var6 = (var5 << 2) + 2;
                           int var7 = (var4 << 2) + 2;
                           int var8 = var7 << 4 | var6;
                           var3[var4 << 2 | var5] = var2x[var8];
                        }
                     }

                     for (int var9 = 1; var9 < 64; var9++) {
                        System.arraycopy(var3, 0, var3, var9 * 16, 16);
                     }

                     return var0xx.set("Biomes", var0xx.createIntList(Arrays.stream(var3)));
                  }
               }
            })));
   }
}
