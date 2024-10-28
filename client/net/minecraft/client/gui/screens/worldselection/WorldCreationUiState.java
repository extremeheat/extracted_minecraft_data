package net.minecraft.client.gui.screens.worldselection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
   private final List<Consumer<WorldCreationUiState>> listeners = new ArrayList();
   private String name;
   private SelectedGameMode gameMode;
   private Difficulty difficulty;
   @Nullable
   private Boolean allowCommands;
   private String seed;
   private boolean generateStructures;
   private boolean bonusChest;
   private final Path savesFolder;
   private String targetFolder;
   private WorldCreationContext settings;
   private WorldTypeEntry worldType;
   private final List<WorldTypeEntry> normalPresetList;
   private final List<WorldTypeEntry> altPresetList;
   private GameRules gameRules;

   public WorldCreationUiState(Path var1, WorldCreationContext var2, Optional<ResourceKey<WorldPreset>> var3, OptionalLong var4) {
      super();
      this.name = DEFAULT_WORLD_NAME.getString();
      this.gameMode = WorldCreationUiState.SelectedGameMode.SURVIVAL;
      this.difficulty = Difficulty.NORMAL;
      this.normalPresetList = new ArrayList();
      this.altPresetList = new ArrayList();
      this.gameRules = new GameRules();
      this.savesFolder = var1;
      this.settings = var2;
      this.worldType = new WorldTypeEntry((Holder)findPreset(var2, var3).orElse((Object)null));
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
         this.settings = this.settings.withOptions((var1x) -> {
            return var1x.withBonusChest(var1);
         });
      }

      boolean var2 = this.isGenerateStructures();
      if (var2 != this.settings.options().generateStructures()) {
         this.settings = this.settings.withOptions((var1x) -> {
            return var1x.withStructures(var2);
         });
      }

      Iterator var3 = this.listeners.iterator();

      while(var3.hasNext()) {
         Consumer var4 = (Consumer)var3.next();
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

   public void setGameMode(SelectedGameMode var1) {
      this.gameMode = var1;
      this.onChanged();
   }

   public SelectedGameMode getGameMode() {
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
      } else if (this.allowCommands == null) {
         return this.getGameMode() == WorldCreationUiState.SelectedGameMode.CREATIVE;
      } else {
         return this.allowCommands;
      }
   }

   public void setSeed(String var1) {
      this.seed = var1;
      this.settings = this.settings.withOptions((var1x) -> {
         return var1x.withSeed(WorldOptions.parseSeed(this.getSeed()));
      });
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
         this.settings = new WorldCreationContext(this.settings.options(), this.settings.datapackDimensions(), this.settings.selectedDimensions(), this.settings.worldgenRegistries(), this.settings.dataPackResources(), var1);
         return true;
      } else {
         return false;
      }
   }

   public boolean isDebug() {
      return this.settings.selectedDimensions().isDebug();
   }

   public void setWorldType(WorldTypeEntry var1) {
      this.worldType = var1;
      Holder var2 = var1.preset();
      if (var2 != null) {
         this.updateDimensions((var1x, var2x) -> {
            return ((WorldPreset)var2.value()).createWorldDimensions();
         });
      }

   }

   public WorldTypeEntry getWorldType() {
      return this.worldType;
   }

   @Nullable
   public PresetEditor getPresetEditor() {
      Holder var1 = this.getWorldType().preset();
      return var1 != null ? (PresetEditor)PresetEditor.EDITORS.get(var1.unwrapKey()) : null;
   }

   public List<WorldTypeEntry> getNormalPresetList() {
      return this.normalPresetList;
   }

   public List<WorldTypeEntry> getAltPresetList() {
      return this.altPresetList;
   }

   private void updatePresetLists() {
      Registry var1 = this.getSettings().worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET);
      this.normalPresetList.clear();
      this.normalPresetList.addAll((Collection)getNonEmptyList(var1, WorldPresetTags.NORMAL).orElseGet(() -> {
         return var1.holders().map(WorldTypeEntry::new).toList();
      }));
      this.altPresetList.clear();
      this.altPresetList.addAll((Collection)getNonEmptyList(var1, WorldPresetTags.EXTENDED).orElse(this.normalPresetList));
      Holder var2 = this.worldType.preset();
      if (var2 != null) {
         this.worldType = (WorldTypeEntry)findPreset(this.getSettings(), var2.unwrapKey()).map(WorldTypeEntry::new).orElse((WorldTypeEntry)this.normalPresetList.get(0));
      }

   }

   private static Optional<Holder<WorldPreset>> findPreset(WorldCreationContext var0, Optional<ResourceKey<WorldPreset>> var1) {
      return var1.flatMap((var1x) -> {
         return var0.worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET).getHolder(var1x);
      });
   }

   private static Optional<List<WorldTypeEntry>> getNonEmptyList(Registry<WorldPreset> var0, TagKey<WorldPreset> var1) {
      return var0.getTag(var1).map((var0x) -> {
         return var0x.stream().map(WorldTypeEntry::new).toList();
      }).filter((var0x) -> {
         return !var0x.isEmpty();
      });
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

      private SelectedGameMode(final String var3, final GameType var4) {
         this.gameType = var4;
         this.displayName = Component.translatable("selectWorld.gameMode." + var3);
         this.info = Component.translatable("selectWorld.gameMode." + var3 + ".info");
      }

      public Component getInfo() {
         return this.info;
      }

      // $FF: synthetic method
      private static SelectedGameMode[] $values() {
         return new SelectedGameMode[]{SURVIVAL, HARDCORE, CREATIVE, DEBUG};
      }
   }

   public static record WorldTypeEntry(@Nullable Holder<WorldPreset> preset) {
      private static final Component CUSTOM_WORLD_DESCRIPTION = Component.translatable("generator.custom");

      public WorldTypeEntry(@Nullable Holder<WorldPreset> var1) {
         super();
         this.preset = var1;
      }

      public Component describePreset() {
         return (Component)Optional.ofNullable(this.preset).flatMap(Holder::unwrapKey).map((var0) -> {
            return Component.translatable(var0.location().toLanguageKey("generator"));
         }).orElse(CUSTOM_WORLD_DESCRIPTION);
      }

      public boolean isAmplified() {
         return Optional.ofNullable(this.preset).flatMap(Holder::unwrapKey).filter((var0) -> {
            return var0.equals(WorldPresets.AMPLIFIED);
         }).isPresent();
      }

      @Nullable
      public Holder<WorldPreset> preset() {
         return this.preset;
      }
   }
}
