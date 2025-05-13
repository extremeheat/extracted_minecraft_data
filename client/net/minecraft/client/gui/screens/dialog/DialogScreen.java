package net.minecraft.client.gui.screens.dialog;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.body.DialogBodyHandlers;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.ClickAction;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.body.DialogBody;

public abstract class DialogScreen<T extends Dialog> extends Screen {
   public static final Component DISCONNECT = Component.translatable("menu.custom_screen_info.disconnect");
   private static final int WARNING_BUTTON_SIZE = 20;
   private static final WidgetSprites WARNING_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("dialog/warning_button"), ResourceLocation.withDefaultNamespace("dialog/warning_button_disabled"), ResourceLocation.withDefaultNamespace("dialog/warning_button_highlighted"));
   private final T dialog;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   @Nullable
   private final Screen previousScreen;
   @Nullable
   private ScrollableLayout bodyScroll;
   private Button warningButton;

   public DialogScreen(@Nullable Screen var1, T var2) {
      super(var2.common().title());
      this.dialog = var2;
      this.previousScreen = var1;
   }

   protected final void init() {
      super.init();
      this.warningButton = this.createWarningButton();
      this.warningButton.setTabOrderGroup(-10);
      LinearLayout var1 = LinearLayout.vertical().spacing(10);
      var1.defaultCellSetting().alignHorizontallyCenter();
      this.dialogInit(this.dialog);
      this.layout.addToHeader(this.createTitleWithWarningButton());

      for(DialogBody var3 : this.dialog.common().body()) {
         LayoutElement var4 = DialogBodyHandlers.createBodyElement(this, var3);
         if (var4 != null) {
            var1.addChild(var4);
         }
      }

      this.populateBodyElements(var1, this.dialog);
      this.bodyScroll = new ScrollableLayout(this.minecraft, var1, this.layout.getContentHeight());
      this.layout.addToContents(this.bodyScroll);
      this.updateHeaderAndFooter(this.layout, this.dialog);
      this.layout.visitWidgets((var1x) -> {
         if (var1x != this.warningButton) {
            this.addRenderableWidget(var1x);
         }

      });
      this.addRenderableWidget(this.warningButton);
      this.repositionElements();
   }

   protected void dialogInit(T var1) {
   }

   protected void populateBodyElements(LinearLayout var1, T var2) {
   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, T var2) {
   }

   protected void repositionElements() {
      this.bodyScroll.setMaxHeight(this.layout.getContentHeight());
      this.layout.arrangeElements();
      this.makeSureWarningButtonIsInBounds();
   }

   protected LayoutElement warningButton() {
      return this.warningButton;
   }

   protected LayoutElement createTitleWithWarningButton() {
      LinearLayout var1 = LinearLayout.horizontal().spacing(10);
      var1.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();
      var1.addChild(new StringWidget(this.title, this.font));
      var1.addChild(this.warningButton());
      return var1;
   }

   protected void makeSureWarningButtonIsInBounds() {
      int var1 = this.warningButton.getX();
      int var2 = this.warningButton.getY();
      if (var1 < 0 || var2 < 0 || var1 > this.width - 20 || var2 > this.height - 20) {
         this.warningButton.setX(Math.max(0, this.width - 40));
         this.warningButton.setX(Math.min(5, this.height));
      }

   }

   private Button createWarningButton() {
      ImageButton var1 = new ImageButton(0, 0, 20, 20, WARNING_BUTTON_SPRITES, (var1x) -> this.minecraft.setScreen(new ConfirmScreen((var1) -> {
            if (var1) {
               this.minecraft.setScreen((Screen)null);
               this.minecraft.player.connection.getConnection().disconnect(DISCONNECT);
            } else {
               this.minecraft.setScreen(this);
            }

         }, Component.translatable("menu.custom_screen_info.title"), Component.translatable("menu.custom_screen_info.contents"), CommonComponents.disconnectButtonLabel(this.minecraft.isLocalServer()), CommonComponents.GUI_BACK)), Component.translatable("menu.custom_screen_info.button_narration"));
      var1.setTooltip(Tooltip.create(Component.translatable("menu.custom_screen_info.tooltip")));
      return var1;
   }

   public static Button.Builder createDialogButton(CommonButtonData var0, Button.OnPress var1) {
      Button.Builder var2 = Button.builder(var0.label(), var1);
      var2.width(var0.width());
      if (var0.tooltip().isPresent()) {
         var2 = var2.tooltip(Tooltip.create((Component)var0.tooltip().get()));
      }

      return var2;
   }

   protected Button.Builder createClickActionButton(ClickAction var1) {
      return createDialogButton(var1.buttonData(), (var2) -> this.closeScreen(var1.onClick()));
   }

   public boolean shouldCloseOnEsc() {
      return this.dialog.common().canCloseWithEscape();
   }

   public void onClose() {
      this.closeScreen(this.dialog.onCancel());
   }

   private void closeScreen(Optional<ClickEvent> var1) {
      var1.ifPresent((var1x) -> defaultHandleClickEvent(var1x, this.minecraft, this.previousScreen));
      if (this.minecraft.screen == this) {
         this.minecraft.setScreen(this.previousScreen);
      }

   }

   @Nullable
   public Screen previousScreen() {
      return this.previousScreen;
   }

   protected static LayoutElement packControlsIntoColumns(List<? extends LayoutElement> var0, int var1) {
      GridLayout var2 = new GridLayout();
      var2.defaultCellSetting().alignHorizontallyCenter();
      var2.columnSpacing(2).rowSpacing(2);
      int var3 = var0.size();
      int var4 = var3 / var1;
      int var5 = var4 * var1;

      for(int var6 = 0; var6 < var5; ++var6) {
         var2.addChild((LayoutElement)var0.get(var6), var6 / var1, var6 % var1);
      }

      if (var3 != var5) {
         LinearLayout var8 = LinearLayout.horizontal().spacing(2);
         var8.defaultCellSetting().alignHorizontallyCenter();

         for(int var7 = var5; var7 < var3; ++var7) {
            var8.addChild((LayoutElement)var0.get(var7));
         }

         var2.addChild(var8, var4 + 1, 0, 1, var1);
      }

      return var2;
   }
}
