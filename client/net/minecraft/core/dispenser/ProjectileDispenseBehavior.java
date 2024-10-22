package net.minecraft.core.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.block.DispenserBlock;

public class ProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
   private final ProjectileItem projectileItem;
   private final ProjectileItem.DispenseConfig dispenseConfig;

   public ProjectileDispenseBehavior(Item var1) {
      super();
      if (var1 instanceof ProjectileItem var2) {
         this.projectileItem = var2;
         this.dispenseConfig = var2.createDispenseConfig();
      } else {
         throw new IllegalArgumentException(var1 + " not instance of " + ProjectileItem.class.getSimpleName());
      }
   }

   @Override
   public ItemStack execute(BlockSource var1, ItemStack var2) {
      ServerLevel var3 = var1.level();
      Direction var4 = var1.state().getValue(DispenserBlock.FACING);
      Position var5 = this.dispenseConfig.positionFunction().getDispensePosition(var1, var4);
      Projectile.spawnProjectileUsingShoot(
         this.projectileItem.asProjectile(var3, var5, var2, var4),
         var3,
         var2,
         (double)var4.getStepX(),
         (double)var4.getStepY(),
         (double)var4.getStepZ(),
         this.dispenseConfig.power(),
         this.dispenseConfig.uncertainty()
      );
      var2.shrink(1);
      return var2;
   }

   @Override
   protected void playSound(BlockSource var1) {
      var1.level().levelEvent(this.dispenseConfig.overrideDispenseEvent().orElse(1002), var1.pos(), 0);
   }
}
