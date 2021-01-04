package net.minecraft.world.damagesource;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CombatTracker {
   private final List<CombatEntry> entries = Lists.newArrayList();
   private final LivingEntity mob;
   private int lastDamageTime;
   private int combatStartTime;
   private int combatEndTime;
   private boolean inCombat;
   private boolean takingDamage;
   private String nextLocation;

   public CombatTracker(LivingEntity var1) {
      super();
      this.mob = var1;
   }

   public void prepareForDamage() {
      this.resetPreparedStatus();
      if (this.mob.onLadder()) {
         Block var1 = this.mob.level.getBlockState(new BlockPos(this.mob.x, this.mob.getBoundingBox().minY, this.mob.z)).getBlock();
         if (var1 == Blocks.LADDER) {
            this.nextLocation = "ladder";
         } else if (var1 == Blocks.VINE) {
            this.nextLocation = "vines";
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
         return new TranslatableComponent("death.attack.generic", new Object[]{this.mob.getDisplayName()});
      } else {
         CombatEntry var1 = this.getMostSignificantFall();
         CombatEntry var2 = (CombatEntry)this.entries.get(this.entries.size() - 1);
         Component var4 = var2.getAttackerName();
         Entity var5 = var2.getSource().getEntity();
         Object var3;
         if (var1 != null && var2.getSource() == DamageSource.FALL) {
            Component var6 = var1.getAttackerName();
            if (var1.getSource() != DamageSource.FALL && var1.getSource() != DamageSource.OUT_OF_WORLD) {
               if (var6 == null || var4 != null && var6.equals(var4)) {
                  if (var4 != null) {
                     ItemStack var9 = var5 instanceof LivingEntity ? ((LivingEntity)var5).getMainHandItem() : ItemStack.EMPTY;
                     if (!var9.isEmpty() && var9.hasCustomHoverName()) {
                        var3 = new TranslatableComponent("death.fell.finish.item", new Object[]{this.mob.getDisplayName(), var4, var9.getDisplayName()});
                     } else {
                        var3 = new TranslatableComponent("death.fell.finish", new Object[]{this.mob.getDisplayName(), var4});
                     }
                  } else {
                     var3 = new TranslatableComponent("death.fell.killer", new Object[]{this.mob.getDisplayName()});
                  }
               } else {
                  Entity var7 = var1.getSource().getEntity();
                  ItemStack var8 = var7 instanceof LivingEntity ? ((LivingEntity)var7).getMainHandItem() : ItemStack.EMPTY;
                  if (!var8.isEmpty() && var8.hasCustomHoverName()) {
                     var3 = new TranslatableComponent("death.fell.assist.item", new Object[]{this.mob.getDisplayName(), var6, var8.getDisplayName()});
                  } else {
                     var3 = new TranslatableComponent("death.fell.assist", new Object[]{this.mob.getDisplayName(), var6});
                  }
               }
            } else {
               var3 = new TranslatableComponent("death.fell.accident." + this.getFallLocation(var1), new Object[]{this.mob.getDisplayName()});
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
      Iterator var5 = this.entries.iterator();

      while(true) {
         CombatEntry var6;
         do {
            do {
               if (!var5.hasNext()) {
                  if (var2 != null && var4 >= var3 / 3.0F) {
                     return var2;
                  }

                  return var1;
               }

               var6 = (CombatEntry)var5.next();
               if (var6.getSource().getEntity() instanceof Player && (var2 == null || var6.getDamage() > var4)) {
                  var4 = var6.getDamage();
                  var2 = (Player)var6.getSource().getEntity();
               }
            } while(!(var6.getSource().getEntity() instanceof LivingEntity));
         } while(var1 != null && var6.getDamage() <= var3);

         var3 = var6.getDamage();
         var1 = (LivingEntity)var6.getSource().getEntity();
      }
   }

   @Nullable
   private CombatEntry getMostSignificantFall() {
      CombatEntry var1 = null;
      CombatEntry var2 = null;
      float var3 = 0.0F;
      float var4 = 0.0F;

      for(int var5 = 0; var5 < this.entries.size(); ++var5) {
         CombatEntry var6 = (CombatEntry)this.entries.get(var5);
         CombatEntry var7 = var5 > 0 ? (CombatEntry)this.entries.get(var5 - 1) : null;
         if ((var6.getSource() == DamageSource.FALL || var6.getSource() == DamageSource.OUT_OF_WORLD) && var6.getFallDistance() > 0.0F && (var1 == null || var6.getFallDistance() > var4)) {
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
      } else if (var3 > 5.0F && var2 != null) {
         return var2;
      } else {
         return null;
      }
   }

   private String getFallLocation(CombatEntry var1) {
      return var1.getLocation() == null ? "generic" : var1.getLocation();
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
}
