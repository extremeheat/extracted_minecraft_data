package net.minecraft.client.gui.screens;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.RenderPipelines;
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
      if (this.list != null) {
         this.list.resetRows();
         this.updateButtonValidity();
      }

   }

   protected void init() {
      this.layout.addTitleHeader(this.title, this.font);
      this.list = (DetailsList)this.layout.addToContents(new DetailsList());
      LinearLayout var1 = (LinearLayout)this.layout.addToFooter(LinearLayout.vertical().spacing(4));
      var1.defaultCellSetting().alignVerticallyMiddle();
      LinearLayout var2 = (LinearLayout)var1.addChild(LinearLayout.horizontal().spacing(8));
      LinearLayout var3 = (LinearLayout)var1.addChild(LinearLayout.horizontal().spacing(8));
      this.deleteLayerButton = (Button)var2.addChild(Button.builder(Component.translatable("createWorld.customize.flat.removeLayer"), (var1x) -> {
         if (this.list != null) {
            AbstractSelectionList.Entry var3 = this.list.getSelected();
            if (var3 instanceof DetailsList.LayerEntry) {
               DetailsList.LayerEntry var2 = (DetailsList.LayerEntry)var3;
               this.list.deleteLayer(var2);
            }
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
      return this.list != null && this.list.getSelected() instanceof DetailsList.LayerEntry;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   class DetailsList extends ObjectSelectionList<Entry> {
      static final Component LAYER_MATERIAL_TITLE;
      static final Component HEIGHT_TITLE;

      public DetailsList() {
         super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height - 103, 43, 24);
         this.populateList();
      }

      private void populateList() {
         HeaderEntry var10001 = new HeaderEntry(CreateFlatWorldScreen.this.font);
         Objects.requireNonNull(CreateFlatWorldScreen.this.font);
         this.addEntry(var10001, (int)(9.0 * 1.5));
         List var1 = CreateFlatWorldScreen.this.generator.getLayersInfo().reversed();

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            this.addEntry(new LayerEntry((FlatLayerInfo)var1.get(var2), var2));
         }

      }

      public void setSelected(@Nullable Entry var1) {
         super.setSelected(var1);
         CreateFlatWorldScreen.this.updateButtonValidity();
      }

      public void resetRows() {
         int var1 = this.children().indexOf(this.getSelected());
         this.clearEntries();
         this.populateList();
         List var2 = this.children();
         if (var1 >= 0 && var1 < var2.size()) {
            this.setSelected((Entry)var2.get(var1));
         }

      }

      void deleteLayer(LayerEntry var1) {
         List var2 = CreateFlatWorldScreen.this.generator.getLayersInfo();
         int var3 = this.children().indexOf(var1);
         this.removeEntry(var1);
         var2.remove(var1.layerInfo);
         this.setSelected(var2.isEmpty() ? null : (Entry)this.children().get(Math.min(var3, var2.size())));
         CreateFlatWorldScreen.this.generator.updateLayers();
         this.resetRows();
         CreateFlatWorldScreen.this.updateButtonValidity();
      }

      static {
         LAYER_MATERIAL_TITLE = Component.translatable("createWorld.customize.flat.tile").withStyle(ChatFormatting.UNDERLINE);
         HEIGHT_TITLE = Component.translatable("createWorld.customize.flat.height").withStyle(ChatFormatting.UNDERLINE);
      }

      abstract static class Entry extends ObjectSelectionList.Entry<Entry> {
         Entry() {
            super();
         }
      }

      class LayerEntry extends Entry {
         final FlatLayerInfo layerInfo;
         private final int index;

         public LayerEntry(final FlatLayerInfo var2, final int var3) {
            super();
            this.layerInfo = var2;
            this.index = var3;
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            BlockState var6 = this.layerInfo.getBlockState();
            ItemStack var7 = this.getDisplayItem(var6);
            this.blitSlot(var1, this.getContentX(), this.getContentY(), var7);
            int var10000 = this.getContentYMiddle();
            Objects.requireNonNull(CreateFlatWorldScreen.this.font);
            int var8 = var10000 - 9 / 2;
            var1.drawString(CreateFlatWorldScreen.this.font, (Component)var7.getHoverName(), this.getContentX() + 18 + 5, var8, -1);
            MutableComponent var9;
            if (this.index == 0) {
               var9 = Component.translatable("createWorld.customize.flat.layer.top", this.layerInfo.getHeight());
            } else if (this.index == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
               var9 = Component.translatable("createWorld.customize.flat.layer.bottom", this.layerInfo.getHeight());
            } else {
               var9 = Component.translatable("createWorld.customize.flat.layer", this.layerInfo.getHeight());
            }

            var1.drawString(CreateFlatWorldScreen.this.font, (Component)var9, this.getContentRight() - CreateFlatWorldScreen.this.font.width((FormattedText)var9), var8, -1);
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
            ItemStack var1 = this.getDisplayItem(this.layerInfo.getBlockState());
            return (Component)(!var1.isEmpty() ? CommonComponents.joinForNarration(Component.translatable("narrator.select", var1.getHoverName()), CreateFlatWorldScreen.DetailsList.HEIGHT_TITLE, Component.literal(String.valueOf(this.layerInfo.getHeight()))) : CommonComponents.EMPTY);
         }

         public boolean mouseClicked(double var1, double var3, int var5, boolean var6) {
            DetailsList.this.setSelected((Entry)this);
            return super.mouseClicked(var1, var3, var5, var6);
         }

         private void blitSlot(GuiGraphics var1, int var2, int var3, ItemStack var4) {
            this.blitSlotBg(var1, var2 + 1, var3 + 1);
            if (!var4.isEmpty()) {
               var1.renderFakeItem(var4, var2 + 2, var3 + 2);
            }

         }

         private void blitSlotBg(GuiGraphics var1, int var2, int var3) {
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)CreateFlatWorldScreen.SLOT_SPRITE, var2, var3, 18, 18);
         }
      }

      static class HeaderEntry extends Entry {
         private final Font font;

         public HeaderEntry(Font var1) {
            super();
            this.font = var1;
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            var1.drawString(this.font, (Component)CreateFlatWorldScreen.DetailsList.LAYER_MATERIAL_TITLE, this.getContentX(), this.getContentY(), -1);
            var1.drawString(this.font, (Component)CreateFlatWorldScreen.DetailsList.HEIGHT_TITLE, this.getContentRight() - this.font.width((FormattedText)CreateFlatWorldScreen.DetailsList.HEIGHT_TITLE), this.getContentY(), -1);
         }

         public Component getNarration() {
            return CommonComponents.joinForNarration(CreateFlatWorldScreen.DetailsList.LAYER_MATERIAL_TITLE, CreateFlatWorldScreen.DetailsList.HEIGHT_TITLE);
         }
      }
   }
}
