package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;

public class BlockRotProcessor extends StructureProcessor {
   public static final Codec<BlockRotProcessor> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               RegistryCodecs.homogeneousList(Registry.BLOCK_REGISTRY).optionalFieldOf("rottable_blocks").forGetter(var0x -> var0x.rottableBlocks),
               Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter(var0x -> var0x.integrity)
            )
            .apply(var0, BlockRotProcessor::new)
   );
   private Optional<HolderSet<Block>> rottableBlocks;
   private final float integrity;

   public BlockRotProcessor(TagKey<Block> var1, float var2) {
      this(Optional.of(Registry.BLOCK.getOrCreateTag(var1)), var2);
   }

   public BlockRotProcessor(float var1) {
      this(Optional.empty(), var1);
   }

   private BlockRotProcessor(Optional<HolderSet<Block>> var1, float var2) {
      super();
      this.integrity = var2;
      this.rottableBlocks = var1;
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
      RandomSource var7 = var6.getRandom(var5.pos);
      return (!this.rottableBlocks.isPresent() || var4.state.is(this.rottableBlocks.get())) && !(var7.nextFloat() <= this.integrity) ? null : var5;
   }

   @Override
   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.BLOCK_ROT;
   }
}
