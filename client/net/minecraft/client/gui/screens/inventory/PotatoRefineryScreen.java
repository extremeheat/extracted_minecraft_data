package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.PotatoRefineryMenu;
import net.minecraft.world.inventory.Slot;

public class PotatoRefineryScreen extends AbstractContainerScreen<PotatoRefineryMenu> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/potato_refinery.png");
   private static final ResourceLocation LIT_PROGRESS_SPRITE = new ResourceLocation("container/potato_refinery/lit_progress");
   private static final ResourceLocation BURN_PROGRESS_SPRITE = new ResourceLocation("container/potato_refinery/burn_progress");
   private boolean widthTooNarrow;
   private final ResourceLocation texture;
   private final ResourceLocation litProgressSprite;
   private final ResourceLocation burnProgressSprite;

   public PotatoRefineryScreen(PotatoRefineryMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.imageHeight += 20;
      this.inventoryLabelY += 20;
      this.texture = TEXTURE;
      this.litProgressSprite = LIT_PROGRESS_SPRITE;
      this.burnProgressSprite = BURN_PROGRESS_SPRITE;
   }

   @Override
   public void init() {
      super.init();
      this.widthTooNarrow = this.width < 379;
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
   }

   @Override
   public void containerTick() {
      super.containerTick();
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   @Override
   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      var1.blit(this.texture, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      if (this.menu.isLit()) {
         boolean var7 = true;
         boolean var8 = true;
         int var9 = Mth.ceil(this.menu.getLitProgress() * 11.0F) + 1;
         var1.blitSprite(this.litProgressSprite, 17, 12, 0, 12 - var9, var5 + 51, var6 + 54 + 12 - var9, 17, var9);
      }

      boolean var10 = true;
      int var11 = Mth.ceil(this.menu.getBurnProgress() * 46.0F);
      var1.blitSprite(this.burnProgressSprite, 46, 16, 0, 0, var5 + 69, var6 + 18, var11, 16);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      return super.mouseClicked(var1, var3, var5);
   }

   @Override
   protected void slotClicked(Slot var1, int var2, int var3, ClickType var4) {
      super.slotClicked(var1, var2, var3, var4);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return super.keyPressed(var1, var2, var3);
   }

   @Override
   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      return var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return super.charTyped(var1, var2);
   }
}
