package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.VanillaPack;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadingPackFinder implements IPackFinder {
   private static final Logger field_195751_a = LogManager.getLogger();
   private static final Pattern field_195752_b = Pattern.compile("^[a-fA-F0-9]{40}$");
   private final VanillaPack field_195753_c;
   private final File field_195754_d;
   private final ReentrantLock field_195755_e = new ReentrantLock();
   @Nullable
   private ListenableFuture<?> field_195756_f;
   @Nullable
   private ResourcePackInfoClient field_195757_g;

   public DownloadingPackFinder(File var1, ResourceIndex var2) {
      super();
      this.field_195754_d = var1;
      this.field_195753_c = new VirtualAssetsPack(var2);
   }

   public <T extends ResourcePackInfo> void func_195730_a(Map<String, T> var1, ResourcePackInfo.IFactory<T> var2) {
      ResourcePackInfo var3 = ResourcePackInfo.func_195793_a("vanilla", true, () -> {
         return this.field_195753_c;
      }, var2, ResourcePackInfo.Priority.BOTTOM);
      if (var3 != null) {
         var1.put("vanilla", var3);
      }

      if (this.field_195757_g != null) {
         var1.put("server", this.field_195757_g);
      }

   }

   public VanillaPack func_195746_a() {
      return this.field_195753_c;
   }

   public static Map<String, String> func_195742_b() {
      HashMap var0 = Maps.newHashMap();
      var0.put("X-Minecraft-Username", Minecraft.func_71410_x().func_110432_I().func_111285_a());
      var0.put("X-Minecraft-UUID", Minecraft.func_71410_x().func_110432_I().func_148255_b());
      var0.put("X-Minecraft-Version", "1.13.2");
      var0.put("X-Minecraft-Pack-Format", String.valueOf(4));
      var0.put("User-Agent", "Minecraft Java/1.13.2");
      return var0;
   }

   public ListenableFuture<?> func_195744_a(String var1, String var2) {
      String var3 = DigestUtils.sha1Hex(var1);
      final String var4 = field_195752_b.matcher(var2).matches() ? var2 : "";
      final File var5 = new File(this.field_195754_d, var3);
      this.field_195755_e.lock();

      ListenableFuture var10;
      try {
         this.func_195749_c();
         if (var5.exists()) {
            if (this.func_195745_a(var4, var5)) {
               ListenableFuture var14 = this.func_195741_a(var5);
               return var14;
            }

            field_195751_a.warn("Deleting file {}", var5);
            FileUtils.deleteQuietly(var5);
         }

         this.func_195747_e();
         GuiScreenWorking var6 = new GuiScreenWorking();
         Map var7 = func_195742_b();
         Minecraft var8 = Minecraft.func_71410_x();
         Futures.getUnchecked(var8.func_152344_a(() -> {
            var8.func_147108_a(var6);
         }));
         final SettableFuture var9 = SettableFuture.create();
         this.field_195756_f = HttpUtil.func_180192_a(var5, var1, var7, 52428800, var6, var8.func_110437_J());
         Futures.addCallback(this.field_195756_f, new FutureCallback<Object>() {
            public void onSuccess(@Nullable Object var1) {
               if (DownloadingPackFinder.this.func_195745_a(var4, var5)) {
                  DownloadingPackFinder.this.func_195741_a(var5);
                  var9.set((Object)null);
               } else {
                  DownloadingPackFinder.field_195751_a.warn("Deleting file {}", var5);
                  FileUtils.deleteQuietly(var5);
               }

            }

            public void onFailure(Throwable var1) {
               FileUtils.deleteQuietly(var5);
               var9.setException(var1);
            }
         });
         var10 = this.field_195756_f;
      } finally {
         this.field_195755_e.unlock();
      }

      return var10;
   }

   public void func_195749_c() {
      this.field_195755_e.lock();

      try {
         if (this.field_195756_f != null) {
            this.field_195756_f.cancel(true);
         }

         this.field_195756_f = null;
         if (this.field_195757_g != null) {
            this.field_195757_g = null;
            Minecraft.func_71410_x().func_175603_A();
         }
      } finally {
         this.field_195755_e.unlock();
      }

   }

   private boolean func_195745_a(String var1, File var2) {
      try {
         String var3 = DigestUtils.sha1Hex(new FileInputStream(var2));
         if (var1.isEmpty()) {
            field_195751_a.info("Found file {} without verification hash", var2);
            return true;
         }

         if (var3.toLowerCase(java.util.Locale.ROOT).equals(var1.toLowerCase(java.util.Locale.ROOT))) {
            field_195751_a.info("Found file {} matching requested hash {}", var2, var1);
            return true;
         }

         field_195751_a.warn("File {} had wrong hash (expected {}, found {}).", var2, var1, var3);
      } catch (IOException var4) {
         field_195751_a.warn("File {} couldn't be hashed.", var2, var4);
      }

      return false;
   }

   private void func_195747_e() {
      try {
         ArrayList var1 = Lists.newArrayList(FileUtils.listFiles(this.field_195754_d, TrueFileFilter.TRUE, (IOFileFilter)null));
         var1.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
         int var2 = 0;
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            File var4 = (File)var3.next();
            if (var2++ >= 10) {
               field_195751_a.info("Deleting old server resource pack {}", var4.getName());
               FileUtils.deleteQuietly(var4);
            }
         }
      } catch (IllegalArgumentException var5) {
         field_195751_a.error("Error while deleting old server resource pack : {}", var5.getMessage());
      }

   }

   public ListenableFuture<Object> func_195741_a(File var1) {
      PackMetadataSection var2 = null;
      NativeImage var3 = null;

      try {
         FilePack var4 = new FilePack(var1);
         Throwable var5 = null;

         try {
            var2 = (PackMetadataSection)var4.func_195760_a(PackMetadataSection.field_198964_a);

            try {
               InputStream var6 = var4.func_195763_b("pack.png");
               Throwable var7 = null;

               try {
                  var3 = NativeImage.func_195713_a(var6);
               } catch (Throwable var34) {
                  var7 = var34;
                  throw var34;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var33) {
                           var7.addSuppressed(var33);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }
            } catch (IllegalArgumentException | IOException var36) {
            }
         } catch (Throwable var37) {
            var5 = var37;
            throw var37;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var32) {
                     var5.addSuppressed(var32);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (IOException var39) {
      }

      if (var2 == null) {
         return Futures.immediateFailedFuture(new RuntimeException("Invalid resourcepack"));
      } else {
         this.field_195757_g = new ResourcePackInfoClient("server", true, () -> {
            return new FilePack(var1);
         }, new TextComponentTranslation("resourcePack.server.name", new Object[0]), var2.func_198963_a(), PackCompatibility.func_198969_a(var2.func_198962_b()), ResourcePackInfo.Priority.TOP, true, var3);
         return Minecraft.func_71410_x().func_175603_A();
      }
   }
}
