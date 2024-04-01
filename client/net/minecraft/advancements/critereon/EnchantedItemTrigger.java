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

public class EnchantedItemTrigger extends SimpleCriterionTrigger<EnchantedItemTrigger.TriggerInstance> {
   public EnchantedItemTrigger() {
      super();
   }

   @Override
   public Codec<EnchantedItemTrigger.TriggerInstance> codec() {
      return EnchantedItemTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2, int var3) {
      this.trigger(var1, var2x -> var2x.matches(var2, var3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<ItemPredicate> c, MinMaxBounds.Ints d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<ItemPredicate> item;
      private final MinMaxBounds.Ints levels;
      public static final Codec<EnchantedItemTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(EnchantedItemTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(EnchantedItemTrigger.TriggerInstance::item),
                  ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "levels", MinMaxBounds.Ints.ANY)
                     .forGetter(EnchantedItemTrigger.TriggerInstance::levels)
               )
               .apply(var0, EnchantedItemTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2, MinMaxBounds.Ints var3) {
         super();
         this.player = var1;
         this.item = var2;
         this.levels = var3;
      }

      public static Criterion<EnchantedItemTrigger.TriggerInstance> enchantedItem() {
         return CriteriaTriggers.ENCHANTED_ITEM
            .createCriterion(new EnchantedItemTrigger.TriggerInstance(Optional.empty(), Optional.empty(), MinMaxBounds.Ints.ANY));
      }

      public boolean matches(ItemStack var1, int var2) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).matches(var1)) {
            return false;
         } else {
            return this.levels.matches(var2);
         }
      }
   }
}
