package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireworkRocketItem extends Item implements ProjectileItem {
   public static final byte[] CRAFTABLE_DURATIONS = new byte[]{1, 2, 3};
   public static final double ROCKET_PLACEMENT_OFFSET = 0.15;

   public FireworkRocketItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      if (var2 instanceof ServerLevel var3) {
         ItemStack var4 = var1.getItemInHand();
         Vec3 var5 = var1.getClickLocation();
         Direction var6 = var1.getClickedFace();
         Projectile.spawnProjectile(
            new FireworkRocketEntity(
               var2,
               var1.getPlayer(),
               var5.x + (double)var6.getStepX() * 0.15,
               var5.y + (double)var6.getStepY() * 0.15,
               var5.z + (double)var6.getStepZ() * 0.15,
               var4
            ),
            var3,
            var4
         );
         var4.shrink(1);
      }

      return InteractionResult.SUCCESS;
   }

   @Override
   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      if (var2.isFallFlying()) {
         ItemStack var4 = var2.getItemInHand(var3);
         if (var1 instanceof ServerLevel var5) {
            Projectile.spawnProjectile(new FireworkRocketEntity(var1, var4, var2), var5, var4);
            var4.consume(1, var2);
            var2.awardStat(Stats.ITEM_USED.get(this));
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      Fireworks var5 = var1.get(DataComponents.FIREWORKS);
      if (var5 != null) {
         var5.addToTooltip(var2, var3::add, var4);
      }
   }

   @Override
   public Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4) {
      return new FireworkRocketEntity(var1, var3.copyWithCount(1), var2.x(), var2.y(), var2.z(), true);
   }

   @Override
   public ProjectileItem.DispenseConfig createDispenseConfig() {
      return ProjectileItem.DispenseConfig.builder()
         .positionFunction(FireworkRocketItem::getEntityJustOutsideOfBlockPos)
         .uncertainty(1.0F)
         .power(0.5F)
         .overrideDispenseEvent(1004)
         .build();
   }

   private static Vec3 getEntityJustOutsideOfBlockPos(BlockSource var0, Direction var1) {
      return var0.center()
         .add((double)var1.getStepX() * 0.5000099999997474, (double)var1.getStepY() * 0.5000099999997474, (double)var1.getStepZ() * 0.5000099999997474);
   }
}
