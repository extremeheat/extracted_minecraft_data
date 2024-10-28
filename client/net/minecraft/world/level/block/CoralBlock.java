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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class CoralBlock extends Block {
   public static final MapCodec<Block> DEAD_CORAL_FIELD;
   public static final MapCodec<CoralBlock> CODEC;
   private final Block deadBlock;

   public CoralBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.deadBlock = var1;
   }

   public MapCodec<CoralBlock> codec() {
      return CODEC;
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!this.scanForWater(var2, var3)) {
         var2.setBlock(var3, this.deadBlock.defaultBlockState(), 2);
      }

   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!this.scanForWater(var4, var5)) {
         var4.scheduleTick(var5, (Block)this, 60 + var4.getRandom().nextInt(40));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected boolean scanForWater(BlockGetter var1, BlockPos var2) {
      Direction[] var3 = Direction.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction var6 = var3[var5];
         FluidState var7 = var1.getFluidState(var2.relative(var6));
         if (var7.is(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      if (!this.scanForWater(var1.getLevel(), var1.getClickedPos())) {
         var1.getLevel().scheduleTick(var1.getClickedPos(), this, 60 + var1.getLevel().getRandom().nextInt(40));
      }

      return this.defaultBlockState();
   }

   static {
      DEAD_CORAL_FIELD = BuiltInRegistries.BLOCK.byNameCodec().fieldOf("dead");
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(DEAD_CORAL_FIELD.forGetter((var0x) -> {
            return var0x.deadBlock;
         }), propertiesCodec()).apply(var0, CoralBlock::new);
      });
   }
}
