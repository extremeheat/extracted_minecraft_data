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
import net.minecraft.world.level.ItemLike;

public class ShotCrossbowTrigger extends SimpleCriterionTrigger<ShotCrossbowTrigger.TriggerInstance> {
   public ShotCrossbowTrigger() {
      super();
   }

   @Override
   public Codec<ShotCrossbowTrigger.TriggerInstance> codec() {
      return ShotCrossbowTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<ItemPredicate> c) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<ItemPredicate> item;
      public static final Codec<ShotCrossbowTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(ShotCrossbowTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(ShotCrossbowTrigger.TriggerInstance::item)
               )
               .apply(var0, ShotCrossbowTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2) {
         super();
         this.player = var1;
         this.item = var2;
      }

      public static Criterion<ShotCrossbowTrigger.TriggerInstance> shotCrossbow(Optional<ItemPredicate> var0) {
         return CriteriaTriggers.SHOT_CROSSBOW.createCriterion(new ShotCrossbowTrigger.TriggerInstance(Optional.empty(), var0));
      }

      public static Criterion<ShotCrossbowTrigger.TriggerInstance> shotCrossbow(ItemLike var0) {
         return CriteriaTriggers.SHOT_CROSSBOW
            .createCriterion(new ShotCrossbowTrigger.TriggerInstance(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(var0).build())));
      }

      public boolean matches(ItemStack var1) {
         return this.item.isEmpty() || ((ItemPredicate)this.item.get()).matches(var1);
      }
   }
}
