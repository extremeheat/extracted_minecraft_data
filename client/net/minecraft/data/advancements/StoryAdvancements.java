package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.CuredZombieVillagerTrigger;
import net.minecraft.advancements.criterion.DamagePredicate;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.advancements.criterion.EnchantedItemTrigger;
import net.minecraft.advancements.criterion.EntityHurtPlayerTrigger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.dimension.DimensionType;

public class StoryAdvancements implements Consumer<Consumer<Advancement>> {
   public StoryAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.func_200278_a().func_203902_a(Blocks.field_196658_i, new TextComponentTranslation("advancements.story.root.title", new Object[0]), new TextComponentTranslation("advancements.story.root.description", new Object[0]), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, false, false, false).func_200275_a("crafting_table", InventoryChangeTrigger.Instance.func_203922_a(Blocks.field_150462_ai)).func_203904_a(var1, "story/root");
      Advancement var3 = Advancement.Builder.func_200278_a().func_203905_a(var2).func_203902_a(Items.field_151039_o, new TextComponentTranslation("advancements.story.mine_stone.title", new Object[0]), new TextComponentTranslation("advancements.story.mine_stone.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("get_stone", InventoryChangeTrigger.Instance.func_203922_a(Blocks.field_150347_e)).func_203904_a(var1, "story/mine_stone");
      Advancement var4 = Advancement.Builder.func_200278_a().func_203905_a(var3).func_203902_a(Items.field_151050_s, new TextComponentTranslation("advancements.story.upgrade_tools.title", new Object[0]), new TextComponentTranslation("advancements.story.upgrade_tools.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("stone_pickaxe", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151050_s)).func_203904_a(var1, "story/upgrade_tools");
      Advancement var5 = Advancement.Builder.func_200278_a().func_203905_a(var4).func_203902_a(Items.field_151042_j, new TextComponentTranslation("advancements.story.smelt_iron.title", new Object[0]), new TextComponentTranslation("advancements.story.smelt_iron.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("iron", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151042_j)).func_203904_a(var1, "story/smelt_iron");
      Advancement var6 = Advancement.Builder.func_200278_a().func_203905_a(var5).func_203902_a(Items.field_151035_b, new TextComponentTranslation("advancements.story.iron_tools.title", new Object[0]), new TextComponentTranslation("advancements.story.iron_tools.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("iron_pickaxe", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151035_b)).func_203904_a(var1, "story/iron_tools");
      Advancement var7 = Advancement.Builder.func_200278_a().func_203905_a(var6).func_203902_a(Items.field_151045_i, new TextComponentTranslation("advancements.story.mine_diamond.title", new Object[0]), new TextComponentTranslation("advancements.story.mine_diamond.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("diamond", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151045_i)).func_203904_a(var1, "story/mine_diamond");
      Advancement var8 = Advancement.Builder.func_200278_a().func_203905_a(var5).func_203902_a(Items.field_151129_at, new TextComponentTranslation("advancements.story.lava_bucket.title", new Object[0]), new TextComponentTranslation("advancements.story.lava_bucket.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("lava_bucket", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151129_at)).func_203904_a(var1, "story/lava_bucket");
      Advancement var9 = Advancement.Builder.func_200278_a().func_203905_a(var5).func_203902_a(Items.field_151030_Z, new TextComponentTranslation("advancements.story.obtain_armor.title", new Object[0]), new TextComponentTranslation("advancements.story.obtain_armor.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200270_a(RequirementsStrategy.OR).func_200275_a("iron_helmet", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151028_Y)).func_200275_a("iron_chestplate", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151030_Z)).func_200275_a("iron_leggings", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151165_aa)).func_200275_a("iron_boots", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151167_ab)).func_203904_a(var1, "story/obtain_armor");
      Advancement var10 = Advancement.Builder.func_200278_a().func_203905_a(var7).func_203902_a(Items.field_151134_bR, new TextComponentTranslation("advancements.story.enchant_item.title", new Object[0]), new TextComponentTranslation("advancements.story.enchant_item.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("enchanted_item", EnchantedItemTrigger.Instance.func_203918_c()).func_203904_a(var1, "story/enchant_item");
      Advancement var11 = Advancement.Builder.func_200278_a().func_203905_a(var8).func_203902_a(Blocks.field_150343_Z, new TextComponentTranslation("advancements.story.form_obsidian.title", new Object[0]), new TextComponentTranslation("advancements.story.form_obsidian.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("obsidian", InventoryChangeTrigger.Instance.func_203922_a(Blocks.field_150343_Z)).func_203904_a(var1, "story/form_obsidian");
      Advancement var12 = Advancement.Builder.func_200278_a().func_203905_a(var9).func_203902_a(Items.field_185159_cQ, new TextComponentTranslation("advancements.story.deflect_arrow.title", new Object[0]), new TextComponentTranslation("advancements.story.deflect_arrow.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("deflected_projectile", EntityHurtPlayerTrigger.Instance.func_203921_a(DamagePredicate.Builder.func_203971_a().func_203969_a(DamageSourcePredicate.Builder.func_203981_a().func_203978_a(true)).func_203968_a(true))).func_203904_a(var1, "story/deflect_arrow");
      Advancement var13 = Advancement.Builder.func_200278_a().func_203905_a(var7).func_203902_a(Items.field_151163_ad, new TextComponentTranslation("advancements.story.shiny_gear.title", new Object[0]), new TextComponentTranslation("advancements.story.shiny_gear.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200270_a(RequirementsStrategy.OR).func_200275_a("diamond_helmet", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151161_ac)).func_200275_a("diamond_chestplate", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151163_ad)).func_200275_a("diamond_leggings", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151173_ae)).func_200275_a("diamond_boots", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151175_af)).func_203904_a(var1, "story/shiny_gear");
      Advancement var14 = Advancement.Builder.func_200278_a().func_203905_a(var11).func_203902_a(Items.field_151033_d, new TextComponentTranslation("advancements.story.enter_the_nether.title", new Object[0]), new TextComponentTranslation("advancements.story.enter_the_nether.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("entered_nether", ChangeDimensionTrigger.Instance.func_203911_a(DimensionType.NETHER)).func_203904_a(var1, "story/enter_the_nether");
      Advancement var15 = Advancement.Builder.func_200278_a().func_203905_a(var14).func_203902_a(Items.field_151153_ao, new TextComponentTranslation("advancements.story.cure_zombie_villager.title", new Object[0]), new TextComponentTranslation("advancements.story.cure_zombie_villager.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).func_200275_a("cured_zombie", CuredZombieVillagerTrigger.Instance.func_203916_c()).func_203904_a(var1, "story/cure_zombie_villager");
      Advancement var16 = Advancement.Builder.func_200278_a().func_203905_a(var14).func_203902_a(Items.field_151061_bv, new TextComponentTranslation("advancements.story.follow_ender_eye.title", new Object[0]), new TextComponentTranslation("advancements.story.follow_ender_eye.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("in_stronghold", PositionTrigger.Instance.func_203932_a(LocationPredicate.func_204007_a("Stronghold"))).func_203904_a(var1, "story/follow_ender_eye");
      Advancement var17 = Advancement.Builder.func_200278_a().func_203905_a(var16).func_203902_a(Blocks.field_150377_bs, new TextComponentTranslation("advancements.story.enter_the_end.title", new Object[0]), new TextComponentTranslation("advancements.story.enter_the_end.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("entered_end", ChangeDimensionTrigger.Instance.func_203911_a(DimensionType.THE_END)).func_203904_a(var1, "story/enter_the_end");
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }
}
