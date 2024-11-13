package net.minecraft.world.level.block;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StemBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<StemBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceKey.codec(Registries.BLOCK).fieldOf("fruit").forGetter((var0x) -> var0x.fruit), ResourceKey.codec(Registries.BLOCK).fieldOf("attached_stem").forGetter((var0x) -> var0x.attachedStem), ResourceKey.codec(Registries.ITEM).fieldOf("seed").forGetter((var0x) -> var0x.seed), propertiesCodec()).apply(var0, StemBlock::new));
   public static final int MAX_AGE = 7;
   public static final IntegerProperty AGE;
   protected static final float AABB_OFFSET = 1.0F;
   protected static final VoxelShape[] SHAPE_BY_AGE;
   private final ResourceKey<Block> fruit;
   private final ResourceKey<Block> attachedStem;
   private final ResourceKey<Item> seed;

   public MapCodec<StemBlock> codec() {
      return CODEC;
   }

   protected StemBlock(ResourceKey<Block> var1, ResourceKey<Block> var2, ResourceKey<Item> var3, BlockBehaviour.Properties var4) {
      super(var4);
      this.fruit = var1;
      this.attachedStem = var2;
      this.seed = var3;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_AGE[(Integer)var1.getValue(AGE)];
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(Blocks.FARMLAND);
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.getRawBrightness(var3, 0) >= 9) {
         float var5 = CropBlock.getGrowthSpeed(this, var2, var3);
         if (var4.nextInt((int)(25.0F / var5) + 1) == 0) {
            int var6 = (Integer)var1.getValue(AGE);
            if (var6 < 7) {
               var1 = (BlockState)var1.setValue(AGE, var6 + 1);
               var2.setBlock(var3, var1, 2);
            } else {
               Direction var7 = Direction.Plane.HORIZONTAL.getRandomDirection(var4);
               BlockPos var8 = var3.relative(var7);
               BlockState var9 = var2.getBlockState(var8.below());
               if (var2.getBlockState(var8).isAir() && (var9.is(Blocks.FARMLAND) || var9.is(BlockTags.DIRT))) {
                  Registry var10 = var2.registryAccess().lookupOrThrow(Registries.BLOCK);
                  Optional var11 = var10.getOptional(this.fruit);
                  Optional var12 = var10.getOptional(this.attachedStem);
                  if (var11.isPresent() && var12.isPresent()) {
                     var2.setBlockAndUpdate(var8, ((Block)var11.get()).defaultBlockState());
                     var2.setBlockAndUpdate(var3, (BlockState)((Block)var12.get()).defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, var7));
                  }
               }
            }
         }

      }
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      return new ItemStack((ItemLike)DataFixUtils.orElse(var1.registryAccess().lookupOrThrow(Registries.ITEM).getOptional(this.seed), this));
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return (Integer)var3.getValue(AGE) != 7;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      int var5 = Math.min(7, (Integer)var4.getValue(AGE) + Mth.nextInt(var1.random, 2, 5));
      BlockState var6 = (BlockState)var4.setValue(AGE, var5);
      var1.setBlock(var3, var6, 2);
      if (var5 == 7) {
         var6.randomTick(var1, var3, var1.random);
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   static {
      AGE = BlockStateProperties.AGE_7;
      SHAPE_BY_AGE = new VoxelShape[]{Block.box(7.0, 0.0, 7.0, 9.0, 2.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 4.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 8.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 10.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 12.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 14.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)};
   }
}
