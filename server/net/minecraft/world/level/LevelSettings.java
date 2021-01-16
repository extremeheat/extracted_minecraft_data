package net.minecraft.world.level;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.Difficulty;

public final class LevelSettings {
   private final String levelName;
   private final GameType gameType;
   private final boolean hardcore;
   private final Difficulty difficulty;
   private final boolean allowCommands;
   private final GameRules gameRules;
   private final DataPackConfig dataPackConfig;

   public LevelSettings(String var1, GameType var2, boolean var3, Difficulty var4, boolean var5, GameRules var6, DataPackConfig var7) {
      super();
      this.levelName = var1;
      this.gameType = var2;
      this.hardcore = var3;
      this.difficulty = var4;
      this.allowCommands = var5;
      this.gameRules = var6;
      this.dataPackConfig = var7;
   }

   public static LevelSettings parse(Dynamic<?> var0, DataPackConfig var1) {
      GameType var2 = GameType.byId(var0.get("GameType").asInt(0));
      return new LevelSettings(var0.get("LevelName").asString(""), var2, var0.get("hardcore").asBoolean(false), (Difficulty)var0.get("Difficulty").asNumber().map((var0x) -> {
         return Difficulty.byId(var0x.byteValue());
      }).result().orElse(Difficulty.NORMAL), var0.get("allowCommands").asBoolean(var2 == GameType.CREATIVE), new GameRules(var0.get("GameRules")), var1);
   }

   public String levelName() {
      return this.levelName;
   }

   public GameType gameType() {
      return this.gameType;
   }

   public boolean hardcore() {
      return this.hardcore;
   }

   public Difficulty difficulty() {
      return this.difficulty;
   }

   public boolean allowCommands() {
      return this.allowCommands;
   }

   public GameRules gameRules() {
      return this.gameRules;
   }

   public DataPackConfig getDataPackConfig() {
      return this.dataPackConfig;
   }

   public LevelSettings withGameType(GameType var1) {
      return new LevelSettings(this.levelName, var1, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataPackConfig);
   }

   public LevelSettings withDifficulty(Difficulty var1) {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, var1, this.allowCommands, this.gameRules, this.dataPackConfig);
   }

   public LevelSettings withDataPackConfig(DataPackConfig var1) {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, var1);
   }

   public LevelSettings copy() {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules.copy(), this.dataPackConfig);
   }
}
