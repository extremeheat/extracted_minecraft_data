package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerUnlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.WorldEffect;

public class PlayerTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public PlayerTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return PlayerTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1, (var0) -> true);
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)).apply(var0, TriggerInstance::new));

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

      public static Criterion<TriggerInstance> playerUnlockUnlocked() {
         return CriteriaTriggers.PLAYER_UNLOCK_UNLOCKED.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerUnlockBought() {
         return CriteriaTriggers.PLAYER_UNLOCK_BOUGHT.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> playerUnlockBought(Optional<EntityPredicate> var0) {
         return CriteriaTriggers.PLAYER_UNLOCK_BOUGHT.createCriterion(new TriggerInstance(EntityPredicate.wrap(var0)));
      }

      public static Criterion<TriggerInstance> playerUnlockBought(Holder<PlayerUnlock> var0) {
         return CriteriaTriggers.PLAYER_UNLOCK_BOUGHT.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().withPlayerUnlocks(UnlockPredicate.Builder.unlocks().isUnlocked(var0)).build())))));
      }

      public static Criterion<TriggerInstance> mineRevisitorActivated() {
         return CriteriaTriggers.MINE_REVISITOR_ACTIVATED.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> mineCrafterUpgraded() {
         return CriteriaTriggers.MINE_CRAFTER_UPGRADED.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> levelCompleted() {
         return CriteriaTriggers.LEVEL_COMPLETED.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> specialMineCompleted() {
         return CriteriaTriggers.SPECIAL_MINE_COMPLETED.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> specialMineCompleted(SpecialMine var0) {
         return CriteriaTriggers.SPECIAL_MINE_COMPLETED.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().setSpecialMine((ResourceKey)BuiltInRegistries.SPECIAL_MINE.getResourceKey(var0).get()))))));
      }

      @SafeVarargs
      public static Criterion<TriggerInstance> levelCompletedWithPlayerUnlocks(Holder<PlayerUnlock>... var0) {
         return CriteriaTriggers.LEVEL_COMPLETED.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().withPlayerUnlocks(UnlockPredicate.Builder.unlocks().isUnlocked(var0)).build())))));
      }

      @SafeVarargs
      public static Criterion<TriggerInstance> specialMineCompletedWithPlayerUnlocks(Holder<PlayerUnlock>... var0) {
         return CriteriaTriggers.SPECIAL_MINE_COMPLETED.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().withPlayerUnlocks(UnlockPredicate.Builder.unlocks().isUnlocked(var0)).build())))));
      }

      public static Criterion<TriggerInstance> specialMineCompletedWithEffects(WorldEffect... var0) {
         return CriteriaTriggers.SPECIAL_MINE_COMPLETED.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().hasWorldEffects(var0))))));
      }

      public static Criterion<TriggerInstance> levelCompletedWithEffects(WorldEffect... var0) {
         return CriteriaTriggers.LEVEL_COMPLETED.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().hasWorldEffects(var0))))));
      }

      public static Criterion<TriggerInstance> levelFailed() {
         return CriteriaTriggers.LEVEL_FAILED.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> levelFailedWithEffects(WorldEffect... var0) {
         return CriteriaTriggers.LEVEL_FAILED.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().hasWorldEffects(var0))))));
      }

      public static Criterion<TriggerInstance> walkOnBlockWithEquipment(HolderGetter<Block> var0, HolderGetter<Item> var1, Block var2, Item var3) {
         return located(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().feet(ItemPredicate.Builder.item().of(var1, var3))).steppingOn(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var0, var2))));
      }
   }
}
