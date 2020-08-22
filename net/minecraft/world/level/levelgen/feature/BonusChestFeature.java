package net.minecraft.world.level.levelgen.feature;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BonusChestFeature extends Feature {
   public BonusChestFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      ChunkPos var6 = new ChunkPos(var4);
      List var7 = (List)IntStream.rangeClosed(var6.getMinBlockX(), var6.getMaxBlockX()).boxed().collect(Collectors.toList());
      Collections.shuffle(var7, var3);
      List var8 = (List)IntStream.rangeClosed(var6.getMinBlockZ(), var6.getMaxBlockZ()).boxed().collect(Collectors.toList());
      Collections.shuffle(var8, var3);
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
      Iterator var10 = var7.iterator();

      while(var10.hasNext()) {
         Integer var11 = (Integer)var10.next();
         Iterator var12 = var8.iterator();

         while(var12.hasNext()) {
            Integer var13 = (Integer)var12.next();
            var9.set(var11, 0, var13);
            BlockPos var14 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var9);
            if (var1.isEmptyBlock(var14) || var1.getBlockState(var14).getCollisionShape(var1, var14).isEmpty()) {
               var1.setBlock(var14, Blocks.CHEST.defaultBlockState(), 2);
               RandomizableContainerBlockEntity.setLootTable(var1, var3, var14, BuiltInLootTables.SPAWN_BONUS_CHEST);
               BlockState var15 = Blocks.TORCH.defaultBlockState();
               Iterator var16 = Direction.Plane.HORIZONTAL.iterator();

               while(var16.hasNext()) {
                  Direction var17 = (Direction)var16.next();
                  BlockPos var18 = var14.relative(var17);
                  if (var15.canSurvive(var1, var18)) {
                     var1.setBlock(var18, var15, 2);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}
