package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CreakingHeartDecorator extends TreeDecorator {
   public static final MapCodec<CreakingHeartDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(CreakingHeartDecorator::new, (var0) -> {
      return var0.probability;
   });
   private final float probability;

   public CreakingHeartDecorator(float var1) {
      super();
      this.probability = var1;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.CREAKING_HEART;
   }

   public void place(TreeDecorator.Context var1) {
      RandomSource var2 = var1.random();
      ObjectArrayList var3 = var1.logs();
      if (!var3.isEmpty()) {
         if (!(var2.nextFloat() >= this.probability)) {
            ArrayList var4 = new ArrayList(var3);
            Util.shuffle(var4, var2);
            Optional var5 = var4.stream().filter((var1x) -> {
               Direction[] var2 = Direction.values();
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  Direction var5 = var2[var4];
                  if (!var1.checkBlock(var1x.relative(var5), (var0) -> {
                     return var0.is(BlockTags.LOGS);
                  })) {
                     return false;
                  }
               }

               return true;
            }).findFirst();
            if (!var5.isEmpty()) {
               var1.setBlock((BlockPos)var5.get(), (BlockState)((BlockState)Blocks.CREAKING_HEART.defaultBlockState().setValue(CreakingHeartBlock.ACTIVE, true)).setValue(CreakingHeartBlock.NATURAL, true));
            }
         }
      }
   }
}
