package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FungusBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<FungusBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("feature").forGetter(var0x -> var0x.feature),
               BuiltInRegistries.BLOCK.byNameCodec().fieldOf("grows_on").forGetter(var0x -> var0x.requiredBlock),
               propertiesCodec()
            )
            .apply(var0, FungusBlock::new)
   );
   protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 9.0, 12.0);
   private static final double BONEMEAL_SUCCESS_PROBABILITY = 0.4;
   private final Block requiredBlock;
   private final ResourceKey<ConfiguredFeature<?, ?>> feature;

   @Override
   public MapCodec<FungusBlock> codec() {
      return CODEC;
   }

   protected FungusBlock(ResourceKey<ConfiguredFeature<?, ?>> var1, Block var2, BlockBehaviour.Properties var3) {
      super(var3);
      this.feature = var1;
      this.requiredBlock = var2;
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(BlockTags.NYLIUM) || var1.is(Blocks.MYCELIUM) || var1.is(Blocks.SOUL_SOIL) || super.mayPlaceOn(var1, var2, var3);
   }

   private Optional<? extends Holder<ConfiguredFeature<?, ?>>> getFeature(LevelReader var1) {
      return var1.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(this.feature);
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      BlockState var4 = var1.getBlockState(var2.below());
      return var4.is(this.requiredBlock);
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return (double)var2.nextFloat() < 0.4;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      this.getFeature(var1).ifPresent(var3x -> var3x.value().place(var1, var1.getChunkSource().getGenerator(), var2, var3));
   }
}
