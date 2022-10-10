package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;

public class TippedArrow extends TypedEntityRenameHelper {
   public TippedArrow(Schema var1, boolean var2) {
      super("EntityTippedArrowFix", var1, var2);
   }

   protected String func_211311_a(String var1) {
      return Objects.equals(var1, "TippedArrow") ? "Arrow" : var1;
   }
}
