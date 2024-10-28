package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WebBlock extends Block {
   public static final MapCodec<WebBlock> CODEC = simpleCodec(WebBlock::new);

   public MapCodec<WebBlock> codec() {
      return CODEC;
   }

   public WebBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      Vec3 var5 = new Vec3(0.25, 0.05000000074505806, 0.25);
      if (var4 instanceof LivingEntity var6) {
         if (var6.hasEffect(MobEffects.WEAVING)) {
            var5 = new Vec3(0.5, 0.25, 0.5);
         }
      }

      var4.makeStuckInBlock(var1, var5);
   }
}
