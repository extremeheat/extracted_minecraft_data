package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntitySkeletonSplitFix extends SimpleEntityRenameFix {
   public EntitySkeletonSplitFix(Schema var1, boolean var2) {
      super("EntitySkeletonSplitFix", var1, var2);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String var1, Dynamic<?> var2) {
      if (Objects.equals(var1, "Skeleton")) {
         int var3 = var2.get("SkeletonType").asInt(0);
         if (var3 == 1) {
            var1 = "WitherSkeleton";
         } else if (var3 == 2) {
            var1 = "Stray";
         }
      }

      return Pair.of(var1, var2);
   }
}
