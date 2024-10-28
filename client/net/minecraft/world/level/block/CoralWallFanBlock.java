package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class CoralWallFanBlock extends BaseCoralWallFanBlock {
   public static final MapCodec<CoralWallFanBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(CoralBlock.DEAD_CORAL_FIELD.forGetter((var0x) -> {
         return var0x.deadBlock;
      }), propertiesCodec()).apply(var0, CoralWallFanBlock::new);
   });
   private final Block deadBlock;

   public MapCodec<CoralWallFanBlock> codec() {
      return CODEC;
   }

   protected CoralWallFanBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.deadBlock = var1;
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      this.tryScheduleDieTick(var1, var2, var2, var2.random, var3);
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!scanForWater(var1, var2, var3)) {
         var2.setBlock(var3, (BlockState)((BlockState)this.deadBlock.defaultBlockState().setValue(WATERLOGGED, false)).setValue(FACING, (Direction)var1.getValue(FACING)), 2);
      }

   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if (var5.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var2, var4)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if ((Boolean)var1.getValue(WATERLOGGED)) {
            var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
         }

         this.tryScheduleDieTick(var1, var2, var3, var8, var4);
         return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }
}
