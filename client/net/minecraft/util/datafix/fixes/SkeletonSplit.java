package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class SkeletonSplit extends EntityRenameHelper {
   public SkeletonSplit(Schema var1, boolean var2) {
      super("EntitySkeletonSplitFix", var1, var2);
   }

   protected Pair<String, Dynamic<?>> func_209758_a(String var1, Dynamic<?> var2) {
      if (Objects.equals(var1, "Skeleton")) {
         int var3 = var2.getInt("SkeletonType");
         if (var3 == 1) {
            var1 = "WitherSkeleton";
         } else if (var3 == 2) {
            var1 = "Stray";
         }
      }

      return Pair.of(var1, var2);
   }
}
