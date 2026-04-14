package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BeeNestDestroyedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public BeeNestDestroyedTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return BeeNestDestroyedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final BlockState state, final ItemStack itemStack, final int numBeesInside) {
      this.trigger(player, (t) -> t.matches(state, itemStack, numBeesInside));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Block>> block, Optional<ItemPredicate> item, MinMaxBounds.Ints beesInside) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("block").forGetter(TriggerInstance::block), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item), MinMaxBounds.Ints.CODEC.optionalFieldOf("num_bees_inside", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::beesInside)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> destroyedBeeNest(final Block block, final ItemPredicate.Builder itemPredicate, final MinMaxBounds.Ints numBeesInside) {
         return CriteriaTriggers.BEE_NEST_DESTROYED.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(block.builtInRegistryHolder()), Optional.of(itemPredicate.build()), numBeesInside));
      }

      public boolean matches(final BlockState state, final ItemStack itemStack, final int numBeesInside) {
         if (this.block.isPresent() && !state.is((Holder)this.block.get())) {
            return false;
         } else {
            return this.item.isPresent() && !((ItemPredicate)this.item.get()).test((ItemInstance)itemStack) ? false : this.beesInside.matches(numBeesInside);
         }
      }
   }
}
