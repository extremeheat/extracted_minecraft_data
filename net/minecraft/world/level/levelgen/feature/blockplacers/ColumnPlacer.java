package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class ColumnPlacer extends BlockPlacer {
   private final int minSize;
   private final int extraSize;

   public ColumnPlacer(int var1, int var2) {
      super(BlockPlacerType.COLUMN_PLACER);
      this.minSize = var1;
      this.extraSize = var2;
   }

   public ColumnPlacer(Dynamic var1) {
      this(var1.get("min_size").asInt(1), var1.get("extra_size").asInt(2));
   }

   public void place(LevelAccessor var1, BlockPos var2, BlockState var3, Random var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos(var2);
      int var6 = this.minSize + var4.nextInt(var4.nextInt(this.extraSize + 1) + 1);

      for(int var7 = 0; var7 < var6; ++var7) {
         var1.setBlock(var5, var3, 2);
         var5.move(Direction.UP);
      }

   }

   public Object serialize(DynamicOps var1) {
      return (new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("type"), var1.createString(Registry.BLOCK_PLACER_TYPES.getKey(this.type).toString()), var1.createString("min_size"), var1.createInt(this.minSize), var1.createString("extra_size"), var1.createInt(this.extraSize))))).getValue();
   }
}
