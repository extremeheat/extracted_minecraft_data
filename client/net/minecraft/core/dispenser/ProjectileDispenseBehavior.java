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

   public ProjectileDispenseBehavior(final Item item) {
      super();
      if (item instanceof ProjectileItem projectileItem) {
         this.projectileItem = projectileItem;
         this.dispenseConfig = projectileItem.createDispenseConfig();
      } else {
         String var10002 = String.valueOf(item);
         throw new IllegalArgumentException(var10002 + " not instance of " + ProjectileItem.class.getSimpleName());
      }
   }

   public ItemStack execute(final BlockSource source, final ItemStack dispensed) {
      ServerLevel level = source.level();
      Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
      Position position = this.dispenseConfig.positionFunction().getDispensePosition(source, direction);
      Projectile.spawnProjectileUsingShoot(this.projectileItem.asProjectile(level, position, dispensed, direction), level, dispensed, (double)direction.getStepX(), (double)direction.getStepY(), (double)direction.getStepZ(), this.dispenseConfig.power(), this.dispenseConfig.uncertainty());
      dispensed.shrink(1);
      return dispensed;
   }

   protected void playSound(final BlockSource source) {
      source.level().levelEvent(this.dispenseConfig.overrideDispenseEvent().orElse(1002), source.pos(), 0);
   }
}
