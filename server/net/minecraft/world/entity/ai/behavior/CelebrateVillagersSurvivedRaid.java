package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CelebrateVillagersSurvivedRaid extends Behavior<Villager> {
   @Nullable
   private Raid currentRaid;

   public CelebrateVillagersSurvivedRaid(int var1, int var2) {
      super(ImmutableMap.of(), var1, var2);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      BlockPos var3 = var2.blockPosition();
      this.currentRaid = var1.getRaidAt(var3);
      return this.currentRaid != null && this.currentRaid.isVictory() && MoveToSkySeeingSpot.hasNoBlocksAbove(var1, var2, var3);
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return this.currentRaid != null && !this.currentRaid.isStopped();
   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      this.currentRaid = null;
      var2.getBrain().updateActivityFromSchedule(var1.getDayTime(), var1.getGameTime());
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      Random var5 = var2.getRandom();
      if (var5.nextInt(100) == 0) {
         var2.playCelebrateSound();
      }

      if (var5.nextInt(200) == 0 && MoveToSkySeeingSpot.hasNoBlocksAbove(var1, var2, var2.blockPosition())) {
         DyeColor var6 = (DyeColor)Util.getRandom((Object[])DyeColor.values(), var5);
         int var7 = var5.nextInt(3);
         ItemStack var8 = this.getFirework(var6, var7);
         FireworkRocketEntity var9 = new FireworkRocketEntity(var2.level, var2, var2.getX(), var2.getEyeY(), var2.getZ(), var8);
         var2.level.addFreshEntity(var9);
      }

   }

   private ItemStack getFirework(DyeColor var1, int var2) {
      ItemStack var3 = new ItemStack(Items.FIREWORK_ROCKET, 1);
      ItemStack var4 = new ItemStack(Items.FIREWORK_STAR);
      CompoundTag var5 = var4.getOrCreateTagElement("Explosion");
      ArrayList var6 = Lists.newArrayList();
      var6.add(var1.getFireworkColor());
      var5.putIntArray("Colors", (List)var6);
      var5.putByte("Type", (byte)FireworkRocketItem.Shape.BURST.getId());
      CompoundTag var7 = var3.getOrCreateTagElement("Fireworks");
      ListTag var8 = new ListTag();
      CompoundTag var9 = var4.getTagElement("Explosion");
      if (var9 != null) {
         var8.add(var9);
      }

      var7.putByte("Flight", (byte)var2);
      if (!var8.isEmpty()) {
         var7.put("Explosions", var8);
      }

      return var3;
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Villager)var2, var3);
   }
}
