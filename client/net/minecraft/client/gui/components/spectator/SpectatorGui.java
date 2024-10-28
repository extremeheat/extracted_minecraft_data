package net.minecraft.client.gui.components.spectator;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class SpectatorGui implements SpectatorMenuListener {
   private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
   private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
   private static final long FADE_OUT_DELAY = 5000L;
   private static final long FADE_OUT_TIME = 2000L;
   private final Minecraft minecraft;
   private long lastSelectionTime;
   @Nullable
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

   public void renderHotbar(GuiGraphics var1) {
      if (this.menu != null) {
         float var2 = this.getHotbarAlpha();
         if (var2 <= 0.0F) {
            this.menu.exit();
         } else {
            int var3 = var1.guiWidth() / 2;
            var1.pose().pushPose();
            var1.pose().translate(0.0F, 0.0F, -90.0F);
            int var4 = Mth.floor((float)var1.guiHeight() - 22.0F * var2);
            SpectatorPage var5 = this.menu.getCurrentPage();
            this.renderPage(var1, var2, var3, var4, var5);
            var1.pose().popPose();
         }
      }
   }

   protected void renderPage(GuiGraphics var1, float var2, int var3, int var4, SpectatorPage var5) {
      RenderSystem.enableBlend();
      var1.setColor(1.0F, 1.0F, 1.0F, var2);
      var1.blitSprite(HOTBAR_SPRITE, var3 - 91, var4, 182, 22);
      if (var5.getSelectedSlot() >= 0) {
         var1.blitSprite(HOTBAR_SELECTION_SPRITE, var3 - 91 - 1 + var5.getSelectedSlot() * 20, var4 - 1, 24, 23);
      }

      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);

      for(int var6 = 0; var6 < 9; ++var6) {
         this.renderSlot(var1, var6, var1.guiWidth() / 2 - 90 + var6 * 20 + 2, (float)(var4 + 3), var2, var5.getItem(var6));
      }

      RenderSystem.disableBlend();
   }

   private void renderSlot(GuiGraphics var1, int var2, int var3, float var4, float var5, SpectatorMenuItem var6) {
      if (var6 != SpectatorMenu.EMPTY_SLOT) {
         int var7 = (int)(var5 * 255.0F);
         var1.pose().pushPose();
         var1.pose().translate((float)var3, var4, 0.0F);
         float var8 = var6.isEnabled() ? 1.0F : 0.25F;
         var1.setColor(var8, var8, var8, var5);
         var6.renderIcon(var1, var8, var7);
         var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         var1.pose().popPose();
         if (var7 > 3 && var6.isEnabled()) {
            Component var9 = this.minecraft.options.keyHotbarSlots[var2].getTranslatedKeyMessage();
            var1.drawString(this.minecraft.font, var9, var3 + 19 - 2 - this.minecraft.font.width((FormattedText)var9), (int)var4 + 6 + 3, 16777215 + (var7 << 24));
         }
      }

   }

   public void renderTooltip(GuiGraphics var1) {
      int var2 = (int)(this.getHotbarAlpha() * 255.0F);
      if (var2 > 3 && this.menu != null) {
         SpectatorMenuItem var3 = this.menu.getSelectedItem();
         Component var4 = var3 == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt() : var3.getName();
         if (var4 != null) {
            int var5 = this.minecraft.font.width((FormattedText)var4);
            int var6 = (var1.guiWidth() - var5) / 2;
            int var7 = var1.guiHeight() - 35;
            var1.drawStringWithBackdrop(this.minecraft.font, var4, var6, var7, var5, FastColor.ARGB32.color(var2, -1));
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

   public void onMouseScrolled(int var1) {
      int var2;
      for(var2 = this.menu.getSelectedSlot() + var1; var2 >= 0 && var2 <= 8 && (this.menu.getItem(var2) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(var2).isEnabled()); var2 += var1) {
      }

      if (var2 >= 0 && var2 <= 8) {
         this.menu.selectSlot(var2);
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
