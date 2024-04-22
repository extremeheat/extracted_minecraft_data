package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import net.minecraft.server.PlayerAdvancements;

public interface CriterionTrigger<T extends CriterionTriggerInstance> {
   void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<T> var2);

   void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<T> var2);

   void removePlayerListeners(PlayerAdvancements var1);

   Codec<T> codec();

   default Criterion<T> createCriterion(T var1) {
      return new Criterion<>(this, (T)var1);
   }

   public static record Listener<T extends CriterionTriggerInstance>(T trigger, AdvancementHolder advancement, String criterion) {
      public Listener(T trigger, AdvancementHolder advancement, String criterion) {
         super();
         this.trigger = (T)trigger;
         this.advancement = advancement;
         this.criterion = criterion;
      }

      public void run(PlayerAdvancements var1) {
         var1.award(this.advancement, this.criterion);
      }
   }
}