package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import net.minecraft.server.PlayerAdvancements;

public interface CriterionTrigger<T extends CriterionTriggerInstance> {
   void addPlayerListener(final PlayerAdvancements player, final Listener<T> listener);

   void removePlayerListener(final PlayerAdvancements player, final Listener<T> listener);

   void removePlayerListeners(final PlayerAdvancements player);

   Codec<T> codec();

   default Criterion<T> createCriterion(final T instance) {
      return new Criterion<T>(this, instance);
   }

   public static record Listener<T extends CriterionTriggerInstance>(T trigger, AdvancementHolder advancement, String criterion) {
      public Listener {
         super();
      }

      public void run(final PlayerAdvancements player) {
         player.award(this.advancement, this.criterion);
      }
   }
}
