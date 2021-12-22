package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;

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

   public void place(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, Random var3, List<BlockPos> var4, List<BlockPos> var5) {
      if (!(var3.nextFloat() >= this.probability)) {
         Direction var6 = BeehiveBlock.getRandomOffset(var3);
         int var7 = !var5.isEmpty() ? Math.max(((BlockPos)var5.get(0)).getY() - 1, ((BlockPos)var4.get(0)).getY()) : Math.min(((BlockPos)var4.get(0)).getY() + 1 + var3.nextInt(3), ((BlockPos)var4.get(var4.size() - 1)).getY());
         List var8 = (List)var4.stream().filter((var1x) -> {
            return var1x.getY() == var7;
         }).collect(Collectors.toList());
         if (!var8.isEmpty()) {
            BlockPos var9 = (BlockPos)var8.get(var3.nextInt(var8.size()));
            BlockPos var10 = var9.relative(var6);
            if (Feature.isAir(var1, var10) && Feature.isAir(var1, var10.relative(Direction.SOUTH))) {
               var2.accept(var10, (BlockState)Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, Direction.SOUTH));
               var1.getBlockEntity(var10, BlockEntityType.BEEHIVE).ifPresent((var1x) -> {
                  int var2 = 2 + var3.nextInt(2);

                  for(int var3x = 0; var3x < var2; ++var3x) {
                     CompoundTag var4 = new CompoundTag();
                     var4.putString("id", Registry.ENTITY_TYPE.getKey(EntityType.BEE).toString());
                     var1x.storeBee(var4, var3.nextInt(599), false);
                  }

               });
            }
         }
      }
   }
}
