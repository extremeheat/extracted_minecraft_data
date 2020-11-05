package net.minecraft.world.level.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.SharedConstants;

public class LevelVersion {
   private final int levelDataVersion;
   private final long lastPlayed;
   private final String minecraftVersionName;
   private final int minecraftVersion;
   private final boolean snapshot;

   public LevelVersion(int var1, long var2, String var4, int var5, boolean var6) {
      super();
      this.levelDataVersion = var1;
      this.lastPlayed = var2;
      this.minecraftVersionName = var4;
      this.minecraftVersion = var5;
      this.snapshot = var6;
   }

   public static LevelVersion parse(Dynamic<?> var0) {
      int var1 = var0.get("version").asInt(0);
      long var2 = var0.get("LastPlayed").asLong(0L);
      OptionalDynamic var4 = var0.get("Version");
      return var4.result().isPresent() ? new LevelVersion(var1, var2, var4.get("Name").asString(SharedConstants.getCurrentVersion().getName()), var4.get("Id").asInt(SharedConstants.getCurrentVersion().getWorldVersion()), var4.get("Snapshot").asBoolean(!SharedConstants.getCurrentVersion().isStable())) : new LevelVersion(var1, var2, "", 0, false);
   }

   public int levelDataVersion() {
      return this.levelDataVersion;
   }

   public long lastPlayed() {
      return this.lastPlayed;
   }

   public String minecraftVersionName() {
      return this.minecraftVersionName;
   }

   public int minecraftVersion() {
      return this.minecraftVersion;
   }

   public boolean snapshot() {
      return this.snapshot;
   }
}
