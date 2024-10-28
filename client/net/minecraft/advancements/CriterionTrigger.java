package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import net.minecraft.server.PlayerAdvancements;

public interface CriterionTrigger<T extends CriterionTriggerInstance> {
   void addPlayerListener(PlayerAdvancements var1, Listener<T> var2);

   void removePlayerListener(PlayerAdvancements var1, Listener<T> var2);

   void removePlayerListeners(PlayerAdvancements var1);

   Codec<T> codec();

   default Criterion<T> createCriterion(T var1) {
      return new Criterion(this, var1);
   }

   public static record Listener<T extends CriterionTriggerInstance>(T trigger, AdvancementHolder advancement, String criterion) {
      public Listener(T var1, AdvancementHolder var2, String var3) {
         super();
         this.trigger = var1;
         this.advancement = var2;
         this.criterion = var3;
      }

      public void run(PlayerAdvancements var1) {
         var1.award(this.advancement, this.criterion);
      }

      public T trigger() {
         return this.trigger;
      }

      public AdvancementHolder advancement() {
         return this.advancement;
      }

      public String criterion() {
         return this.criterion;
      }
   }
}
