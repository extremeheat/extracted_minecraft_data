package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LubricationComponent;

public class PotatoRefinementTrigger extends SimpleCriterionTrigger<PotatoRefinementTrigger.TriggerInstance> {
   static final Codec<PotatoRefinementTrigger.ItemStackPredicate> ITEM_STACK_PREDICATE_CODEC = StringRepresentable.fromEnum(
         PotatoRefinementTrigger.Type::values
      )
      .dispatch(PotatoRefinementTrigger.ItemStackPredicate::type, PotatoRefinementTrigger.Type::codec);

   public PotatoRefinementTrigger() {
      super();
   }

   @Override
   public Codec<PotatoRefinementTrigger.TriggerInstance> codec() {
      return PotatoRefinementTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public interface ItemStackPredicate extends Predicate<ItemStack> {
      PotatoRefinementTrigger.Type type();
   }

   static record MinLubricationItemStackPredicate(ItemPredicate b, int c) implements PotatoRefinementTrigger.ItemStackPredicate {
      private final ItemPredicate itemPredicate;
      private final int minLubricationLevel;
      public static final Codec<PotatoRefinementTrigger.MinLubricationItemStackPredicate> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ItemPredicate.CODEC.fieldOf("item_predicate").forGetter(PotatoRefinementTrigger.MinLubricationItemStackPredicate::itemPredicate),
                  Codec.INT.fieldOf("min_lubrication").forGetter(PotatoRefinementTrigger.MinLubricationItemStackPredicate::minLubricationLevel)
               )
               .apply(var0, PotatoRefinementTrigger.MinLubricationItemStackPredicate::new)
      );

      MinLubricationItemStackPredicate(ItemPredicate var1, int var2) {
         super();
         this.itemPredicate = var1;
         this.minLubricationLevel = var2;
      }

      @Override
      public PotatoRefinementTrigger.Type type() {
         return PotatoRefinementTrigger.Type.LUBRICATION;
      }

      public boolean test(ItemStack var1) {
         if (this.itemPredicate.matches(var1)) {
            LubricationComponent var2 = var1.get(DataComponents.LUBRICATION);
            if (var2 != null) {
               return var2.getLevel() >= this.minLubricationLevel;
            }
         }

         return false;
      }
   }

   static record StandardItemStackPredicate(ItemPredicate b) implements PotatoRefinementTrigger.ItemStackPredicate {
      private final ItemPredicate itemPredicate;
      public static final Codec<PotatoRefinementTrigger.StandardItemStackPredicate> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(ItemPredicate.CODEC.fieldOf("item_predicate").forGetter(PotatoRefinementTrigger.StandardItemStackPredicate::itemPredicate))
               .apply(var0, PotatoRefinementTrigger.StandardItemStackPredicate::new)
      );

      StandardItemStackPredicate(ItemPredicate var1) {
         super();
         this.itemPredicate = var1;
      }

      @Override
      public PotatoRefinementTrigger.Type type() {
         return PotatoRefinementTrigger.Type.STANDARD;
      }

      public boolean test(ItemStack var1) {
         return this.itemPredicate.matches(var1);
      }
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, PotatoRefinementTrigger.ItemStackPredicate c)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final PotatoRefinementTrigger.ItemStackPredicate resultPredicate;
      public static final Codec<PotatoRefinementTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(PotatoRefinementTrigger.TriggerInstance::player),
                  PotatoRefinementTrigger.ITEM_STACK_PREDICATE_CODEC
                     .fieldOf("result_predicate")
                     .forGetter(PotatoRefinementTrigger.TriggerInstance::resultPredicate)
               )
               .apply(var0, PotatoRefinementTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, PotatoRefinementTrigger.ItemStackPredicate var2) {
         super();
         this.player = var1;
         this.resultPredicate = var2;
      }

      public static Criterion<PotatoRefinementTrigger.TriggerInstance> refined(Item var0) {
         return CriteriaTriggers.POTATO_REFINED
            .createCriterion(
               new PotatoRefinementTrigger.TriggerInstance(
                  Optional.empty(), new PotatoRefinementTrigger.StandardItemStackPredicate(ItemPredicate.Builder.item().of(var0).build())
               )
            );
      }

      public static Criterion<PotatoRefinementTrigger.TriggerInstance> lubricatedAtLeast(int var0) {
         return CriteriaTriggers.POTATO_REFINED
            .createCriterion(
               new PotatoRefinementTrigger.TriggerInstance(
                  Optional.empty(), new PotatoRefinementTrigger.MinLubricationItemStackPredicate(ItemPredicate.Builder.item().build(), var0)
               )
            );
      }

      public static Criterion<PotatoRefinementTrigger.TriggerInstance> lubricatedAtLeast(ItemPredicate var0, int var1) {
         return CriteriaTriggers.POTATO_REFINED
            .createCriterion(
               new PotatoRefinementTrigger.TriggerInstance(Optional.empty(), new PotatoRefinementTrigger.MinLubricationItemStackPredicate(var0, var1))
            );
      }

      public boolean matches(ItemStack var1) {
         return this.resultPredicate.test(var1);
      }
   }

   static enum Type implements StringRepresentable {
      STANDARD("standard", () -> PotatoRefinementTrigger.StandardItemStackPredicate.CODEC),
      LUBRICATION("lubrication", () -> PotatoRefinementTrigger.MinLubricationItemStackPredicate.CODEC);

      private final String serializedName;
      private final Supplier<Codec<? extends PotatoRefinementTrigger.ItemStackPredicate>> codec;

      private Type(String var3, Supplier<Codec<? extends PotatoRefinementTrigger.ItemStackPredicate>> var4) {
         this.serializedName = var3;
         this.codec = var4;
      }

      private Codec<? extends PotatoRefinementTrigger.ItemStackPredicate> codec() {
         return (Codec<? extends PotatoRefinementTrigger.ItemStackPredicate>)this.codec.get();
      }

      @Override
      public String getSerializedName() {
         return this.serializedName;
      }
   }
}
