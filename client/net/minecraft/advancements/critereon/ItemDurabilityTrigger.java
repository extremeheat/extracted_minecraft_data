package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public ItemDurabilityTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return ItemDurabilityTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2, int var3) {
      this.trigger(var1, (var2x) -> var2x.matches(var2, var3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, MinMaxBounds.Ints durability, MinMaxBounds.Ints delta) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item), MinMaxBounds.Ints.CODEC.optionalFieldOf("durability", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::durability), MinMaxBounds.Ints.CODEC.optionalFieldOf("delta", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::delta)).apply(var0, TriggerInstance::new));

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2, MinMaxBounds.Ints var3, MinMaxBounds.Ints var4) {
         super();
         this.player = var1;
         this.item = var2;
         this.durability = var3;
         this.delta = var4;
      }

      public static Criterion<TriggerInstance> changedDurability(Optional<ItemPredicate> var0, MinMaxBounds.Ints var1) {
         return changedDurability(Optional.empty(), var0, var1);
      }

      public static Criterion<TriggerInstance> changedDurability(Optional<ContextAwarePredicate> var0, Optional<ItemPredicate> var1, MinMaxBounds.Ints var2) {
         return CriteriaTriggers.ITEM_DURABILITY_CHANGED.createCriterion(new TriggerInstance(var0, var1, var2, MinMaxBounds.Ints.ANY));
      }

      public boolean matches(ItemStack var1, int var2) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test(var1)) {
            return false;
         } else if (!this.durability.matches(var1.getMaxDamage() - var2)) {
            return false;
         } else {
            return this.delta.matches(var1.getDamageValue() - var2);
         }
      }
   }
}
