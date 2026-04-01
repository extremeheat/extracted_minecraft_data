package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public class UseItemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public UseItemTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return UseItemTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Item item) {
      this.trigger(player, (t) -> t.matches(item));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Item>> item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), Item.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> usedItem() {
         return CriteriaTriggers.USE_ITEM.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> usedItem(final Item item) {
         return CriteriaTriggers.USE_ITEM.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(item.builtInRegistryHolder())));
      }

      public boolean matches(final Item other) {
         return this.item.isEmpty() || ((Holder)this.item.get()).is((Holder)other.builtInRegistryHolder());
      }
   }
}
