package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;

public class FilledBucketTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public FilledBucketTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return FilledBucketTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack item) {
      this.trigger(player, (t) -> t.matches(item));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> filledBucket(final ItemPredicate.Builder item) {
         return CriteriaTriggers.FILLED_BUCKET.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(item.build())));
      }

      public boolean matches(final ItemStack item) {
         return !this.item.isPresent() || ((ItemPredicate)this.item.get()).test((ItemInstance)item);
      }
   }
}
