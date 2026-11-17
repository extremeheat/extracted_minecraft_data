package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class BrushArmadilloDispenseBehavior implements DispenseBehavior {
   public static final BrushArmadilloDispenseBehavior INSTANCE = new BrushArmadilloDispenseBehavior();
   public static final MapCodec<BrushArmadilloDispenseBehavior> MAP_CODEC;

   private BrushArmadilloDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      ServerLevel level = source.level();
      BlockPos pos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));

      for(Armadillo armadillo : level.getEntitiesOfClass(Armadillo.class, new AABB(pos), EntitySelector.NO_SPECTATORS)) {
         if (armadillo.brushOffScute((Entity)null, dispensed)) {
            dispensed.hurtAndBreak(16, level, (ServerPlayer)null, (item) -> {
            });
            return DispenseBehavior.DispenseResult.success(dispensed);
         }
      }

      return DispenseBehavior.DispenseResult.failure();
   }

   public MapCodec<BrushArmadilloDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
