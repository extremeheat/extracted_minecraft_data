package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class ElderGuardianSplit extends EntityRenameHelper {
   public ElderGuardianSplit(Schema var1, boolean var2) {
      super("EntityElderGuardianSplitFix", var1, var2);
   }

   protected Pair<String, Dynamic<?>> func_209758_a(String var1, Dynamic<?> var2) {
      return Pair.of(Objects.equals(var1, "Guardian") && var2.getBoolean("Elder") ? "ElderGuardian" : var1, var2);
   }
}
