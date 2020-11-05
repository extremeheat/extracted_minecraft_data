package net.minecraft.client.gui.components.spectator;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpectatorGui extends GuiComponent implements SpectatorMenuListener {
   private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation SPECTATOR_LOCATION = new ResourceLocation("textures/gui/spectator_widgets.png");
   private final Minecraft minecraft;
   private long lastSelectionTime;
   private SpectatorMenu menu;

   public SpectatorGui(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void onHotbarSelected(int var1) {
      this.lastSelectionTime = Util.getMillis();
      if (this.menu != null) {
         this.menu.selectSlot(var1);
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }

   private float getHotbarAlpha() {
      long var1 = this.lastSelectionTime - Util.getMillis() + 5000L;
      return Mth.clamp((float)var1 / 2000.0F, 0.0F, 1.0F);
   }

   public void renderHotbar(PoseStack var1, float var2) {
      if (this.menu != null) {
         float var3 = this.getHotbarAlpha();
         if (var3 <= 0.0F) {
            this.menu.exit();
         } else {
            int var4 = this.minecraft.getWindow().getGuiScaledWidth() / 2;
            int var5 = this.getBlitOffset();
            this.setBlitOffset(-90);
            int var6 = Mth.floor((float)this.minecraft.getWindow().getGuiScaledHeight() - 22.0F * var3);
            SpectatorPage var7 = this.menu.getCurrentPage();
            this.renderPage(var1, var3, var4, var6, var7);
            this.setBlitOffset(var5);
         }
      }
   }

   protected void renderPage(PoseStack var1, float var2, int var3, int var4, SpectatorPage var5) {
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, var2);
      this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
      this.blit(var1, var3 - 91, var4, 0, 0, 182, 22);
      if (var5.getSelectedSlot() >= 0) {
         this.blit(var1, var3 - 91 - 1 + var5.getSelectedSlot() * 20, var4 - 1, 0, 22, 24, 22);
      }

      for(int var6 = 0; var6 < 9; ++var6) {
         this.renderSlot(var1, var6, this.minecraft.getWindow().getGuiScaledWidth() / 2 - 90 + var6 * 20 + 2, (float)(var4 + 3), var2, var5.getItem(var6));
      }

      RenderSystem.disableRescaleNormal();
      RenderSystem.disableBlend();
   }

   private void renderSlot(PoseStack var1, int var2, int var3, float var4, float var5, SpectatorMenuItem var6) {
      this.minecraft.getTextureManager().bind(SPECTATOR_LOCATION);
      if (var6 != SpectatorMenu.EMPTY_SLOT) {
         int var7 = (int)(var5 * 255.0F);
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)var3, var4, 0.0F);
         float var8 = var6.isEnabled() ? 1.0F : 0.25F;
         RenderSystem.color4f(var8, var8, var8, var5);
         var6.renderIcon(var1, var8, var7);
         RenderSystem.popMatrix();
         if (var7 > 3 && var6.isEnabled()) {
            Component var9 = this.minecraft.options.keyHotbarSlots[var2].getTranslatedKeyMessage();
            this.minecraft.font.drawShadow(var1, var9, (float)(var3 + 19 - 2 - this.minecraft.font.width((FormattedText)var9)), var4 + 6.0F + 3.0F, 16777215 + (var7 << 24));
         }
      }

   }

   public void renderTooltip(PoseStack var1) {
      int var2 = (int)(this.getHotbarAlpha() * 255.0F);
      if (var2 > 3 && this.menu != null) {
         SpectatorMenuItem var3 = this.menu.getSelectedItem();
         Component var4 = var3 == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt() : var3.getName();
         if (var4 != null) {
            int var5 = (this.minecraft.getWindow().getGuiScaledWidth() - this.minecraft.font.width((FormattedText)var4)) / 2;
            int var6 = this.minecraft.getWindow().getGuiScaledHeight() - 35;
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.minecraft.font.drawShadow(var1, var4, (float)var5, (float)var6, 16777215 + (var2 << 24));
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
         }
      }

   }

   public void onSpectatorMenuClosed(SpectatorMenu var1) {
      this.menu = null;
      this.lastSelectionTime = 0L;
   }

   public boolean isMenuActive() {
      return this.menu != null;
   }

   public void onMouseScrolled(double var1) {
      int var3;
      for(var3 = this.menu.getSelectedSlot() + (int)var1; var3 >= 0 && var3 <= 8 && (this.menu.getItem(var3) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(var3).isEnabled()); var3 = (int)((double)var3 + var1)) {
      }

      if (var3 >= 0 && var3 <= 8) {
         this.menu.selectSlot(var3);
         this.lastSelectionTime = Util.getMillis();
      }

   }

   public void onMouseMiddleClick() {
      this.lastSelectionTime = Util.getMillis();
      if (this.isMenuActive()) {
         int var1 = this.menu.getSelectedSlot();
         if (var1 != -1) {
            this.menu.selectSlot(var1);
         }
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }
}
