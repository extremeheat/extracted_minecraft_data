package net.minecraft.world.damagesource;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CombatTracker {
   public static final int RESET_DAMAGE_STATUS_TIME = 100;
   public static final int RESET_COMBAT_STATUS_TIME = 300;
   private final List<CombatEntry> entries = Lists.newArrayList();
   private final LivingEntity mob;
   private int lastDamageTime;
   private int combatStartTime;
   private int combatEndTime;
   private boolean inCombat;
   private boolean takingDamage;
   @Nullable
   private String nextLocation;

   public CombatTracker(LivingEntity var1) {
      super();
      this.mob = var1;
   }

   public void prepareForDamage() {
      this.resetPreparedStatus();
      Optional var1 = this.mob.getLastClimbablePos();
      if (var1.isPresent()) {
         BlockState var2 = this.mob.level.getBlockState((BlockPos)var1.get());
         if (var2.is(Blocks.LADDER) || var2.is(BlockTags.TRAPDOORS)) {
            this.nextLocation = "ladder";
         } else if (var2.is(Blocks.VINE)) {
            this.nextLocation = "vines";
         } else if (var2.is(Blocks.WEEPING_VINES) || var2.is(Blocks.WEEPING_VINES_PLANT)) {
            this.nextLocation = "weeping_vines";
         } else if (var2.is(Blocks.TWISTING_VINES) || var2.is(Blocks.TWISTING_VINES_PLANT)) {
            this.nextLocation = "twisting_vines";
         } else if (var2.is(Blocks.SCAFFOLDING)) {
            this.nextLocation = "scaffolding";
         } else {
            this.nextLocation = "other_climbable";
         }
      } else if (this.mob.isInWater()) {
         this.nextLocation = "water";
      }
   }

   public void recordDamage(DamageSource var1, float var2, float var3) {
      this.recheckStatus();
      this.prepareForDamage();
      CombatEntry var4 = new CombatEntry(var1, this.mob.tickCount, var2, var3, this.nextLocation, this.mob.fallDistance);
      this.entries.add(var4);
      this.lastDamageTime = this.mob.tickCount;
      this.takingDamage = true;
      if (var4.isCombatRelated() && !this.inCombat && this.mob.isAlive()) {
         this.inCombat = true;
         this.combatStartTime = this.mob.tickCount;
         this.combatEndTime = this.combatStartTime;
         this.mob.onEnterCombat();
      }
   }

   public Component getDeathMessage() {
      if (this.entries.isEmpty()) {
         return Component.translatable("death.attack.generic", this.mob.getDisplayName());
      } else {
         CombatEntry var1 = this.getMostSignificantFall();
         CombatEntry var2 = this.entries.get(this.entries.size() - 1);
         Component var4 = var2.getAttackerName();
         Entity var5 = var2.getSource().getEntity();
         Object var3;
         if (var1 != null && var2.getSource() == DamageSource.FALL) {
            Component var6 = var1.getAttackerName();
            if (var1.getSource() == DamageSource.FALL || var1.getSource() == DamageSource.OUT_OF_WORLD) {
               var3 = Component.translatable("death.fell.accident." + this.getFallLocation(var1), this.mob.getDisplayName());
            } else if (var6 != null && !var6.equals(var4)) {
               Entity var9 = var1.getSource().getEntity();
               ItemStack var8 = var9 instanceof LivingEntity ? ((LivingEntity)var9).getMainHandItem() : ItemStack.EMPTY;
               if (!var8.isEmpty() && var8.hasCustomHoverName()) {
                  var3 = Component.translatable("death.fell.assist.item", this.mob.getDisplayName(), var6, var8.getDisplayName());
               } else {
                  var3 = Component.translatable("death.fell.assist", this.mob.getDisplayName(), var6);
               }
            } else if (var4 != null) {
               ItemStack var7 = var5 instanceof LivingEntity ? ((LivingEntity)var5).getMainHandItem() : ItemStack.EMPTY;
               if (!var7.isEmpty() && var7.hasCustomHoverName()) {
                  var3 = Component.translatable("death.fell.finish.item", this.mob.getDisplayName(), var4, var7.getDisplayName());
               } else {
                  var3 = Component.translatable("death.fell.finish", this.mob.getDisplayName(), var4);
               }
            } else {
               var3 = Component.translatable("death.fell.killer", this.mob.getDisplayName());
            }
         } else {
            var3 = var2.getSource().getLocalizedDeathMessage(this.mob);
         }

         return (Component)var3;
      }
   }

   @Nullable
   public LivingEntity getKiller() {
      LivingEntity var1 = null;
      Player var2 = null;
      float var3 = 0.0F;
      float var4 = 0.0F;

      for(CombatEntry var6 : this.entries) {
         if (var6.getSource().getEntity() instanceof Player && (var2 == null || var6.getDamage() > var4)) {
            var4 = var6.getDamage();
            var2 = (Player)var6.getSource().getEntity();
         }

         if (var6.getSource().getEntity() instanceof LivingEntity && (var1 == null || var6.getDamage() > var3)) {
            var3 = var6.getDamage();
            var1 = (LivingEntity)var6.getSource().getEntity();
         }
      }

      return (LivingEntity)(var2 != null && var4 >= var3 / 3.0F ? var2 : var1);
   }

   @Nullable
   private CombatEntry getMostSignificantFall() {
      CombatEntry var1 = null;
      CombatEntry var2 = null;
      float var3 = 0.0F;
      float var4 = 0.0F;

      for(int var5 = 0; var5 < this.entries.size(); ++var5) {
         CombatEntry var6 = this.entries.get(var5);
         CombatEntry var7 = var5 > 0 ? this.entries.get(var5 - 1) : null;
         if ((var6.getSource() == DamageSource.FALL || var6.getSource() == DamageSource.OUT_OF_WORLD)
            && var6.getFallDistance() > 0.0F
            && (var1 == null || var6.getFallDistance() > var4)) {
            if (var5 > 0) {
               var1 = var7;
            } else {
               var1 = var6;
            }

            var4 = var6.getFallDistance();
         }

         if (var6.getLocation() != null && (var2 == null || var6.getDamage() > var3)) {
            var2 = var6;
            var3 = var6.getDamage();
         }
      }

      if (var4 > 5.0F && var1 != null) {
         return var1;
      } else {
         return var3 > 5.0F && var2 != null ? var2 : null;
      }
   }

   private String getFallLocation(CombatEntry var1) {
      return var1.getLocation() == null ? "generic" : var1.getLocation();
   }

   public boolean isTakingDamage() {
      this.recheckStatus();
      return this.takingDamage;
   }

   public boolean isInCombat() {
      this.recheckStatus();
      return this.inCombat;
   }

   public int getCombatDuration() {
      return this.inCombat ? this.mob.tickCount - this.combatStartTime : this.combatEndTime - this.combatStartTime;
   }

   private void resetPreparedStatus() {
      this.nextLocation = null;
   }

   public void recheckStatus() {
      int var1 = this.inCombat ? 300 : 100;
      if (this.takingDamage && (!this.mob.isAlive() || this.mob.tickCount - this.lastDamageTime > var1)) {
         boolean var2 = this.inCombat;
         this.takingDamage = false;
         this.inCombat = false;
         this.combatEndTime = this.mob.tickCount;
         if (var2) {
            this.mob.onLeaveCombat();
         }

         this.entries.clear();
      }
   }

   public LivingEntity getMob() {
      return this.mob;
   }

   @Nullable
   public CombatEntry getLastEntry() {
      return this.entries.isEmpty() ? null : this.entries.get(this.entries.size() - 1);
   }

   public int getKillerId() {
      LivingEntity var1 = this.getKiller();
      return var1 == null ? -1 : var1.getId();
   }
}
