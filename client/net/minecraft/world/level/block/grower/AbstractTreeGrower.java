package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractTreeGrower {
   public AbstractTreeGrower() {
      super();
   }

   @Nullable
   protected abstract ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource var1, boolean var2);

   public boolean growTree(ServerLevel var1, ChunkGenerator var2, BlockPos var3, BlockState var4, RandomSource var5) {
      ResourceKey var6 = this.getConfiguredFeature(var5, this.hasFlowers(var1, var3));
      if (var6 == null) {
         return false;
      } else {
         Holder var7 = var1.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(var6).orElse(null);
         if (var7 == null) {
            return false;
         } else {
            ConfiguredFeature var8 = (ConfiguredFeature)var7.value();
            BlockState var9 = var1.getFluidState(var3).createLegacyBlock();
            var1.setBlock(var3, var9, 4);
            if (var8.place(var1, var2, var5, var3)) {
               if (var1.getBlockState(var3) == var9) {
                  var1.sendBlockUpdated(var3, var4, var9, 2);
               }

               return true;
            } else {
               var1.setBlock(var3, var4, 4);
               return false;
            }
         }
      }
   }

   private boolean hasFlowers(LevelAccessor var1, BlockPos var2) {
      for(BlockPos var4 : BlockPos.MutableBlockPos.betweenClosed(var2.below().north(2).west(2), var2.above().south(2).east(2))) {
         if (var1.getBlockState(var4).is(BlockTags.FLOWERS)) {
            return true;
         }
      }

      return false;
   }
}
