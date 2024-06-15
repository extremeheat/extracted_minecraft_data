package net.minecraft.client.gui.screens.worldselection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public class WorldCreationUiState {
   private static final Component DEFAULT_WORLD_NAME = Component.translatable("selectWorld.newWorld");
   private final List<Consumer<WorldCreationUiState>> listeners = new ArrayList<>();
   private String name = DEFAULT_WORLD_NAME.getString();
   private WorldCreationUiState.SelectedGameMode gameMode = WorldCreationUiState.SelectedGameMode.SURVIVAL;
   private Difficulty difficulty = Difficulty.NORMAL;
   @Nullable
   private Boolean allowCommands;
   private String seed;
   private boolean generateStructures;
   private boolean bonusChest;
   private final Path savesFolder;
   private String targetFolder;
   private WorldCreationContext settings;
   private WorldCreationUiState.WorldTypeEntry worldType;
   private final List<WorldCreationUiState.WorldTypeEntry> normalPresetList = new ArrayList<>();
   private final List<WorldCreationUiState.WorldTypeEntry> altPresetList = new ArrayList<>();
   private GameRules gameRules = new GameRules();

   public WorldCreationUiState(Path var1, WorldCreationContext var2, Optional<ResourceKey<WorldPreset>> var3, OptionalLong var4) {
      super();
      this.savesFolder = var1;
      this.settings = var2;
      this.worldType = new WorldCreationUiState.WorldTypeEntry(findPreset(var2, var3).orElse(null));
      this.updatePresetLists();
      this.seed = var4.isPresent() ? Long.toString(var4.getAsLong()) : "";
      this.generateStructures = var2.options().generateStructures();
      this.bonusChest = var2.options().generateBonusChest();
      this.targetFolder = this.findResultFolder(this.name);
   }

   public void addListener(Consumer<WorldCreationUiState> var1) {
      this.listeners.add(var1);
   }

   public void onChanged() {
      boolean var1 = this.isBonusChest();
      if (var1 != this.settings.options().generateBonusChest()) {
         this.settings = this.settings.withOptions(var1x -> var1x.withBonusChest(var1));
      }

      boolean var2 = this.isGenerateStructures();
      if (var2 != this.settings.options().generateStructures()) {
         this.settings = this.settings.withOptions(var1x -> var1x.withStructures(var2));
      }

      for (Consumer var4 : this.listeners) {
         var4.accept(this);
      }
   }

   public void setName(String var1) {
      this.name = var1;
      this.targetFolder = this.findResultFolder(var1);
      this.onChanged();
   }

   private String findResultFolder(String var1) {
      String var2 = var1.trim();

      try {
         return FileUtil.findAvailableName(this.savesFolder, !var2.isEmpty() ? var2 : DEFAULT_WORLD_NAME.getString(), "");
      } catch (Exception var5) {
         try {
            return FileUtil.findAvailableName(this.savesFolder, "World", "");
         } catch (IOException var4) {
            throw new RuntimeException("Could not create save folder", var4);
         }
      }
   }

   public String getName() {
      return this.name;
   }

   public String getTargetFolder() {
      return this.targetFolder;
   }

   public void setGameMode(WorldCreationUiState.SelectedGameMode var1) {
      this.gameMode = var1;
      this.onChanged();
   }

   public WorldCreationUiState.SelectedGameMode getGameMode() {
      return this.isDebug() ? WorldCreationUiState.SelectedGameMode.DEBUG : this.gameMode;
   }

   public void setDifficulty(Difficulty var1) {
      this.difficulty = var1;
      this.onChanged();
   }

   public Difficulty getDifficulty() {
      return this.isHardcore() ? Difficulty.HARD : this.difficulty;
   }

   public boolean isHardcore() {
      return this.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE;
   }

   public void setAllowCommands(boolean var1) {
      this.allowCommands = var1;
      this.onChanged();
   }

   public boolean isAllowCommands() {
      if (this.isDebug()) {
         return true;
      } else if (this.isHardcore()) {
         return false;
      } else {
         return this.allowCommands == null ? this.getGameMode() == WorldCreationUiState.SelectedGameMode.CREATIVE : this.allowCommands;
      }
   }

   public void setSeed(String var1) {
      this.seed = var1;
      this.settings = this.settings.withOptions(var1x -> var1x.withSeed(WorldOptions.parseSeed(this.getSeed())));
      this.onChanged();
   }

   public String getSeed() {
      return this.seed;
   }

   public void setGenerateStructures(boolean var1) {
      this.generateStructures = var1;
      this.onChanged();
   }

   public boolean isGenerateStructures() {
      return this.isDebug() ? false : this.generateStructures;
   }

   public void setBonusChest(boolean var1) {
      this.bonusChest = var1;
      this.onChanged();
   }

   public boolean isBonusChest() {
      return !this.isDebug() && !this.isHardcore() ? this.bonusChest : false;
   }

   public void setSettings(WorldCreationContext var1) {
      this.settings = var1;
      this.updatePresetLists();
      this.onChanged();
   }

   public WorldCreationContext getSettings() {
      return this.settings;
   }

   public void updateDimensions(WorldCreationContext.DimensionsUpdater var1) {
      this.settings = this.settings.withDimensions(var1);
      this.onChanged();
   }

   protected boolean tryUpdateDataConfiguration(WorldDataConfiguration var1) {
      WorldDataConfiguration var2 = this.settings.dataConfiguration();
      if (var2.dataPacks().getEnabled().equals(var1.dataPacks().getEnabled()) && var2.enabledFeatures().equals(var1.enabledFeatures())) {
         this.settings = new WorldCreationContext(
            this.settings.options(),
            this.settings.datapackDimensions(),
            this.settings.selectedDimensions(),
            this.settings.worldgenRegistries(),
            this.settings.dataPackResources(),
            var1
         );
         return true;
      } else {
         return false;
      }
   }

   public boolean isDebug() {
      return this.settings.selectedDimensions().isDebug();
   }

   public void setWorldType(WorldCreationUiState.WorldTypeEntry var1) {
      this.worldType = var1;
      Holder var2 = var1.preset();
      if (var2 != null) {
         this.updateDimensions((var1x, var2x) -> ((WorldPreset)var2.value()).createWorldDimensions());
      }
   }

   public WorldCreationUiState.WorldTypeEntry getWorldType() {
      return this.worldType;
   }

   @Nullable
   public PresetEditor getPresetEditor() {
      Holder var1 = this.getWorldType().preset();
      return var1 != null ? PresetEditor.EDITORS.get(var1.unwrapKey()) : null;
   }

   public List<WorldCreationUiState.WorldTypeEntry> getNormalPresetList() {
      return this.normalPresetList;
   }

   public List<WorldCreationUiState.WorldTypeEntry> getAltPresetList() {
      return this.altPresetList;
   }

   private void updatePresetLists() {
      Registry var1 = this.getSettings().worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET);
      this.normalPresetList.clear();
      this.normalPresetList
         .addAll(getNonEmptyList(var1, WorldPresetTags.NORMAL).orElseGet(() -> var1.holders().map(WorldCreationUiState.WorldTypeEntry::new).toList()));
      this.altPresetList.clear();
      this.altPresetList.addAll(getNonEmptyList(var1, WorldPresetTags.EXTENDED).orElse(this.normalPresetList));
      Holder var2 = this.worldType.preset();
      if (var2 != null) {
         this.worldType = findPreset(this.getSettings(), var2.unwrapKey()).map(WorldCreationUiState.WorldTypeEntry::new).orElse(this.normalPresetList.get(0));
      }
   }

   private static Optional<Holder<WorldPreset>> findPreset(WorldCreationContext var0, Optional<ResourceKey<WorldPreset>> var1) {
      return var1.flatMap(var1x -> var0.worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET).getHolder((ResourceKey<WorldPreset>)var1x));
   }

   private static Optional<List<WorldCreationUiState.WorldTypeEntry>> getNonEmptyList(Registry<WorldPreset> var0, TagKey<WorldPreset> var1) {
      return var0.getTag(var1).map(var0x -> var0x.stream().map(WorldCreationUiState.WorldTypeEntry::new).toList()).filter(var0x -> !var0x.isEmpty());
   }

   public void setGameRules(GameRules var1) {
      this.gameRules = var1;
      this.onChanged();
   }

   public GameRules getGameRules() {
      return this.gameRules;
   }

   public static enum SelectedGameMode {
      SURVIVAL("survival", GameType.SURVIVAL),
      HARDCORE("hardcore", GameType.SURVIVAL),
      CREATIVE("creative", GameType.CREATIVE),
      DEBUG("spectator", GameType.SPECTATOR);

      public final GameType gameType;
      public final Component displayName;
      private final Component info;

      private SelectedGameMode(final String nullxx, final GameType nullxxx) {
         this.gameType = nullxxx;
         this.displayName = Component.translatable("selectWorld.gameMode." + nullxx);
         this.info = Component.translatable("selectWorld.gameMode." + nullxx + ".info");
      }

      public Component getInfo() {
         return this.info;
      }
   }

   public static record WorldTypeEntry(@Nullable Holder<WorldPreset> preset) {
      private static final Component CUSTOM_WORLD_DESCRIPTION = Component.translatable("generator.custom");

      public WorldTypeEntry(@Nullable Holder<WorldPreset> preset) {
         super();
         this.preset = preset;
      }

      public Component describePreset() {
         return Optional.ofNullable(this.preset)
            .flatMap(Holder::unwrapKey)
            .map(var0 -> Component.translatable(var0.location().toLanguageKey("generator")))
            .orElse(CUSTOM_WORLD_DESCRIPTION);
      }

      public boolean isAmplified() {
         return Optional.ofNullable(this.preset).flatMap(Holder::unwrapKey).filter(var0 -> var0.equals(WorldPresets.AMPLIFIED)).isPresent();
      }
   }
}
