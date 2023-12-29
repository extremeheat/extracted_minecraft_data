package net.minecraft.world.level.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class PlayerDataStorage {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final File playerDir;
   protected final DataFixer fixerUpper;

   public PlayerDataStorage(LevelStorageSource.LevelStorageAccess var1, DataFixer var2) {
      super();
      this.fixerUpper = var2;
      this.playerDir = var1.getLevelPath(LevelResource.PLAYER_DATA_DIR).toFile();
      this.playerDir.mkdirs();
   }

   public void save(Player var1) {
      try {
         CompoundTag var2 = var1.saveWithoutId(new CompoundTag());
         Path var3 = this.playerDir.toPath();
         Path var4 = Files.createTempFile(var3, var1.getStringUUID() + "-", ".dat");
         NbtIo.writeCompressed(var2, var4);
         Path var5 = var3.resolve(var1.getStringUUID() + ".dat");
         Path var6 = var3.resolve(var1.getStringUUID() + ".dat_old");
         Util.safeReplaceFile(var5, var4, var6);
      } catch (Exception var7) {
         LOGGER.warn("Failed to save player data for {}", var1.getName().getString());
      }
   }

   @Nullable
   public CompoundTag load(Player var1) {
      CompoundTag var2 = null;

      try {
         File var3 = new File(this.playerDir, var1.getStringUUID() + ".dat");
         if (var3.exists() && var3.isFile()) {
            var2 = NbtIo.readCompressed(var3.toPath(), NbtAccounter.unlimitedHeap());
         }
      } catch (Exception var4) {
         LOGGER.warn("Failed to load player data for {}", var1.getName().getString());
      }

      if (var2 != null) {
         int var5 = NbtUtils.getDataVersion(var2, -1);
         var2 = DataFixTypes.PLAYER.updateToCurrentVersion(this.fixerUpper, var2, var5);
         var1.load(var2);
      }

      return var2;
   }

   public String[] getSeenPlayers() {
      String[] var1 = this.playerDir.list();
      if (var1 == null) {
         var1 = new String[0];
      }

      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2].endsWith(".dat")) {
            var1[var2] = var1[var2].substring(0, var1[var2].length() - 4);
         }
      }

      return var1;
   }
}
