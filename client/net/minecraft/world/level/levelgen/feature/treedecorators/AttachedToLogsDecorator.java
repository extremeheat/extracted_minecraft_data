package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class AttachedToLogsDecorator extends TreeDecorator {
   public static final MapCodec<AttachedToLogsDecorator> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((var0x) -> var0x.probability), BlockStateProvider.CODEC.fieldOf("block_provider").forGetter((var0x) -> var0x.blockProvider), ExtraCodecs.nonEmptyList(Direction.CODEC.listOf()).fieldOf("directions").forGetter((var0x) -> var0x.directions)).apply(var0, AttachedToLogsDecorator::new));
   private final float probability;
   private final BlockStateProvider blockProvider;
   private final List<Direction> directions;

   public AttachedToLogsDecorator(float var1, BlockStateProvider var2, List<Direction> var3) {
      super();
      this.probability = var1;
      this.blockProvider = var2;
      this.directions = var3;
   }

   public void place(TreeDecorator.Context var1) {
      RandomSource var2 = var1.random();

      for(BlockPos var4 : Util.shuffledCopy(var1.logs(), var2)) {
         Direction var5 = (Direction)Util.getRandom(this.directions, var2);
         BlockPos var6 = var4.relative(var5);
         if (var2.nextFloat() <= this.probability && var1.isAir(var6)) {
            var1.setBlock(var6, this.blockProvider.getState(var2, var6));
         }
      }

   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.ATTACHED_TO_LOGS;
   }
}
