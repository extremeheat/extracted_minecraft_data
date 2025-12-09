package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;
import org.jspecify.annotations.Nullable;

public abstract class AbstractMountInventoryScreen<T extends AbstractMountInventoryMenu> extends AbstractContainerScreen<T> {
   protected final int inventoryColumns;
   protected float xMouse;
   protected float yMouse;
   protected LivingEntity mount;

   public AbstractMountInventoryScreen(T var1, Inventory var2, Component var3, int var4, LivingEntity var5) {
      super(var1, var2, var3);
      this.inventoryColumns = var4;
      this.mount = var5;
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(RenderPipelines.GUI_TEXTURED, this.getBackgroundTextureLocation(), var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      if (this.inventoryColumns > 0 && this.getChestSlotsSpriteLocation() != null) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, this.getChestSlotsSpriteLocation(), 90, 54, 0, 0, var5 + 79, var6 + 17, this.inventoryColumns * 18, 54);
      }

      if (this.shouldRenderSaddleSlot()) {
         this.drawSlot(var1, var5 + 7, var6 + 35 - 18);
      }

      if (this.shouldRenderArmorSlot()) {
         this.drawSlot(var1, var5 + 7, var6 + 35);
      }

      InventoryScreen.renderEntityInInventoryFollowsMouse(var1, var5 + 26, var6 + 18, var5 + 78, var6 + 70, 17, 0.25F, this.xMouse, this.yMouse, this.mount);
   }

   protected void drawSlot(GuiGraphics var1, int var2, int var3) {
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)this.getSlotSpriteLocation(), var2, var3, 18, 18);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.xMouse = (float)var2;
      this.yMouse = (float)var3;
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected abstract Identifier getBackgroundTextureLocation();

   protected abstract Identifier getSlotSpriteLocation();

   protected abstract @Nullable Identifier getChestSlotsSpriteLocation();

   protected abstract boolean shouldRenderSaddleSlot();

   protected abstract boolean shouldRenderArmorSlot();
}
