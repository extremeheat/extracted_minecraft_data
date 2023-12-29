package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
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

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<Holder<Block>> c, Optional<ItemPredicate> d, MinMaxBounds.Ints e)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<Holder<Block>> block;
      private final Optional<ItemPredicate> item;
      private final MinMaxBounds.Ints beesInside;
      public static final Codec<BeeNestDestroyedTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(BeeNestDestroyedTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(BuiltInRegistries.BLOCK.holderByNameCodec(), "block")
                     .forGetter(BeeNestDestroyedTrigger.TriggerInstance::block),
                  ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(BeeNestDestroyedTrigger.TriggerInstance::item),
                  ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "num_bees_inside", MinMaxBounds.Ints.ANY)
                     .forGetter(BeeNestDestroyedTrigger.TriggerInstance::beesInside)
               )
               .apply(var0, BeeNestDestroyedTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<Holder<Block>> var2, Optional<ItemPredicate> var3, MinMaxBounds.Ints var4) {
         super();
         this.player = var1;
         this.block = var2;
         this.item = var3;
         this.beesInside = var4;
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
            return this.item.isPresent() && !this.item.get().matches(var2) ? false : this.beesInside.matches(var3);
         }
      }
   }
}
