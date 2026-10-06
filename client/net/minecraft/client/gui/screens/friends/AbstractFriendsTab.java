package net.minecraft.client.gui.screens.friends;

import java.net.URI;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.PrivacyConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class AbstractFriendsTab implements Tab {
   public static final Identifier LIST_SEPARATOR_TOP = Identifier.withDefaultNamespace("friends/list_separator_top");
   public static final Identifier BACKGROUND_LIGHT_SPRITE = Identifier.withDefaultNamespace("friends/background_light");
   public static final int SPACING = 8;
   public static final int SCROLLBAR_SPACING = 0;
   public static final int LIST_MARGIN = 6;
   public static final int SEPARATOR_HEIGHT = 2;
   private static final int MANAGE_ACCOUNT_FOOTER_MARGIN = 6;
   private final LinearLayout layout;
   protected final FrameLayout headerFrame;
   protected final LinearLayout scrollableContent;
   protected final ScrollableLayout scrollableLayout;
   protected final int width;
   protected int height;

   public AbstractFriendsTab(final Minecraft minecraft, final int width, final int height) {
      super();
      this.width = width;
      this.height = height;
      this.layout = LinearLayout.vertical();
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.headerFrame = new FrameLayout(0, 0, width, 0);
      this.scrollableContent = LinearLayout.vertical();
      this.scrollableLayout = new ScrollableLayout(minecraft, this.scrollableContent, height, ScrollableLayout.ReserveStrategy.BOTH);
      this.scrollableLayout.setScrollbarSpacing(0);
      this.layout.addChild(this.headerFrame);
      this.layout.addChild(this.scrollableLayout);
   }

   protected int getListContentWidth() {
      return this.width - 12;
   }

   protected void rearrangeElements() {
      this.layout.arrangeElements();
      int listHeight = this.height - this.headerFrame.getHeight();
      this.scrollableLayout.setMinHeight(listHeight);
      this.scrollableLayout.setMaxHeight(listHeight);
   }

   public void visitChildren(final Consumer<AbstractWidget> childrenConsumer) {
      this.layout.visitWidgets(childrenConsumer);
   }

   public void doLayout(final ScreenRectangle screenRectangle) {
      this.layout.arrangeElements();
      FrameLayout.alignInRectangle(this.layout, screenRectangle, 0.5F, 0.16666667F);
   }

   public Layout getLayout() {
      return this.layout;
   }

   public final void disable() {
      this.scrollableContent.visitWidgets((widget) -> {
         if (widget instanceof AbstractFriendsEntryContainerWidget entry) {
            entry.disable();
         }

      });
   }

   public void setHeight(final int height) {
      this.height = height;
      this.rearrangeElements();
   }

   protected void showCenteredContent(final LayoutElement content) {
      this.scrollableLayout.alignVerticallyMiddle();
      this.scrollableContent.removeChildren();
      this.scrollableContent.addChild(content);
   }

   protected FocusableTextWidget createText(final Component message, final Font font, final int maxWidth) {
      return FocusableTextWidget.builder(message, font).maxWidth(maxWidth).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build();
   }

   protected MultiLineTextWidget createCenteredText(final Component message, final Font font, final int maxWidth) {
      return this.createText(message, font, maxWidth).setCentered(true);
   }

   protected void showManageAccountFooter(final Screen parentScreen, final Component message) {
      this.scrollableContent.addChild(this.createManageAccountFooter(parentScreen, message), (Consumer)((settings) -> settings.paddingTop(6)));
   }

   protected FrameLayout createManageAccountFooter(final Screen parentScreen, final Component message) {
      int maxWidth = this.getListContentWidth();
      MultiLineTextWidget textWidget = this.createCenteredText(message, parentScreen.getFont(), maxWidth);
      textWidget.setComponentClickHandler((style) -> {
         ClickEvent patt1$temp = style.getClickEvent();
         if (patt1$temp instanceof ClickEvent.OpenUrl $b$0) {
            ClickEvent.OpenUrl var10000 = $b$0;

            try {
               var7 = var10000.uri();
            } catch (Throwable var6) {
               throw new MatchException(var6.toString(), var6);
            }

            URI patt2$temp = var7;
            AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
            PrivacyConfirmLinkScreen.confirmLinkNow(parentScreen, patt2$temp);
         }

      });
      FrameLayout frame = new FrameLayout(maxWidth, textWidget.getHeight());
      frame.defaultChildLayoutSetting().alignHorizontallyCenter().alignVerticallyMiddle();
      frame.addChild(textWidget);
      return frame;
   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
   }
}
