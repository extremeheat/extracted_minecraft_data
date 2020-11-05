package net.minecraft.client.gui.screens;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PresetFlatWorldScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final List<PresetFlatWorldScreen.PresetInfo> PRESETS = Lists.newArrayList();
   private final CreateFlatWorldScreen parent;
   private Component shareText;
   private Component listText;
   private PresetFlatWorldScreen.PresetsList list;
   private Button selectButton;
   private EditBox export;
   private FlatLevelGeneratorSettings settings;

   public PresetFlatWorldScreen(CreateFlatWorldScreen var1) {
      super(new TranslatableComponent("createWorld.customize.presets.title"));
      this.parent = var1;
   }

   @Nullable
   private static FlatLayerInfo getLayerInfoFromString(String var0, int var1) {
      String[] var2 = var0.split("\\*", 2);
      int var3;
      if (var2.length == 2) {
         try {
            var3 = Math.max(Integer.parseInt(var2[0]), 0);
         } catch (NumberFormatException var10) {
            LOGGER.error("Error while parsing flat world string => {}", var10.getMessage());
            return null;
         }
      } else {
         var3 = 1;
      }

      int var4 = Math.min(var1 + var3, 256);
      int var5 = var4 - var1;
      String var7 = var2[var2.length - 1];

      Block var6;
      try {
         var6 = (Block)Registry.BLOCK.getOptional(new ResourceLocation(var7)).orElse((Object)null);
      } catch (Exception var9) {
         LOGGER.error("Error while parsing flat world string => {}", var9.getMessage());
         return null;
      }

      if (var6 == null) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", var7);
         return null;
      } else {
         FlatLayerInfo var8 = new FlatLayerInfo(var5, var6);
         var8.setStart(var1);
         return var8;
      }
   }

   private static List<FlatLayerInfo> getLayersInfoFromString(String var0) {
      ArrayList var1 = Lists.newArrayList();
      String[] var2 = var0.split(",");
      int var3 = 0;
      String[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         FlatLayerInfo var8 = getLayerInfoFromString(var7, var3);
         if (var8 == null) {
            return Collections.emptyList();
         }

         var1.add(var8);
         var3 += var8.getHeight();
      }

      return var1;
   }

   public static FlatLevelGeneratorSettings fromString(Registry<Biome> var0, String var1, FlatLevelGeneratorSettings var2) {
      Iterator var3 = Splitter.on(';').split(var1).iterator();
      if (!var3.hasNext()) {
         return FlatLevelGeneratorSettings.getDefault(var0);
      } else {
         List var4 = getLayersInfoFromString((String)var3.next());
         if (var4.isEmpty()) {
            return FlatLevelGeneratorSettings.getDefault(var0);
         } else {
            FlatLevelGeneratorSettings var5 = var2.withLayers(var4, var2.structureSettings());
            ResourceKey var6 = Biomes.PLAINS;
            if (var3.hasNext()) {
               try {
                  ResourceLocation var7 = new ResourceLocation((String)var3.next());
                  var6 = ResourceKey.create(Registry.BIOME_REGISTRY, var7);
                  var0.getOptional(var6).orElseThrow(() -> {
                     return new IllegalArgumentException("Invalid Biome: " + var7);
                  });
               } catch (Exception var8) {
                  LOGGER.error("Error while parsing flat world string => {}", var8.getMessage());
               }
            }

            var5.setBiome(() -> {
               return (Biome)var0.getOrThrow(var6);
            });
            return var5;
         }
      }
   }

   private static String save(Registry<Biome> var0, FlatLevelGeneratorSettings var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < var1.getLayersInfo().size(); ++var3) {
         if (var3 > 0) {
            var2.append(",");
         }

         var2.append(var1.getLayersInfo().get(var3));
      }

      var2.append(";");
      var2.append(var0.getKey(var1.getBiome()));
      return var2.toString();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.shareText = new TranslatableComponent("createWorld.customize.presets.share");
      this.listText = new TranslatableComponent("createWorld.customize.presets.list");
      this.export = new EditBox(this.font, 50, 40, this.width - 100, 20, this.shareText);
      this.export.setMaxLength(1230);
      WritableRegistry var1 = this.parent.parent.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.BIOME_REGISTRY);
      this.export.setValue(save(var1, this.parent.settings()));
      this.settings = this.parent.settings();
      this.children.add(this.export);
      this.list = new PresetFlatWorldScreen.PresetsList();
      this.children.add(this.list);
      this.selectButton = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableComponent("createWorld.customize.presets.select"), (var2) -> {
         FlatLevelGeneratorSettings var3 = fromString(var1, this.export.getValue(), this.settings);
         this.parent.setConfig(var3);
         this.minecraft.setScreen(this.parent);
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (var1x) -> {
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

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.list.render(var1, var2, var3, var4);
      RenderSystem.pushMatrix();
      RenderSystem.translatef(0.0F, 0.0F, 400.0F);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 8, 16777215);
      drawString(var1, this.font, this.shareText, 50, 30, 10526880);
      drawString(var1, this.font, this.listText, 50, 70, 10526880);
      RenderSystem.popMatrix();
      this.export.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }

   public void tick() {
      this.export.tick();
      super.tick();
   }

   public void updateButtonValidity(boolean var1) {
      this.selectButton.active = var1 || this.export.getValue().length() > 1;
   }

   private static void preset(Component var0, ItemLike var1, ResourceKey<Biome> var2, List<StructureFeature<?>> var3, boolean var4, boolean var5, boolean var6, FlatLayerInfo... var7) {
      PRESETS.add(new PresetFlatWorldScreen.PresetInfo(var1.asItem(), var0, (var6x) -> {
         HashMap var7x = Maps.newHashMap();
         Iterator var8 = var3.iterator();

         while(var8.hasNext()) {
            StructureFeature var9 = (StructureFeature)var8.next();
            var7x.put(var9, StructureSettings.DEFAULTS.get(var9));
         }

         StructureSettings var11 = new StructureSettings(var4 ? Optional.of(StructureSettings.DEFAULT_STRONGHOLD) : Optional.empty(), var7x);
         FlatLevelGeneratorSettings var12 = new FlatLevelGeneratorSettings(var11, var6x);
         if (var5) {
            var12.setDecoration();
         }

         if (var6) {
            var12.setAddLakes();
         }

         for(int var10 = var7.length - 1; var10 >= 0; --var10) {
            var12.getLayersInfo().add(var7[var10]);
         }

         var12.setBiome(() -> {
            return (Biome)var6x.getOrThrow(var2);
         });
         var12.updateLayers();
         return var12.withStructureSettings(var11);
      }));
   }

   static {
      preset(new TranslatableComponent("createWorld.customize.preset.classic_flat"), Blocks.GRASS_BLOCK, Biomes.PLAINS, Arrays.asList(StructureFeature.VILLAGE), false, false, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.tunnelers_dream"), Blocks.STONE, Biomes.MOUNTAINS, Arrays.asList(StructureFeature.MINESHAFT), true, true, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.water_world"), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Arrays.asList(StructureFeature.OCEAN_RUIN, StructureFeature.SHIPWRECK, StructureFeature.OCEAN_MONUMENT), false, false, false, new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.SAND), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.overworld"), Blocks.GRASS, Biomes.PLAINS, Arrays.asList(StructureFeature.VILLAGE, StructureFeature.MINESHAFT, StructureFeature.PILLAGER_OUTPOST, StructureFeature.RUINED_PORTAL), true, true, true, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.snowy_kingdom"), Blocks.SNOW, Biomes.SNOWY_TUNDRA, Arrays.asList(StructureFeature.VILLAGE, StructureFeature.IGLOO), false, false, false, new FlatLayerInfo(1, Blocks.SNOW), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.bottomless_pit"), Items.FEATHER, Biomes.PLAINS, Arrays.asList(StructureFeature.VILLAGE), false, false, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE));
      preset(new TranslatableComponent("createWorld.customize.preset.desert"), Blocks.SAND, Biomes.DESERT, Arrays.asList(StructureFeature.VILLAGE, StructureFeature.DESERT_PYRAMID, StructureFeature.MINESHAFT), true, true, false, new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), false, false, false, new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.the_void"), Blocks.BARRIER, Biomes.THE_VOID, Collections.emptyList(), false, true, false, new FlatLayerInfo(1, Blocks.AIR));
   }

   static class PresetInfo {
      public final Item icon;
      public final Component name;
      public final Function<Registry<Biome>, FlatLevelGeneratorSettings> settings;

      public PresetInfo(Item var1, Component var2, Function<Registry<Biome>, FlatLevelGeneratorSettings> var3) {
         super();
         this.icon = var1;
         this.name = var2;
         this.settings = var3;
      }

      public Component getName() {
         return this.name;
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
            NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select", new Object[]{((PresetFlatWorldScreen.PresetInfo)PresetFlatWorldScreen.PRESETS.get(this.children().indexOf(var1))).getName()})).getString());
         }

         PresetFlatWorldScreen.this.updateButtonValidity(var1 != null);
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

      public class Entry extends ObjectSelectionList.Entry<PresetFlatWorldScreen.PresetsList.Entry> {
         public Entry() {
            super();
         }

         public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            PresetFlatWorldScreen.PresetInfo var11 = (PresetFlatWorldScreen.PresetInfo)PresetFlatWorldScreen.PRESETS.get(var2);
            this.blitSlot(var1, var4, var3, var11.icon);
            PresetFlatWorldScreen.this.font.draw(var1, var11.name, (float)(var4 + 18 + 5), (float)(var3 + 6), 16777215);
         }

         public boolean mouseClicked(double var1, double var3, int var5) {
            if (var5 == 0) {
               this.select();
            }

            return false;
         }

         private void select() {
            PresetsList.this.setSelected(this);
            PresetFlatWorldScreen.PresetInfo var1 = (PresetFlatWorldScreen.PresetInfo)PresetFlatWorldScreen.PRESETS.get(PresetsList.this.children().indexOf(this));
            WritableRegistry var2 = PresetFlatWorldScreen.this.parent.parent.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.BIOME_REGISTRY);
            PresetFlatWorldScreen.this.settings = (FlatLevelGeneratorSettings)var1.settings.apply(var2);
            PresetFlatWorldScreen.this.export.setValue(PresetFlatWorldScreen.save(var2, PresetFlatWorldScreen.this.settings));
            PresetFlatWorldScreen.this.export.moveCursorToStart();
         }

         private void blitSlot(PoseStack var1, int var2, int var3, Item var4) {
            this.blitSlotBg(var1, var2 + 1, var3 + 1);
            RenderSystem.enableRescaleNormal();
            PresetFlatWorldScreen.this.itemRenderer.renderGuiItem(new ItemStack(var4), var2 + 2, var3 + 2);
            RenderSystem.disableRescaleNormal();
         }

         private void blitSlotBg(PoseStack var1, int var2, int var3) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            PresetsList.this.minecraft.getTextureManager().bind(GuiComponent.STATS_ICON_LOCATION);
            GuiComponent.blit(var1, var2, var3, PresetFlatWorldScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
         }
      }
   }
}
