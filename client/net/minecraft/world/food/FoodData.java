package net.minecraft.world.food;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

public class FoodData {
   private int foodLevel = 20;
   private float saturationLevel = 5.0F;
   private float exhaustionLevel;
   private int tickTimer;
   private int lastFoodLevel = 20;

   public FoodData() {
      super();
   }

   private void add(int var1, float var2) {
      this.foodLevel = Mth.clamp(var1 + this.foodLevel, 0, 20);
      this.saturationLevel = Mth.clamp(var2 + this.saturationLevel, 0.0F, (float)this.foodLevel);
   }

   public void eat(int var1, float var2) {
      this.add(var1, FoodConstants.saturationByModifier(var1, var2));
   }

   public void eat(FoodProperties var1) {
      this.add(var1.nutrition(), var1.saturation());
   }

   public void tick(Player var1) {
      Difficulty var2 = var1.level().getDifficulty();
      this.lastFoodLevel = this.foodLevel;
      if (this.exhaustionLevel > 4.0F) {
         this.exhaustionLevel -= 4.0F;
         if (this.saturationLevel > 0.0F) {
            this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
         } else if (var2 != Difficulty.PEACEFUL) {
            this.foodLevel = Math.max(this.foodLevel - 1, 0);
         }
      }

      boolean var3 = var1.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
      if (var3 && this.saturationLevel > 0.0F && var1.isHurt() && this.foodLevel >= 20) {
         ++this.tickTimer;
         if (this.tickTimer >= 10) {
            float var4 = Math.min(this.saturationLevel, 6.0F);
            var1.heal(var4 / 6.0F);
            this.addExhaustion(var4);
            this.tickTimer = 0;
         }
      } else if (var3 && this.foodLevel >= 18 && var1.isHurt()) {
         ++this.tickTimer;
         if (this.tickTimer >= 80) {
            var1.heal(1.0F);
            this.addExhaustion(6.0F);
            this.tickTimer = 0;
         }
      } else if (this.foodLevel <= 0) {
         ++this.tickTimer;
         if (this.tickTimer >= 80) {
            if (var1.getHealth() > 10.0F || var2 == Difficulty.HARD || var1.getHealth() > 1.0F && var2 == Difficulty.NORMAL) {
               var1.hurt(var1.damageSources().starve(), 1.0F);
            }

            this.tickTimer = 0;
         }
      } else {
         this.tickTimer = 0;
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      if (var1.contains("foodLevel", 99)) {
         this.foodLevel = var1.getInt("foodLevel");
         this.tickTimer = var1.getInt("foodTickTimer");
         this.saturationLevel = var1.getFloat("foodSaturationLevel");
         this.exhaustionLevel = var1.getFloat("foodExhaustionLevel");
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      var1.putInt("foodLevel", this.foodLevel);
      var1.putInt("foodTickTimer", this.tickTimer);
      var1.putFloat("foodSaturationLevel", this.saturationLevel);
      var1.putFloat("foodExhaustionLevel", this.exhaustionLevel);
   }

   public int getFoodLevel() {
      return this.foodLevel;
   }

   public int getLastFoodLevel() {
      return this.lastFoodLevel;
   }

   public boolean needsFood() {
      return this.foodLevel < 20;
   }

   public void addExhaustion(float var1) {
      this.exhaustionLevel = Math.min(this.exhaustionLevel + var1, 40.0F);
   }

   public float getExhaustionLevel() {
      return this.exhaustionLevel;
   }

   public float getSaturationLevel() {
      return this.saturationLevel;
   }

   public void setFoodLevel(int var1) {
      this.foodLevel = var1;
   }

   public void setSaturation(float var1) {
      this.saturationLevel = var1;
   }

   public void setExhaustion(float var1) {
      this.exhaustionLevel = var1;
   }
}
