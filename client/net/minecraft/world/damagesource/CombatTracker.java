package net.minecraft.world.damagesource;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

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

   public CombatTracker(LivingEntity var1) {
      super();
      this.mob = var1;
   }

   public void recordDamage(DamageSource var1, float var2) {
      this.recheckStatus();
      FallLocation var3 = FallLocation.getCurrentFallLocation(this.mob);
      CombatEntry var4 = new CombatEntry(var1, var2, var3, this.mob.fallDistance);
      this.entries.add(var4);
      this.lastDamageTime = this.mob.tickCount;
      this.takingDamage = true;
      if (!this.inCombat && this.mob.isAlive() && shouldEnterCombat(var1)) {
         this.inCombat = true;
         this.combatStartTime = this.mob.tickCount;
         this.combatEndTime = this.combatStartTime;
         this.mob.onEnterCombat();
      }
   }

   private static boolean shouldEnterCombat(DamageSource var0) {
      return var0.getEntity() instanceof LivingEntity;
   }

   private Component getMessageForAssistedFall(Entity var1, Component var2, String var3, String var4) {
      ItemStack var5 = var1 instanceof LivingEntity var6 ? var6.getMainHandItem() : ItemStack.EMPTY;
      return !var5.isEmpty() && var5.hasCustomHoverName()
         ? Component.translatable(var3, this.mob.getDisplayName(), var2, var5.getDisplayName())
         : Component.translatable(var4, this.mob.getDisplayName(), var2);
   }

   private Component getFallMessage(CombatEntry var1, @Nullable Entity var2) {
      DamageSource var3 = var1.source();
      if (!var3.is(DamageTypeTags.IS_FALL) && !var3.is(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL)) {
         Component var7 = getDisplayName(var2);
         Entity var5 = var3.getEntity();
         Component var6 = getDisplayName(var5);
         if (var6 != null && !var6.equals(var7)) {
            return this.getMessageForAssistedFall(var5, var6, "death.fell.assist.item", "death.fell.assist");
         } else {
            return (Component)(var7 != null
               ? this.getMessageForAssistedFall(var2, var7, "death.fell.finish.item", "death.fell.finish")
               : Component.translatable("death.fell.killer", this.mob.getDisplayName()));
         }
      } else {
         FallLocation var4 = Objects.requireNonNullElse(var1.fallLocation(), FallLocation.GENERIC);
         return Component.translatable(var4.languageKey(), this.mob.getDisplayName());
      }
   }

   @Nullable
   private static Component getDisplayName(@Nullable Entity var0) {
      return var0 == null ? null : var0.getDisplayName();
   }

   public Component getDeathMessage() {
      if (this.entries.isEmpty()) {
         return Component.translatable("death.attack.generic", this.mob.getDisplayName());
      } else {
         CombatEntry var1 = this.entries.get(this.entries.size() - 1);
         DamageSource var2 = var1.source();
         CombatEntry var3 = this.getMostSignificantFall();
         DeathMessageType var4 = var2.type().deathMessageType();
         if (var4 == DeathMessageType.FALL_VARIANTS && var3 != null) {
            return this.getFallMessage(var3, var2.getEntity());
         } else if (var4 == DeathMessageType.INTENTIONAL_GAME_DESIGN) {
            String var5 = "death.attack." + var2.getMsgId();
            MutableComponent var6 = ComponentUtils.wrapInSquareBrackets(Component.translatable(var5 + ".link")).withStyle(INTENTIONAL_GAME_DESIGN_STYLE);
            return Component.translatable(var5 + ".message", this.mob.getDisplayName(), var6);
         } else {
            return var2.getLocalizedDeathMessage(this.mob);
         }
      }
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
         DamageSource var8 = var6.source();
         boolean var9 = var8.is(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL);
         float var10 = var9 ? 3.4028235E38F : var6.fallDistance();
         if ((var8.is(DamageTypeTags.IS_FALL) || var9) && var10 > 0.0F && (var1 == null || var10 > var4)) {
            if (var5 > 0) {
               var1 = var7;
            } else {
               var1 = var6;
            }

            var4 = var10;
         }

         if (var6.fallLocation() != null && (var2 == null || var6.damage() > var3)) {
            var2 = var6;
            var3 = var6.damage();
         }
      }

      if (var4 > 5.0F && var1 != null) {
         return var1;
      } else {
         return var3 > 5.0F && var2 != null ? var2 : null;
      }
   }

   public int getCombatDuration() {
      return this.inCombat ? this.mob.tickCount - this.combatStartTime : this.combatEndTime - this.combatStartTime;
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
}
