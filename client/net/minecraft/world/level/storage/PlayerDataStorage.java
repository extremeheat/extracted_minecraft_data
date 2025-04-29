package net.minecraft.world.level.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class PlayerDataStorage {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final File playerDir;
   protected final DataFixer fixerUpper;
   private static final DateTimeFormatter FORMATTER = FileNameDateFormatter.create();

   public PlayerDataStorage(LevelStorageSource.LevelStorageAccess var1, DataFixer var2) {
      super();
      this.fixerUpper = var2;
      this.playerDir = var1.getLevelPath(LevelResource.PLAYER_DATA_DIR).toFile();
      this.playerDir.mkdirs();
   }

   public void save(Player var1) {
      try (ProblemReporter.ScopedCollector var2 = new ProblemReporter.ScopedCollector(var1.problemPath(), LOGGER)) {
         TagValueOutput var3 = TagValueOutput.createWithContext(var2, var1.registryAccess());
         var1.saveWithoutId(var3);
         Path var4 = this.playerDir.toPath();
         Path var5 = Files.createTempFile(var4, var1.getStringUUID() + "-", ".dat");
         CompoundTag var6 = var3.buildResult();
         NbtIo.writeCompressed(var6, var5);
         Path var7 = var4.resolve(var1.getStringUUID() + ".dat");
         Path var8 = var4.resolve(var1.getStringUUID() + ".dat_old");
         Util.safeReplaceFile(var7, var5, var8);
      } catch (Exception var11) {
         LOGGER.warn("Failed to save player data for {}", var1.getName().getString());
      }

   }

   private void backup(Player var1, String var2) {
      Path var3 = this.playerDir.toPath();
      String var10001 = var1.getStringUUID();
      Path var4 = var3.resolve(var10001 + var2);
      var10001 = var1.getStringUUID();
      Path var5 = var3.resolve(var10001 + "_corrupted_" + LocalDateTime.now().format(FORMATTER) + var2);
      if (Files.isRegularFile(var4, new LinkOption[0])) {
         try {
            Files.copy(var4, var5, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
         } catch (Exception var7) {
            LOGGER.warn("Failed to copy the player.dat file for {}", var1.getName().getString(), var7);
         }

      }
   }

   private Optional<CompoundTag> load(Player var1, String var2) {
      File var10002 = this.playerDir;
      String var10003 = var1.getStringUUID();
      File var3 = new File(var10002, var10003 + var2);
      if (var3.exists() && var3.isFile()) {
         try {
            return Optional.of(NbtIo.readCompressed(var3.toPath(), NbtAccounter.unlimitedHeap()));
         } catch (Exception var5) {
            LOGGER.warn("Failed to load player data for {}", var1.getName().getString());
         }
      }

      return Optional.empty();
   }

   public Optional<ValueInput> load(Player var1, ProblemReporter var2) {
      Optional var3 = this.load(var1, ".dat");
      if (var3.isEmpty()) {
         this.backup(var1, ".dat");
      }

      return var3.or(() -> this.load(var1, ".dat_old")).map((var3x) -> {
         int var4 = NbtUtils.getDataVersion(var3x, -1);
         var3x = DataFixTypes.PLAYER.updateToCurrentVersion(this.fixerUpper, var3x, var4);
         ValueInput var5 = TagValueInput.create(var2, var1.registryAccess(), var3x);
         var1.load(var5);
         return var5;
      });
   }
}
