package net.minecraft.client.gui.screens;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class CreateFlatWorldScreen extends Screen {
   private static final Component TITLE = Component.translatable("createWorld.customize.flat.title");
   static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");
   private static final int SLOT_BG_SIZE = 18;
   private static final int SLOT_STAT_HEIGHT = 20;
   private static final int SLOT_BG_X = 1;
   private static final int SLOT_BG_Y = 1;
   private static final int SLOT_FG_X = 2;
   private static final int SLOT_FG_Y = 2;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 64);
   protected final CreateWorldScreen parent;
   private final Consumer<FlatLevelGeneratorSettings> applySettings;
   FlatLevelGeneratorSettings generator;
   @Nullable
   private DetailsList list;
   @Nullable
   private Button deleteLayerButton;

   public CreateFlatWorldScreen(CreateWorldScreen var1, Consumer<FlatLevelGeneratorSettings> var2, FlatLevelGeneratorSettings var3) {
      super(TITLE);
      this.parent = var1;
      this.applySettings = var2;
      this.generator = var3;
   }

   public FlatLevelGeneratorSettings settings() {
      return this.generator;
   }

   public void setConfig(FlatLevelGeneratorSettings var1) {
      this.generator = var1;
   }

   protected void init() {
      this.layout.addTitleHeader(this.title, this.font);
      this.list = (DetailsList)this.layout.addToContents(new DetailsList());
      LinearLayout var1 = (LinearLayout)this.layout.addToFooter(LinearLayout.vertical().spacing(4));
      var1.defaultCellSetting().alignVerticallyMiddle();
      LinearLayout var2 = (LinearLayout)var1.addChild(LinearLayout.horizontal().spacing(8));
      LinearLayout var3 = (LinearLayout)var1.addChild(LinearLayout.horizontal().spacing(8));
      this.deleteLayerButton = (Button)var2.addChild(Button.builder(Component.translatable("createWorld.customize.flat.removeLayer"), (var1x) -> {
         if (this.hasValidSelection()) {
            List var2 = this.generator.getLayersInfo();
            int var3 = this.list.children().indexOf(this.list.getSelected());
            int var4 = var2.size() - var3 - 1;
            var2.remove(var4);
            this.list.setSelected(var2.isEmpty() ? null : (DetailsList.Entry)this.list.children().get(Math.min(var3, var2.size() - 1)));
            this.generator.updateLayers();
            this.list.resetRows();
            this.updateButtonValidity();
         }
      }).build());
      var2.addChild(Button.builder(Component.translatable("createWorld.customize.presets"), (var1x) -> {
         this.minecraft.setScreen(new PresetFlatWorldScreen(this));
         this.generator.updateLayers();
         this.updateButtonValidity();
      }).build());
      var3.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.applySettings.accept(this.generator);
         this.onClose();
         this.generator.updateLayers();
      }).build());
      var3.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> {
         this.onClose();
         this.generator.updateLayers();
      }).build());
      this.generator.updateLayers();
      this.updateButtonValidity();
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   protected void repositionElements() {
      if (this.list != null) {
         this.list.updateSize(this.width, this.layout);
      }

      this.layout.arrangeElements();
   }

   void updateButtonValidity() {
      if (this.deleteLayerButton != null) {
         this.deleteLayerButton.active = this.hasValidSelection();
      }

   }

   private boolean hasValidSelection() {
      return this.list != null && this.list.getSelected() != null;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   private class DetailsList extends ObjectSelectionList<Entry> {
      private static final Component LAYER_MATERIAL_TITLE;
      private static final Component HEIGHT_TITLE;

      public DetailsList() {
         super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height - 103, 43, 24);
         Objects.requireNonNull(CreateFlatWorldScreen.this.font);
         this.setRenderHeader(true, (int)(9.0 * 1.5));

         for(int var2 = 0; var2 < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++var2) {
            this.addEntry(new Entry());
         }

      }

      public void setSelected(@Nullable Entry var1) {
         super.setSelected(var1);
         CreateFlatWorldScreen.this.updateButtonValidity();
      }

      public void resetRows() {
         int var1 = this.children().indexOf(this.getSelected());
         this.clearEntries();

         for(int var2 = 0; var2 < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++var2) {
            this.addEntry(new Entry());
         }

         List var3 = this.children();
         if (var1 >= 0 && var1 < var3.size()) {
            this.setSelected((Entry)var3.get(var1));
         }

      }

      protected void renderHeader(GuiGraphics var1, int var2, int var3) {
         var1.drawString(CreateFlatWorldScreen.this.font, (Component)LAYER_MATERIAL_TITLE, var2, var3, -1);
         var1.drawString(CreateFlatWorldScreen.this.font, (Component)HEIGHT_TITLE, var2 + this.getRowWidth() - CreateFlatWorldScreen.this.font.width((FormattedText)HEIGHT_TITLE) - 8, var3, -1);
      }

      static {
         LAYER_MATERIAL_TITLE = Component.translatable("createWorld.customize.flat.tile").withStyle(ChatFormatting.UNDERLINE);
         HEIGHT_TITLE = Component.translatable("createWorld.customize.flat.height").withStyle(ChatFormatting.UNDERLINE);
      }

      class Entry extends ObjectSelectionList.Entry<Entry> {
         Entry() {
            super();
         }

         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            FlatLayerInfo var11 = (FlatLayerInfo)CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - var2 - 1);
            BlockState var12 = var11.getBlockState();
            ItemStack var13 = this.getDisplayItem(var12);
            this.blitSlot(var1, var4, var3, var13);
            int var10000 = var3 + var6 / 2;
            Objects.requireNonNull(CreateFlatWorldScreen.this.font);
            int var14 = var10000 - 9 / 2;
            var1.drawString(CreateFlatWorldScreen.this.font, (Component)var13.getHoverName(), var4 + 18 + 5, var14, -1);
            MutableComponent var15;
            if (var2 == 0) {
               var15 = Component.translatable("createWorld.customize.flat.layer.top", var11.getHeight());
            } else if (var2 == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
               var15 = Component.translatable("createWorld.customize.flat.layer.bottom", var11.getHeight());
            } else {
               var15 = Component.translatable("createWorld.customize.flat.layer", var11.getHeight());
            }

            var1.drawString(CreateFlatWorldScreen.this.font, (Component)var15, var4 + var5 - CreateFlatWorldScreen.this.font.width((FormattedText)var15) - 8, var14, -1);
         }

         private ItemStack getDisplayItem(BlockState var1) {
            Item var2 = var1.getBlock().asItem();
            if (var2 == Items.AIR) {
               if (var1.is(Blocks.WATER)) {
                  var2 = Items.WATER_BUCKET;
               } else if (var1.is(Blocks.LAVA)) {
                  var2 = Items.LAVA_BUCKET;
               }
            }

            return new ItemStack(var2);
         }

         public Component getNarration() {
            FlatLayerInfo var1 = (FlatLayerInfo)CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - DetailsList.this.children().indexOf(this) - 1);
            ItemStack var2 = this.getDisplayItem(var1.getBlockState());
            return (Component)(!var2.isEmpty() ? Component.translatable("narrator.select", var2.getHoverName()) : CommonComponents.EMPTY);
         }

         public boolean mouseClicked(double var1, double var3, int var5) {
            DetailsList.this.setSelected(this);
            return super.mouseClicked(var1, var3, var5);
         }

         private void blitSlot(GuiGraphics var1, int var2, int var3, ItemStack var4) {
            this.blitSlotBg(var1, var2 + 1, var3 + 1);
            if (!var4.isEmpty()) {
               var1.renderFakeItem(var4, var2 + 2, var3 + 2);
            }

         }

         private void blitSlotBg(GuiGraphics var1, int var2, int var3) {
            var1.blitSprite(RenderType::guiTextured, (ResourceLocation)CreateFlatWorldScreen.SLOT_SPRITE, var2, var3, 18, 18);
         }
      }
   }
}
