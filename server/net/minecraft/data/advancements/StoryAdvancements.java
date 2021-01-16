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
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class StoryAdvancements implements Consumer<Consumer<Advancement>> {
   public StoryAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.advancement().display((ItemLike)Blocks.GRASS_BLOCK, new TranslatableComponent("advancements.story.root.title"), new TranslatableComponent("advancements.story.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, false, false, false).addCriterion("crafting_table", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE)).save(var1, "story/root");
      Advancement var3 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.WOODEN_PICKAXE, new TranslatableComponent("advancements.story.mine_stone.title"), new TranslatableComponent("advancements.story.mine_stone.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("get_stone", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of((Tag)ItemTags.STONE_TOOL_MATERIALS).build())).save(var1, "story/mine_stone");
      Advancement var4 = Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.STONE_PICKAXE, new TranslatableComponent("advancements.story.upgrade_tools.title"), new TranslatableComponent("advancements.story.upgrade_tools.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("stone_pickaxe", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONE_PICKAXE)).save(var1, "story/upgrade_tools");
      Advancement var5 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.IRON_INGOT, new TranslatableComponent("advancements.story.smelt_iron.title"), new TranslatableComponent("advancements.story.smelt_iron.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("iron", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT)).save(var1, "story/smelt_iron");
      Advancement var6 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.IRON_PICKAXE, new TranslatableComponent("advancements.story.iron_tools.title"), new TranslatableComponent("advancements.story.iron_tools.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("iron_pickaxe", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_PICKAXE)).save(var1, "story/iron_tools");
      Advancement var7 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.DIAMOND, new TranslatableComponent("advancements.story.mine_diamond.title"), new TranslatableComponent("advancements.story.mine_diamond.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("diamond", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND)).save(var1, "story/mine_diamond");
      Advancement var8 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.LAVA_BUCKET, new TranslatableComponent("advancements.story.lava_bucket.title"), new TranslatableComponent("advancements.story.lava_bucket.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("lava_bucket", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.LAVA_BUCKET)).save(var1, "story/lava_bucket");
      Advancement var9 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.IRON_CHESTPLATE, new TranslatableComponent("advancements.story.obtain_armor.title"), new TranslatableComponent("advancements.story.obtain_armor.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("iron_helmet", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_HELMET)).addCriterion("iron_chestplate", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_CHESTPLATE)).addCriterion("iron_leggings", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_LEGGINGS)).addCriterion("iron_boots", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_BOOTS)).save(var1, "story/obtain_armor");
      Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.ENCHANTED_BOOK, new TranslatableComponent("advancements.story.enchant_item.title"), new TranslatableComponent("advancements.story.enchant_item.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("enchanted_item", (CriterionTriggerInstance)EnchantedItemTrigger.TriggerInstance.enchantedItem()).save(var1, "story/enchant_item");
      Advancement var10 = Advancement.Builder.advancement().parent(var8).display((ItemLike)Blocks.OBSIDIAN, new TranslatableComponent("advancements.story.form_obsidian.title"), new TranslatableComponent("advancements.story.form_obsidian.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("obsidian", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.OBSIDIAN)).save(var1, "story/form_obsidian");
      Advancement.Builder.advancement().parent(var9).display((ItemLike)Items.SHIELD, new TranslatableComponent("advancements.story.deflect_arrow.title"), new TranslatableComponent("advancements.story.deflect_arrow.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("deflected_projectile", (CriterionTriggerInstance)EntityHurtPlayerTrigger.TriggerInstance.entityHurtPlayer(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().isProjectile(true)).blocked(true))).save(var1, "story/deflect_arrow");
      Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.DIAMOND_CHESTPLATE, new TranslatableComponent("advancements.story.shiny_gear.title"), new TranslatableComponent("advancements.story.shiny_gear.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("diamond_helmet", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_HELMET)).addCriterion("diamond_chestplate", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_CHESTPLATE)).addCriterion("diamond_leggings", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_LEGGINGS)).addCriterion("diamond_boots", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND_BOOTS)).save(var1, "story/shiny_gear");
      Advancement var11 = Advancement.Builder.advancement().parent(var10).display((ItemLike)Items.FLINT_AND_STEEL, new TranslatableComponent("advancements.story.enter_the_nether.title"), new TranslatableComponent("advancements.story.enter_the_nether.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_nether", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER)).save(var1, "story/enter_the_nether");
      Advancement.Builder.advancement().parent(var11).display((ItemLike)Items.GOLDEN_APPLE, new TranslatableComponent("advancements.story.cure_zombie_villager.title"), new TranslatableComponent("advancements.story.cure_zombie_villager.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("cured_zombie", (CriterionTriggerInstance)CuredZombieVillagerTrigger.TriggerInstance.curedZombieVillager()).save(var1, "story/cure_zombie_villager");
      Advancement var12 = Advancement.Builder.advancement().parent(var11).display((ItemLike)Items.ENDER_EYE, new TranslatableComponent("advancements.story.follow_ender_eye.title"), new TranslatableComponent("advancements.story.follow_ender_eye.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("in_stronghold", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(StructureFeature.STRONGHOLD))).save(var1, "story/follow_ender_eye");
      Advancement.Builder.advancement().parent(var12).display((ItemLike)Blocks.END_STONE, new TranslatableComponent("advancements.story.enter_the_end.title"), new TranslatableComponent("advancements.story.enter_the_end.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("entered_end", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END)).save(var1, "story/enter_the_end");
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }
}
