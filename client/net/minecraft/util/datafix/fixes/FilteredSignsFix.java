package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class FilteredSignsFix extends NamedEntityFix {
   public FilteredSignsFix(Schema var1) {
      super(var1, false, "Remove filtered text from signs", References.BLOCK_ENTITY, "minecraft:sign");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> {
         return var0.remove("FilteredText1").remove("FilteredText2").remove("FilteredText3").remove("FilteredText4");
      });
   }
}
