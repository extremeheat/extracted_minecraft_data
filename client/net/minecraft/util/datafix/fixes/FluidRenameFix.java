package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;

public class FluidRenameFix {
   public FluidRenameFix() {
      super();
   }

   public static DataFix create(final Schema outputSchema, final String name, final Function<String, String> renamer) {
      return NamespacedStringRenameFix.create(outputSchema, References.FLUID_NAME, name, renamer);
   }
}
