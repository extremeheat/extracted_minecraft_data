package net.minecraft.client.gui.screens.dialog;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
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
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.Input;
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
   private Supplier<Optional<ClickEvent>> onClose;

   public DialogScreen(@Nullable Screen var1, T var2, DialogConnectionAccess var3) {
      super(var2.common().title());
      this.onClose = DialogControlSet.EMPTY_ACTION;
      this.dialog = var2;
      this.previousScreen = var1;
      this.connectionAccess = var3;
   }

   protected final void init() {
      super.init();
      this.warningButton = this.createWarningButton();
      this.warningButton.setTabOrderGroup(-10);
      DialogControlSet var1 = new DialogControlSet(this);
      LinearLayout var2 = LinearLayout.vertical().spacing(10);
      var2.defaultCellSetting().alignHorizontallyCenter();
      this.layout.addToHeader(this.createTitleWithWarningButton());

      for(DialogBody var4 : this.dialog.common().body()) {
         LayoutElement var5 = DialogBodyHandlers.createBodyElement(this, var4);
         if (var5 != null) {
            var2.addChild(var5);
         }
      }

      for(Input var7 : this.dialog.common().inputs()) {
         Objects.requireNonNull(var2);
         var1.addInput(var7, var2::addChild);
      }

      this.populateBodyElements(var2, var1, this.dialog, this.connectionAccess);
      this.bodyScroll = new ScrollableLayout(this.minecraft, var2, this.layout.getContentHeight());
      this.layout.addToContents(this.bodyScroll);
      this.updateHeaderAndFooter(this.layout, var1, this.dialog, this.connectionAccess);
      this.onClose = var1.bindAction(this.dialog.onCancel());
      this.layout.visitWidgets((var1x) -> {
         if (var1x != this.warningButton) {
            this.addRenderableWidget(var1x);
         }

      });
      this.addRenderableWidget(this.warningButton);
      this.repositionElements();
   }

   protected void populateBodyElements(LinearLayout var1, DialogControlSet var2, T var3, DialogConnectionAccess var4) {
   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, DialogControlSet var2, T var3, DialogConnectionAccess var4) {
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

   public boolean isPauseScreen() {
      return this.dialog.common().pause();
   }

   public boolean shouldCloseOnEsc() {
      return this.dialog.common().canCloseWithEscape();
   }

   public void onClose() {
      this.runAction((Optional)this.onClose.get(), DialogAction.CLOSE);
   }

   public void runAction(Optional<ClickEvent> var1) {
      this.runAction(var1, this.dialog.common().afterAction());
   }

   public void runAction(Optional<ClickEvent> var1, DialogAction var2) {
      Object var10000;
      switch (var2) {
         case NONE -> var10000 = this;
         case CLOSE -> var10000 = this.previousScreen;
         case WAIT_FOR_RESPONSE -> var10000 = new WaitingForResponseScreen(this.previousScreen);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      Object var3 = var10000;
      if (var1.isPresent()) {
         this.handleDialogClickEvent((ClickEvent)var1.get(), (Screen)var3);
      } else {
         this.minecraft.setScreen((Screen)var3);
      }

   }

   private void handleDialogClickEvent(ClickEvent var1, @Nullable Screen var2) {
      Objects.requireNonNull(var1);
      byte var4 = 0;
      //$FF: var4->value
      //0->net/minecraft/network/chat/ClickEvent$RunCommand
      //1->net/minecraft/network/chat/ClickEvent$ShowDialog
      //2->net/minecraft/network/chat/ClickEvent$Custom
      switch (var1.typeSwitch<invokedynamic>(var1, var4)) {
         case 0:
            ClickEvent.RunCommand var5 = (ClickEvent.RunCommand)var1;
            ClickEvent.RunCommand var10000 = var5;

            try {
               var11 = var10000.command();
            } catch (Throwable var9) {
               throw new MatchException(var9.toString(), var9);
            }

            String var10 = var11;
            this.connectionAccess.runCommand(Commands.trimOptionalPrefix(var10), var2);
            break;
         case 1:
            ClickEvent.ShowDialog var7 = (ClickEvent.ShowDialog)var1;
            this.connectionAccess.openDialog(var7.dialog(), var2);
            break;
         case 2:
            ClickEvent.Custom var8 = (ClickEvent.Custom)var1;
            this.connectionAccess.sendCustomAction(var8.id(), var8.payload());
            this.minecraft.setScreen(var2);
            break;
         default:
            defaultHandleClickEvent(var1, this.minecraft, var2);
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
      private final MutableObject<Screen> returnScreen;

      public static Screen create(Minecraft var0, DialogConnectionAccess var1, Screen var2) {
         return new WarningScreen(var0, var1, new MutableObject(var2));
      }

      private WarningScreen(Minecraft var1, DialogConnectionAccess var2, MutableObject<Screen> var3) {
         super((var3x) -> {
            if (var3x) {
               var2.disconnect(DialogScreen.DISCONNECT);
            } else {
               var1.setScreen((Screen)var3.getValue());
            }

         }, Component.translatable("menu.custom_screen_info.title"), Component.translatable("menu.custom_screen_info.contents"), CommonComponents.disconnectButtonLabel(var1.isLocalServer()), CommonComponents.GUI_BACK);
         this.returnScreen = var3;
      }

      @Nullable
      public Screen returnScreen() {
         return (Screen)this.returnScreen.getValue();
      }

      public void updateReturnScreen(@Nullable Screen var1) {
         this.returnScreen.setValue(var1);
      }
   }
}
