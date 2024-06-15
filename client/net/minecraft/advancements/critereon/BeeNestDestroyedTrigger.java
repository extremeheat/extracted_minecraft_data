package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BeeNestDestroyedTrigger extends SimpleCriterionTrigger<BeeNestDestroyedTrigger.TriggerInstance> {
   public BeeNestDestroyedTrigger() {
      super();
   }

   @Override
   public Codec<BeeNestDestroyedTrigger.TriggerInstance> codec() {
      return BeeNestDestroyedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, BlockState var2, ItemStack var3, int var4) {
      this.trigger(var1, var3x -> var3x.matches(var2, var3, var4));
   }

   public static record TriggerInstance(
      Optional<ContextAwarePredicate> player, Optional<Holder<Block>> block, Optional<ItemPredicate> item, MinMaxBounds.Ints beesInside
   ) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<BeeNestDestroyedTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(BeeNestDestroyedTrigger.TriggerInstance::player),
                  BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("block").forGetter(BeeNestDestroyedTrigger.TriggerInstance::block),
                  ItemPredicate.CODEC.optionalFieldOf("item").forGetter(BeeNestDestroyedTrigger.TriggerInstance::item),
                  MinMaxBounds.Ints.CODEC
                     .optionalFieldOf("num_bees_inside", MinMaxBounds.Ints.ANY)
                     .forGetter(BeeNestDestroyedTrigger.TriggerInstance::beesInside)
               )
               .apply(var0, BeeNestDestroyedTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Block>> block, Optional<ItemPredicate> item, MinMaxBounds.Ints beesInside) {
         super();
         this.player = player;
         this.block = block;
         this.item = item;
         this.beesInside = beesInside;
      }

      public static Criterion<BeeNestDestroyedTrigger.TriggerInstance> destroyedBeeNest(Block var0, ItemPredicate.Builder var1, MinMaxBounds.Ints var2) {
         return CriteriaTriggers.BEE_NEST_DESTROYED
            .createCriterion(
               new BeeNestDestroyedTrigger.TriggerInstance(Optional.empty(), Optional.of(var0.builtInRegistryHolder()), Optional.of(var1.build()), var2)
            );
      }

      public boolean matches(BlockState var1, ItemStack var2, int var3) {
         if (this.block.isPresent() && !var1.is(this.block.get())) {
            return false;
         } else {
            return this.item.isPresent() && !this.item.get().test(var2) ? false : this.beesInside.matches(var3);
         }
      }
   }
}
