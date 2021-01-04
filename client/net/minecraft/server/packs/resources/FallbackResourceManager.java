package net.minecraft.server.packs.resources;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements ResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final List<Pack> fallbacks = Lists.newArrayList();
   private final PackType type;

   public FallbackResourceManager(PackType var1) {
      super();
      this.type = var1;
   }

   public void add(Pack var1) {
      this.fallbacks.add(var1);
   }

   public Set<String> getNamespaces() {
      return Collections.emptySet();
   }

   public Resource getResource(ResourceLocation var1) throws IOException {
      this.validateLocation(var1);
      Pack var2 = null;
      ResourceLocation var3 = getMetadataLocation(var1);

      for(int var4 = this.fallbacks.size() - 1; var4 >= 0; --var4) {
         Pack var5 = (Pack)this.fallbacks.get(var4);
         if (var2 == null && var5.hasResource(this.type, var3)) {
            var2 = var5;
         }

         if (var5.hasResource(this.type, var1)) {
            InputStream var6 = null;
            if (var2 != null) {
               var6 = this.getWrappedResource(var3, var2);
            }

            return new SimpleResource(var5.getName(), var1, this.getWrappedResource(var1, var5), var6);
         }
      }

      throw new FileNotFoundException(var1.toString());
   }

   public boolean hasResource(ResourceLocation var1) {
      if (!this.isValidLocation(var1)) {
         return false;
      } else {
         for(int var2 = this.fallbacks.size() - 1; var2 >= 0; --var2) {
            Pack var3 = (Pack)this.fallbacks.get(var2);
            if (var3.hasResource(this.type, var1)) {
               return true;
            }
         }

         return false;
      }
   }

   protected InputStream getWrappedResource(ResourceLocation var1, Pack var2) throws IOException {
      InputStream var3 = var2.getResource(this.type, var1);
      return (InputStream)(LOGGER.isDebugEnabled() ? new FallbackResourceManager.LeakedResourceWarningInputStream(var3, var1, var2.getName()) : var3);
   }

   private void validateLocation(ResourceLocation var1) throws IOException {
      if (!this.isValidLocation(var1)) {
         throw new IOException("Invalid relative path to resource: " + var1);
      }
   }

   private boolean isValidLocation(ResourceLocation var1) {
      return !var1.getPath().contains("..");
   }

   public List<Resource> getResources(ResourceLocation var1) throws IOException {
      this.validateLocation(var1);
      ArrayList var2 = Lists.newArrayList();
      ResourceLocation var3 = getMetadataLocation(var1);
      Iterator var4 = this.fallbacks.iterator();

      while(var4.hasNext()) {
         Pack var5 = (Pack)var4.next();
         if (var5.hasResource(this.type, var1)) {
            InputStream var6 = var5.hasResource(this.type, var3) ? this.getWrappedResource(var3, var5) : null;
            var2.add(new SimpleResource(var5.getName(), var1, this.getWrappedResource(var1, var5), var6));
         }
      }

      if (var2.isEmpty()) {
         throw new FileNotFoundException(var1.toString());
      } else {
         return var2;
      }
   }

   public Collection<ResourceLocation> listResources(String var1, Predicate<String> var2) {
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = this.fallbacks.iterator();

      while(var4.hasNext()) {
         Pack var5 = (Pack)var4.next();
         var3.addAll(var5.getResources(this.type, var1, 2147483647, var2));
      }

      Collections.sort(var3);
      return var3;
   }

   static ResourceLocation getMetadataLocation(ResourceLocation var0) {
      return new ResourceLocation(var0.getNamespace(), var0.getPath() + ".mcmeta");
   }

   static class LeakedResourceWarningInputStream extends InputStream {
      private final InputStream wrapped;
      private final String message;
      private boolean closed;

      public LeakedResourceWarningInputStream(InputStream var1, ResourceLocation var2, String var3) {
         super();
         this.wrapped = var1;
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         (new Exception()).printStackTrace(new PrintStream(var4));
         this.message = "Leaked resource: '" + var2 + "' loaded from pack: '" + var3 + "'\n" + var4;
      }

      public void close() throws IOException {
         this.wrapped.close();
         this.closed = true;
      }

      protected void finalize() throws Throwable {
         if (!this.closed) {
            FallbackResourceManager.LOGGER.warn(this.message);
         }

         super.finalize();
      }

      public int read() throws IOException {
         return this.wrapped.read();
      }
   }
}
