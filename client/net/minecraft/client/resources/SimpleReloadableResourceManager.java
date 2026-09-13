package net.minecraft.client.resources;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager {
   private static final Logger field_147967_a = LogManager.getLogger();
   private static final Joiner field_130074_a = Joiner.on(", ");
   private final Map field_110548_a = Maps.newHashMap();
   private final List field_110546_b = Lists.newArrayList();
   private final Set field_135057_d = Sets.newLinkedHashSet();
   private final IMetadataSerializer field_110547_c;

   public SimpleReloadableResourceManager(IMetadataSerializer var1) {
      super();
      this.field_110547_c = var1;
   }

   public void func_110545_a(IResourcePack var1) {
      for(String var3 : var1.func_110587_b()) {
         this.field_135057_d.add(var3);
         FallbackResourceManager var4 = (FallbackResourceManager)this.field_110548_a.get(var3);
         if (var4 == null) {
            var4 = new FallbackResourceManager(this.field_110547_c);
            this.field_110548_a.put(var3, var4);
         }

         var4.func_110538_a(var1);
      }
   }

   @Override
   public Set func_135055_a() {
      return this.field_135057_d;
   }

   @Override
   public IResource func_110536_a(ResourceLocation var1) {
      IResourceManager var2 = (IResourceManager)this.field_110548_a.get(var1.func_110624_b());
      if (var2 != null) {
         return var2.func_110536_a(var1);
      } else {
         throw new FileNotFoundException(var1.toString());
      }
   }

   @Override
   public List func_135056_b(ResourceLocation var1) {
      IResourceManager var2 = (IResourceManager)this.field_110548_a.get(var1.func_110624_b());
      if (var2 != null) {
         return var2.func_135056_b(var1);
      } else {
         throw new FileNotFoundException(var1.toString());
      }
   }

   private void func_110543_a() {
      this.field_110548_a.clear();
      this.field_135057_d.clear();
   }

   @Override
   public void func_110541_a(List var1) {
      this.func_110543_a();
      field_147967_a.info("Reloading ResourceManager: " + field_130074_a.join(Iterables.transform(var1, new SimpleReloadableResourceManager$1(this))));

      for(IResourcePack var3 : var1) {
         this.func_110545_a(var3);
      }

      this.func_110544_b();
   }

   @Override
   public void func_110542_a(IResourceManagerReloadListener var1) {
      this.field_110546_b.add(var1);
      var1.func_110549_a(this);
   }

   private void func_110544_b() {
      for(IResourceManagerReloadListener var2 : this.field_110546_b) {
         var2.func_110549_a(this);
      }
   }
}
