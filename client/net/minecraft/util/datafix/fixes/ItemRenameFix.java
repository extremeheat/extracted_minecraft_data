package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;

public abstract class ItemRenameFix {
   public ItemRenameFix() {
      super();
   }

   public static DataFix create(final Schema outputSchema, final String name, final Function<String, String> fixItem) {
      return NamespacedStringRenameFix.create(outputSchema, References.ITEM_NAME, name, fixItem);
   }
}
