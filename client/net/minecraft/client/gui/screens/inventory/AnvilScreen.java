package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AnvilScreen extends ItemCombinerScreen<AnvilMenu> {
   private static final ResourceLocation TEXT_FIELD_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/text_field");
   private static final ResourceLocation TEXT_FIELD_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/text_field_disabled");
   private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/error");
   private static final ResourceLocation ANVIL_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/anvil.png");
   private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");
   private EditBox name;
   private final Player player;

   public AnvilScreen(AnvilMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3, ANVIL_LOCATION);
      this.player = var2.player;
      this.titleLabelX = 60;
   }

   protected void subInit() {
      int var1 = (this.width - this.imageWidth) / 2;
      int var2 = (this.height - this.imageHeight) / 2;
      this.name = new EditBox(this.font, var1 + 62, var2 + 24, 103, 12, Component.translatable("container.repair"));
      this.name.setCanLoseFocus(false);
      this.name.setTextColor(-1);
      this.name.setTextColorUneditable(-1);
      this.name.setBordered(false);
      this.name.setMaxLength(50);
      this.name.setResponder(this::onNameChanged);
      this.name.setValue("");
      this.addWidget(this.name);
      this.name.setEditable(((AnvilMenu)this.menu).getSlot(0).hasItem());
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.name);
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.name.getValue();
      this.init(var1, var2, var3);
      this.name.setValue(var4);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.player.closeContainer();
      }

      return !this.name.keyPressed(var1, var2, var3) && !this.name.canConsumeInput() ? super.keyPressed(var1, var2, var3) : true;
   }

   private void onNameChanged(String var1) {
      Slot var2 = ((AnvilMenu)this.menu).getSlot(0);
      if (var2.hasItem()) {
         String var3 = var1;
         if (!var2.getItem().has(DataComponents.CUSTOM_NAME) && var1.equals(var2.getItem().getHoverName().getString())) {
            var3 = "";
         }

         if (((AnvilMenu)this.menu).setItemName(var3)) {
            this.minecraft.player.connection.send(new ServerboundRenameItemPacket(var3));
         }

      }
   }

   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      super.renderLabels(var1, var2, var3);
      int var4 = ((AnvilMenu)this.menu).getCost();
      if (var4 > 0) {
         int var5 = 8453920;
         Object var6;
         if (var4 >= 40 && !this.minecraft.player.getAbilities().instabuild) {
            var6 = TOO_EXPENSIVE_TEXT;
            var5 = 16736352;
         } else if (!((AnvilMenu)this.menu).getSlot(2).hasItem()) {
            var6 = null;
         } else {
            var6 = Component.translatable("container.repair.cost", var4);
            if (!((AnvilMenu)this.menu).getSlot(2).mayPickup(this.player)) {
               var5 = 16736352;
            }
         }

         if (var6 != null) {
            int var7 = this.imageWidth - 8 - this.font.width((FormattedText)var6) - 2;
            boolean var8 = true;
            var1.fill(var7 - 2, 67, this.imageWidth - 8, 79, 1325400064);
            var1.drawString(this.font, (Component)var6, var7, 69, var5);
         }
      }

   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      super.renderBg(var1, var2, var3, var4);
      var1.blitSprite(((AnvilMenu)this.menu).getSlot(0).hasItem() ? TEXT_FIELD_SPRITE : TEXT_FIELD_DISABLED_SPRITE, this.leftPos + 59, this.topPos + 20, 110, 16);
   }

   public void renderFg(GuiGraphics var1, int var2, int var3, float var4) {
      this.name.render(var1, var2, var3, var4);
   }

   protected void renderErrorIcon(GuiGraphics var1, int var2, int var3) {
      if ((((AnvilMenu)this.menu).getSlot(0).hasItem() || ((AnvilMenu)this.menu).getSlot(1).hasItem()) && !((AnvilMenu)this.menu).getSlot(((AnvilMenu)this.menu).getResultSlot()).hasItem()) {
         var1.blitSprite(ERROR_SPRITE, var2 + 99, var3 + 45, 28, 21);
      }

   }

   public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
      if (var2 == 0) {
         this.name.setValue(var3.isEmpty() ? "" : var3.getHoverName().getString());
         this.name.setEditable(!var3.isEmpty());
         this.setFocused(this.name);
      }

   }
}
