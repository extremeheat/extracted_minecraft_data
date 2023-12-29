package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LevitationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

public class VanillaTheEndAdvancements implements AdvancementSubProvider {
   public VanillaTheEndAdvancements() {
      super();
   }

   @Override
   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      AdvancementHolder var3 = Advancement.Builder.advancement()
         .display(
            Blocks.END_STONE,
            Component.translatable("advancements.end.root.title"),
            Component.translatable("advancements.end.root.description"),
            new ResourceLocation("textures/gui/advancements/backgrounds/end.png"),
            AdvancementType.TASK,
            false,
            false,
            false
         )
         .addCriterion("entered_end", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END))
         .save(var2, "end/root");
      AdvancementHolder var4 = Advancement.Builder.advancement()
         .parent(var3)
         .display(
            Blocks.DRAGON_HEAD,
            Component.translatable("advancements.end.kill_dragon.title"),
            Component.translatable("advancements.end.kill_dragon.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("killed_dragon", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON)))
         .save(var2, "end/kill_dragon");
      AdvancementHolder var5 = Advancement.Builder.advancement()
         .parent(var4)
         .display(
            Items.ENDER_PEARL,
            Component.translatable("advancements.end.enter_end_gateway.title"),
            Component.translatable("advancements.end.enter_end_gateway.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("entered_end_gateway", EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.END_GATEWAY))
         .save(var2, "end/enter_end_gateway");
      Advancement.Builder.advancement()
         .parent(var4)
         .display(
            Items.END_CRYSTAL,
            Component.translatable("advancements.end.respawn_dragon.title"),
            Component.translatable("advancements.end.respawn_dragon.description"),
            null,
            AdvancementType.GOAL,
            true,
            true,
            false
         )
         .addCriterion("summoned_dragon", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON)))
         .save(var2, "end/respawn_dragon");
      AdvancementHolder var6 = Advancement.Builder.advancement()
         .parent(var5)
         .display(
            Blocks.PURPUR_BLOCK,
            Component.translatable("advancements.end.find_end_city.title"),
            Component.translatable("advancements.end.find_end_city.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("in_city", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(BuiltinStructures.END_CITY)))
         .save(var2, "end/find_end_city");
      Advancement.Builder.advancement()
         .parent(var4)
         .display(
            Items.DRAGON_BREATH,
            Component.translatable("advancements.end.dragon_breath.title"),
            Component.translatable("advancements.end.dragon_breath.description"),
            null,
            AdvancementType.GOAL,
            true,
            true,
            false
         )
         .addCriterion("dragon_breath", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DRAGON_BREATH))
         .save(var2, "end/dragon_breath");
      Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Items.SHULKER_SHELL,
            Component.translatable("advancements.end.levitate.title"),
            Component.translatable("advancements.end.levitate.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(50))
         .addCriterion("levitated", LevitationTrigger.TriggerInstance.levitated(DistancePredicate.vertical(MinMaxBounds.Doubles.atLeast(50.0))))
         .save(var2, "end/levitate");
      Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Items.ELYTRA,
            Component.translatable("advancements.end.elytra.title"),
            Component.translatable("advancements.end.elytra.description"),
            null,
            AdvancementType.GOAL,
            true,
            true,
            false
         )
         .addCriterion("elytra", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ELYTRA))
         .save(var2, "end/elytra");
      Advancement.Builder.advancement()
         .parent(var4)
         .display(
            Blocks.DRAGON_EGG,
            Component.translatable("advancements.end.dragon_egg.title"),
            Component.translatable("advancements.end.dragon_egg.description"),
            null,
            AdvancementType.GOAL,
            true,
            true,
            false
         )
         .addCriterion("dragon_egg", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DRAGON_EGG))
         .save(var2, "end/dragon_egg");
   }
}
