package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LubricationComponent;

public class ThrowLubricatedTrigger extends SimpleCriterionTrigger<ThrowLubricatedTrigger.TriggerInstance> {
   public ThrowLubricatedTrigger() {
      super();
   }

   @Override
   public Codec<ThrowLubricatedTrigger.TriggerInstance> codec() {
      return ThrowLubricatedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<ItemPredicate> c, int d) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<ItemPredicate> itemPredicate;
      private final int minLubrication;
      public static final Codec<ThrowLubricatedTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(ThrowLubricatedTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item_predicate").forGetter(ThrowLubricatedTrigger.TriggerInstance::itemPredicate),
                  Codec.INT.fieldOf("min_lubrication").forGetter(ThrowLubricatedTrigger.TriggerInstance::minLubrication)
               )
               .apply(var0, ThrowLubricatedTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2, int var3) {
         super();
         this.player = var1;
         this.itemPredicate = var2;
         this.minLubrication = var3;
      }

      public static Criterion<ThrowLubricatedTrigger.TriggerInstance> thrownWithAtLeast(int var0) {
         return CriteriaTriggers.THROW_LUBRICATED.createCriterion(new ThrowLubricatedTrigger.TriggerInstance(Optional.empty(), Optional.empty(), var0));
      }

      public static Criterion<ThrowLubricatedTrigger.TriggerInstance> thrownWithAtLeast(ItemPredicate var0, int var1) {
         return CriteriaTriggers.THROW_LUBRICATED.createCriterion(new ThrowLubricatedTrigger.TriggerInstance(Optional.empty(), Optional.of(var0), var1));
      }

      public boolean matches(ItemStack var1) {
         if (this.itemPredicate.isPresent() && !((ItemPredicate)this.itemPredicate.get()).matches(var1)) {
            return false;
         } else {
            LubricationComponent var2 = var1.get(DataComponents.LUBRICATION);
            if (var2 == null) {
               return false;
            } else {
               return var2.getLevel() >= this.minLubrication;
            }
         }
      }
   }
}
