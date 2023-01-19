package net.minecraft.world.level.block;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface Fallable {
   default void onLand(Level var1, BlockPos var2, BlockState var3, BlockState var4, FallingBlockEntity var5) {
   }

   default void onBrokenAfterFall(Level var1, BlockPos var2, FallingBlockEntity var3) {
   }

   default DamageSource getFallDamageSource() {
      return DamageSource.FALLING_BLOCK;
   }

   default Predicate<Entity> getHurtsEntitySelector() {
      return EntitySelector.NO_SPECTATORS;
   }
}
