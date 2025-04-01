package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityHurtPlayerTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class VanillaStoryAdvancements implements AdvancementSubProvider {
   public VanillaStoryAdvancements() {
      super();
   }

   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      HolderLookup.RegistryLookup var3 = var1.lookupOrThrow(Registries.ITEM);
      AdvancementHolder var4 = Advancement.Builder.advancement().display((ItemLike)Blocks.GRASS_BLOCK, Component.translatable("advancements.story.root.title"), Component.translatable("advancements.story.root.description"), ResourceLocation.withDefaultNamespace("gui/advancements/backgrounds/stone"), AdvancementType.TASK, false, false, false).addCriterion("crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE)).save(var2, "story/root");
      AdvancementHolder var5 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.WOODEN_PICKAXE, Component.translatable("advancements.story.mine_stone.title"), Component.translatable("advancements.story.mine_stone.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("get_stone", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(var3, (TagKey)ItemTags.STONE_TOOL_MATERIALS))).save(var2, "story/mine_stone");
      AdvancementHolder var6 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.STONE_PICKAXE, Component.translatable("advancements.story.upgrade_tools.title"), Component.translatable("advancements.story.upgrade_tools.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("stone_pickaxe", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONE_PICKAXE)).save(var2, "story/upgrade_tools");
      AdvancementHolder var7 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.IRON_INGOT, Component.translatable("advancements.story.smelt_iron.title"), Component.translatable("advancements.story.smelt_iron.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("iron", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT)).save(var2, "story/smelt_iron");
      AdvancementHolder var8 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.IRON_PICKAXE, Component.translatable("advancements.story.iron_tools.title"), Component.translatable("advancements.story.iron_tools.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("iron_pickaxe", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_PICKAXE)).save(var2, "story/iron_tools");
      AdvancementHolder var9 = Advancement.Builder.advancement().parent(var8).display((ItemLike)Items.DIAMOND, Component.translatable("advancements.story.mine_diamond.title"), Component.translatable("advancements.story.mine_diamond.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("diamond", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND)).save(var2, "story/mine_diamond");
      AdvancementHolder var10 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.IRON_CHESTPLATE, Component.translatable("advancements.story.obtain_armor.title"), Component.translatable("advancements.story.obtain_armor.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("iron_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_HELMET)).addCriterion("iron_chestplate", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_CHESTPLATE)).addCriterion("iron_leggings", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_LEGGINGS)).addCriterion("iron_boots", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_BOOTS)).save(var2, "story/obtain_armor");
      Advancement.Builder.advancement().parent(var10).display((ItemLike)Items.SHIELD, Component.translatable("advancements.story.deflect_arrow.title"), Component.translatable("advancements.story.deflect_arrow.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("deflected_projectile", EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))).blocked(true))).save(var2, "story/deflect_arrow");
      Advancement.Builder.advancement().parent(var9).display((ItemLike)Items.DIAMOND_CHESTPLATE, Component.translatable("advancements.story.shiny_gear.title"), Component.translatable("advancements.story.shiny_gear.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("diamond_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_HELMET)).addCriterion("diamond_chestplate", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_CHESTPLATE)).addCriterion("diamond_leggings", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_LEGGINGS)).addCriterion("diamond_boots", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_BOOTS)).save(var2, "story/shiny_gear");
      AdvancementHolder var11 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.EXIT_EYE, Component.translatable("advancements.story.follow_exit_eye.title"), Component.translatable("advancements.story.follow_exit_eye.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("has_eye", InventoryChangeTrigger.TriggerInstance.hasItems(Items.EXIT_EYE)).save(var2, "story/follow_exit_eye");
   }
}
