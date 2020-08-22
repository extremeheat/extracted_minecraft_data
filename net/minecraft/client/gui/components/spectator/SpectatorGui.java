package net.minecraft.client.gui.components.spectator;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpectatorGui extends GuiComponent implements SpectatorMenuListener {
   private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation SPECTATOR_LOCATION = new ResourceLocation("textures/gui/spectator_widgets.png");
   private final Minecraft minecraft;
   private long lastSelectionTime;
   private SpectatorMenu menu;

   public SpectatorGui(Minecraft var1) {
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

   public void renderHotbar(float var1) {
      if (this.menu != null) {
         float var2 = this.getHotbarAlpha();
         if (var2 <= 0.0F) {
            this.menu.exit();
         } else {
            int var3 = this.minecraft.getWindow().getGuiScaledWidth() / 2;
            int var4 = this.getBlitOffset();
            this.setBlitOffset(-90);
            int var5 = Mth.floor((float)this.minecraft.getWindow().getGuiScaledHeight() - 22.0F * var2);
            SpectatorPage var6 = this.menu.getCurrentPage();
            this.renderPage(var2, var3, var5, var6);
            this.setBlitOffset(var4);
         }
      }
   }

   protected void renderPage(float var1, int var2, int var3, SpectatorPage var4) {
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, var1);
      this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
      this.blit(var2 - 91, var3, 0, 0, 182, 22);
      if (var4.getSelectedSlot() >= 0) {
         this.blit(var2 - 91 - 1 + var4.getSelectedSlot() * 20, var3 - 1, 0, 22, 24, 22);
      }

      for(int var5 = 0; var5 < 9; ++var5) {
         this.renderSlot(var5, this.minecraft.getWindow().getGuiScaledWidth() / 2 - 90 + var5 * 20 + 2, (float)(var3 + 3), var1, var4.getItem(var5));
      }

      RenderSystem.disableRescaleNormal();
      RenderSystem.disableBlend();
   }

   private void renderSlot(int var1, int var2, float var3, float var4, SpectatorMenuItem var5) {
      this.minecraft.getTextureManager().bind(SPECTATOR_LOCATION);
      if (var5 != SpectatorMenu.EMPTY_SLOT) {
         int var6 = (int)(var4 * 255.0F);
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)var2, var3, 0.0F);
         float var7 = var5.isEnabled() ? 1.0F : 0.25F;
         RenderSystem.color4f(var7, var7, var7, var4);
         var5.renderIcon(var7, var6);
         RenderSystem.popMatrix();
         String var8 = String.valueOf(this.minecraft.options.keyHotbarSlots[var1].getTranslatedKeyMessage());
         if (var6 > 3 && var5.isEnabled()) {
            this.minecraft.font.drawShadow(var8, (float)(var2 + 19 - 2 - this.minecraft.font.width(var8)), var3 + 6.0F + 3.0F, 16777215 + (var6 << 24));
         }
      }

   }

   public void renderTooltip() {
      int var1 = (int)(this.getHotbarAlpha() * 255.0F);
      if (var1 > 3 && this.menu != null) {
         SpectatorMenuItem var2 = this.menu.getSelectedItem();
         String var3 = var2 == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt().getColoredString() : var2.getName().getColoredString();
         if (var3 != null) {
            int var4 = (this.minecraft.getWindow().getGuiScaledWidth() - this.minecraft.font.width(var3)) / 2;
            int var5 = this.minecraft.getWindow().getGuiScaledHeight() - 35;
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.minecraft.font.drawShadow(var3, (float)var4, (float)var5, 16777215 + (var1 << 24));
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
