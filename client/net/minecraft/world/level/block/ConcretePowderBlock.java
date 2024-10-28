package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ConcretePowderBlock extends FallingBlock {
   public static final MapCodec<ConcretePowderBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("concrete").forGetter((var0x) -> {
         return var0x.concrete;
      }), propertiesCodec()).apply(var0, ConcretePowderBlock::new);
   });
   private final Block concrete;

   public MapCodec<ConcretePowderBlock> codec() {
      return CODEC;
   }

   public ConcretePowderBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.concrete = var1;
   }

   public void onLand(Level var1, BlockPos var2, BlockState var3, BlockState var4, FallingBlockEntity var5) {
      if (shouldSolidify(var1, var2, var4)) {
         var1.setBlock(var2, this.concrete.defaultBlockState(), 3);
      }

   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      return shouldSolidify(var2, var3, var4) ? this.concrete.defaultBlockState() : super.getStateForPlacement(var1);
   }

   private static boolean shouldSolidify(BlockGetter var0, BlockPos var1, BlockState var2) {
      return canSolidify(var2) || touchesLiquid(var0, var1);
   }

   private static boolean touchesLiquid(BlockGetter var0, BlockPos var1) {
      boolean var2 = false;
      BlockPos.MutableBlockPos var3 = var1.mutable();
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         BlockState var8 = var0.getBlockState(var3);
         if (var7 != Direction.DOWN || canSolidify(var8)) {
            var3.setWithOffset(var1, (Direction)var7);
            var8 = var0.getBlockState(var3);
            if (canSolidify(var8) && !var8.isFaceSturdy(var0, var1, var7.getOpposite())) {
               var2 = true;
               break;
            }
         }
      }

      return var2;
   }

   private static boolean canSolidify(BlockState var0) {
      return var0.getFluidState().is(FluidTags.WATER);
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return touchesLiquid(var4, var5) ? this.concrete.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public int getDustColor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.getMapColor(var2, var3).col;
   }
}
