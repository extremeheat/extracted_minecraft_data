package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public abstract class AbstractProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
   public AbstractProjectileDispenseBehavior() {
      super();
   }

   public ItemStack execute(BlockSource var1, ItemStack var2) {
      Level var3 = var1.getLevel();
      Position var4 = DispenserBlock.getDispensePosition(var1);
      Direction var5 = (Direction)var1.getBlockState().getValue(DispenserBlock.FACING);
      Projectile var6 = this.getProjectile(var3, var4, var2);
      var6.shoot((double)var5.getStepX(), (double)((float)var5.getStepY() + 0.1F), (double)var5.getStepZ(), this.getPower(), this.getUncertainty());
      var3.addFreshEntity((Entity)var6);
      var2.shrink(1);
      return var2;
   }

   protected void playSound(BlockSource var1) {
      var1.getLevel().levelEvent(1002, var1.getPos(), 0);
   }

   protected abstract Projectile getProjectile(Level var1, Position var2, ItemStack var3);

   protected float getUncertainty() {
      return 6.0F;
   }

   protected float getPower() {
      return 1.1F;
   }
}
