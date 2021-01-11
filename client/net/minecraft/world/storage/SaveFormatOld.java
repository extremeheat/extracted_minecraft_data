package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat {
   private static final Logger field_151479_b = LogManager.getLogger();
   protected final File field_75808_a;

   public SaveFormatOld(File var1) {
      super();
      if (!var1.exists()) {
         var1.mkdirs();
      }

      this.field_75808_a = var1;
   }

   public String func_154333_a() {
      return "Old Format";
   }

   public List<SaveFormatComparator> func_75799_b() throws AnvilConverterException {
      ArrayList var1 = Lists.newArrayList();

      for(int var2 = 0; var2 < 5; ++var2) {
         String var3 = "World" + (var2 + 1);
         WorldInfo var4 = this.func_75803_c(var3);
         if (var4 != null) {
            var1.add(new SaveFormatComparator(var3, "", var4.func_76057_l(), var4.func_76092_g(), var4.func_76077_q(), false, var4.func_76093_s(), var4.func_76086_u()));
         }
      }

      return var1;
   }

   public void func_75800_d() {
   }

   public WorldInfo func_75803_c(String var1) {
      File var2 = new File(this.field_75808_a, var1);
      if (!var2.exists()) {
         return null;
      } else {
         File var3 = new File(var2, "level.dat");
         NBTTagCompound var4;
         NBTTagCompound var5;
         if (var3.exists()) {
            try {
               var4 = CompressedStreamTools.func_74796_a(new FileInputStream(var3));
               var5 = var4.func_74775_l("Data");
               return new WorldInfo(var5);
            } catch (Exception var7) {
               field_151479_b.error("Exception reading " + var3, var7);
            }
         }

         var3 = new File(var2, "level.dat_old");
         if (var3.exists()) {
            try {
               var4 = CompressedStreamTools.func_74796_a(new FileInputStream(var3));
               var5 = var4.func_74775_l("Data");
               return new WorldInfo(var5);
            } catch (Exception var6) {
               field_151479_b.error("Exception reading " + var3, var6);
            }
         }

         return null;
      }
   }

   public void func_75806_a(String var1, String var2) {
      File var3 = new File(this.field_75808_a, var1);
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

   public boolean func_154335_d(String var1) {
      File var2 = new File(this.field_75808_a, var1);
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
      File var2 = new File(this.field_75808_a, var1);
      if (!var2.exists()) {
         return true;
      } else {
         field_151479_b.info("Deleting level " + var1);

         for(int var3 = 1; var3 <= 5; ++var3) {
            field_151479_b.info("Attempt " + var3 + "...");
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
      for(int var1 = 0; var1 < var0.length; ++var1) {
         File var2 = var0[var1];
         field_151479_b.debug("Deleting " + var2);
         if (var2.isDirectory() && !func_75807_a(var2.listFiles())) {
            field_151479_b.warn("Couldn't delete directory " + var2);
            return false;
         }

         if (!var2.delete()) {
            field_151479_b.warn("Couldn't delete file " + var2);
            return false;
         }
      }

      return true;
   }

   public ISaveHandler func_75804_a(String var1, boolean var2) {
      return new SaveHandler(this.field_75808_a, var1, var2);
   }

   public boolean func_154334_a(String var1) {
      return false;
   }

   public boolean func_75801_b(String var1) {
      return false;
   }

   public boolean func_75805_a(String var1, IProgressUpdate var2) {
      return false;
   }

   public boolean func_90033_f(String var1) {
      File var2 = new File(this.field_75808_a, var1);
      return var2.isDirectory();
   }
}
