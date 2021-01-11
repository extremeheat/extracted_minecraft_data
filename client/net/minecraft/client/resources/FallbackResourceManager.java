package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements IResourceManager {
   private static final Logger field_177246_b = LogManager.getLogger();
   protected final List<IResourcePack> field_110540_a = Lists.newArrayList();
   private final IMetadataSerializer field_110539_b;

   public FallbackResourceManager(IMetadataSerializer var1) {
      super();
      this.field_110539_b = var1;
   }

   public void func_110538_a(IResourcePack var1) {
      this.field_110540_a.add(var1);
   }

   public Set<String> func_135055_a() {
      return null;
   }

   public IResource func_110536_a(ResourceLocation var1) throws IOException {
      IResourcePack var2 = null;
      ResourceLocation var3 = func_110537_b(var1);

      for(int var4 = this.field_110540_a.size() - 1; var4 >= 0; --var4) {
         IResourcePack var5 = (IResourcePack)this.field_110540_a.get(var4);
         if (var2 == null && var5.func_110589_b(var3)) {
            var2 = var5;
         }

         if (var5.func_110589_b(var1)) {
            InputStream var6 = null;
            if (var2 != null) {
               var6 = this.func_177245_a(var3, var2);
            }

            return new SimpleResource(var5.func_130077_b(), var1, this.func_177245_a(var1, var5), var6, this.field_110539_b);
         }
      }

      throw new FileNotFoundException(var1.toString());
   }

   protected InputStream func_177245_a(ResourceLocation var1, IResourcePack var2) throws IOException {
      InputStream var3 = var2.func_110590_a(var1);
      return (InputStream)(field_177246_b.isDebugEnabled() ? new FallbackResourceManager.InputStreamLeakedResourceLogger(var3, var1, var2.func_130077_b()) : var3);
   }

   public List<IResource> func_135056_b(ResourceLocation var1) throws IOException {
      ArrayList var2 = Lists.newArrayList();
      ResourceLocation var3 = func_110537_b(var1);
      Iterator var4 = this.field_110540_a.iterator();

      while(var4.hasNext()) {
         IResourcePack var5 = (IResourcePack)var4.next();
         if (var5.func_110589_b(var1)) {
            InputStream var6 = var5.func_110589_b(var3) ? this.func_177245_a(var3, var5) : null;
            var2.add(new SimpleResource(var5.func_130077_b(), var1, this.func_177245_a(var1, var5), var6, this.field_110539_b));
         }
      }

      if (var2.isEmpty()) {
         throw new FileNotFoundException(var1.toString());
      } else {
         return var2;
      }
   }

   static ResourceLocation func_110537_b(ResourceLocation var0) {
      return new ResourceLocation(var0.func_110624_b(), var0.func_110623_a() + ".mcmeta");
   }

   static class InputStreamLeakedResourceLogger extends InputStream {
      private final InputStream field_177330_a;
      private final String field_177328_b;
      private boolean field_177329_c = false;

      public InputStreamLeakedResourceLogger(InputStream var1, ResourceLocation var2, String var3) {
         super();
         this.field_177330_a = var1;
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         (new Exception()).printStackTrace(new PrintStream(var4));
         this.field_177328_b = "Leaked resource: '" + var2 + "' loaded from pack: '" + var3 + "'\n" + var4.toString();
      }

      public void close() throws IOException {
         this.field_177330_a.close();
         this.field_177329_c = true;
      }

      protected void finalize() throws Throwable {
         if (!this.field_177329_c) {
            FallbackResourceManager.field_177246_b.warn(this.field_177328_b);
         }

         super.finalize();
      }

      public int read() throws IOException {
         return this.field_177330_a.read();
      }
   }
}
