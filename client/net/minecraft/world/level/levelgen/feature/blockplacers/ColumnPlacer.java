package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class ColumnPlacer extends BlockPlacer {
   public static final Codec<ColumnPlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.fieldOf("min_size").forGetter((var0x) -> {
         return var0x.minSize;
      }), Codec.INT.fieldOf("extra_size").forGetter((var0x) -> {
         return var0x.extraSize;
      })).apply(var0, ColumnPlacer::new);
   });
   private final int minSize;
   private final int extraSize;

   public ColumnPlacer(int var1, int var2) {
      super();
      this.minSize = var1;
      this.extraSize = var2;
   }

   protected BlockPlacerType<?> type() {
      return BlockPlacerType.COLUMN_PLACER;
   }

   public void place(LevelAccessor var1, BlockPos var2, BlockState var3, Random var4) {
      BlockPos.MutableBlockPos var5 = var2.mutable();
      int var6 = this.minSize + var4.nextInt(var4.nextInt(this.extraSize + 1) + 1);

      for(int var7 = 0; var7 < var6; ++var7) {
         var1.setBlock(var5, var3, 2);
         var5.move(Direction.UP);
      }

   }
}
