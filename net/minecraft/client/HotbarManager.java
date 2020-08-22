package net.minecraft.client;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.SharedConstants;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HotbarManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File optionsFile;
   private final DataFixer fixerUpper;
   private final Hotbar[] hotbars = new Hotbar[9];
   private boolean loaded;

   public HotbarManager(File var1, DataFixer var2) {
      this.optionsFile = new File(var1, "hotbar.nbt");
      this.fixerUpper = var2;

      for(int var3 = 0; var3 < 9; ++var3) {
         this.hotbars[var3] = new Hotbar();
      }

   }

   private void load() {
      try {
         CompoundTag var1 = NbtIo.read(this.optionsFile);
         if (var1 == null) {
            return;
         }

         if (!var1.contains("DataVersion", 99)) {
            var1.putInt("DataVersion", 1343);
         }

         var1 = NbtUtils.update(this.fixerUpper, DataFixTypes.HOTBAR, var1, var1.getInt("DataVersion"));

         for(int var2 = 0; var2 < 9; ++var2) {
            this.hotbars[var2].fromTag(var1.getList(String.valueOf(var2), 10));
         }
      } catch (Exception var3) {
         LOGGER.error("Failed to load creative mode options", var3);
      }

   }

   public void save() {
      try {
         CompoundTag var1 = new CompoundTag();
         var1.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

         for(int var2 = 0; var2 < 9; ++var2) {
            var1.put(String.valueOf(var2), this.get(var2).createTag());
         }

         NbtIo.write(var1, this.optionsFile);
      } catch (Exception var3) {
         LOGGER.error("Failed to save creative mode options", var3);
      }

   }

   public Hotbar get(int var1) {
      if (!this.loaded) {
         this.load();
         this.loaded = true;
      }

      return this.hotbars[var1];
   }
}
