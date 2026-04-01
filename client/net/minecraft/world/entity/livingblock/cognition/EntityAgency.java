package net.minecraft.world.entity.livingblock.cognition;

import java.util.Objects;
import net.minecraft.world.entity.livingblock.LivingBlock;

public record EntityAgency<T extends LivingBlock>(T entity) implements Agency {
   public EntityAgency {
      super();
   }

   public <V> Prize<V> inPursuitOf(final Desire<V> desire) {
      return new Prize<V>() {
         {
            Objects.requireNonNull(EntityAgency.this);
         }

         public V get() {
            return (V)Objects.requireNonNull(EntityAgency.this.entity.hopesAndDreams.getDesire(desire), "no object of desire");
         }

         public void forget() {
            EntityAgency.this.entity.hopesAndDreams.forget(desire);
         }

         public boolean exists() {
            return EntityAgency.this.entity.hopesAndDreams.hasDesire(desire);
         }
      };
   }

   public <V> Intent<V> withIntentTo(final Action<V> action) {
      return (Intent)action.intent().apply(this.entity);
   }
}
