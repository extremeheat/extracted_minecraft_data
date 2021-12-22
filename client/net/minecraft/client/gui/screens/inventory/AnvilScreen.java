package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AnvilScreen extends ItemCombinerScreen<AnvilMenu> {
   private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation("textures/gui/container/anvil.png");
   private static final Component TOO_EXPENSIVE_TEXT = new TranslatableComponent("container.repair.expensive");
   private EditBox name;
   private final Player player;

   public AnvilScreen(AnvilMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3, ANVIL_LOCATION);
      this.player = var2.player;
      this.titleLabelX = 60;
   }

   public void containerTick() {
      super.containerTick();
      this.name.tick();
   }

   protected void subInit() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      int var1 = (this.width - this.imageWidth) / 2;
      int var2 = (this.height - this.imageHeight) / 2;
      this.name = new EditBox(this.font, var1 + 62, var2 + 24, 103, 12, new TranslatableComponent("container.repair"));
      this.name.setCanLoseFocus(false);
      this.name.setTextColor(-1);
      this.name.setTextColorUneditable(-1);
      this.name.setBordered(false);
      this.name.setMaxLength(50);
      this.name.setResponder(this::onNameChanged);
      this.name.setValue("");
      this.addWidget(this.name);
      this.setInitialFocus(this.name);
      this.name.setEditable(false);
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.name.getValue();
      this.init(var1, var2, var3);
      this.name.setValue(var4);
   }

   public void removed() {
      super.removed();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.player.closeContainer();
      }

      return !this.name.keyPressed(var1, var2, var3) && !this.name.canConsumeInput() ? super.keyPressed(var1, var2, var3) : true;
   }

   private void onNameChanged(String var1) {
      if (!var1.isEmpty()) {
         String var2 = var1;
         Slot var3 = ((AnvilMenu)this.menu).getSlot(0);
         if (var3 != null && var3.hasItem() && !var3.getItem().hasCustomHoverName() && var1.equals(var3.getItem().getHoverName().getString())) {
            var2 = "";
         }

         ((AnvilMenu)this.menu).setItemName(var2);
         this.minecraft.player.connection.send((Packet)(new ServerboundRenameItemPacket(var2)));
      }
   }

   protected void renderLabels(PoseStack var1, int var2, int var3) {
      RenderSystem.disableBlend();
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
            var6 = new TranslatableComponent("container.repair.cost", new Object[]{var4});
            if (!((AnvilMenu)this.menu).getSlot(2).mayPickup(this.player)) {
               var5 = 16736352;
            }
         }

         if (var6 != null) {
            int var7 = this.imageWidth - 8 - this.font.width((FormattedText)var6) - 2;
            boolean var8 = true;
            fill(var1, var7 - 2, 67, this.imageWidth - 8, 79, 1325400064);
            this.font.drawShadow(var1, (Component)var6, (float)var7, 69.0F, var5);
         }
      }

   }

   public void renderFg(PoseStack var1, int var2, int var3, float var4) {
      this.name.render(var1, var2, var3, var4);
   }

   public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
      if (var2 == 0) {
         this.name.setValue(var3.isEmpty() ? "" : var3.getHoverName().getString());
         this.name.setEditable(!var3.isEmpty());
         this.setFocused(this.name);
      }

   }
}
