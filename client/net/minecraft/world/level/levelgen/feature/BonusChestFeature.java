package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BonusChestFeature extends Feature<NoneFeatureConfiguration> {
   public BonusChestFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      RandomSource var2 = var1.random();
      WorldGenLevel var3 = var1.level();
      ChunkPos var4 = new ChunkPos(var1.origin());
      IntArrayList var5 = Util.toShuffledList(IntStream.rangeClosed(var4.getMinBlockX(), var4.getMaxBlockX()), var2);
      IntArrayList var6 = Util.toShuffledList(IntStream.rangeClosed(var4.getMinBlockZ(), var4.getMaxBlockZ()), var2);
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();
      IntListIterator var8 = var5.iterator();

      while(var8.hasNext()) {
         Integer var9 = (Integer)var8.next();
         IntListIterator var10 = var6.iterator();

         while(var10.hasNext()) {
            Integer var11 = (Integer)var10.next();
            var7.set(var9, 0, var11);
            BlockPos var12 = var3.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var7);
            if (var3.isEmptyBlock(var12) || var3.getBlockState(var12).getCollisionShape(var3, var12).isEmpty()) {
               var3.setBlock(var12, Blocks.CHEST.defaultBlockState(), 2);
               RandomizableContainer.setBlockEntityLootTable(var3, var2, var12, BuiltInLootTables.SPAWN_BONUS_CHEST);
               BlockState var13 = Blocks.TORCH.defaultBlockState();
               Iterator var14 = Direction.Plane.HORIZONTAL.iterator();

               while(var14.hasNext()) {
                  Direction var15 = (Direction)var14.next();
                  BlockPos var16 = var12.relative(var15);
                  if (var13.canSurvive(var3, var16)) {
                     var3.setBlock(var16, var13, 2);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}
