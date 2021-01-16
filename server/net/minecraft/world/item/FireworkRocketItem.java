package net.minecraft.world.item;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireworkRocketItem extends Item {
   public FireworkRocketItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      if (!var2.isClientSide) {
         ItemStack var3 = var1.getItemInHand();
         Vec3 var4 = var1.getClickLocation();
         Direction var5 = var1.getClickedFace();
         FireworkRocketEntity var6 = new FireworkRocketEntity(var2, var1.getPlayer(), var4.x + (double)var5.getStepX() * 0.15D, var4.y + (double)var5.getStepY() * 0.15D, var4.z + (double)var5.getStepZ() * 0.15D, var3);
         var2.addFreshEntity(var6);
         var3.shrink(1);
      }

      return InteractionResult.sidedSuccess(var2.isClientSide);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      if (var2.isFallFlying()) {
         ItemStack var4 = var2.getItemInHand(var3);
         if (!var1.isClientSide) {
            var1.addFreshEntity(new FireworkRocketEntity(var1, var4, var2));
            if (!var2.abilities.instabuild) {
               var4.shrink(1);
            }
         }

         return InteractionResultHolder.sidedSuccess(var2.getItemInHand(var3), var1.isClientSide());
      } else {
         return InteractionResultHolder.pass(var2.getItemInHand(var3));
      }
   }

   public static enum Shape {
      SMALL_BALL(0, "small_ball"),
      LARGE_BALL(1, "large_ball"),
      STAR(2, "star"),
      CREEPER(3, "creeper"),
      BURST(4, "burst");

      private static final FireworkRocketItem.Shape[] BY_ID = (FireworkRocketItem.Shape[])Arrays.stream(values()).sorted(Comparator.comparingInt((var0) -> {
         return var0.id;
      })).toArray((var0) -> {
         return new FireworkRocketItem.Shape[var0];
      });
      private final int id;
      private final String name;

      private Shape(int var3, String var4) {
         this.id = var3;
         this.name = var4;
      }

      public int getId() {
         return this.id;
      }
   }
}
