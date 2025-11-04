package net.minecraft.world.item;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.component.TooltipDisplay;

public class SmithingTemplateItem extends Item {
   private static final ChatFormatting TITLE_FORMAT;
   private static final ChatFormatting DESCRIPTION_FORMAT;
   private static final Component INGREDIENTS_TITLE;
   private static final Component APPLIES_TO_TITLE;
   private static final Component SMITHING_TEMPLATE_SUFFIX;
   private static final Component ARMOR_TRIM_APPLIES_TO;
   private static final Component ARMOR_TRIM_INGREDIENTS;
   private static final Component ARMOR_TRIM_BASE_SLOT_DESCRIPTION;
   private static final Component ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION;
   private static final Component NETHERITE_UPGRADE_APPLIES_TO;
   private static final Component NETHERITE_UPGRADE_INGREDIENTS;
   private static final Component NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION;
   private static final Component NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION;
   private static final Identifier EMPTY_SLOT_HELMET;
   private static final Identifier EMPTY_SLOT_CHESTPLATE;
   private static final Identifier EMPTY_SLOT_LEGGINGS;
   private static final Identifier EMPTY_SLOT_BOOTS;
   private static final Identifier EMPTY_SLOT_HOE;
   private static final Identifier EMPTY_SLOT_AXE;
   private static final Identifier EMPTY_SLOT_SWORD;
   private static final Identifier EMPTY_SLOT_SHOVEL;
   private static final Identifier EMPTY_SLOT_SPEAR;
   private static final Identifier EMPTY_SLOT_PICKAXE;
   private static final Identifier EMPTY_SLOT_INGOT;
   private static final Identifier EMPTY_SLOT_REDSTONE_DUST;
   private static final Identifier EMPTY_SLOT_QUARTZ;
   private static final Identifier EMPTY_SLOT_EMERALD;
   private static final Identifier EMPTY_SLOT_DIAMOND;
   private static final Identifier EMPTY_SLOT_LAPIS_LAZULI;
   private static final Identifier EMPTY_SLOT_AMETHYST_SHARD;
   private static final Identifier EMPTY_SLOT_NAUTILUS_ARMOR;
   private final Component appliesTo;
   private final Component ingredients;
   private final Component baseSlotDescription;
   private final Component additionsSlotDescription;
   private final List<Identifier> baseSlotEmptyIcons;
   private final List<Identifier> additionalSlotEmptyIcons;

   public SmithingTemplateItem(Component var1, Component var2, Component var3, Component var4, List<Identifier> var5, List<Identifier> var6, Item.Properties var7) {
      super(var7);
      this.appliesTo = var1;
      this.ingredients = var2;
      this.baseSlotDescription = var3;
      this.additionsSlotDescription = var4;
      this.baseSlotEmptyIcons = var5;
      this.additionalSlotEmptyIcons = var6;
   }

   public static SmithingTemplateItem createArmorTrimTemplate(Item.Properties var0) {
      return new SmithingTemplateItem(ARMOR_TRIM_APPLIES_TO, ARMOR_TRIM_INGREDIENTS, ARMOR_TRIM_BASE_SLOT_DESCRIPTION, ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION, createTrimmableArmorIconList(), createTrimmableMaterialIconList(), var0);
   }

   public static SmithingTemplateItem createNetheriteUpgradeTemplate(Item.Properties var0) {
      return new SmithingTemplateItem(NETHERITE_UPGRADE_APPLIES_TO, NETHERITE_UPGRADE_INGREDIENTS, NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION, NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION, createNetheriteUpgradeIconList(), createNetheriteUpgradeMaterialList(), var0);
   }

