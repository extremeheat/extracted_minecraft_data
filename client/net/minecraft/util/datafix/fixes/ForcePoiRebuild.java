package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;

public class ForcePoiRebuild extends DataFix {
   public ForcePoiRebuild(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(References.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(var1, this.getInputSchema().getType(References.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI rebuild", var1, var0 -> var0x -> var0x.mapSecond(ForcePoiRebuild::cap));
      }
   }

   private static <T> Dynamic<T> cap(Dynamic<T> var0) {
      return var0.update("Sections", var0x -> var0x.updateMapValues(var0xx -> var0xx.mapSecond(var0xxx -> var0xxx.remove("Valid"))));
   }
}
