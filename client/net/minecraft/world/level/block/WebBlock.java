package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WebBlock extends Block {
   public static final MapCodec<WebBlock> CODEC = simpleCodec(WebBlock::new);

   @Override
   public MapCodec<WebBlock> codec() {
      return CODEC;
   }

   public WebBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      var4.makeStuckInBlock(var1, new Vec3(0.25, 0.05000000074505806, 0.25));
   }
}