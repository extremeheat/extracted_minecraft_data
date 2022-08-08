package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractPoiSectionFix extends DataFix {
   private final String name;

   public AbstractPoiSectionFix(Schema var1, String var2) {
      super(var1, false);
      this.name = var2;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(References.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(var1, this.getInputSchema().getType(References.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, var1, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond(this::cap);
            };
         });
      }
   }

   private <T> Dynamic<T> cap(Dynamic<T> var1) {
      return var1.update("Sections", (var1x) -> {
         return var1x.updateMapValues((var1) -> {
            return var1.mapSecond(this::processSection);
         });
      });
   }

   private Dynamic<?> processSection(Dynamic<?> var1) {
      return var1.update("Records", this::processSectionRecords);
   }

   private <T> Dynamic<T> processSectionRecords(Dynamic<T> var1) {
      return (Dynamic)DataFixUtils.orElse(var1.asStreamOpt().result().map((var2) -> {
         return var1.createList(this.processRecords(var2));
      }), var1);
   }

   protected abstract <T> Stream<Dynamic<T>> processRecords(Stream<Dynamic<T>> var1);
}
