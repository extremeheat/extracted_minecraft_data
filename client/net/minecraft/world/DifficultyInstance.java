package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.Mth;

@Immutable
public class DifficultyInstance {
   private static final float DIFFICULTY_TIME_GLOBAL_OFFSET = -72000.0F;
   private static final float MAX_DIFFICULTY_TIME_GLOBAL = 1440000.0F;
   private static final float MAX_DIFFICULTY_TIME_LOCAL = 3600000.0F;
   private final Difficulty base;
   private final float effectiveDifficulty;

   public DifficultyInstance(Difficulty var1, long var2, long var4, float var6) {
      super();
      this.base = var1;
      this.effectiveDifficulty = this.calculateDifficulty(var1, var2, var4, var6);
   }

   public Difficulty getDifficulty() {
      return this.base;
   }

   public float getEffectiveDifficulty() {
      return this.effectiveDifficulty;
   }

   public boolean isHard() {
      return this.effectiveDifficulty >= (float)Difficulty.HARD.ordinal();
   }

   public boolean isHarderThan(float var1) {
      return this.effectiveDifficulty > var1;
   }

   public float getSpecialMultiplier() {
      if (this.effectiveDifficulty < 2.0F) {
         return 0.0F;
      } else {
         return this.effectiveDifficulty > 4.0F ? 1.0F : (this.effectiveDifficulty - 2.0F) / 2.0F;
      }
   }

   private float calculateDifficulty(Difficulty var1, long var2, long var4, float var6) {
      if (var1 == Difficulty.PEACEFUL) {
         return 0.0F;
      } else {
         boolean var7 = var1 == Difficulty.HARD;
         float var8 = 0.75F;
         float var9 = Mth.clamp(((float)var2 + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
         var8 += var9;
         float var10 = 0.0F;
         var10 += Mth.clamp((float)var4 / 3600000.0F, 0.0F, 1.0F) * (var7 ? 1.0F : 0.75F);
         var10 += Mth.clamp(var6 * 0.25F, 0.0F, var9);
         if (var1 == Difficulty.EASY) {
            var10 *= 0.5F;
         }

         var8 += var10;
         return (float)var1.getId() * var8;
      }
   }
}
