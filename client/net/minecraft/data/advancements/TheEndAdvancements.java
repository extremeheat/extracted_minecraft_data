package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

public class TheEndAdvancements implements Consumer<Consumer<Advancement>> {
   public TheEndAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.advancement().display((ItemLike)Blocks.END_STONE, Component.translatable("advancements.end.root.title"), Component.translatable("advancements.end.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false).addCriterion("entered_end", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END)).save(var1, "end/root");
      Advancement var3 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Blocks.DRAGON_HEAD, Component.translatable("advancements.end.kill_dragon.title"), Component.translatable("advancements.end.kill_dragon.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("killed_dragon", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(var1, "end/kill_dragon");
      Advancement var4 = Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.ENDER_PEARL, Component.translatable("advancements.end.enter_end_gateway.title"), Component.translatable("advancements.end.enter_end_gateway.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_end_gateway", (CriterionTriggerInstance)EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.END_GATEWAY)).save(var1, "end/enter_end_gateway");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.END_CRYSTAL, Component.translatable("advancements.end.respawn_dragon.title"), Component.translatable("advancements.end.respawn_dragon.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("summoned_dragon", (CriterionTriggerInstance)SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(var1, "end/respawn_dragon");
      Advancement var5 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Blocks.PURPUR_BLOCK, Component.translatable("advancements.end.find_end_city.title"), Component.translatable("advancements.end.find_end_city.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("in_city", (CriterionTriggerInstance)PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(BuiltinStructures.END_CITY))).save(var1, "end/find_end_city");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.DRAGON_BREATH, Component.translatable("advancements.end.dragon_breath.title"), Component.translatable("advancements.end.dragon_breath.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("dragon_breath", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DRAGON_BREATH)).save(var1, "end/dragon_breath");
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.SHULKER_SHELL, Component.translatable("advancements.end.levitate.title"), Component.translatable("advancements.end.levitate.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("levitated", (CriterionTriggerInstance)LevitationTrigger.TriggerInstance.levitated(DistancePredicate.vertical(MinMaxBounds.Doubles.atLeast(50.0)))).save(var1, "end/levitate");
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.ELYTRA, Component.translatable("advancements.end.elytra.title"), Component.translatable("advancements.end.elytra.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("elytra", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.ELYTRA)).save(var1, "end/elytra");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Blocks.DRAGON_EGG, Component.translatable("advancements.end.dragon_egg.title"), Component.translatable("advancements.end.dragon_egg.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("dragon_egg", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DRAGON_EGG)).save(var1, "end/dragon_egg");
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }
}
