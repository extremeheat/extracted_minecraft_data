package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveHandler implements ISaveHandler, IPlayerFileData {
   private static final Logger field_151478_a = LogManager.getLogger();
   private final File field_75770_b;
   private final File field_75771_c;
   private final long field_75769_e = Util.func_211177_b();
   private final String field_75767_f;
   private final TemplateManager field_186342_h;
   protected final DataFixer field_186341_a;

   public SaveHandler(File var1, String var2, @Nullable MinecraftServer var3, DataFixer var4) {
      super();
      this.field_186341_a = var4;
      this.field_75770_b = new File(var1, var2);
      this.field_75770_b.mkdirs();
      this.field_75771_c = new File(this.field_75770_b, "playerdata");
      this.field_75767_f = var2;
      if (var3 != null) {
         this.field_75771_c.mkdirs();
         this.field_186342_h = new TemplateManager(var3, this.field_75770_b, var4);
      } else {
         this.field_186342_h = null;
      }

      this.func_75766_h();
   }

   private void func_75766_h() {
      try {
         File var1 = new File(this.field_75770_b, "session.lock");
         DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

         try {
            var2.writeLong(this.field_75769_e);
         } finally {
            var2.close();
         }

      } catch (IOException var7) {
         var7.printStackTrace();
         throw new RuntimeException("Failed to check session lock, aborting");
      }
   }

   public File func_75765_b() {
      return this.field_75770_b;
   }

   public void func_75762_c() throws SessionLockException {
      try {
         File var1 = new File(this.field_75770_b, "session.lock");
         DataInputStream var2 = new DataInputStream(new FileInputStream(var1));

         try {
            if (var2.readLong() != this.field_75769_e) {
               throw new SessionLockException("The save is being accessed from another location, aborting");
            }
         } finally {
            var2.close();
         }

      } catch (IOException var7) {
         throw new SessionLockException("Failed to check session lock, aborting");
      }
   }

   public IChunkLoader func_75763_a(Dimension var1) {
      throw new RuntimeException("Old Chunk Storage is no longer supported.");
   }

   @Nullable
   public WorldInfo func_75757_d() {
      File var1 = new File(this.field_75770_b, "level.dat");
      if (var1.exists()) {
         WorldInfo var2 = SaveFormatOld.func_186353_a(var1, this.field_186341_a);
         if (var2 != null) {
            return var2;
         }
      }

      var1 = new File(this.field_75770_b, "level.dat_old");
      return var1.exists() ? SaveFormatOld.func_186353_a(var1, this.field_186341_a) : null;
   }

   public void func_75755_a(WorldInfo var1, @Nullable NBTTagCompound var2) {
      NBTTagCompound var3 = var1.func_76082_a(var2);
      NBTTagCompound var4 = new NBTTagCompound();
      var4.func_74782_a("Data", var3);

      try {
         File var5 = new File(this.field_75770_b, "level.dat_new");
         File var6 = new File(this.field_75770_b, "level.dat_old");
         File var7 = new File(this.field_75770_b, "level.dat");
         CompressedStreamTools.func_74799_a(var4, new FileOutputStream(var5));
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

   public void func_75761_a(WorldInfo var1) {
      this.func_75755_a(var1, (NBTTagCompound)null);
   }

   public void func_75753_a(EntityPlayer var1) {
      try {
         NBTTagCompound var2 = var1.func_189511_e(new NBTTagCompound());
         File var3 = new File(this.field_75771_c, var1.func_189512_bd() + ".dat.tmp");
         File var4 = new File(this.field_75771_c, var1.func_189512_bd() + ".dat");
         CompressedStreamTools.func_74799_a(var2, new FileOutputStream(var3));
         if (var4.exists()) {
            var4.delete();
         }

         var3.renameTo(var4);
      } catch (Exception var5) {
         field_151478_a.warn("Failed to save player data for {}", var1.func_200200_C_().getString());
      }

   }

   @Nullable
   public NBTTagCompound func_75752_b(EntityPlayer var1) {
      NBTTagCompound var2 = null;

      try {
         File var3 = new File(this.field_75771_c, var1.func_189512_bd() + ".dat");
         if (var3.exists() && var3.isFile()) {
            var2 = CompressedStreamTools.func_74796_a(new FileInputStream(var3));
         }
      } catch (Exception var4) {
         field_151478_a.warn("Failed to load player data for {}", var1.func_200200_C_().getString());
      }

      if (var2 != null) {
         int var5 = var2.func_150297_b("DataVersion", 3) ? var2.func_74762_e("DataVersion") : -1;
         var1.func_70020_e(NBTUtil.func_210822_a(this.field_186341_a, DataFixTypes.PLAYER, var2, var5));
      }

      return var2;
   }

   public IPlayerFileData func_75756_e() {
      return this;
   }

   public String[] func_75754_f() {
      String[] var1 = this.field_75771_c.list();
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

   public void func_75759_a() {
   }

   public File func_212423_a(DimensionType var1, String var2) {
      File var3 = new File(var1.func_212679_a(this.field_75770_b), "data");
      var3.mkdirs();
      return new File(var3, var2 + ".dat");
   }

   public TemplateManager func_186340_h() {
      return this.field_186342_h;
   }

   public DataFixer func_197718_i() {
      return this.field_186341_a;
   }
}
