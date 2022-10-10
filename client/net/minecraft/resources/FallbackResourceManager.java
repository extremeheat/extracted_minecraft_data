package net.minecraft.resources;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements IResourceManager {
   private static final Logger field_199024_b = LogManager.getLogger();
   protected final List<IResourcePack> field_199023_a = Lists.newArrayList();
   private final ResourcePackType field_199025_c;

   public FallbackResourceManager(ResourcePackType var1) {
      super();
      this.field_199025_c = var1;
   }

   public void func_199021_a(IResourcePack var1) {
      this.field_199023_a.add(var1);
   }

   public Set<String> func_199001_a() {
      return Collections.emptySet();
   }

   public IResource func_199002_a(ResourceLocation var1) throws IOException {
      this.func_199022_d(var1);
      IResourcePack var2 = null;
      ResourceLocation var3 = func_199020_c(var1);

      for(int var4 = this.field_199023_a.size() - 1; var4 >= 0; --var4) {
         IResourcePack var5 = (IResourcePack)this.field_199023_a.get(var4);
         if (var2 == null && var5.func_195764_b(this.field_199025_c, var3)) {
            var2 = var5;
         }

         if (var5.func_195764_b(this.field_199025_c, var1)) {
            InputStream var6 = null;
            if (var2 != null) {
               var6 = this.func_199019_a(var3, var2);
            }

            return new SimpleResource(var5.func_195762_a(), var1, this.func_199019_a(var1, var5), var6);
         }
      }

      throw new FileNotFoundException(var1.toString());
   }

   protected InputStream func_199019_a(ResourceLocation var1, IResourcePack var2) throws IOException {
      InputStream var3 = var2.func_195761_a(this.field_199025_c, var1);
      return (InputStream)(field_199024_b.isDebugEnabled() ? new FallbackResourceManager.LeakComplainerInputStream(var3, var1, var2.func_195762_a()) : var3);
   }

   private void func_199022_d(ResourceLocation var1) throws IOException {
      if (var1.func_110623_a().contains("..")) {
         throw new IOException("Invalid relative path to resource: " + var1);
      }
   }

   public List<IResource> func_199004_b(ResourceLocation var1) throws IOException {
      this.func_199022_d(var1);
      ArrayList var2 = Lists.newArrayList();
      ResourceLocation var3 = func_199020_c(var1);
      Iterator var4 = this.field_199023_a.iterator();

      while(var4.hasNext()) {
         IResourcePack var5 = (IResourcePack)var4.next();
         if (var5.func_195764_b(this.field_199025_c, var1)) {
            InputStream var6 = var5.func_195764_b(this.field_199025_c, var3) ? this.func_199019_a(var3, var5) : null;
            var2.add(new SimpleResource(var5.func_195762_a(), var1, this.func_199019_a(var1, var5), var6));
         }
      }

      if (var2.isEmpty()) {
         throw new FileNotFoundException(var1.toString());
      } else {
         return var2;
      }
   }

   public Collection<ResourceLocation> func_199003_a(String var1, Predicate<String> var2) {
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = this.field_199023_a.iterator();

      while(var4.hasNext()) {
         IResourcePack var5 = (IResourcePack)var4.next();
         var3.addAll(var5.func_195758_a(this.field_199025_c, var1, 2147483647, var2));
      }

      Collections.sort(var3);
      return var3;
   }

   static ResourceLocation func_199020_c(ResourceLocation var0) {
      return new ResourceLocation(var0.func_110624_b(), var0.func_110623_a() + ".mcmeta");
   }

   static class LeakComplainerInputStream extends InputStream {
      private final InputStream field_198998_a;
      private final String field_198999_b;
      private boolean field_199000_c;

      public LeakComplainerInputStream(InputStream var1, ResourceLocation var2, String var3) {
         super();
         this.field_198998_a = var1;
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         (new Exception()).printStackTrace(new PrintStream(var4));
         this.field_198999_b = "Leaked resource: '" + var2 + "' loaded from pack: '" + var3 + "'\n" + var4;
      }

      public void close() throws IOException {
         this.field_198998_a.close();
         this.field_199000_c = true;
      }

      protected void finalize() throws Throwable {
         if (!this.field_199000_c) {
            FallbackResourceManager.field_199024_b.warn(this.field_198999_b);
         }

         super.finalize();
      }

      public int read() throws IOException {
         return this.field_198998_a.read();
      }
   }
}
