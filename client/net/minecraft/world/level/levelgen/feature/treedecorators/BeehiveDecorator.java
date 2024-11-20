package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BeehiveDecorator extends TreeDecorator {
   public static final MapCodec<BeehiveDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(BeehiveDecorator::new, (var0) -> var0.probability);
   private static final Direction WORLDGEN_FACING;
   private static final Direction[] SPAWN_DIRECTIONS;
   private final float probability;

   public BeehiveDecorator(float var1) {
      super();
      this.probability = var1;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.BEEHIVE;
   }

   public void place(TreeDecorator.Context var1) {
      ObjectArrayList var2 = var1.leaves();
      ObjectArrayList var3 = var1.logs();
      if (!var3.isEmpty()) {
         RandomSource var4 = var1.random();
         if (!(var4.nextFloat() >= this.probability)) {
            int var5 = !var2.isEmpty() ? Math.max(((BlockPos)var2.getFirst()).getY() - 1, ((BlockPos)var3.getFirst()).getY() + 1) : Math.min(((BlockPos)var3.getFirst()).getY() + 1 + var4.nextInt(3), ((BlockPos)var3.getLast()).getY());
            List var6 = (List)var3.stream().filter((var1x) -> var1x.getY() == var5).flatMap((var0) -> {
               Stream var10000 = Stream.of(SPAWN_DIRECTIONS);
               Objects.requireNonNull(var0);
               return var10000.map(var0::relative);
            }).collect(Collectors.toList());
            if (!var6.isEmpty()) {
               Util.shuffle(var6, var4);
               Optional var7 = var6.stream().filter((var1x) -> var1.isAir(var1x) && var1.isAir(var1x.relative(WORLDGEN_FACING))).findFirst();
               if (!var7.isEmpty()) {
                  var1.setBlock((BlockPos)var7.get(), (BlockState)Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, WORLDGEN_FACING));
                  var1.level().getBlockEntity((BlockPos)var7.get(), BlockEntityType.BEEHIVE).ifPresent((var1x) -> {
                     int var2 = 2 + var4.nextInt(2);

                     for(int var3 = 0; var3 < var2; ++var3) {
                        var1x.storeBee(BeehiveBlockEntity.Occupant.create(var4.nextInt(599)));
                     }

                  });
               }
            }
         }
      }
   }

   static {
      WORLDGEN_FACING = Direction.SOUTH;
      SPAWN_DIRECTIONS = (Direction[])Direction.Plane.HORIZONTAL.stream().filter((var0) -> var0 != WORLDGEN_FACING.getOpposite()).toArray((var0) -> new Direction[var0]);
   }
}
