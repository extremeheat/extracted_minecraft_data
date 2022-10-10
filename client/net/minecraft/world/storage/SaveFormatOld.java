package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat {
   private static final Logger field_151479_b = LogManager.getLogger();
   protected final Path field_75808_a;
   protected final Path field_197717_b;
   protected final DataFixer field_186354_b;

   public SaveFormatOld(Path var1, Path var2, DataFixer var3) {
      super();
      this.field_186354_b = var3;

      try {
         Files.createDirectories(Files.exists(var1, new LinkOption[0]) ? var1.toRealPath() : var1);
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }

      this.field_75808_a = var1;
      this.field_197717_b = var2;
   }

   public String func_207741_a() {
      return "Old Format";
   }

   public List<WorldSummary> func_75799_b() throws AnvilConverterException {
      ArrayList var1 = Lists.newArrayList();

      for(int var2 = 0; var2 < 5; ++var2) {
         String var3 = "World" + (var2 + 1);
         WorldInfo var4 = this.func_75803_c(var3);
         if (var4 != null) {
            var1.add(new WorldSummary(var4, var3, "", var4.func_76092_g(), false));
         }
      }

      return var1;
   }

   public void func_75800_d() {
   }

   @Nullable
   public WorldInfo func_75803_c(String var1) {
      File var2 = new File(this.field_75808_a.toFile(), var1);
      if (!var2.exists()) {
         return null;
      } else {
         File var3 = new File(var2, "level.dat");
         if (var3.exists()) {
            WorldInfo var4 = func_186353_a(var3, this.field_186354_b);
            if (var4 != null) {
               return var4;
            }
         }

         var3 = new File(var2, "level.dat_old");
         return var3.exists() ? func_186353_a(var3, this.field_186354_b) : null;
      }
   }

   @Nullable
   public static WorldInfo func_186353_a(File var0, DataFixer var1) {
      try {
         NBTTagCompound var2 = CompressedStreamTools.func_74796_a(new FileInputStream(var0));
         NBTTagCompound var3 = var2.func_74775_l("Data");
         NBTTagCompound var4 = var3.func_150297_b("Player", 10) ? var3.func_74775_l("Player") : null;
         var3.func_82580_o("Player");
         int var5 = var3.func_150297_b("DataVersion", 99) ? var3.func_74762_e("DataVersion") : -1;
         return new WorldInfo(NBTUtil.func_210822_a(var1, DataFixTypes.LEVEL, var3, var5), var1, var5, var4);
      } catch (Exception var6) {
         field_151479_b.error("Exception reading {}", var0, var6);
         return null;
      }
   }

   public void func_75806_a(String var1, String var2) {
      File var3 = new File(this.field_75808_a.toFile(), var1);
      if (var3.exists()) {
         File var4 = new File(var3, "level.dat");
         if (var4.exists()) {
            try {
               NBTTagCompound var5 = CompressedStreamTools.func_74796_a(new FileInputStream(var4));
               NBTTagCompound var6 = var5.func_74775_l("Data");
               var6.func_74778_a("LevelName", var2);
               CompressedStreamTools.func_74799_a(var5, new FileOutputStream(var4));
            } catch (Exception var7) {
               var7.printStackTrace();
            }
         }

      }
   }

   public boolean func_207742_d(String var1) {
      File var2 = new File(this.field_75808_a.toFile(), var1);
      if (var2.exists()) {
         return false;
      } else {
         try {
            var2.mkdir();
            var2.delete();
            return true;
         } catch (Throwable var4) {
            field_151479_b.warn("Couldn't make new level", var4);
            return false;
         }
      }
   }

   public boolean func_75802_e(String var1) {
      File var2 = new File(this.field_75808_a.toFile(), var1);
      if (!var2.exists()) {
         return true;
      } else {
         field_151479_b.info("Deleting level {}", var1);

         for(int var3 = 1; var3 <= 5; ++var3) {
            field_151479_b.info("Attempt {}...", var3);
            if (func_75807_a(var2.listFiles())) {
               break;
            }

            field_151479_b.warn("Unsuccessful in deleting contents.");
            if (var3 < 5) {
               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
               }
            }
         }

         return var2.delete();
      }
   }

   protected static boolean func_75807_a(File[] var0) {
      File[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         File var4 = var1[var3];
         field_151479_b.debug("Deleting {}", var4);
         if (var4.isDirectory() && !func_75807_a(var4.listFiles())) {
            field_151479_b.warn("Couldn't delete directory {}", var4);
            return false;
         }

         if (!var4.delete()) {
            field_151479_b.warn("Couldn't delete file {}", var4);
            return false;
         }
      }

      return true;
   }

   public ISaveHandler func_197715_a(String var1, @Nullable MinecraftServer var2) {
      return new SaveHandler(this.field_75808_a.toFile(), var1, var2, this.field_186354_b);
   }

   public boolean func_207743_a(String var1) {
      return false;
   }

   public boolean func_75801_b(String var1) {
      return false;
   }

   public boolean func_75805_a(String var1, IProgressUpdate var2) {
      return false;
   }

   public boolean func_90033_f(String var1) {
      return Files.isDirectory(this.field_75808_a.resolve(var1), new LinkOption[0]);
   }

   public File func_186352_b(String var1, String var2) {
      return this.field_75808_a.resolve(var1).resolve(var2).toFile();
   }

   public Path func_197714_g(String var1) {
      return this.field_75808_a.resolve(var1);
   }

   public Path func_197712_e() {
      return this.field_197717_b;
   }
}
