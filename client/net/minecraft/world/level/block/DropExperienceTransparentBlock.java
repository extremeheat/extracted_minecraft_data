package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropExperienceTransparentBlock extends DropExperienceBlock {
   public static final MapCodec<DropExperienceTransparentBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(IntProvider.codec(0, 10).fieldOf("experience").forGetter(var0x -> var0x.xpRange), propertiesCodec())
            .apply(var0, DropExperienceTransparentBlock::new)
   );

   @Override
   public MapCodec<DropExperienceTransparentBlock> codec() {
      return CODEC;
   }

   public DropExperienceTransparentBlock(IntProvider var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
   }

   @Override
   protected int getLightBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return 2;
   }
}
