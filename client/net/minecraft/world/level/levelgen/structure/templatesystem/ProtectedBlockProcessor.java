package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.jspecify.annotations.Nullable;

public class ProtectedBlockProcessor extends StructureProcessor {
   public final TagKey<Block> cannotReplace;
   public static final MapCodec<ProtectedBlockProcessor> CODEC;

   public ProtectedBlockProcessor(final TagKey<Block> cannotReplace) {
      super();
      this.cannotReplace = cannotReplace;
   }

   public StructureTemplate.@Nullable StructureBlockInfo processBlock(final LevelReader level, final BlockPos targetPosition, final BlockPos referencePos, final StructureTemplate.StructureBlockInfo originalBlockInfo, final StructureTemplate.StructureBlockInfo processedBlockInfo, final StructurePlaceSettings settings) {
      return Feature.isReplaceable(this.cannotReplace).test(level.getBlockState(processedBlockInfo.pos())) ? processedBlockInfo : null;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.PROTECTED_BLOCKS;
   }

   static {
      CODEC = TagKey.hashedCodec(Registries.BLOCK).xmap(ProtectedBlockProcessor::new, (e) -> e.cannotReplace).fieldOf("value");
   }
}
