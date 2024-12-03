package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class GlowLichenBlock extends MultifaceSpreadeableBlock implements BonemealableBlock {
   public static final MapCodec<GlowLichenBlock> CODEC = simpleCodec(GlowLichenBlock::new);
   private final MultifaceSpreader spreader = new MultifaceSpreader(this);

   public MapCodec<GlowLichenBlock> codec() {
      return CODEC;
   }

   public GlowLichenBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public static ToIntFunction<BlockState> emission(int var0) {
      return (var1) -> MultifaceBlock.hasAnyFace(var1) ? var0 : 0;
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return Direction.stream().anyMatch((var4) -> this.spreader.canSpreadInAnyDirection(var3, var1, var2, var4.getOpposite()));
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      this.spreader.spreadFromRandomFaceTowardRandomDirection(var4, var1, var3, var2);
   }

   protected boolean propagatesSkylightDown(BlockState var1) {
      return var1.getFluidState().isEmpty();
   }

   public MultifaceSpreader getSpreader() {
      return this.spreader;
   }
}
