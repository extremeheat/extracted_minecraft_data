package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class CoralBlock extends Block {
   public static final MapCodec<Block> DEAD_CORAL_FIELD = BuiltInRegistries.BLOCK.byNameCodec().fieldOf("dead");
   public static final MapCodec<CoralBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(DEAD_CORAL_FIELD.forGetter(var0x -> var0x.deadBlock), propertiesCodec()).apply(var0, CoralBlock::new)
   );
   private final Block deadBlock;

   public CoralBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.deadBlock = var1;
   }

   @Override
   public MapCodec<CoralBlock> codec() {
      return CODEC;
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!this.scanForWater(var2, var3)) {
         var2.setBlock(var3, this.deadBlock.defaultBlockState(), 2);
      }
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (!this.scanForWater(var2, var4)) {
         var3.scheduleTick(var4, this, 60 + var8.nextInt(40));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected boolean scanForWater(BlockGetter var1, BlockPos var2) {
      for (Direction var6 : Direction.values()) {
         FluidState var7 = var1.getFluidState(var2.relative(var6));
         if (var7.is(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      if (!this.scanForWater(var1.getLevel(), var1.getClickedPos())) {
         var1.getLevel().scheduleTick(var1.getClickedPos(), this, 60 + var1.getLevel().getRandom().nextInt(40));
      }

      return this.defaultBlockState();
   }
}
