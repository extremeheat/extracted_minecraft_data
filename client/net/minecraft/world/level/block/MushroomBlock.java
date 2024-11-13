package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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

public class MushroomBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<MushroomBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("feature").forGetter((var0x) -> var0x.feature), propertiesCodec()).apply(var0, MushroomBlock::new));
   protected static final float AABB_OFFSET = 3.0F;
   protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
   private final ResourceKey<ConfiguredFeature<?, ?>> feature;

   public MapCodec<MushroomBlock> codec() {
      return CODEC;
   }

   public MushroomBlock(ResourceKey<ConfiguredFeature<?, ?>> var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.feature = var1;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(25) == 0) {
         int var5 = 5;
         boolean var6 = true;

         for(BlockPos var8 : BlockPos.betweenClosed(var3.offset(-4, -1, -4), var3.offset(4, 1, 4))) {
            if (var2.getBlockState(var8).is(this)) {
               --var5;
               if (var5 <= 0) {
                  return;
               }
            }
         }

         BlockPos var9 = var3.offset(var4.nextInt(3) - 1, var4.nextInt(2) - var4.nextInt(2), var4.nextInt(3) - 1);

         for(int var10 = 0; var10 < 4; ++var10) {
            if (var2.isEmptyBlock(var9) && var1.canSurvive(var2, var9)) {
               var3 = var9;
            }

            var9 = var3.offset(var4.nextInt(3) - 1, var4.nextInt(2) - var4.nextInt(2), var4.nextInt(3) - 1);
         }

         if (var2.isEmptyBlock(var9) && var1.canSurvive(var2, var9)) {
            var2.setBlock(var9, var1, 2);
         }
      }

   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.isSolidRender();
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      BlockState var5 = var2.getBlockState(var4);
      if (var5.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
         return true;
      } else {
         return var2.getRawBrightness(var3, 0) < 13 && this.mayPlaceOn(var5, var2, var4);
      }
   }

   public boolean growMushroom(ServerLevel var1, BlockPos var2, BlockState var3, RandomSource var4) {
      Optional var5 = var1.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(this.feature);
      if (var5.isEmpty()) {
         return false;
      } else {
         var1.removeBlock(var2, false);
         if (((ConfiguredFeature)((Holder)var5.get()).value()).place(var1, var1.getChunkSource().getGenerator(), var4, var2)) {
            return true;
         } else {
            var1.setBlock(var2, var3, 3);
            return false;
         }
      }
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return true;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return (double)var2.nextFloat() < 0.4;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      this.growMushroom(var1, var3, var4, var2);
   }
}
