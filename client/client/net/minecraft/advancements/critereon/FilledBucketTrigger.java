package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FilledBucketTrigger extends SimpleCriterionTrigger<FilledBucketTrigger.TriggerInstance> {
   public FilledBucketTrigger() {
      super();
   }

   @Override
   public Codec<FilledBucketTrigger.TriggerInstance> codec() {
      return FilledBucketTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<FilledBucketTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(FilledBucketTrigger.TriggerInstance::player),
                  ItemPredicate.CODEC.optionalFieldOf("item").forGetter(FilledBucketTrigger.TriggerInstance::item)
               )
               .apply(var0, FilledBucketTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) {
         super();
         this.player = player;
         this.item = item;
      }

      public static Criterion<FilledBucketTrigger.TriggerInstance> filledBucket(ItemPredicate.Builder var0) {
         return CriteriaTriggers.FILLED_BUCKET.createCriterion(new FilledBucketTrigger.TriggerInstance(Optional.empty(), Optional.of(var0.build())));
      }

      public boolean matches(ItemStack var1) {
         return !this.item.isPresent() || this.item.get().matches(var1);
      }
   }
}
