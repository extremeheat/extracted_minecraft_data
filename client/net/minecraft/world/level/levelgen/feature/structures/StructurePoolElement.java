package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public abstract class StructurePoolElement {
   @Nullable
   private volatile StructureTemplatePool.Projection projection;

   protected StructurePoolElement(StructureTemplatePool.Projection var1) {
      super();
      this.projection = var1;
   }

   protected StructurePoolElement(Dynamic<?> var1) {
      super();
      this.projection = StructureTemplatePool.Projection.byName(var1.get("projection").asString(StructureTemplatePool.Projection.RIGID.getName()));
   }

   public abstract List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureManager var1, BlockPos var2, Rotation var3, Random var4);

   public abstract BoundingBox getBoundingBox(StructureManager var1, BlockPos var2, Rotation var3);

   public abstract boolean place(StructureManager var1, LevelAccessor var2, BlockPos var3, Rotation var4, BoundingBox var5, Random var6);

   public abstract StructurePoolElementType getType();

   public void handleDataMarker(LevelAccessor var1, StructureTemplate.StructureBlockInfo var2, BlockPos var3, Rotation var4, Random var5, BoundingBox var6) {
   }

   public StructurePoolElement setProjection(StructureTemplatePool.Projection var1) {
      this.projection = var1;
      return this;
   }

   public StructureTemplatePool.Projection getProjection() {
      StructureTemplatePool.Projection var1 = this.projection;
      if (var1 == null) {
         throw new IllegalStateException();
      } else {
         return var1;
      }
   }

   protected abstract <T> Dynamic<T> getDynamic(DynamicOps<T> var1);

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      Object var2 = this.getDynamic(var1).getValue();
      Object var3 = var1.mergeInto(var2, var1.createString("element_type"), var1.createString(Registry.STRUCTURE_POOL_ELEMENT.getKey(this.getType()).toString()));
      return new Dynamic(var1, var1.mergeInto(var3, var1.createString("projection"), var1.createString(this.projection.getName())));
   }

   public int getGroundLevelDelta() {
      return 1;
   }
}
