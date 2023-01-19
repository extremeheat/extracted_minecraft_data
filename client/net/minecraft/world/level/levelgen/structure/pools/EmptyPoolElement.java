package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class EmptyPoolElement extends StructurePoolElement {
   public static final Codec<EmptyPoolElement> CODEC = Codec.unit(() -> EmptyPoolElement.INSTANCE);
   public static final EmptyPoolElement INSTANCE = new EmptyPoolElement();

   private EmptyPoolElement() {
      super(StructureTemplatePool.Projection.TERRAIN_MATCHING);
   }

   @Override
   public Vec3i getSize(StructureTemplateManager var1, Rotation var2) {
      return Vec3i.ZERO;
   }

   @Override
   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager var1, BlockPos var2, Rotation var3, RandomSource var4) {
      return Collections.emptyList();
   }

   @Override
   public BoundingBox getBoundingBox(StructureTemplateManager var1, BlockPos var2, Rotation var3) {
      throw new IllegalStateException("Invalid call to EmtyPoolElement.getBoundingBox, filter me!");
   }

   @Override
   public boolean place(
      StructureTemplateManager var1,
      WorldGenLevel var2,
      StructureManager var3,
      ChunkGenerator var4,
      BlockPos var5,
      BlockPos var6,
      Rotation var7,
      BoundingBox var8,
      RandomSource var9,
      boolean var10
   ) {
      return true;
   }

   @Override
   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.EMPTY;
   }

   @Override
   public String toString() {
      return "Empty";
   }
}
