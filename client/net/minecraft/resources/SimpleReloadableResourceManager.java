package net.minecraft.resources;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager {
   private static final Logger field_199012_a = LogManager.getLogger();
   private final Map<String, FallbackResourceManager> field_199014_c = Maps.newHashMap();
   private final List<IResourceManagerReloadListener> field_199015_d = Lists.newArrayList();
   private final Set<String> field_199016_e = Sets.newLinkedHashSet();
   private final ResourcePackType field_199017_f;

   public SimpleReloadableResourceManager(ResourcePackType var1) {
      super();
      this.field_199017_f = var1;
   }

   public void func_199009_a(IResourcePack var1) {
      FallbackResourceManager var4;
      for(Iterator var2 = var1.func_195759_a(this.field_199017_f).iterator(); var2.hasNext(); var4.func_199021_a(var1)) {
         String var3 = (String)var2.next();
         this.field_199016_e.add(var3);
         var4 = (FallbackResourceManager)this.field_199014_c.get(var3);
         if (var4 == null) {
            var4 = new FallbackResourceManager(this.field_199017_f);
            this.field_199014_c.put(var3, var4);
         }
      }

   }

   public Set<String> func_199001_a() {
      return this.field_199016_e;
   }

   public IResource func_199002_a(ResourceLocation var1) throws IOException {
      IResourceManager var2 = (IResourceManager)this.field_199014_c.get(var1.func_110624_b());
      if (var2 != null) {
         return var2.func_199002_a(var1);
      } else {
         throw new FileNotFoundException(var1.toString());
      }
   }

   public List<IResource> func_199004_b(ResourceLocation var1) throws IOException {
      IResourceManager var2 = (IResourceManager)this.field_199014_c.get(var1.func_110624_b());
      if (var2 != null) {
         return var2.func_199004_b(var1);
      } else {
         throw new FileNotFoundException(var1.toString());
      }
   }

   public Collection<ResourceLocation> func_199003_a(String var1, Predicate<String> var2) {
      HashSet var3 = Sets.newHashSet();
      Iterator var4 = this.field_199014_c.values().iterator();

      while(var4.hasNext()) {
         FallbackResourceManager var5 = (FallbackResourceManager)var4.next();
         var3.addAll(var5.func_199003_a(var1, var2));
      }

      ArrayList var6 = Lists.newArrayList(var3);
      Collections.sort(var6);
      return var6;
   }

   private void func_199008_b() {
      this.field_199014_c.clear();
      this.field_199016_e.clear();
   }

   public void func_199005_a(List<IResourcePack> var1) {
      this.func_199008_b();
      field_199012_a.info("Reloading ResourceManager: {}", var1.stream().map(IResourcePack::func_195762_a).collect(Collectors.joining(", ")));
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         IResourcePack var3 = (IResourcePack)var2.next();
         this.func_199009_a(var3);
      }

      if (field_199012_a.isDebugEnabled()) {
         this.func_199011_d();
      } else {
         this.func_199010_c();
      }

   }

   public void func_199006_a(IResourceManagerReloadListener var1) {
      this.field_199015_d.add(var1);
      if (field_199012_a.isDebugEnabled()) {
         field_199012_a.info(this.func_199007_b(var1));
      } else {
         var1.func_195410_a(this);
      }

   }

   private void func_199010_c() {
      Iterator var1 = this.field_199015_d.iterator();

      while(var1.hasNext()) {
         IResourceManagerReloadListener var2 = (IResourceManagerReloadListener)var1.next();
         var2.func_195410_a(this);
      }

   }

   private void func_199011_d() {
      field_199012_a.info("Reloading all resources! {} listeners to update.", this.field_199015_d.size());
      ArrayList var1 = Lists.newArrayList();
      Stopwatch var2 = Stopwatch.createStarted();
      Iterator var3 = this.field_199015_d.iterator();

      while(var3.hasNext()) {
         IResourceManagerReloadListener var4 = (IResourceManagerReloadListener)var3.next();
         var1.add(this.func_199007_b(var4));
      }

      var2.stop();
      field_199012_a.info("----");
      field_199012_a.info("Complete resource reload took {} ms", var2.elapsed(TimeUnit.MILLISECONDS));
      var3 = var1.iterator();

      while(var3.hasNext()) {
         String var5 = (String)var3.next();
         field_199012_a.info(var5);
      }

      field_199012_a.info("----");
   }

   private String func_199007_b(IResourceManagerReloadListener var1) {
      Stopwatch var2 = Stopwatch.createStarted();
      var1.func_195410_a(this);
      var2.stop();
      return "Resource reload for " + var1.getClass().getSimpleName() + " took " + var2.elapsed(TimeUnit.MILLISECONDS) + " ms";
   }
}
