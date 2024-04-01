package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BeehiveDecorator extends TreeDecorator {
   public static final Codec<BeehiveDecorator> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(var0x -> var0x.probability),
               Codec.BOOL.fieldOf("fixed_height").orElse(true).forGetter(var0x -> var0x.fixedHeight)
            )
            .apply(var0, BeehiveDecorator::new)
   );
   private static final Direction WORLDGEN_FACING = Direction.SOUTH;
   private static final Direction[] SPAWN_DIRECTIONS = Direction.Plane.HORIZONTAL
      .stream()
      .filter(var0 -> var0 != WORLDGEN_FACING.getOpposite())
      .toArray(var0 -> new Direction[var0]);
   private final float probability;
   private final boolean fixedHeight;

   public BeehiveDecorator(float var1, boolean var2) {
      super();
      this.probability = var1;
      this.fixedHeight = var2;
   }

   @Override
   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.BEEHIVE;
   }

   @Override
   public void place(TreeDecorator.Context var1) {
      RandomSource var2 = var1.random();
      if (!(var2.nextFloat() >= this.probability)) {
         ObjectArrayList var3 = var1.leaves();
         ObjectArrayList var4 = var1.logs();
         if (!var4.isEmpty()) {
            int var5;
            if (this.fixedHeight) {
               var5 = !var3.isEmpty()
                  ? Math.max(((BlockPos)var3.get(0)).getY() - 1, ((BlockPos)var4.get(0)).getY() + 1)
                  : Math.min(((BlockPos)var4.get(0)).getY() + 1 + var2.nextInt(3), ((BlockPos)var4.get(var4.size() - 1)).getY());
            } else {
               var5 = var2.nextIntBetweenInclusive(((BlockPos)var4.get(0)).getY() + 1, ((BlockPos)var4.get(var4.size() - 1)).getY() - 1);
            }

            List var6 = var4.stream()
               .filter(var1x -> var1x.getY() == var5)
               .flatMap(var0 -> Stream.of(SPAWN_DIRECTIONS).map(var0::relative))
               .collect(Collectors.toList());
            if (!var6.isEmpty()) {
               Collections.shuffle(var6);
               Optional var7 = var6.stream().filter(var1x -> var1.isAir(var1x) && var1.isAir(var1x.relative(WORLDGEN_FACING))).findFirst();
               if (!var7.isEmpty()) {
                  var1.setBlock((BlockPos)var7.get(), Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, WORLDGEN_FACING));
                  var1.level().getBlockEntity((BlockPos)var7.get(), BlockEntityType.BEEHIVE).ifPresent(var1x -> {
                     int var2xx = 2 + var2.nextInt(2);

                     for(int var3xx = 0; var3xx < var2xx; ++var3xx) {
                        var1x.storeBee(BeehiveBlockEntity.Occupant.create(var2.nextInt(599)));
                     }
                  });
               }
            }
         }
      }
   }
}
