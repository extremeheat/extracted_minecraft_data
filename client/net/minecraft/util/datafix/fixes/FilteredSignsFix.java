package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class FilteredSignsFix extends NamedEntityWriteReadFix {
   public FilteredSignsFix(Schema var1) {
      super(var1, false, "Remove filtered text from signs", References.BLOCK_ENTITY, "minecraft:sign");
   }

   protected <T> Dynamic<T> fix(Dynamic<T> var1) {
      return var1.remove("FilteredText1").remove("FilteredText2").remove("FilteredText3").remove("FilteredText4");
   }
}