   private static List<Identifier> createTrimmableArmorIconList() {
      return List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_BOOTS);
   }

   private static List<Identifier> createTrimmableMaterialIconList() {
      return List.of(EMPTY_SLOT_INGOT, EMPTY_SLOT_REDSTONE_DUST, EMPTY_SLOT_LAPIS_LAZULI, EMPTY_SLOT_QUARTZ, EMPTY_SLOT_DIAMOND, EMPTY_SLOT_EMERALD, EMPTY_SLOT_AMETHYST_SHARD);
   }

   private static List<Identifier> createNetheriteUpgradeIconList() {
      return List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_SWORD, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_PICKAXE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_AXE, EMPTY_SLOT_BOOTS, EMPTY_SLOT_HOE, EMPTY_SLOT_SHOVEL, EMPTY_SLOT_NAUTILUS_ARMOR, EMPTY_SLOT_SPEAR);
   }

   private static List<Identifier> createNetheriteUpgradeMaterialList() {
      return List.of(EMPTY_SLOT_INGOT);
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, TooltipDisplay var3, Consumer<Component> var4, TooltipFlag var5) {
      var4.accept(SMITHING_TEMPLATE_SUFFIX);
      var4.accept(CommonComponents.EMPTY);
      var4.accept(APPLIES_TO_TITLE);
      var4.accept(CommonComponents.space().append(this.appliesTo));
      var4.accept(INGREDIENTS_TITLE);
      var4.accept(CommonComponents.space().append(this.ingredients));
   }

   public Component getBaseSlotDescription() {
      return this.baseSlotDescription;
   }

   public Component getAdditionSlotDescription() {
      return this.additionsSlotDescription;
   }

   public List<Identifier> getBaseSlotEmptyIcons() {
      return this.baseSlotEmptyIcons;
   }

   public List<Identifier> getAdditionalSlotEmptyIcons() {
      return this.additionalSlotEmptyIcons;
   }

   static {
      TITLE_FORMAT = ChatFormatting.GRAY;
      DESCRIPTION_FORMAT = ChatFormatting.BLUE;
      INGREDIENTS_TITLE = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.ingredients"))).withStyle(TITLE_FORMAT);
      APPLIES_TO_TITLE = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.applies_to"))).withStyle(TITLE_FORMAT);
      SMITHING_TEMPLATE_SUFFIX = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template"))).withStyle(TITLE_FORMAT);
      ARMOR_TRIM_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.armor_trim.applies_to"))).withStyle(DESCRIPTION_FORMAT);
      ARMOR_TRIM_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.armor_trim.ingredients"))).withStyle(DESCRIPTION_FORMAT);
      ARMOR_TRIM_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.armor_trim.base_slot_description")));
      ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.armor_trim.additions_slot_description")));
      NETHERITE_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.netherite_upgrade.applies_to"))).withStyle(DESCRIPTION_FORMAT);
      NETHERITE_UPGRADE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.netherite_upgrade.ingredients"))).withStyle(DESCRIPTION_FORMAT);
      NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.netherite_upgrade.base_slot_description")));
      NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.netherite_upgrade.additions_slot_description")));
      EMPTY_SLOT_HELMET = Identifier.withDefaultNamespace("container/slot/helmet");
      EMPTY_SLOT_CHESTPLATE = Identifier.withDefaultNamespace("container/slot/chestplate");
      EMPTY_SLOT_LEGGINGS = Identifier.withDefaultNamespace("container/slot/leggings");
      EMPTY_SLOT_BOOTS = Identifier.withDefaultNamespace("container/slot/boots");
      EMPTY_SLOT_HOE = Identifier.withDefaultNamespace("container/slot/hoe");
      EMPTY_SLOT_AXE = Identifier.withDefaultNamespace("container/slot/axe");
      EMPTY_SLOT_SWORD = Identifier.withDefaultNamespace("container/slot/sword");
      EMPTY_SLOT_SHOVEL = Identifier.withDefaultNamespace("container/slot/shovel");
      EMPTY_SLOT_SPEAR = Identifier.withDefaultNamespace("container/slot/spear");
      EMPTY_SLOT_PICKAXE = Identifier.withDefaultNamespace("container/slot/pickaxe");
      EMPTY_SLOT_INGOT = Identifier.withDefaultNamespace("container/slot/ingot");
      EMPTY_SLOT_REDSTONE_DUST = Identifier.withDefaultNamespace("container/slot/redstone_dust");
      EMPTY_SLOT_QUARTZ = Identifier.withDefaultNamespace("container/slot/quartz");
      EMPTY_SLOT_EMERALD = Identifier.withDefaultNamespace("container/slot/emerald");
      EMPTY_SLOT_DIAMOND = Identifier.withDefaultNamespace("container/slot/diamond");
      EMPTY_SLOT_LAPIS_LAZULI = Identifier.withDefaultNamespace("container/slot/lapis_lazuli");
      EMPTY_SLOT_AMETHYST_SHARD = Identifier.withDefaultNamespace("container/slot/amethyst_shard");
      EMPTY_SLOT_NAUTILUS_ARMOR = Identifier.withDefaultNamespace("container/slot/nautilus_armor");
   }
}
