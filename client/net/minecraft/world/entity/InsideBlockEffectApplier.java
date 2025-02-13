package net.minecraft.world.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.Util;

public interface InsideBlockEffectApplier {
   InsideBlockEffectApplier NOOP = new InsideBlockEffectApplier() {
      public void apply(InsideBlockEffectType var1) {
      }

      public void runBefore(InsideBlockEffectType var1, Consumer<Entity> var2) {
      }

      public void runAfter(InsideBlockEffectType var1, Consumer<Entity> var2) {
      }
   };

   void apply(InsideBlockEffectType var1);

   void runBefore(InsideBlockEffectType var1, Consumer<Entity> var2);

   void runAfter(InsideBlockEffectType var1, Consumer<Entity> var2);

   public static class StepBasedCollector implements InsideBlockEffectApplier {
      private static final InsideBlockEffectType[] APPLY_ORDER = InsideBlockEffectType.values();
      private static final int NO_STEP = -1;
      private final Set<InsideBlockEffectType> effectsInStep = EnumSet.noneOf(InsideBlockEffectType.class);
      private final Map<InsideBlockEffectType, List<Consumer<Entity>>> beforeEffectsInStep = Util.<InsideBlockEffectType, List<Consumer<Entity>>>makeEnumMap(InsideBlockEffectType.class, (var0) -> new ArrayList());
      private final Map<InsideBlockEffectType, List<Consumer<Entity>>> afterEffectsInStep = Util.<InsideBlockEffectType, List<Consumer<Entity>>>makeEnumMap(InsideBlockEffectType.class, (var0) -> new ArrayList());
      private final List<Consumer<Entity>> finalEffects = new ArrayList();
      private int lastStep = -1;

      public StepBasedCollector() {
         super();
      }

      public void advanceStep(int var1) {
         if (this.lastStep != var1) {
            this.lastStep = var1;
            this.flushStep();
         }

      }

      public void applyAndClear(Entity var1) {
         this.flushStep();

         for(Consumer var3 : this.finalEffects) {
            if (!var1.isAlive()) {
               break;
            }

            var3.accept(var1);
         }

         this.finalEffects.clear();
         this.lastStep = -1;
      }

      private void flushStep() {
         for(InsideBlockEffectType var4 : APPLY_ORDER) {
            List var5 = (List)this.beforeEffectsInStep.get(var4);
            this.finalEffects.addAll(var5);
            var5.clear();
            if (this.effectsInStep.remove(var4)) {
               this.finalEffects.add(var4.effect());
            }

            List var6 = (List)this.afterEffectsInStep.get(var4);
            this.finalEffects.addAll(var6);
            var6.clear();
         }

      }

      public void apply(InsideBlockEffectType var1) {
         this.effectsInStep.add(var1);
      }

      public void runBefore(InsideBlockEffectType var1, Consumer<Entity> var2) {
         ((List)this.beforeEffectsInStep.get(var1)).add(var2);
      }

      public void runAfter(InsideBlockEffectType var1, Consumer<Entity> var2) {
         ((List)this.afterEffectsInStep.get(var1)).add(var2);
      }
   }
}
