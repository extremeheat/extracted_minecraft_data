package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PlayerTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public PlayerTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return PlayerTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1, (var0) -> {
         return true;
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> var1) {
         super();
         this.player = var1;
      }

      public static Criterion<TriggerInstance> located(LocationPredicate.Builder var0) {
         return CriteriaTriggers.LOCATION.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().located(var0)))));
      }

      public static Criterion<TriggerInstance> located(EntityPredicate.Builder var0) {
         return CriteriaTriggers.LOCATION.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(var0.build()))));
      }

      public static Criterion<TriggerInstance> located(Optional<EntityPredicate> var0) {
         return CriteriaTriggers.LOCATION.createCriterion(new TriggerInstance(EntityPredicate.wrap(var0)));
      }

      public static Criterion<TriggerInstance> sleptInBed() {
         return CriteriaTriggers.SLEPT_IN_BED.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> raidWon() {
         return CriteriaTriggers.RAID_WIN.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> avoidVibration() {
         return CriteriaTriggers.AVOID_VIBRATION.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> tick() {
         return CriteriaTriggers.TICK.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> walkOnBlockWithEquipment(HolderGetter<Block> var0, HolderGetter<Item> var1, Block var2, Item var3) {
         return located(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().feet(ItemPredicate.Builder.item().of(var1, var3))).steppingOn(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var0, var2))));
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }
   }
}
