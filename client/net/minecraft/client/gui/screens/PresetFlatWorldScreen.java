package net.minecraft.client.gui.screens;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.slf4j.Logger;

public class PresetFlatWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SLOT_TEX_SIZE = 128;
   private static final int SLOT_BG_SIZE = 18;
   private static final int SLOT_STAT_HEIGHT = 20;
   private static final int SLOT_BG_X = 1;
   private static final int SLOT_BG_Y = 1;
   private static final int SLOT_FG_X = 2;
   private static final int SLOT_FG_Y = 2;
   private static final ResourceKey<Biome> DEFAULT_BIOME;
   public static final Component UNKNOWN_PRESET;
   private final CreateFlatWorldScreen parent;
   private Component shareText;
   private Component listText;
   private PresetsList list;
   private Button selectButton;
   EditBox export;
   FlatLevelGeneratorSettings settings;

   public PresetFlatWorldScreen(CreateFlatWorldScreen var1) {
      super(Component.translatable("createWorld.customize.presets.title"));
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

      int var4 = Math.min(var1 + var3, DimensionType.Y_SIZE);
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
         return new FlatLayerInfo(var5, var6);
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

   public static FlatLevelGeneratorSettings fromString(Registry<Biome> var0, Registry<StructureSet> var1, String var2, FlatLevelGeneratorSettings var3) {
      Iterator var4 = Splitter.on(';').split(var2).iterator();
      if (!var4.hasNext()) {
         return FlatLevelGeneratorSettings.getDefault(var0, var1);
      } else {
         List var5 = getLayersInfoFromString((String)var4.next());
         if (var5.isEmpty()) {
            return FlatLevelGeneratorSettings.getDefault(var0, var1);
         } else {
            FlatLevelGeneratorSettings var6 = var3.withLayers(var5, var3.structureOverrides());
            ResourceKey var7 = DEFAULT_BIOME;
            if (var4.hasNext()) {
               try {
                  ResourceLocation var8 = new ResourceLocation((String)var4.next());
                  var7 = ResourceKey.create(Registry.BIOME_REGISTRY, var8);
                  var0.getOptional(var7).orElseThrow(() -> {
                     return new IllegalArgumentException("Invalid Biome: " + var8);
                  });
               } catch (Exception var9) {
                  LOGGER.error("Error while parsing flat world string => {}", var9.getMessage());
                  var7 = DEFAULT_BIOME;
               }
            }

            var6.setBiome(var0.getOrCreateHolderOrThrow(var7));
            return var6;
         }
      }
   }

   static String save(FlatLevelGeneratorSettings var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < var0.getLayersInfo().size(); ++var2) {
         if (var2 > 0) {
            var1.append(",");
         }

         var1.append(var0.getLayersInfo().get(var2));
      }

      var1.append(";");
      var1.append(var0.getBiome().unwrapKey().map(ResourceKey::location).orElseThrow(() -> {
         return new IllegalStateException("Biome not registered");
      }));
      return var1.toString();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.shareText = Component.translatable("createWorld.customize.presets.share");
      this.listText = Component.translatable("createWorld.customize.presets.list");
      this.export = new EditBox(this.font, 50, 40, this.width - 100, 20, this.shareText);
      this.export.setMaxLength(1230);
      RegistryAccess var1 = this.parent.parent.worldGenSettingsComponent.registryHolder();
      Registry var2 = var1.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry var3 = var1.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
      this.export.setValue(save(this.parent.settings()));
      this.settings = this.parent.settings();
      this.addWidget(this.export);
      this.list = new PresetsList(this.parent.parent.worldGenSettingsComponent.registryHolder());
      this.addWidget(this.list);
      this.selectButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 28, 150, 20, Component.translatable("createWorld.customize.presets.select"), (var3x) -> {
         FlatLevelGeneratorSettings var4 = fromString(var2, var3, this.export.getValue(), this.settings);
         this.parent.setConfig(var4);
         this.minecraft.setScreen(this.parent);
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (var1x) -> {
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
      var1.pushPose();
      var1.translate(0.0, 0.0, 400.0);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 8, 16777215);
      drawString(var1, this.font, this.shareText, 50, 30, 10526880);
      drawString(var1, this.font, this.listText, 50, 70, 10526880);
      var1.popPose();
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

   static {
      DEFAULT_BIOME = Biomes.PLAINS;
      UNKNOWN_PRESET = Component.translatable("flat_world_preset.unknown");
   }

   private class PresetsList extends ObjectSelectionList<Entry> {
      public PresetsList(RegistryAccess var2) {
         super(PresetFlatWorldScreen.this.minecraft, PresetFlatWorldScreen.this.width, PresetFlatWorldScreen.this.height, 80, PresetFlatWorldScreen.this.height - 37, 24);
         Iterator var3 = var2.registryOrThrow(Registry.FLAT_LEVEL_GENERATOR_PRESET_REGISTRY).getTagOrEmpty(FlatLevelGeneratorPresetTags.VISIBLE).iterator();

         while(var3.hasNext()) {
            Holder var4 = (Holder)var3.next();
            this.addEntry(new Entry(var4));
         }

      }

      public void setSelected(@Nullable Entry var1) {
         super.setSelected(var1);
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
               ((Entry)this.getSelected()).select();
            }

            return false;
         }
      }

      public class Entry extends ObjectSelectionList.Entry<Entry> {
         private final FlatLevelGeneratorPreset preset;
         private final Component name;

         public Entry(Holder<FlatLevelGeneratorPreset> var2) {
            super();
            this.preset = (FlatLevelGeneratorPreset)var2.value();
            this.name = (Component)var2.unwrapKey().map((var0) -> {
               return Component.translatable(var0.location().toLanguageKey("flat_world_preset"));
            }).orElse(PresetFlatWorldScreen.UNKNOWN_PRESET);
         }

         public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            this.blitSlot(var1, var4, var3, (Item)this.preset.displayItem().value());
            PresetFlatWorldScreen.this.font.draw(var1, this.name, (float)(var4 + 18 + 5), (float)(var3 + 6), 16777215);
         }

         public boolean mouseClicked(double var1, double var3, int var5) {
            if (var5 == 0) {
               this.select();
            }

            return false;
         }

         void select() {
            PresetsList.this.setSelected(this);
            PresetFlatWorldScreen.this.settings = this.preset.settings();
            PresetFlatWorldScreen.this.export.setValue(PresetFlatWorldScreen.save(PresetFlatWorldScreen.this.settings));
            PresetFlatWorldScreen.this.export.moveCursorToStart();
         }

         private void blitSlot(PoseStack var1, int var2, int var3, Item var4) {
            this.blitSlotBg(var1, var2 + 1, var3 + 1);
            PresetFlatWorldScreen.this.itemRenderer.renderGuiItem(new ItemStack(var4), var2 + 2, var3 + 2);
         }

         private void blitSlotBg(PoseStack var1, int var2, int var3) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GuiComponent.STATS_ICON_LOCATION);
            GuiComponent.blit(var1, var2, var3, PresetFlatWorldScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", this.name);
         }
      }
   }
}
