package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BeehiveDecorator extends TreeDecorator {
   public static final Codec<BeehiveDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(BeehiveDecorator::new, (var0) -> {
      return var0.probability;
   }).codec();
   private final float probability;

   public BeehiveDecorator(float var1) {
      super();
      this.probability = var1;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.BEEHIVE;
   }

   public void place(WorldGenLevel var1, Random var2, List<BlockPos> var3, List<BlockPos> var4, Set<BlockPos> var5, BoundingBox var6) {
      if (var2.nextFloat() < this.probability) {
         Direction var7 = BeehiveBlock.getRandomOffset(var2);
         int var8 = !var4.isEmpty() ? Math.max(((BlockPos)var4.get(0)).getY() - 1, ((BlockPos)var3.get(0)).getY()) : Math.min(((BlockPos)var3.get(0)).getY() + 1 + var2.nextInt(3), ((BlockPos)var3.get(var3.size() - 1)).getY());
         List var9 = (List)var3.stream().filter((var1x) -> {
            return var1x.getY() == var8;
         }).collect(Collectors.toList());
         if (!var9.isEmpty()) {
            BlockPos var10 = (BlockPos)var9.get(var2.nextInt(var9.size()));
            BlockPos var11 = var10.relative(var7);
            if (Feature.isAir(var1, var11) && Feature.isAir(var1, var11.relative(Direction.SOUTH))) {
               BlockState var12 = (BlockState)Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, Direction.SOUTH);
               this.setBlock(var1, var11, var12, var5, var6);
               BlockEntity var13 = var1.getBlockEntity(var11);
               if (var13 instanceof BeehiveBlockEntity) {
                  BeehiveBlockEntity var14 = (BeehiveBlockEntity)var13;
                  int var15 = 2 + var2.nextInt(2);

                  for(int var16 = 0; var16 < var15; ++var16) {
                     Bee var17 = new Bee(EntityType.BEE, var1.getLevel());
                     var14.addOccupantWithPresetTicks(var17, false, var2.nextInt(599));
                  }
               }

            }
         }
      }
   }
}
