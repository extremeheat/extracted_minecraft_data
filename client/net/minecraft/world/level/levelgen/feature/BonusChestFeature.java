package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BonusChestFeature extends Feature<NoneFeatureConfiguration> {
   public BonusChestFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      for(BlockState var6 = var1.getBlockState(var4); (var6.isAir() || var6.is(BlockTags.LEAVES)) && var4.getY() > 1; var6 = var1.getBlockState(var4)) {
         var4 = var4.below();
      }

      if (var4.getY() < 1) {
         return false;
      } else {
         var4 = var4.above();

         for(int var7 = 0; var7 < 4; ++var7) {
            BlockPos var8 = var4.offset(var3.nextInt(4) - var3.nextInt(4), var3.nextInt(3) - var3.nextInt(3), var3.nextInt(4) - var3.nextInt(4));
            if (var1.isEmptyBlock(var8)) {
               var1.setBlock(var8, Blocks.CHEST.defaultBlockState(), 2);
               RandomizableContainerBlockEntity.setLootTable(var1, var3, var8, BuiltInLootTables.SPAWN_BONUS_CHEST);
               BlockState var9 = Blocks.TORCH.defaultBlockState();
               Iterator var10 = Direction.Plane.HORIZONTAL.iterator();

               while(var10.hasNext()) {
                  Direction var11 = (Direction)var10.next();
                  BlockPos var12 = var8.relative(var11);
                  if (var9.canSurvive(var1, var12)) {
                     var1.setBlock(var12, var9, 2);
                  }
               }

               return true;
            }
         }

         return false;
      }
   }
}
