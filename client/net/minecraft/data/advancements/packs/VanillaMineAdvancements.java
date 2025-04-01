package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.mines.UnlockMode;
import net.minecraft.world.level.mines.WorldEffect;
import net.minecraft.world.level.mines.WorldEffects;

public class VanillaMineAdvancements implements AdvancementSubProvider {
   public VanillaMineAdvancements() {
      super();
   }

   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      HolderLookup.RegistryLookup var3 = var1.lookupOrThrow(Registries.ITEM);
      AdvancementHolder var4 = Advancement.Builder.advancement().display((ItemLike)Items.MINE_CRAFTER, Component.translatable("advancements.adventure.enter_a_mine.title"), Component.translatable("advancements.adventure.enter_a_mine.description"), ResourceLocation.withDefaultNamespace("gui/advancements/backgrounds/mines"), AdvancementType.TASK, true, true, false).addCriterion("entered_a_mine", ChangeDimensionTrigger.TriggerInstance.changedDimension()).save(var2, "mines/enter_a_mine");
      AdvancementHolder var5 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.MINE_INGREDIENT, Component.translatable("advancements.mine.get_mine_ingredient.title"), Component.translatable("advancements.mine.get_mine_ingredient.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("mine_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINE_INGREDIENT)).save(var2, "mines/get_mine_ingredient");
      AdvancementHolder var6 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.GOLD_INGOT, Component.translatable("advancements.mine.cash_in.title"), Component.translatable("advancements.mine.cash_in.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("cash_in", ItemUsedOnLocationTrigger.TriggerInstance.cashedInItems(ItemPredicate.Builder.item().of(var3, Items.MINE_INGREDIENT))).save(var2, "mines/cash_in_mine_ingredient");
      AdvancementHolder var7 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.MINE, Component.translatable("advancements.adventure.level.title"), Component.translatable("advancements.adventure.level.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).rewards(AdvancementRewards.Builder.experience(10)).addCriterion("complete_level", PlayerTrigger.TriggerInstance.levelCompleted()).save(var2, "mines/level_completed");
      Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.MINE_REVISITOR, Component.translatable("advancements.adventure.mine_revisitor_activated.title"), Component.translatable("advancements.adventure.mine_revisitor_activated.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("mine_revisitor_activated", PlayerTrigger.TriggerInstance.mineRevisitorActivated()).save(var2, "mines/mine_revisitor_activated");
      Advancement.Builder var8 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.NETHERITE_PICKAXE, Component.translatable("advancements.mines.all_mine_ingredients.title"), Component.translatable("advancements.mines.all_mine_ingredients.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100));
      BuiltInRegistries.WORLD_EFFECT.listElements().filter((var0) -> ((WorldEffect)var0.value()).unlockMode() != UnlockMode.NEVER_UNLOCKED).forEach((var1x) -> var8.addCriterion(var1x.getRegisteredName(), PlayerTrigger.TriggerInstance.levelCompletedWithEffects((WorldEffect)var1x.value())));
      var8.save(var2, "mines/all_mine_ingredients");
      Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.EXPERIENCE_BOTTLE, Component.translatable("advancements.adventure.mine_crafter_upgraded.title"), Component.translatable("advancements.adventure.mine_crafter_upgraded.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).rewards(AdvancementRewards.Builder.experience(10)).addCriterion("mine_crafter_upgraded", PlayerTrigger.TriggerInstance.mineCrafterUpgraded()).save(var2, "mines/mine_crafter_upgraded");
      Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.GRAVE_ADVANCEMENT, Component.translatable("advancements.adventure.level_failed.title"), Component.translatable("advancements.adventure.level_failed.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, true).rewards(AdvancementRewards.Builder.experience(0)).addCriterion("level_failed", PlayerTrigger.TriggerInstance.levelFailed()).save(var2, "mines/player_failed_level");
      AdvancementHolder var9 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.WATER_BUCKET, Component.translatable("advancements.adventure.water_world.title"), Component.translatable("advancements.adventure.water_world.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("complete_level", PlayerTrigger.TriggerInstance.levelCompletedWithEffects(WorldEffects.WATER_WORLD)).save(var2, "mines/complete_water_world_level");
      Advancement.Builder.advancement().parent(var9).display((ItemLike)Items.PUFFERFISH, Component.translatable("advancements.adventure.water_cave.title"), Component.translatable("advancements.adventure.water_cave.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("complete_level", PlayerTrigger.TriggerInstance.levelCompletedWithEffects(WorldEffects.WATER_WORLD, WorldEffects.CAVE_WORLD)).save(var2, "mines/complete_water_cave_level");
      Advancement.Builder.advancement().parent(var9).display((ItemLike)Items.LEATHER_HELMET, Component.translatable("advancements.adventure.deluge.title"), Component.translatable("advancements.adventure.deluge.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("complete_level", PlayerTrigger.TriggerInstance.levelCompletedWithEffects(WorldEffects.ETERNAL_RAIN, WorldEffects.WATER_WORLD)).save(var2, "mines/complete_deluge_level");
   }
}
