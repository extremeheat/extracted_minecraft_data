package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class PoiTypeRenameFix extends AbstractPoiSectionFix {
   private final Function<String, String> renamer;

   public PoiTypeRenameFix(Schema var1, String var2, Function<String, String> var3) {
      super(var1, var2);
      this.renamer = var3;
   }

   protected <T> Stream<Dynamic<T>> processRecords(Stream<Dynamic<T>> var1) {
      return var1.map((var1x) -> {
         return var1x.update("type", (var1) -> {
            DataResult var10000 = var1.asString().map(this.renamer);
            Objects.requireNonNull(var1);
            return (Dynamic)DataFixUtils.orElse(var10000.map(var1::createString).result(), var1);
         });
      });
   }
}
