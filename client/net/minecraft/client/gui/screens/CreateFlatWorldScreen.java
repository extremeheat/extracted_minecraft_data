package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class CreateFlatWorldScreen extends Screen {
   protected final CreateWorldScreen parent;
   private final Consumer<FlatLevelGeneratorSettings> applySettings;
   private FlatLevelGeneratorSettings generator;
   private Component columnType;
   private Component columnHeight;
   private CreateFlatWorldScreen.DetailsList list;
   private Button deleteLayerButton;

   public CreateFlatWorldScreen(CreateWorldScreen var1, Consumer<FlatLevelGeneratorSettings> var2, FlatLevelGeneratorSettings var3) {
      super(new TranslatableComponent("createWorld.customize.flat.title"));
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
      this.columnType = new TranslatableComponent("createWorld.customize.flat.tile");
      this.columnHeight = new TranslatableComponent("createWorld.customize.flat.height");
      this.list = new CreateFlatWorldScreen.DetailsList();
      this.children.add(this.list);
      this.deleteLayerButton = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 52, 150, 20, new TranslatableComponent("createWorld.customize.flat.removeLayer"), (var1) -> {
         if (this.hasValidSelection()) {
            List var2 = this.generator.getLayersInfo();
            int var3 = this.list.children().indexOf(this.list.getSelected());
            int var4 = var2.size() - var3 - 1;
            var2.remove(var4);
            this.list.setSelected(var2.isEmpty() ? null : (CreateFlatWorldScreen.DetailsList.Entry)this.list.children().get(Math.min(var3, var2.size() - 1)));
            this.generator.updateLayers();
            this.list.resetRows();
            this.updateButtonValidity();
         }
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 52, 150, 20, new TranslatableComponent("createWorld.customize.presets"), (var1) -> {
         this.minecraft.setScreen(new PresetFlatWorldScreen(this));
         this.generator.updateLayers();
         this.updateButtonValidity();
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, CommonComponents.GUI_DONE, (var1) -> {
         this.applySettings.accept(this.generator);
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (var1) -> {
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
      }));
      this.generator.updateLayers();
      this.updateButtonValidity();
   }

   private void updateButtonValidity() {
      this.deleteLayerButton.active = this.hasValidSelection();
   }

   private boolean hasValidSelection() {
      return this.list.getSelected() != null;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.list.render(var1, var2, var3, var4);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 8, 16777215);
      int var5 = this.width / 2 - 92 - 16;
      drawString(var1, this.font, this.columnType, var5, 32, 16777215);
      drawString(var1, this.font, this.columnHeight, var5 + 2 + 213 - this.font.width((FormattedText)this.columnHeight), 32, 16777215);
      super.render(var1, var2, var3, var4);
   }

   class DetailsList extends ObjectSelectionList<CreateFlatWorldScreen.DetailsList.Entry> {
      public DetailsList() {
         super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);

         for(int var2 = 0; var2 < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++var2) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.Entry());
         }

      }

      public void setSelected(@Nullable CreateFlatWorldScreen.DetailsList.Entry var1) {
         super.setSelected(var1);
         if (var1 != null) {
            FlatLayerInfo var2 = (FlatLayerInfo)CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - this.children().indexOf(var1) - 1);
            ItemStack var3 = new ItemStack(var2.getBlockState().getBlock());
            if (!var3.isEmpty()) {
               NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select", new Object[]{var3.getItem().getName(var3)})).getString());
            }
         }

         CreateFlatWorldScreen.this.updateButtonValidity();
      }

      protected boolean isFocused() {
         return CreateFlatWorldScreen.this.getFocused() == this;
      }

      protected int getScrollbarPosition() {
         return this.width - 70;
      }

      public void resetRows() {
         int var1 = this.children().indexOf(this.getSelected());
         this.clearEntries();

         for(int var2 = 0; var2 < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++var2) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.Entry());
         }

         List var3 = this.children();
         if (var1 >= 0 && var1 < var3.size()) {
            this.setSelected((CreateFlatWorldScreen.DetailsList.Entry)var3.get(var1));
         }

      }

      class Entry extends ObjectSelectionList.Entry<CreateFlatWorldScreen.DetailsList.Entry> {
         private Entry() {
            super();
         }

         public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            FlatLayerInfo var11 = (FlatLayerInfo)CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - var2 - 1);
            BlockState var12 = var11.getBlockState();
            Item var13 = var12.getBlock().asItem();
            if (var13 == Items.AIR) {
               if (var12.is(Blocks.WATER)) {
                  var13 = Items.WATER_BUCKET;
               } else if (var12.is(Blocks.LAVA)) {
                  var13 = Items.LAVA_BUCKET;
               }
            }

            ItemStack var14 = new ItemStack(var13);
            this.blitSlot(var1, var4, var3, var14);
            CreateFlatWorldScreen.this.font.draw(var1, var13.getName(var14), (float)(var4 + 18 + 5), (float)(var3 + 3), 16777215);
            String var15;
            if (var2 == 0) {
               var15 = I18n.get("createWorld.customize.flat.layer.top", var11.getHeight());
            } else if (var2 == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
               var15 = I18n.get("createWorld.customize.flat.layer.bottom", var11.getHeight());
            } else {
               var15 = I18n.get("createWorld.customize.flat.layer", var11.getHeight());
            }

            CreateFlatWorldScreen.this.font.draw(var1, var15, (float)(var4 + 2 + 213 - CreateFlatWorldScreen.this.font.width(var15)), (float)(var3 + 3), 16777215);
         }

         public boolean mouseClicked(double var1, double var3, int var5) {
            if (var5 == 0) {
               DetailsList.this.setSelected(this);
               return true;
            } else {
               return false;
            }
         }

         private void blitSlot(PoseStack var1, int var2, int var3, ItemStack var4) {
            this.blitSlotBg(var1, var2 + 1, var3 + 1);
            RenderSystem.enableRescaleNormal();
            if (!var4.isEmpty()) {
               CreateFlatWorldScreen.this.itemRenderer.renderGuiItem(var4, var2 + 2, var3 + 2);
            }

            RenderSystem.disableRescaleNormal();
         }

         private void blitSlotBg(PoseStack var1, int var2, int var3) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            DetailsList.this.minecraft.getTextureManager().bind(GuiComponent.STATS_ICON_LOCATION);
            GuiComponent.blit(var1, var2, var3, CreateFlatWorldScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
         }

         // $FF: synthetic method
         Entry(Object var2) {
            this();
         }
      }
   }
}
