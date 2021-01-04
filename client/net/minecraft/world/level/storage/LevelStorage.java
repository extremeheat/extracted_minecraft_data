package net.minecraft.world.level.storage;

import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelConflictException;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorage implements PlayerIO {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File worldDir;
   private final File playerDir;
   private final long sessionId = Util.getMillis();
   private final String levelId;
   private final StructureManager structureManager;
   protected final DataFixer fixerUpper;

   public LevelStorage(File var1, String var2, @Nullable MinecraftServer var3, DataFixer var4) {
      super();
      this.fixerUpper = var4;
      this.worldDir = new File(var1, var2);
      this.worldDir.mkdirs();
      this.playerDir = new File(this.worldDir, "playerdata");
      this.levelId = var2;
      if (var3 != null) {
         this.playerDir.mkdirs();
         this.structureManager = new StructureManager(var3, this.worldDir, var4);
      } else {
         this.structureManager = null;
      }

      this.initiateSession();
   }

   public void saveLevelData(LevelData var1, @Nullable CompoundTag var2) {
      var1.setVersion(19133);
      CompoundTag var3 = var1.createTag(var2);
      CompoundTag var4 = new CompoundTag();
      var4.put("Data", var3);

      try {
         File var5 = new File(this.worldDir, "level.dat_new");
         File var6 = new File(this.worldDir, "level.dat_old");
         File var7 = new File(this.worldDir, "level.dat");
         NbtIo.writeCompressed(var4, new FileOutputStream(var5));
         if (var6.exists()) {
            var6.delete();
         }

         var7.renameTo(var6);
         if (var7.exists()) {
            var7.delete();
         }

         var5.renameTo(var7);
         if (var5.exists()) {
            var5.delete();
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }

   }

   private void initiateSession() {
      try {
         File var1 = new File(this.worldDir, "session.lock");
         DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

         try {
            var2.writeLong(this.sessionId);
         } finally {
            var2.close();
         }

      } catch (IOException var7) {
         var7.printStackTrace();
         throw new RuntimeException("Failed to check session lock, aborting");
      }
   }

   public File getFolder() {
      return this.worldDir;
   }

   public void checkSession() throws LevelConflictException {
      try {
         File var1 = new File(this.worldDir, "session.lock");
         DataInputStream var2 = new DataInputStream(new FileInputStream(var1));

         try {
            if (var2.readLong() != this.sessionId) {
               throw new LevelConflictException("The save is being accessed from another location, aborting");
            }
         } finally {
            var2.close();
         }

      } catch (IOException var7) {
         throw new LevelConflictException("Failed to check session lock, aborting");
      }
   }

   @Nullable
   public LevelData prepareLevel() {
      File var1 = new File(this.worldDir, "level.dat");
      if (var1.exists()) {
         LevelData var2 = LevelStorageSource.getLevelData(var1, this.fixerUpper);
         if (var2 != null) {
            return var2;
         }
      }

      var1 = new File(this.worldDir, "level.dat_old");
      return var1.exists() ? LevelStorageSource.getLevelData(var1, this.fixerUpper) : null;
   }

   public void saveLevelData(LevelData var1) {
      this.saveLevelData(var1, (CompoundTag)null);
   }

   public void save(Player var1) {
      try {
         CompoundTag var2 = var1.saveWithoutId(new CompoundTag());
         File var3 = new File(this.playerDir, var1.getStringUUID() + ".dat.tmp");
         File var4 = new File(this.playerDir, var1.getStringUUID() + ".dat");
         NbtIo.writeCompressed(var2, new FileOutputStream(var3));
         if (var4.exists()) {
            var4.delete();
         }

         var3.renameTo(var4);
      } catch (Exception var5) {
         LOGGER.warn("Failed to save player data for {}", var1.getName().getString());
      }

   }

   @Nullable
   public CompoundTag load(Player var1) {
      CompoundTag var2 = null;

      try {
         File var3 = new File(this.playerDir, var1.getStringUUID() + ".dat");
         if (var3.exists() && var3.isFile()) {
            var2 = NbtIo.readCompressed(new FileInputStream(var3));
         }
      } catch (Exception var4) {
         LOGGER.warn("Failed to load player data for {}", var1.getName().getString());
      }

      if (var2 != null) {
         int var5 = var2.contains("DataVersion", 3) ? var2.getInt("DataVersion") : -1;
         var1.load(NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, var2, var5));
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

   public StructureManager getStructureManager() {
      return this.structureManager;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }
}
