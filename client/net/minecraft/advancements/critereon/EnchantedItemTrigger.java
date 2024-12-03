package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EnchantedItemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public EnchantedItemTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return EnchantedItemTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2, int var3) {
      this.trigger(var1, (var2x) -> var2x.matches(var2, var3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, MinMaxBounds.Ints levels) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item), MinMaxBounds.Ints.CODEC.optionalFieldOf("levels", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::levels)).apply(var0, TriggerInstance::new));

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2, MinMaxBounds.Ints var3) {
         super();
         this.player = var1;
         this.item = var2;
         this.levels = var3;
      }

      public static Criterion<TriggerInstance> enchantedItem() {
         return CriteriaTriggers.ENCHANTED_ITEM.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), MinMaxBounds.Ints.ANY));
      }

      public boolean matches(ItemStack var1, int var2) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test(var1)) {
            return false;
         } else {
            return this.levels.matches(var2);
         }
      }
   }
}
