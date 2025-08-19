package net.minecraft.client.gui.screens.packs;

import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.util.FormattedCharSequence;

public class TransferableSelectionList extends ObjectSelectionList<Entry> {
   static final ResourceLocation SELECT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/select_highlighted");
   static final ResourceLocation SELECT_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/select");
   static final ResourceLocation UNSELECT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/unselect_highlighted");
   static final ResourceLocation UNSELECT_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/unselect");
   static final ResourceLocation MOVE_UP_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/move_up_highlighted");
   static final ResourceLocation MOVE_UP_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/move_up");
   static final ResourceLocation MOVE_DOWN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/move_down_highlighted");
   static final ResourceLocation MOVE_DOWN_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/move_down");
   static final Component INCOMPATIBLE_TITLE = Component.translatable("pack.incompatible");
   static final Component INCOMPATIBLE_CONFIRM_TITLE = Component.translatable("pack.incompatible.confirm.title");
   private static final int ENTRY_PADDING = 2;
   private final Component title;
   final PackSelectionScreen screen;

   public TransferableSelectionList(Minecraft var1, PackSelectionScreen var2, int var3, int var4, Component var5) {
      super(var1, var3, var4, 33, 36);
      this.screen = var2;
      this.title = var5;
      this.centerListVertically = false;
   }

   public int getRowWidth() {
      return this.width - 4;
   }

   protected int scrollBarX() {
      return this.getRight() - 6;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.getSelected() != null ? ((Entry)this.getSelected()).keyPressed(var1, var2, var3) : super.keyPressed(var1, var2, var3);
   }

