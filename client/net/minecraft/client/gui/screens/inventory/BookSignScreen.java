package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class BookSignScreen extends Screen {
   private static final Component EDIT_TITLE_LABEL = Component.translatable("book.editTitle");
   private static final Component FINALIZE_WARNING_LABEL = Component.translatable("book.finalizeWarning");
   private static final Component TITLE = Component.translatable("book.sign.title");
   private static final Component TITLE_EDIT_BOX = Component.translatable("book.sign.titlebox");
   private final BookEditScreen bookEditScreen;
   private final Player owner;
   private final List<String> pages;
   private final InteractionHand hand;
   private final Component ownerText;
   private EditBox titleBox;
   private String titleValue = "";

   public BookSignScreen(BookEditScreen var1, Player var2, InteractionHand var3, List<String> var4) {
      super(TITLE);
      this.bookEditScreen = var1;
      this.owner = var2;
      this.hand = var3;
      this.pages = var4;
      this.ownerText = Component.translatable("book.byAuthor", var2.getName()).withStyle(ChatFormatting.DARK_GRAY);
   }

   protected void init() {
      Button var1 = Button.builder(Component.translatable("book.finalizeButton"), (var1x) -> {
         this.saveChanges();
         this.minecraft.setScreen((Screen)null);
      }).bounds(this.width / 2 - 100, 196, 98, 20).build();
      var1.active = false;
      this.titleBox = (EditBox)this.addRenderableWidget(new EditBox(this.minecraft.font, (this.width - 114) / 2 - 3, 50, 114, 20, TITLE_EDIT_BOX));
      this.titleBox.setMaxLength(15);
      this.titleBox.setBordered(false);
      this.titleBox.setCentered(true);
      this.titleBox.setTextColor(-16777216);
      this.titleBox.setTextShadow(false);
      this.titleBox.setResponder((var1x) -> var1.active = !StringUtil.isBlank(var1x));
      this.titleBox.setValue(this.titleValue);
      this.addRenderableWidget(var1);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> {
         this.titleValue = this.titleBox.getValue();
         this.minecraft.setScreen(this.bookEditScreen);
      }).bounds(this.width / 2 + 2, 196, 98, 20).build());
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.titleBox);
   }

   private void saveChanges() {
      int var1 = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().getSelectedSlot() : 40;
      this.minecraft.getConnection().send(new ServerboundEditBookPacket(var1, this.pages, Optional.of(this.titleBox.getValue().trim())));
   }

   public boolean isInGameUi() {
      return true;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.titleBox.isFocused() || this.titleBox.getValue().isEmpty() || var1 != 257 && var1 != 335) {
         return super.keyPressed(var1, var2, var3);
      } else {
         this.saveChanges();
         this.minecraft.setScreen((Screen)null);
         return true;
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      int var5 = (this.width - 192) / 2;
      boolean var6 = true;
      int var7 = this.font.width((FormattedText)EDIT_TITLE_LABEL);
      var1.drawString(this.font, (Component)EDIT_TITLE_LABEL, var5 + 36 + (114 - var7) / 2, 34, -16777216, false);
      int var8 = this.font.width((FormattedText)this.ownerText);
      var1.drawString(this.font, (Component)this.ownerText, var5 + 36 + (114 - var8) / 2, 60, -16777216, false);
      var1.drawWordWrap(this.font, FINALIZE_WARNING_LABEL, var5 + 36, 82, 114, -16777216, false);
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      super.renderBackground(var1, var2, var3, var4);
      var1.blit(RenderPipelines.GUI_TEXTURED, BookViewScreen.BOOK_LOCATION, (this.width - 192) / 2, 2, 0.0F, 0.0F, 192, 192, 256, 256);
   }
}
