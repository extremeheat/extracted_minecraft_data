package net.minecraft.client.gui.screens.worldselection;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
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
   final Collection<Pack> enabledPacks;
   private final GridLayout layout = new GridLayout().columnSpacing(10).rowSpacing(20);

   public ConfirmExperimentalFeaturesScreen(Collection<Pack> var1, BooleanConsumer var2) {
      super(TITLE);
      this.enabledPacks = var1;
      this.callback = var2;
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), MESSAGE);
   }

   @Override
   protected void init() {
      super.init();
      GridLayout.RowHelper var1 = this.layout.createRowHelper(2);
      LayoutSettings var2 = var1.newCellSettings().alignHorizontallyCenter();
      var1.addChild(new StringWidget(this.title, this.font), 2, var2);
      MultiLineTextWidget var3 = var1.addChild(new MultiLineTextWidget(MESSAGE, this.font).setCentered(true), 2, var2);
      var3.setMaxWidth(310);
      var1.addChild(
         Button.builder(DETAILS_BUTTON, var1x -> this.minecraft.setScreen(new ConfirmExperimentalFeaturesScreen.DetailsScreen())).width(100).build(), 2, var2
      );
      var1.addChild(Button.builder(CommonComponents.GUI_PROCEED, var1x -> this.callback.accept(true)).build());
      var1.addChild(Button.builder(CommonComponents.GUI_BACK, var1x -> this.callback.accept(false)).build());
      this.layout.visitWidgets(var1x -> {
      });
      this.layout.arrangeElements();
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      FrameLayout.alignInRectangle(this.layout, 0, 0, this.width, this.height, 0.5F, 0.5F);
   }

   @Override
   public void onClose() {
      this.callback.accept(false);
   }

   class DetailsScreen extends Screen {
      private static final Component TITLE = Component.translatable("selectWorld.experimental.details.title");
      final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

      DetailsScreen() {
         super(TITLE);
      }

      @Override
      protected void init() {
         this.layout.addTitleHeader(TITLE, this.font);
         this.layout
            .addToContents(new ConfirmExperimentalFeaturesScreen.DetailsScreen.PackList(this.minecraft, ConfirmExperimentalFeaturesScreen.this.enabledPacks));
         this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, var1 -> this.onClose()).build());
         this.layout.visitWidgets(var1 -> {
         });
         this.repositionElements();
      }

      @Override
      protected void repositionElements() {
         this.layout.arrangeElements();
      }

      @Override
      public void onClose() {
         this.minecraft.setScreen(ConfirmExperimentalFeaturesScreen.this);
      }

      class PackList extends ObjectSelectionList<ConfirmExperimentalFeaturesScreen.DetailsScreen.PackListEntry> {
         public PackList(Minecraft var2, Collection<Pack> var3) {
            super(var2, DetailsScreen.this.width, DetailsScreen.this.layout.getContentHeight(), DetailsScreen.this.layout.getHeaderHeight(), (9 + 2) * 3);

            for(Pack var5 : var3) {
               String var6 = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_SET, var5.getRequestedFeatures());
               if (!var6.isEmpty()) {
                  MutableComponent var7 = ComponentUtils.mergeStyles(var5.getTitle().copy(), Style.EMPTY.withBold(true));
                  MutableComponent var8 = Component.translatable("selectWorld.experimental.details.entry", var6);
                  this.addEntry(DetailsScreen.this.new PackListEntry(var7, var8, MultiLineLabel.create(DetailsScreen.this.font, var8, this.getRowWidth())));
               }
            }
         }

         @Override
         public int getRowWidth() {
            return this.width * 3 / 4;
         }
      }

      class PackListEntry extends ObjectSelectionList.Entry<ConfirmExperimentalFeaturesScreen.DetailsScreen.PackListEntry> {
         private final Component packId;
         private final Component message;
         private final MultiLineLabel splitMessage;

         PackListEntry(Component var2, Component var3, MultiLineLabel var4) {
            super();
            this.packId = var2;
            this.message = var3;
            this.splitMessage = var4;
         }

         @Override
         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            var1.drawString(DetailsScreen.this.minecraft.font, this.packId, var4, var3, -1);
            this.splitMessage.renderLeftAligned(var1, var4, var3 + 12, 9, -1);
         }

         @Override
         public Component getNarration() {
            return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.packId, this.message));
         }
      }
   }
}
