package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class EmptyPoolElement extends StructurePoolElement {
   public static final EmptyPoolElement INSTANCE = new EmptyPoolElement();

   private EmptyPoolElement() {
      super(StructureTemplatePool.Projection.TERRAIN_MATCHING);
   }

   public List getShuffledJigsawBlocks(StructureManager var1, BlockPos var2, Rotation var3, Random var4) {
      return Collections.emptyList();
   }

   public BoundingBox getBoundingBox(StructureManager var1, BlockPos var2, Rotation var3) {
      return BoundingBox.getUnknownBox();
   }

   public boolean place(StructureManager var1, LevelAccessor var2, ChunkGenerator var3, BlockPos var4, Rotation var5, BoundingBox var6, Random var7) {
      return true;
   }

   public StructurePoolElementType getType() {
      return StructurePoolElementType.EMPTY;
   }

   public Dynamic getDynamic(DynamicOps var1) {
      return new Dynamic(var1, var1.emptyMap());
   }

   public String toString() {
      return "Empty";
   }
}
