package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SmithingScreen extends ItemCombinerScreen<SmithingMenu> {
   private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/smithing/error");
   private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM = ResourceLocation.withDefaultNamespace("container/slot/smithing_template_armor_trim");
   private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE = ResourceLocation.withDefaultNamespace("container/slot/smithing_template_netherite_upgrade");
   private static final Component MISSING_TEMPLATE_TOOLTIP = Component.translatable("container.upgrade.missing_template_tooltip");
   private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
   private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES;
   private static final int TITLE_LABEL_X = 44;
   private static final int TITLE_LABEL_Y = 15;
   private static final int ERROR_ICON_WIDTH = 28;
   private static final int ERROR_ICON_HEIGHT = 21;
   private static final int ERROR_ICON_X = 65;
   private static final int ERROR_ICON_Y = 46;
   private static final int TOOLTIP_WIDTH = 115;
   private static final int ARMOR_STAND_Y_ROT = 210;
   private static final int ARMOR_STAND_X_ROT = 25;
   private static final Vector3f ARMOR_STAND_TRANSLATION;
   private static final Quaternionf ARMOR_STAND_ANGLE;
   private static final int ARMOR_STAND_SCALE = 25;
   private static final int ARMOR_STAND_LEFT = 121;
   private static final int ARMOR_STAND_TOP = 20;
   private static final int ARMOR_STAND_RIGHT = 161;
   private static final int ARMOR_STAND_BOTTOM = 80;
   private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(0);
   private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(1);
   private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(2);
   private final ArmorStandRenderState armorStandPreview = new ArmorStandRenderState();

   public SmithingScreen(SmithingMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3, ResourceLocation.withDefaultNamespace("textures/gui/container/smithing.png"));
      this.titleLabelX = 44;
      this.titleLabelY = 15;
      this.armorStandPreview.entityType = EntityType.ARMOR_STAND;
      this.armorStandPreview.showBasePlate = false;
      this.armorStandPreview.showArms = true;
      this.armorStandPreview.xRot = 25.0F;
      this.armorStandPreview.bodyRot = 210.0F;
   }

   protected void subInit() {
      this.updateArmorStandPreview(((SmithingMenu)this.menu).getSlot(3).getItem());
   }

   public void containerTick() {
      super.containerTick();
      Optional var1 = this.getTemplateItem();
      this.templateIcon.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
      this.baseIcon.tick((List)var1.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
      this.additionalIcon.tick((List)var1.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of()));
   }

   private Optional<SmithingTemplateItem> getTemplateItem() {
      ItemStack var1 = ((SmithingMenu)this.menu).getSlot(0).getItem();
      if (!var1.isEmpty()) {
         Item var3 = var1.getItem();
         if (var3 instanceof SmithingTemplateItem) {
            SmithingTemplateItem var2 = (SmithingTemplateItem)var3;
            return Optional.of(var2);
         }
      }

      return Optional.empty();
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderOnboardingTooltips(var1, var2, var3);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      super.renderBg(var1, var2, var3, var4);
      this.templateIcon.render(this.menu, var1, var2, this.leftPos, this.topPos);
      this.baseIcon.render(this.menu, var1, var2, this.leftPos, this.topPos);
      this.additionalIcon.render(this.menu, var1, var2, this.leftPos, this.topPos);
      int var5 = this.leftPos + 121;
      int var6 = this.topPos + 20;
      int var7 = this.leftPos + 161;
      int var8 = this.topPos + 80;
      var1.submitEntityRenderState(this.armorStandPreview, 25.0F, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ANGLE, (Quaternionf)null, var5, var6, var7, var8);
   }

   public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
      if (var2 == 3) {
         this.updateArmorStandPreview(var3);
      }

   }

   private void updateArmorStandPreview(ItemStack var1) {
      this.armorStandPreview.leftHandItemStack = ItemStack.EMPTY;
      this.armorStandPreview.leftHandItemState.clear();
      this.armorStandPreview.headEquipment = ItemStack.EMPTY;
      this.armorStandPreview.headItem.clear();
      this.armorStandPreview.chestEquipment = ItemStack.EMPTY;
      this.armorStandPreview.legsEquipment = ItemStack.EMPTY;
      this.armorStandPreview.feetEquipment = ItemStack.EMPTY;
      if (!var1.isEmpty()) {
         Equippable var2 = (Equippable)var1.get(DataComponents.EQUIPPABLE);
         EquipmentSlot var3 = var2 != null ? var2.slot() : null;
         ItemModelResolver var4 = this.minecraft.getItemModelResolver();
         byte var6 = 0;
         //$FF: var6->value
         //0->HEAD
         //1->CHEST
         //2->LEGS
         //3->FEET
         switch (var3.enumSwitch<invokedynamic>(var3, var6)) {
            case -1:
            default:
               this.armorStandPreview.leftHandItemStack = var1.copy();
               var4.updateForTopItem(this.armorStandPreview.leftHandItemState, var1, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, (Level)null, (ItemOwner)null, 0);
               break;
            case 0:
               if (HumanoidArmorLayer.shouldRender(var1, EquipmentSlot.HEAD)) {
                  this.armorStandPreview.headEquipment = var1.copy();
               } else {
                  var4.updateForTopItem(this.armorStandPreview.headItem, var1, ItemDisplayContext.HEAD, (Level)null, (ItemOwner)null, 0);
               }
               break;
            case 1:
               this.armorStandPreview.chestEquipment = var1.copy();
               break;
            case 2:
               this.armorStandPreview.legsEquipment = var1.copy();
               break;
            case 3:
               this.armorStandPreview.feetEquipment = var1.copy();
         }
      }

   }

   protected void renderErrorIcon(GuiGraphics var1, int var2, int var3) {
      if (this.hasRecipeError()) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ERROR_SPRITE, var2 + 65, var3 + 46, 28, 21);
      }

   }

   private void renderOnboardingTooltips(GuiGraphics var1, int var2, int var3) {
      Optional var4 = Optional.empty();
      if (this.hasRecipeError() && this.isHovering(65, 46, 28, 21, (double)var2, (double)var3)) {
         var4 = Optional.of(ERROR_TOOLTIP);
      }

      if (this.hoveredSlot != null) {
         ItemStack var5 = ((SmithingMenu)this.menu).getSlot(0).getItem();
         ItemStack var6 = this.hoveredSlot.getItem();
         if (var5.isEmpty()) {
            if (this.hoveredSlot.index == 0) {
               var4 = Optional.of(MISSING_TEMPLATE_TOOLTIP);
            }
         } else {
            Item var8 = var5.getItem();
            if (var8 instanceof SmithingTemplateItem) {
               SmithingTemplateItem var7 = (SmithingTemplateItem)var8;
               if (var6.isEmpty()) {
                  if (this.hoveredSlot.index == 1) {
                     var4 = Optional.of(var7.getBaseSlotDescription());
                  } else if (this.hoveredSlot.index == 2) {
                     var4 = Optional.of(var7.getAdditionSlotDescription());
                  }
               }
            }
         }
      }

      var4.ifPresent((var4x) -> var1.setTooltipForNextFrame(this.font, this.font.split(var4x, 115), var2, var3));
   }

   private boolean hasRecipeError() {
      return ((SmithingMenu)this.menu).hasRecipeError();
   }

   static {
      EMPTY_SLOT_SMITHING_TEMPLATES = List.of(EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE);
      ARMOR_STAND_TRANSLATION = new Vector3f(0.0F, 1.0F, 0.0F);
      ARMOR_STAND_ANGLE = (new Quaternionf()).rotationXYZ(0.43633232F, 0.0F, 3.1415927F);
   }
}
