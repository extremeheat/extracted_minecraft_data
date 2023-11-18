package net.minecraft.client.gui.screens;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.slf4j.Logger;

public class PresetFlatWorldScreen extends Screen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int SLOT_TEX_SIZE = 128;
   private static final int SLOT_BG_SIZE = 18;
   private static final int SLOT_STAT_HEIGHT = 20;
   private static final int SLOT_BG_X = 1;
   private static final int SLOT_BG_Y = 1;
   private static final int SLOT_FG_X = 2;
   private static final int SLOT_FG_Y = 2;
   private static final ResourceKey<Biome> DEFAULT_BIOME = Biomes.PLAINS;
   public static final Component UNKNOWN_PRESET = Component.translatable("flat_world_preset.unknown");
   private final CreateFlatWorldScreen parent;
   private Component shareText;
   private Component listText;
   private PresetFlatWorldScreen.PresetsList list;
   private Button selectButton;
   EditBox export;
   FlatLevelGeneratorSettings settings;

   public PresetFlatWorldScreen(CreateFlatWorldScreen var1) {
      super(Component.translatable("createWorld.customize.presets.title"));
      this.parent = var1;
   }

   @Nullable
   private static FlatLayerInfo getLayerInfoFromString(HolderGetter<Block> var0, String var1, int var2) {
      List var3 = Splitter.on('*').limit(2).splitToList(var1);
      int var4;
      String var5;
      if (var3.size() == 2) {
         var5 = (String)var3.get(1);

         try {
            var4 = Math.max(Integer.parseInt((String)var3.get(0)), 0);
         } catch (NumberFormatException var11) {
            LOGGER.error("Error while parsing flat world string", var11);
            return null;
         }
      } else {
         var5 = (String)var3.get(0);
         var4 = 1;
      }

      int var6 = Math.min(var2 + var4, DimensionType.Y_SIZE);
      int var7 = var6 - var2;

      Optional var8;
      try {
         var8 = var0.get(ResourceKey.create(Registries.BLOCK, new ResourceLocation(var5)));
      } catch (Exception var10) {
         LOGGER.error("Error while parsing flat world string", var10);
         return null;
      }

      if (var8.isEmpty()) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", var5);
         return null;
      } else {
         return new FlatLayerInfo(var7, (Block)((Holder.Reference)var8.get()).value());
      }
   }

   private static List<FlatLayerInfo> getLayersInfoFromString(HolderGetter<Block> var0, String var1) {
      ArrayList var2 = Lists.newArrayList();
      String[] var3 = var1.split(",");
      int var4 = 0;

      for(String var8 : var3) {
         FlatLayerInfo var9 = getLayerInfoFromString(var0, var8, var4);
         if (var9 == null) {
            return Collections.emptyList();
         }

         var2.add(var9);
         var4 += var9.getHeight();
      }

      return var2;
   }

   public static FlatLevelGeneratorSettings fromString(
      HolderGetter<Block> var0,
      HolderGetter<Biome> var1,
      HolderGetter<StructureSet> var2,
      HolderGetter<PlacedFeature> var3,
      String var4,
      FlatLevelGeneratorSettings var5
   ) {
      Iterator var6 = Splitter.on(';').split(var4).iterator();
      if (!var6.hasNext()) {
         return FlatLevelGeneratorSettings.getDefault(var1, var2, var3);
      } else {
         List var7 = getLayersInfoFromString(var0, (String)var6.next());
         if (var7.isEmpty()) {
            return FlatLevelGeneratorSettings.getDefault(var1, var2, var3);
         } else {
            Holder.Reference var8 = var1.getOrThrow(DEFAULT_BIOME);
            Object var9 = var8;
            if (var6.hasNext()) {
               String var10 = (String)var6.next();
               var9 = Optional.ofNullable(ResourceLocation.tryParse(var10))
                  .map(var0x -> ResourceKey.create(Registries.BIOME, var0x))
                  .flatMap(var1::get)
                  .orElseGet(() -> {
                     LOGGER.warn("Invalid biome: {}", var10);
                     return var8;
                  });
            }

            return var5.withBiomeAndLayers(var7, var5.structureOverrides(), (Holder<Biome>)var9);
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
      var1.append(var0.getBiome().unwrapKey().map(ResourceKey::location).orElseThrow(() -> new IllegalStateException("Biome not registered")));
      return var1.toString();
   }

   @Override
   protected void init() {
      this.shareText = Component.translatable("createWorld.customize.presets.share");
      this.listText = Component.translatable("createWorld.customize.presets.list");
      this.export = new EditBox(this.font, 50, 40, this.width - 100, 20, this.shareText);
      this.export.setMaxLength(1230);
      WorldCreationContext var1 = this.parent.parent.getUiState().getSettings();
      RegistryAccess.Frozen var2 = var1.worldgenLoadContext();
      FeatureFlagSet var3 = var1.dataConfiguration().enabledFeatures();
      HolderLookup.RegistryLookup var4 = var2.lookupOrThrow(Registries.BIOME);
      HolderLookup.RegistryLookup var5 = var2.lookupOrThrow(Registries.STRUCTURE_SET);
      HolderLookup.RegistryLookup var6 = var2.lookupOrThrow(Registries.PLACED_FEATURE);
      HolderLookup var7 = var2.lookupOrThrow(Registries.BLOCK).filterFeatures(var3);
      this.export.setValue(save(this.parent.settings()));
      this.settings = this.parent.settings();
      this.addWidget(this.export);
      this.list = new PresetFlatWorldScreen.PresetsList(var2, var3);
      this.addWidget(this.list);
      this.selectButton = this.addRenderableWidget(Button.builder(Component.translatable("createWorld.customize.presets.select"), var5x -> {
         FlatLevelGeneratorSettings var6x = fromString(var7, var4, var5, var6, this.export.getValue(), this.settings);
         this.parent.setConfig(var6x);
         this.minecraft.setScreen(this.parent);
      }).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build());
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.minecraft.setScreen(this.parent))
            .bounds(this.width / 2 + 5, this.height - 28, 150, 20)
            .build()
      );
      this.updateButtonValidity(this.list.getSelected() != null);
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.list.mouseScrolled(var1, var3, var5);
   }

   @Override
   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.export.getValue();
      this.init(var1, var2, var3);
      this.export.setValue(var4);
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.list.render(var1, var2, var3, var4);
      var1.pose().pushPose();
      var1.pose().translate(0.0F, 0.0F, 400.0F);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);
      var1.drawString(this.font, this.shareText, 50, 30, 10526880);
      var1.drawString(this.font, this.listText, 50, 70, 10526880);
      var1.pose().popPose();
      this.export.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }

   @Override
   public void tick() {
      this.export.tick();
      super.tick();
   }

   public void updateButtonValidity(boolean var1) {
      this.selectButton.active = var1 || this.export.getValue().length() > 1;
   }

   class PresetsList extends ObjectSelectionList<PresetFlatWorldScreen.PresetsList.Entry> {
      public PresetsList(RegistryAccess var2, FeatureFlagSet var3) {
         super(
            PresetFlatWorldScreen.this.minecraft,
            PresetFlatWorldScreen.this.width,
            PresetFlatWorldScreen.this.height,
            80,
            PresetFlatWorldScreen.this.height - 37,
            24
         );

         for(Holder var5 : var2.registryOrThrow(Registries.FLAT_LEVEL_GENERATOR_PRESET).getTagOrEmpty(FlatLevelGeneratorPresetTags.VISIBLE)) {
            Set var6 = ((FlatLevelGeneratorPreset)var5.value())
               .settings()
               .getLayersInfo()
               .stream()
               .map(var0 -> var0.getBlockState().getBlock())
               .filter(var1x -> !var1x.isEnabled(var3))
               .collect(Collectors.toSet());
            if (!var6.isEmpty()) {
               PresetFlatWorldScreen.LOGGER
                  .info(
                     "Discarding flat world preset {} since it contains experimental blocks {}",
                     var5.unwrapKey().map(var0 -> var0.location().toString()).orElse("<unknown>"),
                     var6
                  );
            } else {
               this.addEntry(new PresetFlatWorldScreen.PresetsList.Entry(var5));
            }
         }
      }

      public void setSelected(@Nullable PresetFlatWorldScreen.PresetsList.Entry var1) {
         super.setSelected(var1);
         PresetFlatWorldScreen.this.updateButtonValidity(var1 != null);
      }

      @Override
      public boolean keyPressed(int var1, int var2, int var3) {
         if (super.keyPressed(var1, var2, var3)) {
            return true;
         } else {
            if (CommonInputs.selected(var1) && this.getSelected() != null) {
               this.getSelected().select();
            }

            return false;
         }
      }

      public class Entry extends ObjectSelectionList.Entry<PresetFlatWorldScreen.PresetsList.Entry> {
         private static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
         private final FlatLevelGeneratorPreset preset;
         private final Component name;

         public Entry(Holder<FlatLevelGeneratorPreset> var2) {
            super();
            this.preset = (FlatLevelGeneratorPreset)var2.value();
            this.name = var2.unwrapKey()
               .map(var0 -> Component.translatable(var0.location().toLanguageKey("flat_world_preset")))
               .orElse(PresetFlatWorldScreen.UNKNOWN_PRESET);
         }

         @Override
         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            this.blitSlot(var1, var4, var3, this.preset.displayItem().value());
            var1.drawString(PresetFlatWorldScreen.this.font, this.name, var4 + 18 + 5, var3 + 6, 16777215, false);
         }

         @Override
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

         private void blitSlot(GuiGraphics var1, int var2, int var3, Item var4) {
            this.blitSlotBg(var1, var2 + 1, var3 + 1);
            var1.renderFakeItem(new ItemStack(var4), var2 + 2, var3 + 2);
         }

         private void blitSlotBg(GuiGraphics var1, int var2, int var3) {
            var1.blit(STATS_ICON_LOCATION, var2, var3, 0, 0.0F, 0.0F, 18, 18, 128, 128);
         }

         @Override
         public Component getNarration() {
            return Component.translatable("narrator.select", this.name);
         }
      }
   }
}
