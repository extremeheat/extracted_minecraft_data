package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface DispenseSource {
   ServerLevel level();

   BlockPos pos();

   Direction direction();

   ItemStack insertItem(final ItemStack stack);

   default Vec3 center() {
      return this.pos().getCenter();
   }
}
