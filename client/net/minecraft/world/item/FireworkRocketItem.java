package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
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
         FireworkRocketEntity var5 = new FireworkRocketEntity(var2, var4.x, var4.y, var4.z, var3);
         var2.addFreshEntity(var5);
         var3.shrink(1);
      }

      return InteractionResult.SUCCESS;
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

         return new InteractionResultHolder(InteractionResult.SUCCESS, var2.getItemInHand(var3));
      } else {
         return new InteractionResultHolder(InteractionResult.PASS, var2.getItemInHand(var3));
      }
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      CompoundTag var5 = var1.getTagElement("Fireworks");
      if (var5 != null) {
         if (var5.contains("Flight", 99)) {
            var3.add((new TranslatableComponent("item.minecraft.firework_rocket.flight", new Object[0])).append(" ").append(String.valueOf(var5.getByte("Flight"))).withStyle(ChatFormatting.GRAY));
         }

         ListTag var6 = var5.getList("Explosions", 10);
         if (!var6.isEmpty()) {
            for(int var7 = 0; var7 < var6.size(); ++var7) {
               CompoundTag var8 = var6.getCompound(var7);
               ArrayList var9 = Lists.newArrayList();
               FireworkStarItem.appendHoverText(var8, var9);
               if (!var9.isEmpty()) {
                  for(int var10 = 1; var10 < var9.size(); ++var10) {
                     var9.set(var10, (new TextComponent("  ")).append((Component)var9.get(var10)).withStyle(ChatFormatting.GRAY));
                  }

                  var3.addAll(var9);
               }
            }
         }

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

      public String getName() {
         return this.name;
      }

      public static FireworkRocketItem.Shape byId(int var0) {
         return var0 >= 0 && var0 < BY_ID.length ? BY_ID[var0] : SMALL_BALL;
      }
   }
}
