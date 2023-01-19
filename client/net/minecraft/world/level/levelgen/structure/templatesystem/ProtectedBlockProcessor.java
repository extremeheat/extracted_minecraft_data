package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;

public class ProtectedBlockProcessor extends StructureProcessor {
   public final TagKey<Block> cannotReplace;
   public static final Codec<ProtectedBlockProcessor> CODEC = TagKey.hashedCodec(Registry.BLOCK_REGISTRY)
      .xmap(ProtectedBlockProcessor::new, var0 -> var0.cannotReplace);

   public ProtectedBlockProcessor(TagKey<Block> var1) {
      super();
      this.cannotReplace = var1;
   }

   @Nullable
   @Override
   public StructureTemplate.StructureBlockInfo processBlock(
      LevelReader var1,
      BlockPos var2,
      BlockPos var3,
      StructureTemplate.StructureBlockInfo var4,
      StructureTemplate.StructureBlockInfo var5,
      StructurePlaceSettings var6
   ) {
      return Feature.isReplaceable(this.cannotReplace).test(var1.getBlockState(var5.pos)) ? var5 : null;
   }

   @Override
   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.PROTECTED_BLOCKS;
   }
}
