package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
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

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<ItemPredicate> c) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<ItemPredicate> item;
      public static final Codec<FilledBucketTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(FilledBucketTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(FilledBucketTrigger.TriggerInstance::item)
               )
               .apply(var0, FilledBucketTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2) {
         super();
         this.player = var1;
         this.item = var2;
      }

      public static Criterion<FilledBucketTrigger.TriggerInstance> filledBucket(ItemPredicate.Builder var0) {
         return CriteriaTriggers.FILLED_BUCKET.createCriterion(new FilledBucketTrigger.TriggerInstance(Optional.empty(), Optional.of(var0.build())));
      }

      public boolean matches(ItemStack var1) {
         return !this.item.isPresent() || this.item.get().matches(var1);
      }
   }
}
