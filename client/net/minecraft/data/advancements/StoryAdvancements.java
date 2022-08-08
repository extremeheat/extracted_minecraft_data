package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.CuredZombieVillagerTrigger;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EnchantedItemTrigger;
import net.minecraft.advancements.critereon.EntityHurtPlayerTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

public class StoryAdvancements implements Consumer<Consumer<Advancement>> {
   public StoryAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.advancement().display((ItemLike)Blocks.GRASS_BLOCK, Component.translatable("advancements.story.root.title"), Component.translatable("advancements.story.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, false, false, false).addCriterion("crafting_table", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE)).save(var1, "story/root");
      Advancement var3 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.WOODEN_PICKAXE, Component.translatable("advancements.story.mine_stone.title"), Component.translatable("advancements.story.mine_stone.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("get_stone", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ItemTags.STONE_TOOL_MATERIALS).build())).save(var1, "story/mine_stone");
      Advancement var4 = Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.STONE_PICKAXE, Component.translatable("advancements.story.upgrade_tools.title"), Component.translatable("advancements.story.upgrade_tools.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("stone_pickaxe", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONE_PICKAXE)).save(var1, "story/upgrade_tools");
      Advancement var5 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.IRON_INGOT, Component.translatable("advancements.story.smelt_iron.title"), Component.translatable("advancements.story.smelt_iron.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("iron", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT)).save(var1, "story/smelt_iron");
      Advancement var6 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.IRON_PICKAXE, Component.translatable("advancements.story.iron_tools.title"), Component.translatable("advancements.story.iron_tools.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("iron_pickaxe", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_PICKAXE)).save(var1, "story/iron_tools");
      Advancement var7 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.DIAMOND, Component.translatable("advancements.story.mine_diamond.title"), Component.translatable("advancements.story.mine_diamond.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("diamond", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND)).save(var1, "story/mine_diamond");
      Advancement var8 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.LAVA_BUCKET, Component.translatable("advancements.story.lava_bucket.title"), Component.translatable("advancements.story.lava_bucket.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("lava_bucket", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.LAVA_BUCKET)).save(var1, "story/lava_bucket");
      Advancement var9 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.IRON_CHESTPLATE, Component.translatable("advancements.story.obtain_armor.title"), Component.translatable("advancements.story.obtain_armor.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("iron_helmet", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_HELMET)).addCriterion("iron_chestplate", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_CHESTPLATE)).addCriterion("iron_leggings", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_LEGGINGS)).addCriterion("iron_boots", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_BOOTS)).save(var1, "story/obtain_armor");
      Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.ENCHANTED_BOOK, Component.translatable("advancements.story.enchant_item.title"), Component.translatable("advancements.story.enchant_item.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("enchanted_item", (CriterionTriggerInstance)EnchantedItemTrigger.TriggerInstance.enchantedItem()).save(var1, "story/enchant_item");
      Advancement var10 = Advancement.Builder.advancement().parent(var8).display((ItemLike)Blocks.OBSIDIAN, Component.translatable("advancements.story.form_obsidian.title"), Component.translatable("advancements.story.form_obsidian.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("obsidian", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.OBSIDIAN)).save(var1, "story/form_obsidian");
      Advancement.Builder.advancement().parent(var9).display((ItemLike)Items.SHIELD, Component.translatable("advancements.story.deflect_arrow.title"), Component.translatable("advancements.story.deflect_arrow.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("deflected_projectile", (CriterionTriggerInstance)EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().isProjectile(true)).blocked(true))).save(var1, "story/deflect_arrow");
      Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.DIAMOND_CHESTPLATE, Component.translatable("advancements.story.shiny_gear.title"), Component.translatable("advancements.story.shiny_gear.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("diamond_helmet", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_HELMET)).addCriterion("diamond_chestplate", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_CHESTPLATE)).addCriterion("diamond_leggings", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_LEGGINGS)).addCriterion("diamond_boots", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_BOOTS)).save(var1, "story/shiny_gear");
      Advancement var11 = Advancement.Builder.advancement().parent(var10).display((ItemLike)Items.FLINT_AND_STEEL, Component.translatable("advancements.story.enter_the_nether.title"), Component.translatable("advancements.story.enter_the_nether.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_nether", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER)).save(var1, "story/enter_the_nether");
      Advancement.Builder.advancement().parent(var11).display((ItemLike)Items.GOLDEN_APPLE, Component.translatable("advancements.story.cure_zombie_villager.title"), Component.translatable("advancements.story.cure_zombie_villager.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("cured_zombie", (CriterionTriggerInstance)CuredZombieVillagerTrigger.TriggerInstance.curedZombieVillager()).save(var1, "story/cure_zombie_villager");
      Advancement var12 = Advancement.Builder.advancement().parent(var11).display((ItemLike)Items.ENDER_EYE, Component.translatable("advancements.story.follow_ender_eye.title"), Component.translatable("advancements.story.follow_ender_eye.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("in_stronghold", (CriterionTriggerInstance)PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(BuiltinStructures.STRONGHOLD))).save(var1, "story/follow_ender_eye");
      Advancement.Builder.advancement().parent(var12).display((ItemLike)Blocks.END_STONE, Component.translatable("advancements.story.enter_the_end.title"), Component.translatable("advancements.story.enter_the_end.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_end", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END)).save(var1, "story/enter_the_end");
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }
}
