package net.minecraft.client.gui.screens.worldselection;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.flag.FeatureFlags;

public class ConfirmExperimentalFeaturesScreen extends Screen {
   private static final Component TITLE = Component.translatable("selectWorld.experimental.title");
   private static final Component MESSAGE = Component.translatable("selectWorld.experimental.message");
   private static final Component DETAILS_BUTTON = Component.translatable("selectWorld.experimental.details");
   private static final int COLUMN_SPACING = 10;
   private static final int DETAILS_BUTTON_WIDTH = 100;
   private final BooleanConsumer callback;
   private final Collection<Pack> enabledPacks;
   private final GridLayout layout = (new GridLayout()).columnSpacing(10).rowSpacing(20);

   public ConfirmExperimentalFeaturesScreen(final Collection<Pack> enabledPacks, final BooleanConsumer callback) {
      super(TITLE);
      this.enabledPacks = enabledPacks;
      this.callback = callback;
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), MESSAGE);
   }

   protected void init() {
      super.init();
      GridLayout.RowHelper helper = this.layout.createRowHelper(2);
      LayoutSettings centered = helper.newCellSettings().alignHorizontallyCenter();
      helper.addChild(new StringWidget(this.title, this.font), 2, centered);
      MultiLineTextWidget messageLabel = (MultiLineTextWidget)helper.addChild((new MultiLineTextWidget(MESSAGE, this.font)).setCentered(true), 2, centered);
      messageLabel.setMaxWidth(310);
      helper.addChild(Button.builder(DETAILS_BUTTON, (button) -> this.minecraft.setScreen(new DetailsScreen())).width(100).build(), 2, centered);
      helper.addChild(Button.builder(CommonComponents.GUI_PROCEED, (button) -> this.callback.accept(true)).build());
      helper.addChild(Button.builder(CommonComponents.GUI_BACK, (button) -> this.callback.accept(false)).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.layout.arrangeElements();
      this.repositionElements();
   }

   protected void repositionElements() {
      FrameLayout.alignInRectangle(this.layout, 0, 0, this.width, this.height, 0.5F, 0.5F);
   }

   public void onClose() {
      this.callback.accept(false);
   }

   private class DetailsScreen extends Screen {
      private static final Component TITLE = Component.translatable("selectWorld.experimental.details.title");
      private final HeaderAndFooterLayout layout;
      private PackList list;

      private DetailsScreen() {
         Objects.requireNonNull(ConfirmExperimentalFeaturesScreen.this);
         super(TITLE);
         this.layout = new HeaderAndFooterLayout(this);
      }

      protected void init() {
         this.layout.addTitleHeader(TITLE, this.font);
         this.list = (PackList)this.layout.addToContents(new PackList(this.minecraft, ConfirmExperimentalFeaturesScreen.this.enabledPacks));
         this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose()).build());
         this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
         this.repositionElements();
      }

      protected void repositionElements() {
         if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
         }

         this.layout.arrangeElements();
      }

      public void onClose() {
         this.minecraft.setScreen(ConfirmExperimentalFeaturesScreen.this);
      }

      private class PackList extends ObjectSelectionList<PackListEntry> {
         public PackList(final Minecraft minecraft, final Collection<Pack> selectedPacks) {
            Objects.requireNonNull(DetailsScreen.this);
            int var10002 = DetailsScreen.this.width;
            int var10003 = DetailsScreen.this.layout.getContentHeight();
            int var10004 = DetailsScreen.this.layout.getHeaderHeight();
            Objects.requireNonNull(minecraft.font);
            super(minecraft, var10002, var10003, var10004, (9 + 2) * 3);

            for(Pack pack : selectedPacks) {
               String nonVanillaFeatures = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_SET, pack.getRequestedFeatures());
               if (!nonVanillaFeatures.isEmpty()) {
                  Component title = ComponentUtils.mergeStyles(pack.getTitle(), Style.EMPTY.withBold(true));
                  Component message = Component.translatable("selectWorld.experimental.details.entry", nonVanillaFeatures);
                  this.addEntry(DetailsScreen.this.new PackListEntry(title, message, MultiLineLabel.create(DetailsScreen.this.font, message, this.getRowWidth())));
               }
            }

         }

         public int getRowWidth() {
            return this.width * 3 / 4;
         }
      }

      private class PackListEntry extends ObjectSelectionList.Entry<PackListEntry> {
         private final Component packId;
         private final Component message;
         private final MultiLineLabel splitMessage;

         private PackListEntry(final Component packId, final Component message, final MultiLineLabel splitMessage) {
            Objects.requireNonNull(DetailsScreen.this);
            super();
            this.packId = packId;
            this.message = message;
            this.splitMessage = splitMessage;
         }

         public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            ActiveTextCollector textRenderer = graphics.textRenderer();
            graphics.text(DetailsScreen.this.minecraft.font, (Component)this.packId, this.getContentX(), this.getContentY(), -1);
            MultiLineLabel var10000 = this.splitMessage;
            TextAlignment var10001 = TextAlignment.LEFT;
            int var10002 = this.getContentX();
            int var10003 = this.getContentY() + 12;
            Objects.requireNonNull(DetailsScreen.this.font);
            var10000.visitLines(var10001, var10002, var10003, 9, textRenderer);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.packId, this.message));
         }
      }
   }
}
