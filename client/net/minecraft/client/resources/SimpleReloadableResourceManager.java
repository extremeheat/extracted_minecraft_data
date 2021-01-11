package net.minecraft.client.resources;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
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
   private final Map<String, FallbackResourceManager> field_110548_a = Maps.newHashMap();
   private final List<IResourceManagerReloadListener> field_110546_b = Lists.newArrayList();
   private final Set<String> field_135057_d = Sets.newLinkedHashSet();
   private final IMetadataSerializer field_110547_c;

   public SimpleReloadableResourceManager(IMetadataSerializer var1) {
      super();
      this.field_110547_c = var1;
   }

   public void func_110545_a(IResourcePack var1) {
      FallbackResourceManager var4;
      for(Iterator var2 = var1.func_110587_b().iterator(); var2.hasNext(); var4.func_110538_a(var1)) {
         String var3 = (String)var2.next();
         this.field_135057_d.add(var3);
         var4 = (FallbackResourceManager)this.field_110548_a.get(var3);
         if (var4 == null) {
            var4 = new FallbackResourceManager(this.field_110547_c);
            this.field_110548_a.put(var3, var4);
         }
      }

   }

   public Set<String> func_135055_a() {
      return this.field_135057_d;
   }

   public IResource func_110536_a(ResourceLocation var1) throws IOException {
      IResourceManager var2 = (IResourceManager)this.field_110548_a.get(var1.func_110624_b());
      if (var2 != null) {
         return var2.func_110536_a(var1);
      } else {
         throw new FileNotFoundException(var1.toString());
      }
   }

   public List<IResource> func_135056_b(ResourceLocation var1) throws IOException {
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

   public void func_110541_a(List<IResourcePack> var1) {
      this.func_110543_a();
      field_147967_a.info("Reloading ResourceManager: " + field_130074_a.join(Iterables.transform(var1, new Function<IResourcePack, String>() {
         public String apply(IResourcePack var1) {
            return var1.func_130077_b();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((IResourcePack)var1);
         }
      })));
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         IResourcePack var3 = (IResourcePack)var2.next();
         this.func_110545_a(var3);
      }

      this.func_110544_b();
   }

   public void func_110542_a(IResourceManagerReloadListener var1) {
      this.field_110546_b.add(var1);
      var1.func_110549_a(this);
   }

   private void func_110544_b() {
      Iterator var1 = this.field_110546_b.iterator();

      while(var1.hasNext()) {
         IResourceManagerReloadListener var2 = (IResourceManagerReloadListener)var1.next();
         var2.func_110549_a(this);
      }

   }
}
