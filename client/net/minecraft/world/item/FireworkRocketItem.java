package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireworkRocketItem extends Item implements ProjectileItem {
   public static final byte[] CRAFTABLE_DURATIONS = new byte[]{1, 2, 3};
   public static final double ROCKET_PLACEMENT_OFFSET = 0.15;

   public FireworkRocketItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      Player var3 = var1.getPlayer();
      if (var3 != null && var3.isFallFlying()) {
         return InteractionResult.PASS;
      } else {
         if (var2 instanceof ServerLevel) {
            ServerLevel var4 = (ServerLevel)var2;
            ItemStack var5 = var1.getItemInHand();
            Vec3 var6 = var1.getClickLocation();
            Direction var7 = var1.getClickedFace();
            Projectile.spawnProjectile(new FireworkRocketEntity(var2, var1.getPlayer(), var6.x + (double)var7.getStepX() * 0.15, var6.y + (double)var7.getStepY() * 0.15, var6.z + (double)var7.getStepZ() * 0.15, var5), var4, var5);
            var5.shrink(1);
         }

         return InteractionResult.SUCCESS;
      }
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      if (var2.isFallFlying()) {
         ItemStack var4 = var2.getItemInHand(var3);
         if (var1 instanceof ServerLevel) {
            ServerLevel var5 = (ServerLevel)var1;
            if (var2.dropAllLeashConnections((Player)null)) {
               var1.playSound((Entity)null, (Entity)var2, SoundEvents.LEAD_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }

            Projectile.spawnProjectile(new FireworkRocketEntity(var1, var4, var2), var5, var4);
            var4.consume(1, var2);
            var2.awardStat(Stats.ITEM_USED.get(this));
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4) {
      return new FireworkRocketEntity(var1, var3.copyWithCount(1), var2.x(), var2.y(), var2.z(), true);
   }

   public ProjectileItem.DispenseConfig createDispenseConfig() {
      return ProjectileItem.DispenseConfig.builder().positionFunction(FireworkRocketItem::getEntityJustOutsideOfBlockPos).uncertainty(1.0F).power(0.5F).overrideDispenseEvent(1004).build();
   }

   private static Vec3 getEntityJustOutsideOfBlockPos(BlockSource var0, Direction var1) {
      return var0.center().add((double)var1.getStepX() * 0.5000099999997474, (double)var1.getStepY() * 0.5000099999997474, (double)var1.getStepZ() * 0.5000099999997474);
   }
}
