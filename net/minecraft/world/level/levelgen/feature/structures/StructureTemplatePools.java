package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class StructureTemplatePools {
   private final Map pools = Maps.newHashMap();

   public StructureTemplatePools() {
      this.register(StructureTemplatePool.EMPTY);
   }

   public void register(StructureTemplatePool var1) {
      this.pools.put(var1.getName(), var1);
   }

   public StructureTemplatePool getPool(ResourceLocation var1) {
      StructureTemplatePool var2 = (StructureTemplatePool)this.pools.get(var1);
      return var2 != null ? var2 : StructureTemplatePool.INVALID;
   }
}
