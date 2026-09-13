package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class FallbackResourceManager implements IResourceManager {
   protected final List field_110540_a = new ArrayList();
   private final IMetadataSerializer field_110539_b;

   public FallbackResourceManager(IMetadataSerializer var1) {
      super();
      this.field_110539_b = var1;
   }

   public void func_110538_a(IResourcePack var1) {
      this.field_110540_a.add(var1);
   }

   @Override
   public Set func_135055_a() {
      return null;
   }

   @Override
   public IResource func_110536_a(ResourceLocation var1) {
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
               var6 = var2.func_110590_a(var3);
            }

            return new SimpleResource(var1, var5.func_110590_a(var1), var6, this.field_110539_b);
         }
      }

      throw new FileNotFoundException(var1.toString());
   }

   @Override
   public List func_135056_b(ResourceLocation var1) {
      ArrayList var2 = Lists.newArrayList();
      ResourceLocation var3 = func_110537_b(var1);

      for(IResourcePack var5 : this.field_110540_a) {
         if (var5.func_110589_b(var1)) {
            InputStream var6 = var5.func_110589_b(var3) ? var5.func_110590_a(var3) : null;
            var2.add(new SimpleResource(var1, var5.func_110590_a(var1), var6, this.field_110539_b));
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
}