   public void updateList(Stream<PackSelectionModel.Entry> var1, @Nullable PackSelectionModel.EntryBase var2) {
      this.clearEntries();
      MutableComponent var3 = Component.empty().append(this.title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
      HeaderEntry var10001 = new HeaderEntry(this.minecraft.font, var3);
      Objects.requireNonNull(this.minecraft.font);
      this.addEntry(var10001, (int)(9.0F * 1.5F));
      this.setSelected((AbstractSelectionList.Entry)null);
      var1.forEach((var2x) -> {
         PackEntry var3 = new PackEntry(this.minecraft, this, var2x);
         this.addEntry(var3);
         if (var2 != null && var2.getId().equals(var2x.getId())) {
            this.screen.setFocused(this);
            this.setFocused(var3);
         }

      });
   }

   public abstract class Entry extends ObjectSelectionList.Entry<Entry> {
      public Entry() {
         super();
      }

      public int getWidth() {
         return super.getWidth() - (TransferableSelectionList.this.scrollbarVisible() ? 6 : 0);
      }

      public abstract String getPackId();
   }

   public class PackEntry extends Entry {
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

      public PackEntry(final Minecraft var2, final TransferableSelectionList var3, final PackSelectionModel.Entry var4) {
         super();
         this.minecraft = var2;
         this.pack = var4;
         this.parent = var3;
         this.nameDisplayCache = cacheName(var2, var4.getTitle());
         this.descriptionDisplayCache = cacheDescription(var2, var4.getExtendedDescription());
         this.incompatibleNameDisplayCache = cacheName(var2, TransferableSelectionList.INCOMPATIBLE_TITLE);
         this.incompatibleDescriptionDisplayCache = cacheDescription(var2, var4.getCompatibility().getDescription());
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
         return MultiLineLabel.create(var0.font, 157, 2, var1);
      }

      public Component getNarration() {
         return Component.translatable("narrator.select", this.pack.getTitle());
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         PackCompatibility var6 = this.pack.getCompatibility();
         if (!var6.isCompatible()) {
            int var7 = this.getContentX() - 1;
            int var8 = this.getContentY() - 1;
            int var9 = this.getContentRight() + 1;
            int var10 = this.getContentBottom() + 1;
            var1.fill(var7, var8, var9, var10, -8978432);
         }

         var1.blit(RenderPipelines.GUI_TEXTURED, this.pack.getIconTexture(), this.getContentX(), this.getContentY(), 0.0F, 0.0F, 32, 32, 32, 32);
         FormattedCharSequence var11 = this.nameDisplayCache;
         MultiLineLabel var12 = this.descriptionDisplayCache;
         if (this.showHoverOverlay() && ((Boolean)this.minecraft.options.touchscreen().get() || var4 || this.parent.getSelected() == this && this.parent.isFocused())) {
            var1.fill(this.getContentX(), this.getContentY(), this.getContentX() + 32, this.getContentY() + 32, -1601138544);
            int var13 = var2 - this.getContentX();
            int var14 = var3 - this.getContentY();
            if (!this.pack.getCompatibility().isCompatible()) {
               var11 = this.incompatibleNameDisplayCache;
               var12 = this.incompatibleDescriptionDisplayCache;
            }

            if (this.pack.canSelect()) {
               if (var13 < 32) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.SELECT_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
               } else {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.SELECT_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
               }
            } else {
               if (this.pack.canUnselect()) {
                  if (var13 < 16) {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.UNSELECT_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  } else {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.UNSELECT_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  }
               }

               if (this.pack.canMoveUp()) {
                  if (var13 < 32 && var13 > 16 && var14 < 16) {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.MOVE_UP_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  } else {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.MOVE_UP_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  }
               }

               if (this.pack.canMoveDown()) {
                  if (var13 < 32 && var13 > 16 && var14 > 16) {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.MOVE_DOWN_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  } else {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.MOVE_DOWN_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  }
               }
            }
         }

         var1.drawString(this.minecraft.font, (FormattedCharSequence)var11, this.getContentX() + 32 + 2, this.getContentY() + 1, -1);
         var12.render(var1, MultiLineLabel.Align.LEFT, this.getContentX() + 32 + 2, this.getContentY() + 12, 10, true, -8355712);
      }

      public String getPackId() {
         return this.pack.getId();
      }

      private boolean showHoverOverlay() {
         return !this.pack.isFixedPosition() || !this.pack.isRequired();
      }

      public boolean keyPressed(int var1, int var2, int var3) {
         switch (var1) {
            case 32:
            case 257:
               this.keyboardSelection();
               return true;
            default:
               if (Screen.hasShiftDown()) {
                  switch (var1) {
                     case 264:
                        this.keyboardMoveDown();
                        return true;
                     case 265:
                        this.keyboardMoveUp();
                        return true;
                  }
               }

               return super.keyPressed(var1, var2, var3);
         }
      }

      public void keyboardSelection() {
         if (this.pack.canSelect()) {
            this.handlePackSelection();
         } else if (this.pack.canUnselect()) {
            this.pack.unselect();
         }

      }

      private void keyboardMoveUp() {
         if (this.pack.canMoveUp()) {
            this.pack.moveUp();
         }

      }

      private void keyboardMoveDown() {
         if (this.pack.canMoveDown()) {
            this.pack.moveDown();
         }

      }

      private void handlePackSelection() {
         if (this.pack.getCompatibility().isCompatible()) {
            this.pack.select();
         } else {
            Component var1 = this.pack.getCompatibility().getConfirmation();
            this.minecraft.setScreen(new ConfirmScreen((var1x) -> {
               this.minecraft.setScreen(this.parent.screen);
               if (var1x) {
                  this.pack.select();
               }

            }, TransferableSelectionList.INCOMPATIBLE_CONFIRM_TITLE, var1));
         }

      }

      public boolean shouldTakeFocusAfterInteraction() {
         return TransferableSelectionList.this.children().stream().anyMatch((var1) -> var1.getPackId().equals(this.getPackId()));
      }

      public boolean mouseClicked(double var1, double var3, int var5, boolean var6) {
         double var7 = var1 - (double)this.getX();
         double var9 = var3 - (double)this.getY();
         if (this.showHoverOverlay() && var7 <= 32.0) {
            this.parent.screen.clearSelected();
            if (this.pack.canSelect()) {
               this.handlePackSelection();
               return true;
            }

            if (var7 < 16.0 && this.pack.canUnselect()) {
               this.pack.unselect();
               return true;
            }

            if (var7 > 16.0 && var9 < 16.0 && this.pack.canMoveUp()) {
               this.pack.moveUp();
               return true;
            }

            if (var7 > 16.0 && var9 > 16.0 && this.pack.canMoveDown()) {
               this.pack.moveDown();
               return true;
            }
         }

         return super.mouseClicked(var1, var3, var5, var6);
      }
   }

   public class HeaderEntry extends Entry {
      private final Font font;
      private final Component text;

      public HeaderEntry(final Font var2, final Component var3) {
         super();
         this.font = var2;
         this.text = var3;
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         Font var10001 = this.font;
         Component var10002 = this.text;
         int var10003 = this.getX() + this.getWidth() / 2;
         int var10004 = this.getContentYMiddle();
         Objects.requireNonNull(this.font);
         var1.drawCenteredString(var10001, (Component)var10002, var10003, var10004 - 9 / 2, -1);
      }

      public Component getNarration() {
         return this.text;
      }

      public String getPackId() {
         return "";
      }
   }
}
