package net.minecraft.world.food;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;

public class FoodProperties {
   private final int nutrition;
   private final float saturationModifier;
   private final boolean isMeat;
   private final boolean canAlwaysEat;
   private final boolean fastFood;
   private final List<Pair<MobEffectInstance, Float>> effects;

   private FoodProperties(int var1, float var2, boolean var3, boolean var4, boolean var5, List<Pair<MobEffectInstance, Float>> var6) {
      super();
      this.nutrition = var1;
      this.saturationModifier = var2;
      this.isMeat = var3;
      this.canAlwaysEat = var4;
      this.fastFood = var5;
      this.effects = var6;
   }

   public int getNutrition() {
      return this.nutrition;
   }

   public float getSaturationModifier() {
      return this.saturationModifier;
   }

   public boolean isMeat() {
      return this.isMeat;
   }

   public boolean canAlwaysEat() {
      return this.canAlwaysEat;
   }

   public boolean isFastFood() {
      return this.fastFood;
   }

   public List<Pair<MobEffectInstance, Float>> getEffects() {
      return this.effects;
   }

   // $FF: synthetic method
   FoodProperties(int var1, float var2, boolean var3, boolean var4, boolean var5, List var6, Object var7) {
      this(var1, var2, var3, var4, var5, var6);
   }

   public static class Builder {
      private int nutrition;
      private float saturationModifier;
      private boolean isMeat;
      private boolean canAlwaysEat;
      private boolean fastFood;
      private final List<Pair<MobEffectInstance, Float>> effects = Lists.newArrayList();

      public Builder() {
         super();
      }

      public FoodProperties.Builder nutrition(int var1) {
         this.nutrition = var1;
         return this;
      }

      public FoodProperties.Builder saturationMod(float var1) {
         this.saturationModifier = var1;
         return this;
      }

      public FoodProperties.Builder meat() {
         this.isMeat = true;
         return this;
      }

      public FoodProperties.Builder alwaysEat() {
         this.canAlwaysEat = true;
         return this;
      }

      public FoodProperties.Builder fast() {
         this.fastFood = true;
         return this;
      }

      public FoodProperties.Builder effect(MobEffectInstance var1, float var2) {
         this.effects.add(Pair.of(var1, var2));
         return this;
      }

      public FoodProperties build() {
         return new FoodProperties(this.nutrition, this.saturationModifier, this.isMeat, this.canAlwaysEat, this.fastFood, this.effects);
      }
   }
}
