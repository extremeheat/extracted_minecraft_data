package net.minecraft.client.gui.screens.dialog;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
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
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.ClickAction;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.body.DialogBody;
import org.apache.commons.lang3.mutable.MutableObject;

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
   private final DialogConnectionAccess connectionAccess;

   public DialogScreen(@Nullable Screen var1, T var2, DialogConnectionAccess var3) {
      super(var2.common().title());
      this.dialog = var2;
      this.previousScreen = var1;
      this.connectionAccess = var3;
   }

   protected final void init() {
      super.init();
      this.warningButton = this.createWarningButton();
      this.warningButton.setTabOrderGroup(-10);
      LinearLayout var1 = LinearLayout.vertical().spacing(10);
      var1.defaultCellSetting().alignHorizontallyCenter();
      this.dialogInit(this.dialog, this.connectionAccess);
      this.layout.addToHeader(this.createTitleWithWarningButton());

      for(DialogBody var3 : this.dialog.common().body()) {
         LayoutElement var4 = DialogBodyHandlers.createBodyElement(this, var3);
         if (var4 != null) {
            var1.addChild(var4);
         }
      }

      this.populateBodyElements(var1, this.dialog, this.connectionAccess);
      this.bodyScroll = new ScrollableLayout(this.minecraft, var1, this.layout.getContentHeight());
      this.layout.addToContents(this.bodyScroll);
      this.updateHeaderAndFooter(this.layout, this.dialog, this.connectionAccess);
      this.layout.visitWidgets((var1x) -> {
         if (var1x != this.warningButton) {
            this.addRenderableWidget(var1x);
         }

      });
      this.addRenderableWidget(this.warningButton);
      this.repositionElements();
   }

   protected void dialogInit(T var1, DialogConnectionAccess var2) {
   }

   protected void populateBodyElements(LinearLayout var1, T var2, DialogConnectionAccess var3) {
   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, T var2, DialogConnectionAccess var3) {
   }

   protected void repositionElements() {
      this.bodyScroll.setMaxHeight(this.layout.getContentHeight());
      this.layout.arrangeElements();
      this.makeSureWarningButtonIsInBounds();
   }

   protected LayoutElement createTitleWithWarningButton() {
      LinearLayout var1 = LinearLayout.horizontal().spacing(10);
      var1.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();
      var1.addChild(new StringWidget(this.title, this.font));
      var1.addChild(this.warningButton);
      return var1;
   }

   protected void makeSureWarningButtonIsInBounds() {
      int var1 = this.warningButton.getX();
      int var2 = this.warningButton.getY();
      if (var1 < 0 || var2 < 0 || var1 > this.width - 20 || var2 > this.height - 20) {
         this.warningButton.setX(Math.max(0, this.width - 40));
         this.warningButton.setY(Math.min(5, this.height));
      }

   }

   private Button createWarningButton() {
      ImageButton var1 = new ImageButton(0, 0, 20, 20, WARNING_BUTTON_SPRITES, (var1x) -> this.minecraft.setScreen(DialogScreen.WarningScreen.create(this.minecraft, this.connectionAccess, this)), Component.translatable("menu.custom_screen_info.button_narration"));
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
      var1.ifPresent(this::handleDialogClickEvent);
      if (this.minecraft.screen == this) {
         this.minecraft.setScreen(this.previousScreen);
      }

   }

   private void handleDialogClickEvent(ClickEvent var1) {
      Objects.requireNonNull(var1);
      byte var3 = 0;
      //$FF: var3->value
      //0->net/minecraft/network/chat/ClickEvent$RunCommand
      //1->net/minecraft/network/chat/ClickEvent$ShowDialog
      //2->net/minecraft/network/chat/ClickEvent$Custom
      switch (var1.typeSwitch<invokedynamic>(var1, var3)) {
         case 0:
            ClickEvent.RunCommand var4 = (ClickEvent.RunCommand)var1;
            ClickEvent.RunCommand var10000 = var4;

            try {
               var10 = var10000.command();
            } catch (Throwable var8) {
               throw new MatchException(var8.toString(), var8);
            }

            String var9 = var10;
            this.connectionAccess.runCommand(Commands.trimOptionalPrefix(var9), this.previousScreen);
            break;
         case 1:
            ClickEvent.ShowDialog var6 = (ClickEvent.ShowDialog)var1;
            this.connectionAccess.openDialog(var6.dialog());
            break;
         case 2:
            ClickEvent.Custom var7 = (ClickEvent.Custom)var1;
            this.connectionAccess.sendCustomAction(var7.id(), var7.payload());
            break;
         default:
            defaultHandleClickEvent(var1, this.minecraft, this.previousScreen);
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

         var2.addChild(var8, var4, 0, 1, var1);
      }

      return var2;
   }

   public static class WarningScreen extends ConfirmScreen {
      private final MutableObject<DialogScreen<?>> returnScreen;

      public static Screen create(Minecraft var0, DialogConnectionAccess var1, DialogScreen<?> var2) {
         return new WarningScreen(var0, var1, new MutableObject(var2));
      }

      private WarningScreen(Minecraft var1, DialogConnectionAccess var2, MutableObject<DialogScreen<?>> var3) {
         super((var3x) -> {
            if (var3x) {
               var1.setScreen((Screen)null);
               var2.disconnect(DialogScreen.DISCONNECT);
            } else {
               var1.setScreen((Screen)var3.getValue());
            }

         }, Component.translatable("menu.custom_screen_info.title"), Component.translatable("menu.custom_screen_info.contents"), CommonComponents.disconnectButtonLabel(var1.isLocalServer()), CommonComponents.GUI_BACK);
         this.returnScreen = var3;
      }

      public DialogScreen<?> returnScreen() {
         return (DialogScreen)this.returnScreen.getValue();
      }

      public void updateReturnScreen(DialogScreen<?> var1) {
         this.returnScreen.setValue(var1);
      }

      public void clearReturnScreen() {
         this.returnScreen.setValue((Object)null);
      }
   }
}
