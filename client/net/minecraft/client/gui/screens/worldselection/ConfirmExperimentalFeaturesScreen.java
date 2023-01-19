package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.util.Mth;
import net.minecraft.world.flag.FeatureFlags;

public class ConfirmExperimentalFeaturesScreen extends Screen {
   private static final Component TITLE = Component.translatable("selectWorld.experimental.title");
   private static final Component MESSAGE = Component.translatable("selectWorld.experimental.message");
   private static final Component DETAILS_BUTTON = Component.translatable("selectWorld.experimental.details");
   private static final int MARGIN = 20;
   private final BooleanConsumer callback;
   final Collection<Pack> enabledPacks;
   private MultiLineLabel multilineMessage = MultiLineLabel.EMPTY;

   public ConfirmExperimentalFeaturesScreen(Collection<Pack> var1, BooleanConsumer var2) {
      super(TITLE);
      this.enabledPacks = var1;
      this.callback = var2;
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), MESSAGE);
   }

   private int messageHeight() {
      return this.multilineMessage.getLineCount() * 9;
   }

   private int titleTop() {
      int var1 = (this.height - this.messageHeight()) / 2;
      return Mth.clamp(var1 - 20 - 9, 10, 80);
   }

   @Override
   protected void init() {
      super.init();
      this.multilineMessage = MultiLineLabel.create(this.font, MESSAGE, this.width - 50);
      int var1 = Mth.clamp(this.titleTop() + 20 + this.messageHeight() + 20, this.height / 6 + 96, this.height - 24);
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_PROCEED, var1x -> this.callback.accept(true)).bounds(this.width / 2 - 50 - 105, var1, 100, 20).build()
      );
      this.addRenderableWidget(
         Button.builder(DETAILS_BUTTON, var1x -> this.minecraft.setScreen(new ConfirmExperimentalFeaturesScreen.DetailsScreen()))
            .bounds(this.width / 2 - 50, var1, 100, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_BACK, var1x -> this.callback.accept(false)).bounds(this.width / 2 - 50 + 105, var1, 100, 20).build()
      );
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, this.titleTop(), 16777215);
      this.multilineMessage.renderCentered(var1, this.width / 2, this.titleTop() + 20);
      super.render(var1, var2, var3, var4);
   }

   @Override
   public void onClose() {
      this.callback.accept(false);
   }

   class DetailsScreen extends Screen {
      private ConfirmExperimentalFeaturesScreen.DetailsScreen.PackList packList;

      DetailsScreen() {
         super(Component.translatable("selectWorld.experimental.details.title"));
      }

      @Override
      public void onClose() {
         this.minecraft.setScreen(ConfirmExperimentalFeaturesScreen.this);
      }

      @Override
      protected void init() {
         super.init();
         this.addRenderableWidget(
            Button.builder(CommonComponents.GUI_BACK, var1 -> this.onClose()).bounds(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20).build()
         );
         this.packList = new ConfirmExperimentalFeaturesScreen.DetailsScreen.PackList(this.minecraft, ConfirmExperimentalFeaturesScreen.this.enabledPacks);
         this.addWidget(this.packList);
      }

      @Override
      public void render(PoseStack var1, int var2, int var3, float var4) {
         this.renderBackground(var1);
         this.packList.render(var1, var2, var3, var4);
         drawCenteredString(var1, this.font, this.title, this.width / 2, 10, 16777215);
         super.render(var1, var2, var3, var4);
      }

      class PackList extends ObjectSelectionList<ConfirmExperimentalFeaturesScreen.DetailsScreen.PackListEntry> {
         public PackList(Minecraft var2, Collection<Pack> var3) {
            super(var2, DetailsScreen.this.width, DetailsScreen.this.height, 32, DetailsScreen.this.height - 64, (9 + 2) * 3);

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

         @Override
         public boolean isFocused() {
            return DetailsScreen.this.getFocused() == this;
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
         public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            GuiComponent.drawString(var1, DetailsScreen.this.minecraft.font, this.packId, var4, var3, 16777215);
            this.splitMessage.renderLeftAligned(var1, var4, var3 + 12, 9, 16777215);
         }

         @Override
         public Component getNarration() {
            return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.packId, this.message));
         }
      }
   }
}
