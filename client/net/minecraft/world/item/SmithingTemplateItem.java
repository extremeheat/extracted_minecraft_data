package net.minecraft.world.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.armortrim.TrimPattern;

public class SmithingTemplateItem extends Item {
   private static final ChatFormatting TITLE_FORMAT;
   private static final ChatFormatting DESCRIPTION_FORMAT;
   private static final Component INGREDIENTS_TITLE;
   private static final Component APPLIES_TO_TITLE;
   private static final Component NETHERITE_UPGRADE;
   private static final Component ARMOR_TRIM_APPLIES_TO;
   private static final Component ARMOR_TRIM_INGREDIENTS;
   private static final Component ARMOR_TRIM_BASE_SLOT_DESCRIPTION;
   private static final Component ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION;
   private static final Component NETHERITE_UPGRADE_APPLIES_TO;
   private static final Component NETHERITE_UPGRADE_INGREDIENTS;
   private static final Component NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION;
   private static final Component NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION;
   private static final ResourceLocation EMPTY_SLOT_HELMET;
   private static final ResourceLocation EMPTY_SLOT_CHESTPLATE;
   private static final ResourceLocation EMPTY_SLOT_LEGGINGS;
   private static final ResourceLocation EMPTY_SLOT_BOOTS;
   private static final ResourceLocation EMPTY_SLOT_HOE;
   private static final ResourceLocation EMPTY_SLOT_AXE;
   private static final ResourceLocation EMPTY_SLOT_SWORD;
   private static final ResourceLocation EMPTY_SLOT_SHOVEL;
   private static final ResourceLocation EMPTY_SLOT_PICKAXE;
   private static final ResourceLocation EMPTY_SLOT_INGOT;
   private static final ResourceLocation EMPTY_SLOT_REDSTONE_DUST;
   private static final ResourceLocation EMPTY_SLOT_QUARTZ;
   private static final ResourceLocation EMPTY_SLOT_EMERALD;
   private static final ResourceLocation EMPTY_SLOT_DIAMOND;
   private static final ResourceLocation EMPTY_SLOT_LAPIS_LAZULI;
   private static final ResourceLocation EMPTY_SLOT_AMETHYST_SHARD;
   private final Component appliesTo;
   private final Component ingredients;
   private final Component upgradeDescription;
   private final Component baseSlotDescription;
   private final Component additionsSlotDescription;
   private final List<ResourceLocation> baseSlotEmptyIcons;
   private final List<ResourceLocation> additionalSlotEmptyIcons;

   public SmithingTemplateItem(Component var1, Component var2, Component var3, Component var4, Component var5, List<ResourceLocation> var6, List<ResourceLocation> var7, FeatureFlag... var8) {
      super((new Item.Properties()).requiredFeatures(var8));
      this.appliesTo = var1;
      this.ingredients = var2;
      this.upgradeDescription = var3;
      this.baseSlotDescription = var4;
      this.additionsSlotDescription = var5;
      this.baseSlotEmptyIcons = var6;
      this.additionalSlotEmptyIcons = var7;
   }

   public static SmithingTemplateItem createArmorTrimTemplate(ResourceKey<TrimPattern> var0, FeatureFlag... var1) {
      return createArmorTrimTemplate(var0.location(), var1);
   }

   public static SmithingTemplateItem createArmorTrimTemplate(ResourceLocation var0, FeatureFlag... var1) {
      return new SmithingTemplateItem(ARMOR_TRIM_APPLIES_TO, ARMOR_TRIM_INGREDIENTS, Component.translatable(Util.makeDescriptionId("trim_pattern", var0)).withStyle(TITLE_FORMAT), ARMOR_TRIM_BASE_SLOT_DESCRIPTION, ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION, createTrimmableArmorIconList(), createTrimmableMaterialIconList(), var1);
   }

   public static SmithingTemplateItem createNetheriteUpgradeTemplate() {
      return new SmithingTemplateItem(NETHERITE_UPGRADE_APPLIES_TO, NETHERITE_UPGRADE_INGREDIENTS, NETHERITE_UPGRADE, NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION, NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION, createNetheriteUpgradeIconList(), createNetheriteUpgradeMaterialList(), new FeatureFlag[0]);
   }

