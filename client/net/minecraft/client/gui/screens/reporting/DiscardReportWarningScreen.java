package net.minecraft.client.gui.screens.reporting;

import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class DiscardReportWarningScreen extends Screen {
   private static final Identifier DRAFT_REPORT_SPRITE = Identifier.withDefaultNamespace("icon/draft_report");
   private static final int HEADER_HEIGHT = 21;
   private static final int HEADER_CONTENT_MARGIN = -2;
   private static final int FOOTER_HEIGHT = 33;
   private static final int SEPARATOR_HEIGHT = 2;
   private static final int MESSAGE_WIDTH = 300;
   private final @Nullable Screen lastScreen;
   private final Component message;
   private final Consumer<Builder> buttonBuilder;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 21, 33);
   private @Nullable LinearLayout contents;

   public DiscardReportWarningScreen(final @Nullable Screen lastScreen, final Component title, final Component message, final Consumer<Builder> buttonBuilder) {
      super(title);
      this.lastScreen = lastScreen;
      this.message = message;
      this.buttonBuilder = buttonBuilder;
   }

   protected void init() {
      this.layout.setContentMarginTop(-2);
      this.layout.addTitleHeader(this.title, this.font);
      this.contents = (LinearLayout)this.layout.addToContents(LinearLayout.vertical().spacing(8));
      this.contents.defaultCellSetting().alignVerticallyMiddle().alignHorizontallyCenter();
      this.contents.addChild(FocusableTextWidget.builder(this.message, this.font).maxWidth(300).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build());
      this.buttonBuilder.accept(this::addButton);
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (var1) -> this.onClose()).width(200).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
   }

   private Button addButton(final Component message, final boolean hasDraftSprite, final Button.OnPress onPress) {
      Button.Builder<?> builder = (Button.Builder<?>)(hasDraftSprite ? SpriteIconButton.builder(message, onPress, SpriteIconButton.DisplayState.TEXT_AND_ICON).sprite((Identifier)DRAFT_REPORT_SPRITE, 15, 15) : Button.builder(message, onPress));
      return (Button)this.contents.addChild(builder.width(200).build());
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.contents != null) {
         FrameLayout.centerInRectangle(this.contents, this.layout.getX(), this.layout.getY() + this.layout.getHeaderHeight(), this.layout.getWidth(), this.layout.getContentHeight());
      }

   }

   public void onClose() {
      this.minecraft.gui.setScreen(this.lastScreen);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void extractMenuBackground(final GuiGraphicsExtractor graphics) {
      super.extractMenuBackground(graphics);
      boolean inWorld = this.minecraft.level != null;
      Identifier menuListBackground = inWorld ? Screen.INWORLD_MENU_LIST_BACKGROUND : Screen.MENU_LIST_BACKGROUND;
      graphics.blit(RenderPipelines.GUI_TEXTURED, menuListBackground, this.layout.getX(), this.layout.getHeaderHeight(), (float)this.width, (float)(this.height - this.layout.getFooterHeight()), this.width, this.layout.getContentHeight(), 32, 32);
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int xm, final int ym, final float a) {
      super.extractRenderState(graphics, xm, ym, a);
      this.extractListSeparators(graphics, this.layout, 2);
   }

   @FunctionalInterface
   public interface Builder {
      Button addButton(Component message, boolean hasDraftSprite, Button.OnPress onPress);
   }
}
