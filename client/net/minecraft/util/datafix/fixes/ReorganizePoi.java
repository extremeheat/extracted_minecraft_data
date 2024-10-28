package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class ReorganizePoi extends DataFix {
   public ReorganizePoi(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(References.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(var1, this.getInputSchema().getType(References.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI reorganization", var1, (var0) -> {
            return (var0x) -> {
               return var0x.mapSecond(ReorganizePoi::cap);
            };
         });
      }
   }

   private static <T> Dynamic<T> cap(Dynamic<T> var0) {
      HashMap var1 = Maps.newHashMap();

      for(int var2 = 0; var2 < 16; ++var2) {
         String var3 = String.valueOf(var2);
         Optional var4 = var0.get(var3).result();
         if (var4.isPresent()) {
            Dynamic var5 = (Dynamic)var4.get();
            Dynamic var6 = var0.createMap(ImmutableMap.of(var0.createString("Records"), var5));
            var1.put(var0.createInt(var2), var6);
            var0 = var0.remove(var3);
         }
      }

      return var0.set("Sections", var0.createMap(var1));
   }
}