   private static List<ResourceLocation> createTrimmableArmorIconList() {
      return List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_BOOTS);
   }

   private static List<ResourceLocation> createTrimmableMaterialIconList() {
      return List.of(EMPTY_SLOT_INGOT, EMPTY_SLOT_REDSTONE_DUST, EMPTY_SLOT_LAPIS_LAZULI, EMPTY_SLOT_QUARTZ, EMPTY_SLOT_DIAMOND, EMPTY_SLOT_EMERALD, EMPTY_SLOT_AMETHYST_SHARD);
   }

   private static List<ResourceLocation> createNetheriteUpgradeIconList() {
      return List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_SWORD, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_PICKAXE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_AXE, EMPTY_SLOT_BOOTS, EMPTY_SLOT_HOE, EMPTY_SLOT_SHOVEL);
   }

   private static List<ResourceLocation> createNetheriteUpgradeMaterialList() {
      return List.of(EMPTY_SLOT_INGOT);
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      var3.add(this.upgradeDescription);
      var3.add(CommonComponents.EMPTY);
      var3.add(APPLIES_TO_TITLE);
      var3.add(CommonComponents.space().append(this.appliesTo));
      var3.add(INGREDIENTS_TITLE);
      var3.add(CommonComponents.space().append(this.ingredients));
   }

   public Component getBaseSlotDescription() {
      return this.baseSlotDescription;
   }

   public Component getAdditionSlotDescription() {
      return this.additionsSlotDescription;
   }

   public List<ResourceLocation> getBaseSlotEmptyIcons() {
      return this.baseSlotEmptyIcons;
   }

   public List<ResourceLocation> getAdditionalSlotEmptyIcons() {
      return this.additionalSlotEmptyIcons;
   }

   static {
      TITLE_FORMAT = ChatFormatting.GRAY;
      DESCRIPTION_FORMAT = ChatFormatting.BLUE;
      INGREDIENTS_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.ingredients"))).withStyle(TITLE_FORMAT);
      APPLIES_TO_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.applies_to"))).withStyle(TITLE_FORMAT);
      NETHERITE_UPGRADE = Component.translatable(Util.makeDescriptionId("upgrade", ResourceLocation.withDefaultNamespace("netherite_upgrade"))).withStyle(TITLE_FORMAT);
      ARMOR_TRIM_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.armor_trim.applies_to"))).withStyle(DESCRIPTION_FORMAT);
      ARMOR_TRIM_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.armor_trim.ingredients"))).withStyle(DESCRIPTION_FORMAT);
      ARMOR_TRIM_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.armor_trim.base_slot_description")));
      ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.armor_trim.additions_slot_description")));
      NETHERITE_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.applies_to"))).withStyle(DESCRIPTION_FORMAT);
      NETHERITE_UPGRADE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.ingredients"))).withStyle(DESCRIPTION_FORMAT);
      NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.base_slot_description")));
      NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.additions_slot_description")));
      EMPTY_SLOT_HELMET = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet");
      EMPTY_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate");
      EMPTY_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings");
      EMPTY_SLOT_BOOTS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots");
      EMPTY_SLOT_HOE = ResourceLocation.withDefaultNamespace("item/empty_slot_hoe");
      EMPTY_SLOT_AXE = ResourceLocation.withDefaultNamespace("item/empty_slot_axe");
      EMPTY_SLOT_SWORD = ResourceLocation.withDefaultNamespace("item/empty_slot_sword");
      EMPTY_SLOT_SHOVEL = ResourceLocation.withDefaultNamespace("item/empty_slot_shovel");
      EMPTY_SLOT_PICKAXE = ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe");
      EMPTY_SLOT_INGOT = ResourceLocation.withDefaultNamespace("item/empty_slot_ingot");
      EMPTY_SLOT_REDSTONE_DUST = ResourceLocation.withDefaultNamespace("item/empty_slot_redstone_dust");
      EMPTY_SLOT_QUARTZ = ResourceLocation.withDefaultNamespace("item/empty_slot_quartz");
      EMPTY_SLOT_EMERALD = ResourceLocation.withDefaultNamespace("item/empty_slot_emerald");
      EMPTY_SLOT_DIAMOND = ResourceLocation.withDefaultNamespace("item/empty_slot_diamond");
      EMPTY_SLOT_LAPIS_LAZULI = ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli");
      EMPTY_SLOT_AMETHYST_SHARD = ResourceLocation.withDefaultNamespace("item/empty_slot_amethyst_shard");
   }
}
