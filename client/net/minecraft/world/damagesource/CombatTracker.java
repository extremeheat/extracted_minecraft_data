package net.minecraft.world.damagesource;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CombatTracker {
   public static final int RESET_DAMAGE_STATUS_TIME = 100;
   public static final int RESET_COMBAT_STATUS_TIME = 300;
   private static final Style INTENTIONAL_GAME_DESIGN_STYLE = Style.EMPTY
      .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723"))
      .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("MCPE-28723")));
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
         DamageSource var5 = var2.getSource();
         Entity var6 = var5.getEntity();
         DeathMessageType var7 = var5.type().deathMessageType();
         Object var3;
         if (var1 != null && var7 == DeathMessageType.FALL_VARIANTS) {
            Component var13 = var1.getAttackerName();
            DamageSource var14 = var1.getSource();
            if (var14.is(DamageTypeTags.IS_FALL) || var14.is(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL)) {
               var3 = Component.translatable("death.fell.accident." + this.getFallLocation(var1), this.mob.getDisplayName());
            } else if (var13 != null && !var13.equals(var4)) {
               Entity var15 = var14.getEntity();
               ItemStack var16 = var15 instanceof LivingEntity var12 ? var12.getMainHandItem() : ItemStack.EMPTY;
               if (!var16.isEmpty() && var16.hasCustomHoverName()) {
                  var3 = Component.translatable("death.fell.assist.item", this.mob.getDisplayName(), var13, var16.getDisplayName());
               } else {
                  var3 = Component.translatable("death.fell.assist", this.mob.getDisplayName(), var13);
               }
            } else if (var4 != null) {
               ItemStack var10 = var6 instanceof LivingEntity var11 ? var11.getMainHandItem() : ItemStack.EMPTY;
               if (!var10.isEmpty() && var10.hasCustomHoverName()) {
                  var3 = Component.translatable("death.fell.finish.item", this.mob.getDisplayName(), var4, var10.getDisplayName());
               } else {
                  var3 = Component.translatable("death.fell.finish", this.mob.getDisplayName(), var4);
               }
            } else {
               var3 = Component.translatable("death.fell.killer", this.mob.getDisplayName());
            }
         } else {
            if (var7 == DeathMessageType.INTENTIONAL_GAME_DESIGN) {
               String var8 = "death.attack." + var5.getMsgId();
               MutableComponent var9 = ComponentUtils.wrapInSquareBrackets(Component.translatable(var8 + ".link")).withStyle(INTENTIONAL_GAME_DESIGN_STYLE);
               return Component.translatable(var8 + ".message", this.mob.getDisplayName(), var9);
            }

            var3 = var5.getLocalizedDeathMessage(this.mob);
         }

         return (Component)var3;
      }
   }

   @Nullable
   public LivingEntity getKiller() {
      Object var1 = null;
      Object var2 = null;
      float var3 = 0.0F;
      float var4 = 0.0F;

      for(CombatEntry var6 : this.entries) {
         Entity var8 = var6.getSource().getEntity();
         if (var8 instanceof Player var7 && (var2 == null || var6.getDamage() > var4)) {
            var4 = var6.getDamage();
            var2 = var7;
         }

         var8 = var6.getSource().getEntity();
         if (var8 instanceof LivingEntity var9 && (var1 == null || var6.getDamage() > var3)) {
            var3 = var6.getDamage();
            var1 = var9;
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
         DamageSource var8 = var6.getSource();
         boolean var9 = var8.is(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL);
         float var10 = var9 ? 3.4028235E38F : var6.getFallDistance();
         if ((var8.is(DamageTypeTags.IS_FALL) || var9) && var10 > 0.0F && (var1 == null || var10 > var4)) {
            if (var5 > 0) {
               var1 = var7;
            } else {
               var1 = var6;
            }

            var4 = var10;
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
