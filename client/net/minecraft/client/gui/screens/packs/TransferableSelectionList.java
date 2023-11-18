package net.minecraft.client.gui.screens.packs;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.util.FormattedCharSequence;

public class TransferableSelectionList extends ObjectSelectionList<TransferableSelectionList.PackEntry> {
   static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
   static final Component INCOMPATIBLE_TITLE = Component.translatable("pack.incompatible");
   static final Component INCOMPATIBLE_CONFIRM_TITLE = Component.translatable("pack.incompatible.confirm.title");
   private final Component title;
   final PackSelectionScreen screen;

   public TransferableSelectionList(Minecraft var1, PackSelectionScreen var2, int var3, int var4, Component var5) {
      super(var1, var3, var4, 32, var4 - 55 + 4, 36);
      this.screen = var2;
      this.title = var5;
      this.centerListVertically = false;
      this.setRenderHeader(true, (int)(9.0F * 1.5F));
   }

   @Override
   protected void renderHeader(GuiGraphics var1, int var2, int var3) {
      MutableComponent var4 = Component.empty().append(this.title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
      var1.drawString(this.minecraft.font, var4, var2 + this.width / 2 - this.minecraft.font.width(var4) / 2, Math.min(this.y0 + 3, var3), 16777215, false);
   }

   @Override
   public int getRowWidth() {
      return this.width;
   }

   @Override
   protected int getScrollbarPosition() {
      return this.x1 - 6;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.getSelected() != null) {
         switch(var1) {
            case 32:
            case 257:
               this.getSelected().keyboardSelection();
               return true;
            default:
               if (Screen.hasShiftDown()) {
                  switch(var1) {
                     case 264:
                        this.getSelected().keyboardMoveDown();
                        return true;
                     case 265:
                        this.getSelected().keyboardMoveUp();
                        return true;
                  }
               }
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   public static class PackEntry extends ObjectSelectionList.Entry<TransferableSelectionList.PackEntry> {
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
      private final PackSelectionModel.Entry pack;
      private final FormattedCharSequence nameDisplayCache;
      private final MultiLineLabel descriptionDisplayCache;
      private final FormattedCharSequence incompatibleNameDisplayCache;
      private final MultiLineLabel incompatibleDescriptionDisplayCache;

      public PackEntry(Minecraft var1, TransferableSelectionList var2, PackSelectionModel.Entry var3) {
         super();
         this.minecraft = var1;
         this.pack = var3;
         this.parent = var2;
         this.nameDisplayCache = cacheName(var1, var3.getTitle());
         this.descriptionDisplayCache = cacheDescription(var1, var3.getExtendedDescription());
         this.incompatibleNameDisplayCache = cacheName(var1, TransferableSelectionList.INCOMPATIBLE_TITLE);
         this.incompatibleDescriptionDisplayCache = cacheDescription(var1, var3.getCompatibility().getDescription());
      }

      private static FormattedCharSequence cacheName(Minecraft var0, Component var1) {
         int var2 = var0.font.width(var1);
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

      @Override
      public Component getNarration() {
         return Component.translatable("narrator.select", this.pack.getTitle());
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         PackCompatibility var11 = this.pack.getCompatibility();
         if (!var11.isCompatible()) {
            var1.fill(var4 - 1, var3 - 1, var4 + var5 - 9, var3 + var6 + 1, -8978432);
         }

         var1.blit(this.pack.getIconTexture(), var4, var3, 0.0F, 0.0F, 32, 32, 32, 32);
         FormattedCharSequence var12 = this.nameDisplayCache;
         MultiLineLabel var13 = this.descriptionDisplayCache;
         if (this.showHoverOverlay() && (this.minecraft.options.touchscreen().get() || var9 || this.parent.getSelected() == this && this.parent.isFocused())) {
            var1.fill(var4, var3, var4 + 32, var3 + 32, -1601138544);
            int var14 = var7 - var4;
            int var15 = var8 - var3;
            if (!this.pack.getCompatibility().isCompatible()) {
               var12 = this.incompatibleNameDisplayCache;
               var13 = this.incompatibleDescriptionDisplayCache;
            }

            if (this.pack.canSelect()) {
               if (var14 < 32) {
                  var1.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, var4, var3, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  var1.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, var4, var3, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            } else {
               if (this.pack.canUnselect()) {
                  if (var14 < 16) {
                     var1.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, var4, var3, 32.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     var1.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, var4, var3, 32.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.pack.canMoveUp()) {
                  if (var14 < 32 && var14 > 16 && var15 < 16) {
                     var1.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, var4, var3, 96.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     var1.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, var4, var3, 96.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.pack.canMoveDown()) {
                  if (var14 < 32 && var14 > 16 && var15 > 16) {
                     var1.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, var4, var3, 64.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     var1.blit(TransferableSelectionList.ICON_OVERLAY_LOCATION, var4, var3, 64.0F, 0.0F, 32, 32, 256, 256);
                  }
               }
            }
         }

         var1.drawString(this.minecraft.font, var12, var4 + 32 + 2, var3 + 1, 16777215);
         var13.renderLeftAligned(var1, var4 + 32 + 2, var3 + 12, 10, 8421504);
      }

      public String getPackId() {
         return this.pack.getId();
      }

      private boolean showHoverOverlay() {
         return !this.pack.isFixedPosition() || !this.pack.isRequired();
      }

      public void keyboardSelection() {
         if (this.pack.canSelect() && this.handlePackSelection()) {
            this.parent.screen.updateFocus(this.parent);
         } else if (this.pack.canUnselect()) {
            this.pack.unselect();
            this.parent.screen.updateFocus(this.parent);
         }
      }

      void keyboardMoveUp() {
         if (this.pack.canMoveUp()) {
            this.pack.moveUp();
         }
      }

      void keyboardMoveDown() {
         if (this.pack.canMoveDown()) {
            this.pack.moveDown();
         }
      }

      private boolean handlePackSelection() {
         if (this.pack.getCompatibility().isCompatible()) {
            this.pack.select();
            return true;
         } else {
            Component var1 = this.pack.getCompatibility().getConfirmation();
            this.minecraft.setScreen(new ConfirmScreen(var1x -> {
               this.minecraft.setScreen(this.parent.screen);
               if (var1x) {
                  this.pack.select();
               }
            }, TransferableSelectionList.INCOMPATIBLE_CONFIRM_TITLE, var1));
            return false;
         }
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 != 0) {
            return false;
         } else {
            double var6 = var1 - (double)this.parent.getRowLeft();
            double var8 = var3 - (double)this.parent.getRowTop(this.parent.children().indexOf(this));
            if (this.showHoverOverlay() && var6 <= 32.0) {
               this.parent.screen.clearSelected();
               if (this.pack.canSelect()) {
                  this.handlePackSelection();
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
}
