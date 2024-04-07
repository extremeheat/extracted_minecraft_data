package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import java.util.stream.Stream;

public class PoiTypeRenameFix extends AbstractPoiSectionFix {
   private final Function<String, String> renamer;

   public PoiTypeRenameFix(Schema var1, String var2, Function<String, String> var3) {
      super(var1, var2);
      this.renamer = var3;
   }

   @Override
   protected <T> Stream<Dynamic<T>> processRecords(Stream<Dynamic<T>> var1) {
      return var1.map(
         var1x -> var1x.update("type", var1xx -> (Dynamic)DataFixUtils.orElse(var1xx.asString().map(this.renamer).map(var1xx::createString).result(), var1xx))
      );
   }
}
