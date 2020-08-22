package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.Optional;

public abstract class PoiTypeRename extends DataFix {
   public PoiTypeRename(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(References.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(var1, this.getInputSchema().getType(References.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI rename", var1, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond(this::cap);
            };
         });
      }
   }

   private Dynamic cap(Dynamic var1) {
      return var1.update("Sections", (var1x) -> {
         return var1x.updateMapValues((var1) -> {
            return var1.mapSecond((var1x) -> {
               return var1x.update("Records", (var1) -> {
                  return (Dynamic)DataFixUtils.orElse(this.renameRecords(var1), var1);
               });
            });
         });
      });
   }

   private Optional renameRecords(Dynamic var1) {
      return var1.asStreamOpt().map((var2) -> {
         return var1.createList(var2.map((var1x) -> {
            return var1x.update("type", (var1) -> {
               Optional var10000 = var1.asString().map(this::rename);
               var1.getClass();
               return (Dynamic)DataFixUtils.orElse(var10000.map(var1::createString), var1);
            });
         }));
      });
   }

   protected abstract String rename(String var1);
}
