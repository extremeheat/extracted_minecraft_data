package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class EmptyPoolElement extends StructurePoolElement {
   public static final MapCodec<EmptyPoolElement> CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });
   public static final EmptyPoolElement INSTANCE = new EmptyPoolElement();

   private EmptyPoolElement() {
      super(StructureTemplatePool.Projection.TERRAIN_MATCHING);
   }

   public Vec3i getSize(StructureTemplateManager var1, Rotation var2) {
      return Vec3i.ZERO;
   }

   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager var1, BlockPos var2, Rotation var3, RandomSource var4) {
      return Collections.emptyList();
   }

   public BoundingBox getBoundingBox(StructureTemplateManager var1, BlockPos var2, Rotation var3) {
      throw new IllegalStateException("Invalid call to EmtyPoolElement.getBoundingBox, filter me!");
   }

   public boolean place(StructureTemplateManager var1, WorldGenLevel var2, StructureManager var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, Rotation var7, BoundingBox var8, RandomSource var9, LiquidSettings var10, boolean var11) {
      return true;
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.EMPTY;
   }

   public String toString() {
      return "Empty";
   }
}
