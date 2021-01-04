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
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.Feature;

public class StoryAdvancements implements Consumer<Consumer<Advancement>> {
   public StoryAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.advancement().display((ItemLike)Blocks.GRASS_BLOCK, new TranslatableComponent("advancements.story.root.title", new Object[0]), new TranslatableComponent("advancements.story.root.description", new Object[0]), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, false, false, false).addCriterion("crafting_table", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Blocks.CRAFTING_TABLE)).save(var1, "story/root");
      Advancement var3 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.WOODEN_PICKAXE, new TranslatableComponent("advancements.story.mine_stone.title", new Object[0]), new TranslatableComponent("advancements.story.mine_stone.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("get_stone", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Blocks.COBBLESTONE)).save(var1, "story/mine_stone");
      Advancement var4 = Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.STONE_PICKAXE, new TranslatableComponent("advancements.story.upgrade_tools.title", new Object[0]), new TranslatableComponent("advancements.story.upgrade_tools.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("stone_pickaxe", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.STONE_PICKAXE)).save(var1, "story/upgrade_tools");
      Advancement var5 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.IRON_INGOT, new TranslatableComponent("advancements.story.smelt_iron.title", new Object[0]), new TranslatableComponent("advancements.story.smelt_iron.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("iron", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.IRON_INGOT)).save(var1, "story/smelt_iron");
      Advancement var6 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.IRON_PICKAXE, new TranslatableComponent("advancements.story.iron_tools.title", new Object[0]), new TranslatableComponent("advancements.story.iron_tools.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("iron_pickaxe", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.IRON_PICKAXE)).save(var1, "story/iron_tools");
      Advancement var7 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.DIAMOND, new TranslatableComponent("advancements.story.mine_diamond.title", new Object[0]), new TranslatableComponent("advancements.story.mine_diamond.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("diamond", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.DIAMOND)).save(var1, "story/mine_diamond");
      Advancement var8 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.LAVA_BUCKET, new TranslatableComponent("advancements.story.lava_bucket.title", new Object[0]), new TranslatableComponent("advancements.story.lava_bucket.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("lava_bucket", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.LAVA_BUCKET)).save(var1, "story/lava_bucket");
      Advancement var9 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.IRON_CHESTPLATE, new TranslatableComponent("advancements.story.obtain_armor.title", new Object[0]), new TranslatableComponent("advancements.story.obtain_armor.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("iron_helmet", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.IRON_HELMET)).addCriterion("iron_chestplate", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.IRON_CHESTPLATE)).addCriterion("iron_leggings", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.IRON_LEGGINGS)).addCriterion("iron_boots", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.IRON_BOOTS)).save(var1, "story/obtain_armor");
      Advancement var10 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.ENCHANTED_BOOK, new TranslatableComponent("advancements.story.enchant_item.title", new Object[0]), new TranslatableComponent("advancements.story.enchant_item.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("enchanted_item", (CriterionTriggerInstance)EnchantedItemTrigger.TriggerInstance.enchantedItem()).save(var1, "story/enchant_item");
      Advancement var11 = Advancement.Builder.advancement().parent(var8).display((ItemLike)Blocks.OBSIDIAN, new TranslatableComponent("advancements.story.form_obsidian.title", new Object[0]), new TranslatableComponent("advancements.story.form_obsidian.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("obsidian", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Blocks.OBSIDIAN)).save(var1, "story/form_obsidian");
      Advancement var12 = Advancement.Builder.advancement().parent(var9).display((ItemLike)Items.SHIELD, new TranslatableComponent("advancements.story.deflect_arrow.title", new Object[0]), new TranslatableComponent("advancements.story.deflect_arrow.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("deflected_projectile", (CriterionTriggerInstance)EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().isProjectile(true)).blocked(true))).save(var1, "story/deflect_arrow");
      Advancement var13 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.DIAMOND_CHESTPLATE, new TranslatableComponent("advancements.story.shiny_gear.title", new Object[0]), new TranslatableComponent("advancements.story.shiny_gear.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("diamond_helmet", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.DIAMOND_HELMET)).addCriterion("diamond_chestplate", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.DIAMOND_CHESTPLATE)).addCriterion("diamond_leggings", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.DIAMOND_LEGGINGS)).addCriterion("diamond_boots", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItem(Items.DIAMOND_BOOTS)).save(var1, "story/shiny_gear");
      Advancement var14 = Advancement.Builder.advancement().parent(var11).display((ItemLike)Items.FLINT_AND_STEEL, new TranslatableComponent("advancements.story.enter_the_nether.title", new Object[0]), new TranslatableComponent("advancements.story.enter_the_nether.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_nether", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(DimensionType.NETHER)).save(var1, "story/enter_the_nether");
      Advancement var15 = Advancement.Builder.advancement().parent(var14).display((ItemLike)Items.GOLDEN_APPLE, new TranslatableComponent("advancements.story.cure_zombie_villager.title", new Object[0]), new TranslatableComponent("advancements.story.cure_zombie_villager.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("cured_zombie", (CriterionTriggerInstance)CuredZombieVillagerTrigger.TriggerInstance.curedZombieVillager()).save(var1, "story/cure_zombie_villager");
      Advancement var16 = Advancement.Builder.advancement().parent(var14).display((ItemLike)Items.ENDER_EYE, new TranslatableComponent("advancements.story.follow_ender_eye.title", new Object[0]), new TranslatableComponent("advancements.story.follow_ender_eye.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("in_stronghold", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(Feature.STRONGHOLD))).save(var1, "story/follow_ender_eye");
      Advancement var17 = Advancement.Builder.advancement().parent(var16).display((ItemLike)Blocks.END_STONE, new TranslatableComponent("advancements.story.enter_the_end.title", new Object[0]), new TranslatableComponent("advancements.story.enter_the_end.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_end", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(DimensionType.THE_END)).save(var1, "story/enter_the_end");
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }
}
