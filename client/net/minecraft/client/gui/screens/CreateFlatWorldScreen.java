package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class CreateFlatWorldScreen extends Screen {
   private final CreateWorldScreen parent;
   private FlatLevelGeneratorSettings generator = FlatLevelGeneratorSettings.getDefault();
   private String columnType;
   private String columnHeight;
   private CreateFlatWorldScreen.DetailsList list;
   private Button deleteLayerButton;

   public CreateFlatWorldScreen(CreateWorldScreen var1, CompoundTag var2) {
      super(new TranslatableComponent("createWorld.customize.flat.title", new Object[0]));
      this.parent = var1;
      this.loadLayers(var2);
   }

   public String saveLayerString() {
      return this.generator.toString();
   }

   public CompoundTag saveLayers() {
      return (CompoundTag)this.generator.toObject(NbtOps.INSTANCE).getValue();
   }

   public void loadLayers(String var1) {
      this.generator = FlatLevelGeneratorSettings.fromString(var1);
   }

   public void loadLayers(CompoundTag var1) {
      this.generator = FlatLevelGeneratorSettings.fromObject(new Dynamic(NbtOps.INSTANCE, var1));
   }

   protected void init() {
      this.columnType = I18n.get("createWorld.customize.flat.tile");
      this.columnHeight = I18n.get("createWorld.customize.flat.height");
      this.list = new CreateFlatWorldScreen.DetailsList();
      this.children.add(this.list);
      this.deleteLayerButton = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 52, 150, 20, I18n.get("createWorld.customize.flat.removeLayer"), (var1) -> {
         if (this.hasValidSelection()) {
            List var2 = this.generator.getLayersInfo();
            int var3 = this.list.children().indexOf(this.list.getSelected());
            int var4 = var2.size() - var3 - 1;
            var2.remove(var4);
            this.list.setSelected(var2.isEmpty() ? null : (CreateFlatWorldScreen.DetailsList.Entry)this.list.children().get(Math.min(var3, var2.size() - 1)));
            this.generator.updateLayers();
            this.updateButtonValidity();
         }
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 52, 150, 20, I18n.get("createWorld.customize.presets"), (var1) -> {
         this.minecraft.setScreen(new PresetFlatWorldScreen(this));
         this.generator.updateLayers();
         this.updateButtonValidity();
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("gui.done"), (var1) -> {
         this.parent.levelTypeOptions = this.saveLayers();
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
         this.updateButtonValidity();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), (var1) -> {
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
         this.updateButtonValidity();
      }));
      this.generator.updateLayers();
      this.updateButtonValidity();
   }

   public void updateButtonValidity() {
      this.deleteLayerButton.active = this.hasValidSelection();
      this.list.resetRows();
   }

   private boolean hasValidSelection() {
      return this.list.getSelected() != null;
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.list.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
      int var4 = this.width / 2 - 92 - 16;
      this.drawString(this.font, this.columnType, var4, 32, 16777215);
      this.drawString(this.font, this.columnHeight, var4 + 2 + 213 - this.font.width(this.columnHeight), 32, 16777215);
      super.render(var1, var2, var3);
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
            Item var3 = var2.getBlockState().getBlock().asItem();
            if (var3 != Items.AIR) {
               NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select", new Object[]{var3.getName(new ItemStack(var3))})).getString());
            }
         }

      }

      protected void moveSelection(int var1) {
         super.moveSelection(var1);
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

      // $FF: synthetic method
      public void setSelected(@Nullable AbstractSelectionList.Entry var1) {
         this.setSelected((CreateFlatWorldScreen.DetailsList.Entry)var1);
      }

      class Entry extends ObjectSelectionList.Entry<CreateFlatWorldScreen.DetailsList.Entry> {
         private Entry() {
            super();
         }

         public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
            FlatLayerInfo var10 = (FlatLayerInfo)CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - var1 - 1);
            BlockState var11 = var10.getBlockState();
            Block var12 = var11.getBlock();
            Item var13 = var12.asItem();
            if (var13 == Items.AIR) {
               if (var12 == Blocks.WATER) {
                  var13 = Items.WATER_BUCKET;
               } else if (var12 == Blocks.LAVA) {
                  var13 = Items.LAVA_BUCKET;
               }
            }

            ItemStack var14 = new ItemStack(var13);
            String var15 = var13.getName(var14).getColoredString();
            this.blitSlot(var3, var2, var14);
            CreateFlatWorldScreen.this.font.draw(var15, (float)(var3 + 18 + 5), (float)(var2 + 3), 16777215);
            String var16;
            if (var1 == 0) {
               var16 = I18n.get("createWorld.customize.flat.layer.top", var10.getHeight());
            } else if (var1 == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
               var16 = I18n.get("createWorld.customize.flat.layer.bottom", var10.getHeight());
            } else {
               var16 = I18n.get("createWorld.customize.flat.layer", var10.getHeight());
            }

            CreateFlatWorldScreen.this.font.draw(var16, (float)(var3 + 2 + 213 - CreateFlatWorldScreen.this.font.width(var16)), (float)(var2 + 3), 16777215);
         }

         public boolean mouseClicked(double var1, double var3, int var5) {
            if (var5 == 0) {
               DetailsList.this.setSelected(this);
               CreateFlatWorldScreen.this.updateButtonValidity();
               return true;
            } else {
               return false;
            }
         }

         private void blitSlot(int var1, int var2, ItemStack var3) {
            this.blitSlotBg(var1 + 1, var2 + 1);
            GlStateManager.enableRescaleNormal();
            if (!var3.isEmpty()) {
               Lighting.turnOnGui();
               CreateFlatWorldScreen.this.itemRenderer.renderGuiItem(var3, var1 + 2, var2 + 2);
               Lighting.turnOff();
            }

            GlStateManager.disableRescaleNormal();
         }

         private void blitSlotBg(int var1, int var2) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            DetailsList.this.minecraft.getTextureManager().bind(GuiComponent.STATS_ICON_LOCATION);
            GuiComponent.blit(var1, var2, CreateFlatWorldScreen.this.blitOffset, 0.0F, 0.0F, 18, 18, 128, 128);
         }

         // $FF: synthetic method
         Entry(Object var2) {
            this();
         }
      }
   }
}
