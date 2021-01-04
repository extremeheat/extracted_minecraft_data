package net.minecraft.world.level;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.level.storage.LevelData;

public final class LevelSettings {
   private final long seed;
   private final GameType gameType;
   private final boolean generateMapFeatures;
   private final boolean hardcore;
   private final LevelType levelType;
   private boolean allowCommands;
   private boolean startingBonusItems;
   private JsonElement levelTypeOptions;

   public LevelSettings(long var1, GameType var3, boolean var4, boolean var5, LevelType var6) {
      super();
      this.levelTypeOptions = new JsonObject();
      this.seed = var1;
      this.gameType = var3;
      this.generateMapFeatures = var4;
      this.hardcore = var5;
      this.levelType = var6;
   }

   public LevelSettings(LevelData var1) {
      this(var1.getSeed(), var1.getGameType(), var1.isGenerateMapFeatures(), var1.isHardcore(), var1.getGeneratorType());
   }

   public LevelSettings enableStartingBonusItems() {
      this.startingBonusItems = true;
      return this;
   }

   public LevelSettings enableSinglePlayerCommands() {
      this.allowCommands = true;
      return this;
   }

   public LevelSettings setLevelTypeOptions(JsonElement var1) {
      this.levelTypeOptions = var1;
      return this;
   }

   public boolean hasStartingBonusItems() {
      return this.startingBonusItems;
   }

   public long getSeed() {
      return this.seed;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public boolean isGenerateMapFeatures() {
      return this.generateMapFeatures;
   }

   public LevelType getLevelType() {
      return this.levelType;
   }

   public boolean getAllowCommands() {
      return this.allowCommands;
   }

   public JsonElement getLevelTypeOptions() {
      return this.levelTypeOptions;
   }
}
