package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository {
   private static final Logger field_177320_c = LogManager.getLogger();
   private static final FileFilter field_110622_a = new FileFilter() {
      public boolean accept(File var1) {
         boolean var2 = var1.isFile() && var1.getName().endsWith(".zip");
         boolean var3 = var1.isDirectory() && (new File(var1, "pack.mcmeta")).isFile();
         return var2 || var3;
      }
   };
   private final File field_110618_d;
   public final IResourcePack field_110620_b;
   private final File field_148534_e;
   public final IMetadataSerializer field_110621_c;
   private IResourcePack field_148532_f;
   private final ReentrantLock field_177321_h = new ReentrantLock();
   private ListenableFuture<Object> field_177322_i;
   private List<ResourcePackRepository.Entry> field_110619_e = Lists.newArrayList();
   private List<ResourcePackRepository.Entry> field_110617_f = Lists.newArrayList();

   public ResourcePackRepository(File var1, File var2, IResourcePack var3, IMetadataSerializer var4, GameSettings var5) {
      super();
      this.field_110618_d = var1;
      this.field_148534_e = var2;
      this.field_110620_b = var3;
      this.field_110621_c = var4;
      this.func_110616_f();
      this.func_110611_a();
      Iterator var6 = var5.field_151453_l.iterator();

      while(true) {
         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            Iterator var8 = this.field_110619_e.iterator();

            while(var8.hasNext()) {
               ResourcePackRepository.Entry var9 = (ResourcePackRepository.Entry)var8.next();
               if (var9.func_110515_d().equals(var7)) {
                  if (var9.func_183027_f() == 1 || var5.field_183018_l.contains(var9.func_110515_d())) {
                     this.field_110617_f.add(var9);
                     break;
                  }

                  var6.remove();
                  field_177320_c.warn("Removed selected resource pack {} because it's no longer compatible", new Object[]{var9.func_110515_d()});
               }
            }
         }

         return;
      }
   }

   private void func_110616_f() {
      if (this.field_110618_d.exists()) {
         if (!this.field_110618_d.isDirectory() && (!this.field_110618_d.delete() || !this.field_110618_d.mkdirs())) {
            field_177320_c.warn("Unable to recreate resourcepack folder, it exists but is not a directory: " + this.field_110618_d);
         }
      } else if (!this.field_110618_d.mkdirs()) {
         field_177320_c.warn("Unable to create resourcepack folder: " + this.field_110618_d);
      }

   }

   private List<File> func_110614_g() {
      return this.field_110618_d.isDirectory() ? Arrays.asList(this.field_110618_d.listFiles(field_110622_a)) : Collections.emptyList();
   }

   public void func_110611_a() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.func_110614_g().iterator();

      while(var2.hasNext()) {
         File var3 = (File)var2.next();
         ResourcePackRepository.Entry var4 = new ResourcePackRepository.Entry(var3);
         if (!this.field_110619_e.contains(var4)) {
            try {
               var4.func_110516_a();
               var1.add(var4);
            } catch (Exception var6) {
               var1.remove(var4);
            }
         } else {
            int var5 = this.field_110619_e.indexOf(var4);
            if (var5 > -1 && var5 < this.field_110619_e.size()) {
               var1.add(this.field_110619_e.get(var5));
            }
         }
      }

      this.field_110619_e.removeAll(var1);
      var2 = this.field_110619_e.iterator();

      while(var2.hasNext()) {
         ResourcePackRepository.Entry var7 = (ResourcePackRepository.Entry)var2.next();
         var7.func_110517_b();
      }

      this.field_110619_e = var1;
   }

   public List<ResourcePackRepository.Entry> func_110609_b() {
      return ImmutableList.copyOf(this.field_110619_e);
   }

   public List<ResourcePackRepository.Entry> func_110613_c() {
      return ImmutableList.copyOf(this.field_110617_f);
   }

   public void func_148527_a(List<ResourcePackRepository.Entry> var1) {
      this.field_110617_f.clear();
      this.field_110617_f.addAll(var1);
   }

   public File func_110612_e() {
      return this.field_110618_d;
   }

   public ListenableFuture<Object> func_180601_a(String var1, String var2) {
      String var3;
      if (var2.matches("^[a-f0-9]{40}$")) {
         var3 = var2;
      } else {
         var3 = "legacy";
      }

      final File var4 = new File(this.field_148534_e, var3);
      this.field_177321_h.lock();

      ListenableFuture var9;
      try {
         this.func_148529_f();
         if (var4.exists() && var2.length() == 40) {
            try {
               String var5 = Hashing.sha1().hashBytes(Files.toByteArray(var4)).toString();
               if (var5.equals(var2)) {
                  ListenableFuture var16 = this.func_177319_a(var4);
                  return var16;
               }

               field_177320_c.warn("File " + var4 + " had wrong hash (expected " + var2 + ", found " + var5 + "). Deleting it.");
               FileUtils.deleteQuietly(var4);
            } catch (IOException var13) {
               field_177320_c.warn("File " + var4 + " couldn't be hashed. Deleting it.", var13);
               FileUtils.deleteQuietly(var4);
            }
         }

         this.func_183028_i();
         final GuiScreenWorking var15 = new GuiScreenWorking();
         Map var6 = Minecraft.func_175596_ai();
         final Minecraft var7 = Minecraft.func_71410_x();
         Futures.getUnchecked(var7.func_152344_a(new Runnable() {
            public void run() {
               var7.func_147108_a(var15);
            }
         }));
         final SettableFuture var8 = SettableFuture.create();
         this.field_177322_i = HttpUtil.func_180192_a(var4, var1, var6, 52428800, var15, var7.func_110437_J());
         Futures.addCallback(this.field_177322_i, new FutureCallback<Object>() {
            public void onSuccess(Object var1) {
               ResourcePackRepository.this.func_177319_a(var4);
               var8.set((Object)null);
            }

            public void onFailure(Throwable var1) {
               var8.setException(var1);
            }
         });
         var9 = this.field_177322_i;
      } finally {
         this.field_177321_h.unlock();
      }

      return var9;
   }

   private void func_183028_i() {
      ArrayList var1 = Lists.newArrayList(FileUtils.listFiles(this.field_148534_e, TrueFileFilter.TRUE, (IOFileFilter)null));
      Collections.sort(var1, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
      int var2 = 0;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         File var4 = (File)var3.next();
         if (var2++ >= 10) {
            field_177320_c.info("Deleting old server resource pack " + var4.getName());
            FileUtils.deleteQuietly(var4);
         }
      }

   }

   public ListenableFuture<Object> func_177319_a(File var1) {
      this.field_148532_f = new FileResourcePack(var1);
      return Minecraft.func_71410_x().func_175603_A();
   }

   public IResourcePack func_148530_e() {
      return this.field_148532_f;
   }

   public void func_148529_f() {
      this.field_177321_h.lock();

      try {
         if (this.field_177322_i != null) {
            this.field_177322_i.cancel(true);
         }

         this.field_177322_i = null;
         if (this.field_148532_f != null) {
            this.field_148532_f = null;
            Minecraft.func_71410_x().func_175603_A();
         }
      } finally {
         this.field_177321_h.unlock();
      }

   }

   public class Entry {
      private final File field_110523_b;
      private IResourcePack field_110524_c;
      private PackMetadataSection field_110521_d;
      private BufferedImage field_110522_e;
      private ResourceLocation field_110520_f;

      private Entry(File var2) {
         super();
         this.field_110523_b = var2;
      }

      public void func_110516_a() throws IOException {
         this.field_110524_c = (IResourcePack)(this.field_110523_b.isDirectory() ? new FolderResourcePack(this.field_110523_b) : new FileResourcePack(this.field_110523_b));
         this.field_110521_d = (PackMetadataSection)this.field_110524_c.func_135058_a(ResourcePackRepository.this.field_110621_c, "pack");

         try {
            this.field_110522_e = this.field_110524_c.func_110586_a();
         } catch (IOException var2) {
         }

         if (this.field_110522_e == null) {
            this.field_110522_e = ResourcePackRepository.this.field_110620_b.func_110586_a();
         }

         this.func_110517_b();
      }

      public void func_110518_a(TextureManager var1) {
         if (this.field_110520_f == null) {
            this.field_110520_f = var1.func_110578_a("texturepackicon", new DynamicTexture(this.field_110522_e));
         }

         var1.func_110577_a(this.field_110520_f);
      }

      public void func_110517_b() {
         if (this.field_110524_c instanceof Closeable) {
            IOUtils.closeQuietly((Closeable)this.field_110524_c);
         }

      }

      public IResourcePack func_110514_c() {
         return this.field_110524_c;
      }

      public String func_110515_d() {
         return this.field_110524_c.func_130077_b();
      }

      public String func_110519_e() {
         return this.field_110521_d == null ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing 'pack' section)" : this.field_110521_d.func_152805_a().func_150254_d();
      }

      public int func_183027_f() {
         return this.field_110521_d.func_110462_b();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 instanceof ResourcePackRepository.Entry ? this.toString().equals(var1.toString()) : false;
         }
      }

      public int hashCode() {
         return this.toString().hashCode();
      }

      public String toString() {
         return String.format("%s:%s:%d", this.field_110523_b.getName(), this.field_110523_b.isDirectory() ? "folder" : "zip", this.field_110523_b.lastModified());
      }

      // $FF: synthetic method
      Entry(File var2, Object var3) {
         this(var2);
      }
   }
}
