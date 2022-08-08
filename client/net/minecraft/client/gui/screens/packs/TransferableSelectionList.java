package net.minecraft.client.gui.screens.packs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.util.FormattedCharSequence;

public class TransferableSelectionList extends ObjectSelectionList<PackEntry> {
   static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
   static final Component INCOMPATIBLE_TITLE = Component.translatable("pack.incompatible");
   static final Component INCOMPATIBLE_CONFIRM_TITLE = Component.translatable("pack.incompatible.confirm.title");
   private final Component title;

   public TransferableSelectionList(Minecraft var1, int var2, int var3, Component var4) {
      super(var1, var2, var3, 32, var3 - 55 + 4, 36);
      this.title = var4;
      this.centerListVertically = false;
      Objects.requireNonNull(var1.font);
      this.setRenderHeader(true, (int)(9.0F * 1.5F));
   }

   protected void renderHeader(PoseStack var1, int var2, int var3, Tesselator var4) {
      MutableComponent var5 = Component.empty().append(this.title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
      this.minecraft.font.draw(var1, (Component)var5, (float)(var2 + this.width / 2 - this.minecraft.font.width((FormattedText)var5) / 2), (float)Math.min(this.y0 + 3, var3), 16777215);
   }

   public int getRowWidth() {
      return this.width;
   }

   protected int getScrollbarPosition() {
      return this.x1 - 6;
   }

   public static class PackEntry extends ObjectSelectionList.Entry<PackEntry> {
      private static final int ICON_OVERLAY_X_MOVE_RIGHT = 0;
      private static final int ICON_OVERLAY_X_MOVE_LEFT = 32;
      private static final int ICON_OVERLAY_X_MOVE_DOWN = 64;
      private static final int ICON_OVERLAY_X_MOVE_UP = 96;
      private static final int ICON_OVERLAY_Y_UNSELECTED = 0;
      private static final int ICON_OVERLAY_Y_SELECTED = 32;
      private static final int MAX_DESCRIPTION_WIDTH_PIXELS = 157;
      private static final int MAX_NAME_WIDTH_PIXELS = 157;
      private static final String TOO_LONG_NAME_SUFFIX = "...";
      private final TransferableSelectionList parent;
      protected final Minecraft minecraft;
      protected final Screen screen;
      private final PackSelectionModel.Entry pack;
      private final FormattedCharSequence nameDisplayCache;
      private final MultiLineLabel descriptionDisplayCache;
      private final FormattedCharSequence incompatibleNameDisplayCache;
      private final MultiLineLabel incompatibleDescriptionDisplayCache;

      public PackEntry(Minecraft var1, TransferableSelectionList var2, Screen var3, PackSelectionModel.Entry var4) {
         super();
         this.minecraft = var1;
         this.screen = var3;
         this.pack = var4;
         this.parent = var2;
         this.nameDisplayCache = cacheName(var1, var4.getTitle());
         this.descriptionDisplayCache = cacheDescription(var1, var4.getExtendedDescription());
         this.incompatibleNameDisplayCache = cacheName(var1, TransferableSelectionList.INCOMPATIBLE_TITLE);
         this.incompatibleDescriptionDisplayCache = cacheDescription(var1, var4.getCompatibility().getDescription());
      }

      private static FormattedCharSequence cacheName(Minecraft var0, Component var1) {
         int var2 = var0.font.width((FormattedText)var1);
         if (var2 > 157) {
            FormattedText var3 = FormattedText.composite(var0.font.substrByWidth(var1, 157 - var0.font.width("...")), FormattedText.of("..."));
            return Language.getInstance().getVisualOrder(var3);
         } else {
            return var1.getVisualOrderText();
         }
      }

      private static MultiLineLabel cacheDescription(Minecraft var0, Component var1) {
         return MultiLineLabel.create(var0.font, var1, 157, 2);
      }

      public Component getNarration() {
         return Component.translatable("narrator.select", this.pack.getTitle());
      }

      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         PackCompatibility var11 = this.pack.getCompatibility();
         if (!var11.isCompatible()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            GuiComponent.fill(var1, var4 - 1, var3 - 1, var4 + var5 - 9, var3 + var6 + 1, -8978432);
         }

         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, this.pack.getIconTexture());
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         GuiComponent.blit(var1, var4, var3, 0.0F, 0.0F, 32, 32, 32, 32);
         FormattedCharSequence var12 = this.nameDisplayCache;
         MultiLineLabel var13 = this.descriptionDisplayCache;
         if (this.showHoverOverlay() && ((Boolean)this.minecraft.options.touchscreen().get() || var9)) {
            RenderSystem.setShaderTexture(0, TransferableSelectionList.ICON_OVERLAY_LOCATION);
            GuiComponent.fill(var1, var4, var3, var4 + 32, var3 + 32, -1601138544);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int var14 = var7 - var4;
            int var15 = var8 - var3;
            if (!this.pack.getCompatibility().isCompatible()) {
               var12 = this.incompatibleNameDisplayCache;
               var13 = this.incompatibleDescriptionDisplayCache;
            }

            if (this.pack.canSelect()) {
               if (var14 < 32) {
                  GuiComponent.blit(var1, var4, var3, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(var1, var4, var3, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            } else {
               if (this.pack.canUnselect()) {
                  if (var14 < 16) {
                     GuiComponent.blit(var1, var4, var3, 32.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     GuiComponent.blit(var1, var4, var3, 32.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.pack.canMoveUp()) {
                  if (var14 < 32 && var14 > 16 && var15 < 16) {
                     GuiComponent.blit(var1, var4, var3, 96.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     GuiComponent.blit(var1, var4, var3, 96.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.pack.canMoveDown()) {
                  if (var14 < 32 && var14 > 16 && var15 > 16) {
                     GuiComponent.blit(var1, var4, var3, 64.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     GuiComponent.blit(var1, var4, var3, 64.0F, 0.0F, 32, 32, 256, 256);
                  }
               }
            }
         }

         this.minecraft.font.drawShadow(var1, var12, (float)(var4 + 32 + 2), (float)(var3 + 1), 16777215);
         var13.renderLeftAligned(var1, var4 + 32 + 2, var3 + 12, 10, 8421504);
      }

      private boolean showHoverOverlay() {
         return !this.pack.isFixedPosition() || !this.pack.isRequired();
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         double var6 = var1 - (double)this.parent.getRowLeft();
         double var8 = var3 - (double)this.parent.getRowTop(this.parent.children().indexOf(this));
         if (this.showHoverOverlay() && var6 <= 32.0) {
            if (this.pack.canSelect()) {
               PackCompatibility var10 = this.pack.getCompatibility();
               if (var10.isCompatible()) {
                  this.pack.select();
               } else {
                  Component var11 = var10.getConfirmation();
                  this.minecraft.setScreen(new ConfirmScreen((var1x) -> {
                     this.minecraft.setScreen(this.screen);
                     if (var1x) {
                        this.pack.select();
                     }

                  }, TransferableSelectionList.INCOMPATIBLE_CONFIRM_TITLE, var11));
               }

               return true;
            }

            if (var6 < 16.0 && this.pack.canUnselect()) {
               this.pack.unselect();
               return true;
            }

            if (var6 > 16.0 && var8 < 16.0 && this.pack.canMoveUp()) {
               this.pack.moveUp();
               return true;
            }

            if (var6 > 16.0 && var8 > 16.0 && this.pack.canMoveDown()) {
               this.pack.moveDown();
               return true;
            }
         }

         return false;
      }
   }
}
