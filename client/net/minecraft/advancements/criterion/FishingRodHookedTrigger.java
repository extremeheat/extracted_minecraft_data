package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class FishingRodHookedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public FishingRodHookedTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return FishingRodHookedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack rod, final FishingHook hook, final Collection<ItemStack> items) {
      LootContext hookedInContext = EntityPredicate.createContext(player, (Entity)(hook.getHookedIn() != null ? hook.getHookedIn() : hook));
      this.trigger(player, (t) -> t.matches(rod, hookedInContext, items));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> rod, Optional<ContextAwarePredicate> entity, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("rod").forGetter(TriggerInstance::rod), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> fishedItem(final Optional<ItemPredicate> rod, final Optional<EntityPredicate> entity, final Optional<ItemPredicate> item) {
         return CriteriaTriggers.FISHING_ROD_HOOKED.createCriterion(new TriggerInstance(Optional.empty(), rod, EntityPredicate.wrap(entity), item));
      }

      public boolean matches(final ItemStack rod, final LootContext hookedIn, final Collection<ItemStack> items) {
         if (this.rod.isPresent() && !((ItemPredicate)this.rod.get()).test((ItemInstance)rod)) {
            return false;
         } else if (this.entity.isPresent() && !((ContextAwarePredicate)this.entity.get()).matches(hookedIn)) {
            return false;
         } else {
            if (this.item.isPresent()) {
               boolean matched = false;
               Entity hookedInEntity = (Entity)hookedIn.getOptionalParameter(LootContextParams.THIS_ENTITY);
               if (hookedInEntity instanceof LivingBlock) {
                  LivingBlock item = (LivingBlock)hookedInEntity;
                  if (((ItemPredicate)this.item.get()).test((ItemInstance)item.getItemStack())) {
                     matched = true;
                  }
               }

               for(ItemStack item : items) {
                  if (((ItemPredicate)this.item.get()).test((ItemInstance)item)) {
                     matched = true;
                     break;
                  }
               }

               if (!matched) {
                  return false;
               }
            }

            return true;
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validate(validator.entityContext(), "entity", this.entity);
      }
   }
}
