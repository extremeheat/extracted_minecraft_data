package net.minecraft.client.gui.screens.packs;

import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.SelectableEntry;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackCompatibility;

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

   public boolean keyPressed(KeyEvent var1) {
      return this.getSelected() != null ? ((Entry)this.getSelected()).keyPressed(var1) : super.keyPressed(var1);
   }

   public void updateList(Stream<PackSelectionModel.Entry> var1, PackSelectionModel.EntryBase var2) {
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
      this.refreshScrollAmount();
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

   public class PackEntry extends Entry implements SelectableEntry {
      private static final int MAX_DESCRIPTION_WIDTH_PIXELS = 157;
      public static final int ICON_SIZE = 32;
      private final TransferableSelectionList parent;
      protected final Minecraft minecraft;
      private final PackSelectionModel.Entry pack;
      private final StringWidget nameWidget;
      private final MultiLineTextWidget descriptionWidget;

      public PackEntry(final Minecraft var2, final TransferableSelectionList var3, final PackSelectionModel.Entry var4) {
         super();
         this.minecraft = var2;
         this.pack = var4;
         this.parent = var3;
         this.nameWidget = new StringWidget(var4.getTitle(), var2.font);
         this.descriptionWidget = new MultiLineTextWidget(ComponentUtils.mergeStyles(var4.getExtendedDescription(), Style.EMPTY.withColor(-8355712)), var2.font);
         this.descriptionWidget.setMaxRows(2);
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
         if (!this.nameWidget.getMessage().equals(this.pack.getTitle())) {
            this.nameWidget.setMessage(this.pack.getTitle());
         }

         if (!this.descriptionWidget.getMessage().getContents().equals(this.pack.getExtendedDescription().getContents())) {
            this.descriptionWidget.setMessage(ComponentUtils.mergeStyles(this.pack.getExtendedDescription(), Style.EMPTY.withColor(-8355712)));
         }

         if (this.showHoverOverlay() && ((Boolean)this.minecraft.options.touchscreen().get() || var4 || this.parent.getSelected() == this && this.parent.isFocused())) {
            var1.fill(this.getContentX(), this.getContentY(), this.getContentX() + 32, this.getContentY() + 32, -1601138544);
            int var11 = var2 - this.getContentX();
            int var12 = var3 - this.getContentY();
            if (!this.pack.getCompatibility().isCompatible()) {
               this.nameWidget.setMessage(TransferableSelectionList.INCOMPATIBLE_TITLE);
               this.descriptionWidget.setMessage(this.pack.getCompatibility().getDescription());
            }

            if (this.pack.canSelect()) {
               if (this.mouseOverIcon(var11, var12, 32)) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.SELECT_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  TransferableSelectionList.this.handleCursor(var1);
               } else {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.SELECT_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
               }
            } else {
               if (this.pack.canUnselect()) {
                  if (this.mouseOverLeftHalf(var11, var12, 32)) {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.UNSELECT_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                     TransferableSelectionList.this.handleCursor(var1);
                  } else {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.UNSELECT_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  }
               }

               if (this.pack.canMoveUp()) {
                  if (this.mouseOverTopRightQuarter(var11, var12, 32)) {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.MOVE_UP_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                     TransferableSelectionList.this.handleCursor(var1);
                  } else {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.MOVE_UP_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  }
               }

               if (this.pack.canMoveDown()) {
                  if (this.mouseOverBottomRightQuarter(var11, var12, 32)) {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.MOVE_DOWN_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                     TransferableSelectionList.this.handleCursor(var1);
                  } else {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)TransferableSelectionList.MOVE_DOWN_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  }
               }
            }
         }

         this.nameWidget.setMaxWidth(157 - (TransferableSelectionList.this.scrollbarVisible() ? 6 : 0));
         this.nameWidget.setPosition(this.getContentX() + 32 + 2, this.getContentY() + 1);
         this.nameWidget.render(var1, var2, var3, var5);
         this.descriptionWidget.setMaxWidth(157 - (TransferableSelectionList.this.scrollbarVisible() ? 6 : 0));
         this.descriptionWidget.setPosition(this.getContentX() + 32 + 2, this.getContentY() + 12);
         this.descriptionWidget.render(var1, var2, var3, var5);
      }

      public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
         if (this.showHoverOverlay()) {
            int var3 = (int)var1.x() - this.getContentX();
            int var4 = (int)var1.y() - this.getContentY();
            if (this.pack.canSelect() && this.mouseOverIcon(var3, var4, 32)) {
               this.handlePackSelection();
               return true;
            }

            if (this.pack.canUnselect() && this.mouseOverLeftHalf(var3, var4, 32)) {
               this.pack.unselect();
               return true;
            }

            if (this.pack.canMoveUp() && this.mouseOverTopRightQuarter(var3, var4, 32)) {
               this.pack.moveUp();
               return true;
            }

            if (this.pack.canMoveDown() && this.mouseOverBottomRightQuarter(var3, var4, 32)) {
               this.pack.moveDown();
               return true;
            }
         }

         return super.mouseClicked(var1, var2);
      }

      public boolean keyPressed(KeyEvent var1) {
         if (var1.isConfirmation()) {
            this.keyboardSelection();
            return true;
         } else {
            if (var1.hasShiftDown()) {
               if (var1.isUp()) {
                  this.keyboardMoveUp();
                  return true;
               }

               if (var1.isDown()) {
                  this.keyboardMoveDown();
                  return true;
               }
            }

            return super.keyPressed(var1);
         }
      }

      private boolean showHoverOverlay() {
         return !this.pack.isFixedPosition() || !this.pack.isRequired();
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

      public String getPackId() {
         return this.pack.getId();
      }

      public boolean shouldTakeFocusAfterInteraction() {
         return TransferableSelectionList.this.children().stream().anyMatch((var1) -> var1.getPackId().equals(this.getPackId()));
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
