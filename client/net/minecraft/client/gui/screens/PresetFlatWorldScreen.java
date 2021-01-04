package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class PresetFlatWorldScreen extends Screen {
   private static final List<PresetFlatWorldScreen.PresetInfo> PRESETS = Lists.newArrayList();
   private final CreateFlatWorldScreen parent;
   private String shareText;
   private String listText;
   private PresetFlatWorldScreen.PresetsList list;
   private Button selectButton;
   private EditBox export;

   public PresetFlatWorldScreen(CreateFlatWorldScreen var1) {
      super(new TranslatableComponent("createWorld.customize.presets.title", new Object[0]));
      this.parent = var1;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.shareText = I18n.get("createWorld.customize.presets.share");
      this.listText = I18n.get("createWorld.customize.presets.list");
      this.export = new EditBox(this.font, 50, 40, this.width - 100, 20, this.shareText);
      this.export.setMaxLength(1230);
      this.export.setValue(this.parent.saveLayerString());
      this.children.add(this.export);
      this.list = new PresetFlatWorldScreen.PresetsList();
      this.children.add(this.list);
      this.selectButton = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("createWorld.customize.presets.select"), (var1) -> {
         this.parent.loadLayers(this.export.getValue());
         this.minecraft.setScreen(this.parent);
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), (var1) -> {
         this.minecraft.setScreen(this.parent);
      }));
      this.updateButtonValidity(this.list.getSelected() != null);
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.list.mouseScrolled(var1, var3, var5);
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.export.getValue();
      this.init(var1, var2, var3);
      this.export.setValue(var4);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.list.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
      this.drawString(this.font, this.shareText, 50, 30, 10526880);
      this.drawString(this.font, this.listText, 50, 70, 10526880);
      this.export.render(var1, var2, var3);
      super.render(var1, var2, var3);
   }

   public void tick() {
      this.export.tick();
      super.tick();
   }

   public void updateButtonValidity(boolean var1) {
      this.selectButton.active = var1 || this.export.getValue().length() > 1;
   }

   private static void preset(String var0, ItemLike var1, Biome var2, List<String> var3, FlatLayerInfo... var4) {
      FlatLevelGeneratorSettings var5 = (FlatLevelGeneratorSettings)ChunkGeneratorType.FLAT.createSettings();

      for(int var6 = var4.length - 1; var6 >= 0; --var6) {
         var5.getLayersInfo().add(var4[var6]);
      }

      var5.setBiome(var2);
      var5.updateLayers();
      Iterator var8 = var3.iterator();

      while(var8.hasNext()) {
         String var7 = (String)var8.next();
         var5.getStructuresOptions().put(var7, Maps.newHashMap());
      }

      PRESETS.add(new PresetFlatWorldScreen.PresetInfo(var1.asItem(), var0, var5.toString()));
   }

   static {
      preset(I18n.get("createWorld.customize.preset.classic_flat"), Blocks.GRASS_BLOCK, Biomes.PLAINS, Arrays.asList("village"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(I18n.get("createWorld.customize.preset.tunnelers_dream"), Blocks.STONE, Biomes.MOUNTAINS, Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(I18n.get("createWorld.customize.preset.water_world"), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Arrays.asList("biome_1", "oceanmonument"), new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.SAND), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(I18n.get("createWorld.customize.preset.overworld"), Blocks.GRASS, Biomes.PLAINS, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake", "pillager_outpost"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(I18n.get("createWorld.customize.preset.snowy_kingdom"), Blocks.SNOW, Biomes.SNOWY_TUNDRA, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.SNOW), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(I18n.get("createWorld.customize.preset.bottomless_pit"), Items.FEATHER, Biomes.PLAINS, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE));
      preset(I18n.get("createWorld.customize.preset.desert"), Blocks.SAND, Biomes.DESERT, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"), new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(I18n.get("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(I18n.get("createWorld.customize.preset.the_void"), Blocks.BARRIER, Biomes.THE_VOID, Arrays.asList("decoration"), new FlatLayerInfo(1, Blocks.AIR));
   }

   static class PresetInfo {
      public final Item icon;
      public final String name;
      public final String value;

      public PresetInfo(Item var1, String var2, String var3) {
         super();
         this.icon = var1;
         this.name = var2;
         this.value = var3;
      }
   }

   class PresetsList extends ObjectSelectionList<PresetFlatWorldScreen.PresetsList.Entry> {
      public PresetsList() {
         super(PresetFlatWorldScreen.this.minecraft, PresetFlatWorldScreen.this.width, PresetFlatWorldScreen.this.height, 80, PresetFlatWorldScreen.this.height - 37, 24);

         for(int var2 = 0; var2 < PresetFlatWorldScreen.PRESETS.size(); ++var2) {
            this.addEntry(new PresetFlatWorldScreen.PresetsList.Entry());
         }

      }

      public void setSelected(@Nullable PresetFlatWorldScreen.PresetsList.Entry var1) {
         super.setSelected(var1);
         if (var1 != null) {
            NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select", new Object[]{((PresetFlatWorldScreen.PresetInfo)PresetFlatWorldScreen.PRESETS.get(this.children().indexOf(var1))).name})).getString());
         }

      }

      protected void moveSelection(int var1) {
         super.moveSelection(var1);
         PresetFlatWorldScreen.this.updateButtonValidity(true);
      }

      protected boolean isFocused() {
         return PresetFlatWorldScreen.this.getFocused() == this;
      }

      public boolean keyPressed(int var1, int var2, int var3) {
         if (super.keyPressed(var1, var2, var3)) {
            return true;
         } else {
            if ((var1 == 257 || var1 == 335) && this.getSelected() != null) {
               ((PresetFlatWorldScreen.PresetsList.Entry)this.getSelected()).select();
            }

            return false;
         }
      }

      // $FF: synthetic method
      public void setSelected(@Nullable AbstractSelectionList.Entry var1) {
         this.setSelected((PresetFlatWorldScreen.PresetsList.Entry)var1);
      }

      public class Entry extends ObjectSelectionList.Entry<PresetFlatWorldScreen.PresetsList.Entry> {
         public Entry() {
            super();
         }

         public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
            PresetFlatWorldScreen.PresetInfo var10 = (PresetFlatWorldScreen.PresetInfo)PresetFlatWorldScreen.PRESETS.get(var1);
            this.blitSlot(var3, var2, var10.icon);
            PresetFlatWorldScreen.this.font.draw(var10.name, (float)(var3 + 18 + 5), (float)(var2 + 6), 16777215);
         }

         public boolean mouseClicked(double var1, double var3, int var5) {
            if (var5 == 0) {
               this.select();
            }

            return false;
         }

         private void select() {
            PresetsList.this.setSelected(this);
            PresetFlatWorldScreen.this.updateButtonValidity(true);
            PresetFlatWorldScreen.this.export.setValue(((PresetFlatWorldScreen.PresetInfo)PresetFlatWorldScreen.PRESETS.get(PresetsList.this.children().indexOf(this))).value);
            PresetFlatWorldScreen.this.export.moveCursorToStart();
         }

         private void blitSlot(int var1, int var2, Item var3) {
            this.blitSlotBg(var1 + 1, var2 + 1);
            GlStateManager.enableRescaleNormal();
            Lighting.turnOnGui();
            PresetFlatWorldScreen.this.itemRenderer.renderGuiItem(new ItemStack(var3), var1 + 2, var2 + 2);
            Lighting.turnOff();
            GlStateManager.disableRescaleNormal();
         }

         private void blitSlotBg(int var1, int var2) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            PresetsList.this.minecraft.getTextureManager().bind(GuiComponent.STATS_ICON_LOCATION);
            GuiComponent.blit(var1, var2, PresetFlatWorldScreen.this.blitOffset, 0.0F, 0.0F, 18, 18, 128, 128);
         }
      }
   }
}
