package net.minecraft.world.level.levelgen.material;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseChunk;

public class MaterialRuleList implements WorldGenMaterialRule {
   private final List<WorldGenMaterialRule> materialRuleList;

   public MaterialRuleList(List<WorldGenMaterialRule> var1) {
      super();
      this.materialRuleList = var1;
   }

   @Nullable
   public BlockState apply(NoiseChunk var1, int var2, int var3, int var4) {
      Iterator var5 = this.materialRuleList.iterator();

      BlockState var7;
      do {
         if (!var5.hasNext()) {
            return null;
         }

         WorldGenMaterialRule var6 = (WorldGenMaterialRule)var5.next();
         var7 = var6.apply(var1, var2, var3, var4);
      } while(var7 == null);

      return var7;
   }
}
