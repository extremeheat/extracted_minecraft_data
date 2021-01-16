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
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class TheEndAdvancements implements Consumer<Consumer<Advancement>> {
   public TheEndAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.advancement().display((ItemLike)Blocks.END_STONE, new TranslatableComponent("advancements.end.root.title"), new TranslatableComponent("advancements.end.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false).addCriterion("entered_end", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END)).save(var1, "end/root");
      Advancement var3 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Blocks.DRAGON_HEAD, new TranslatableComponent("advancements.end.kill_dragon.title"), new TranslatableComponent("advancements.end.kill_dragon.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("killed_dragon", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(var1, "end/kill_dragon");
      Advancement var4 = Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.ENDER_PEARL, new TranslatableComponent("advancements.end.enter_end_gateway.title"), new TranslatableComponent("advancements.end.enter_end_gateway.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_end_gateway", (CriterionTriggerInstance)EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.END_GATEWAY)).save(var1, "end/enter_end_gateway");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.END_CRYSTAL, new TranslatableComponent("advancements.end.respawn_dragon.title"), new TranslatableComponent("advancements.end.respawn_dragon.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("summoned_dragon", (CriterionTriggerInstance)SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(var1, "end/respawn_dragon");
      Advancement var5 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Blocks.PURPUR_BLOCK, new TranslatableComponent("advancements.end.find_end_city.title"), new TranslatableComponent("advancements.end.find_end_city.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("in_city", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(StructureFeature.END_CITY))).save(var1, "end/find_end_city");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.DRAGON_BREATH, new TranslatableComponent("advancements.end.dragon_breath.title"), new TranslatableComponent("advancements.end.dragon_breath.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("dragon_breath", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DRAGON_BREATH)).save(var1, "end/dragon_breath");
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.SHULKER_SHELL, new TranslatableComponent("advancements.end.levitate.title"), new TranslatableComponent("advancements.end.levitate.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("levitated", (CriterionTriggerInstance)LevitationTrigger.TriggerInstance.levitated(DistancePredicate.vertical(MinMaxBounds.Floats.atLeast(50.0F)))).save(var1, "end/levitate");
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.ELYTRA, new TranslatableComponent("advancements.end.elytra.title"), new TranslatableComponent("advancements.end.elytra.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("elytra", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.ELYTRA)).save(var1, "end/elytra");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Blocks.DRAGON_EGG, new TranslatableComponent("advancements.end.dragon_egg.title"), new TranslatableComponent("advancements.end.dragon_egg.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("dragon_egg", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DRAGON_EGG)).save(var1, "end/dragon_egg");
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }
}
