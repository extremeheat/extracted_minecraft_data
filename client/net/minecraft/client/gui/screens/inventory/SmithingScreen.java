package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import org.joml.Quaternionf;

public class SmithingScreen extends ItemCombinerScreen<SmithingMenu> {
   private static final ResourceLocation SMITHING_LOCATION = new ResourceLocation("textures/gui/container/smithing.png");
   private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM = new ResourceLocation("item/empty_slot_smithing_template_armor_trim");
   private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE = new ResourceLocation(
      "item/empty_slot_smithing_template_netherite_upgrade"
   );
   private static final Component MISSING_TEMPLATE_TOOLTIP = Component.translatable("container.upgrade.missing_template_tooltip");
   private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
   private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES = List.of(
      EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE
   );
   private static final int TITLE_LABEL_X = 44;
   private static final int TITLE_LABEL_Y = 15;
   private static final int ERROR_ICON_WIDTH = 28;
   private static final int ERROR_ICON_HEIGHT = 21;
   private static final int ERROR_ICON_X = 65;
   private static final int ERROR_ICON_Y = 46;
   private static final int TOOLTIP_WIDTH = 115;
   public static final int ARMOR_STAND_Y_ROT = 210;
   public static final int ARMOR_STAND_X_ROT = 25;
   public static final Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, 0.0F, 3.1415927F);
   public static final int ARMOR_STAND_SCALE = 25;
   public static final int ARMOR_STAND_OFFSET_Y = 75;
   public static final int ARMOR_STAND_OFFSET_X = 141;
   private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(0);
   private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(1);
   private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(2);
   @Nullable
   private ArmorStand armorStandPreview;

   public SmithingScreen(SmithingMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3, SMITHING_LOCATION);
      this.titleLabelX = 44;
      this.titleLabelY = 15;
   }

   @Override
   protected void subInit() {
      this.armorStandPreview = new ArmorStand(this.minecraft.level, 0.0, 0.0, 0.0);
      this.armorStandPreview.setNoBasePlate(true);
      this.armorStandPreview.setShowArms(true);
      this.armorStandPreview.yBodyRot = 210.0F;
      this.armorStandPreview.setXRot(25.0F);
      this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
      this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
      this.updateArmorStandPreview(this.menu.getSlot(3).getItem());
   }

   @Override
   public void containerTick() {
      super.containerTick();
      Optional var1 = this.getTemplateItem();
      this.templateIcon.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
      this.baseIcon.tick(var1.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
      this.additionalIcon.tick(var1.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of()));
   }

   private Optional<SmithingTemplateItem> getTemplateItem() {
      ItemStack var1 = this.menu.getSlot(0).getItem();
      if (!var1.isEmpty()) {
         Item var3 = var1.getItem();
         if (var3 instanceof SmithingTemplateItem var2) {
            return Optional.of((SmithingTemplateItem)var2);
         }
      }

      return Optional.empty();
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderOnboardingTooltips(var1, var2, var3);
   }

   @Override
   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      super.renderBg(var1, var2, var3, var4);
      this.templateIcon.render(this.menu, var1, var2, this.leftPos, this.topPos);
      this.baseIcon.render(this.menu, var1, var2, this.leftPos, this.topPos);
      this.additionalIcon.render(this.menu, var1, var2, this.leftPos, this.topPos);
      InventoryScreen.renderEntityInInventory(var1, this.leftPos + 141, this.topPos + 75, 25, ARMOR_STAND_ANGLE, null, this.armorStandPreview);
   }

   @Override
   public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
      if (var2 == 3) {
         this.updateArmorStandPreview(var3);
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void updateArmorStandPreview(ItemStack var1) {
      if (this.armorStandPreview != null) {
         for(EquipmentSlot var5 : EquipmentSlot.values()) {
            this.armorStandPreview.setItemSlot(var5, ItemStack.EMPTY);
         }

         if (!var1.isEmpty()) {
            ItemStack var6 = var1.copy();
            Item var8 = var1.getItem();
            if (var8 instanceof ArmorItem var7) {
               this.armorStandPreview.setItemSlot(var7.getEquipmentSlot(), var6);
            } else {
               this.armorStandPreview.setItemSlot(EquipmentSlot.OFFHAND, var6);
            }
         }
      }
   }

   @Override
   protected void renderErrorIcon(PoseStack var1, int var2, int var3) {
      if (this.hasRecipeError()) {
         blit(var1, var2 + 65, var3 + 46, this.imageWidth, 0, 28, 21);
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void renderOnboardingTooltips(PoseStack var1, int var2, int var3) {
      Optional var4 = Optional.empty();
      if (this.hasRecipeError() && this.isHovering(65, 46, 28, 21, (double)var2, (double)var3)) {
         var4 = Optional.of(ERROR_TOOLTIP);
      }

      if (this.hoveredSlot != null) {
         ItemStack var5 = this.menu.getSlot(0).getItem();
         ItemStack var6 = this.hoveredSlot.getItem();
         if (var5.isEmpty()) {
            if (this.hoveredSlot.index == 0) {
               var4 = Optional.of(MISSING_TEMPLATE_TOOLTIP);
            }
         } else {
            Item var8 = var5.getItem();
            if (var8 instanceof SmithingTemplateItem var7 && var6.isEmpty()) {
               if (this.hoveredSlot.index == 1) {
                  var4 = Optional.of(var7.getBaseSlotDescription());
               } else if (this.hoveredSlot.index == 2) {
                  var4 = Optional.of(var7.getAdditionSlotDescription());
               }
            }
         }
      }

      var4.ifPresent(var4x -> this.renderTooltip(var1, this.font.split(var4x, 115), var2, var3));
   }

   private boolean hasRecipeError() {
      return this.menu.getSlot(0).hasItem()
         && this.menu.getSlot(1).hasItem()
         && this.menu.getSlot(2).hasItem()
         && !this.menu.getSlot(this.menu.getResultSlot()).hasItem();
   }
}
