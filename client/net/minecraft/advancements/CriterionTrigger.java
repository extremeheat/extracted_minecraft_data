package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public interface CriterionTrigger<T extends CriterionTriggerInstance> {
   ResourceLocation getId();

   void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<T> var2);

   void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<T> var2);

   void removePlayerListeners(PlayerAdvancements var1);

   T createInstance(JsonObject var1, DeserializationContext var2);

   public static class Listener<T extends CriterionTriggerInstance> {
      private final T trigger;
      private final Advancement advancement;
      private final String criterion;

      public Listener(T var1, Advancement var2, String var3) {
         super();
         this.trigger = var1;
         this.advancement = var2;
         this.criterion = var3;
      }

      public T getTriggerInstance() {
         return this.trigger;
      }

      public void run(PlayerAdvancements var1) {
         var1.award(this.advancement, this.criterion);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            CriterionTrigger.Listener var2 = (CriterionTrigger.Listener)var1;
            if (!this.trigger.equals(var2.trigger)) {
               return false;
            } else {
               return !this.advancement.equals(var2.advancement) ? false : this.criterion.equals(var2.criterion);
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int var1 = this.trigger.hashCode();
         var1 = 31 * var1 + this.advancement.hashCode();
         var1 = 31 * var1 + this.criterion.hashCode();
         return var1;
      }
   }
}
