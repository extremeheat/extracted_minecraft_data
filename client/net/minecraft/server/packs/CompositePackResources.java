package net.minecraft.server.packs;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;

public class CompositePackResources implements PackResources {
   private final PackResources primaryPackResources;
   private final List<PackResources> packResourcesStack;

   public CompositePackResources(PackResources var1, List<PackResources> var2) {
      super();
      this.primaryPackResources = var1;
      ArrayList var3 = new ArrayList(var2.size() + 1);
      var3.addAll(Lists.reverse(var2));
      var3.add(var1);
      this.packResourcesStack = List.copyOf(var3);
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... var1) {
      return this.primaryPackResources.getRootResource(var1);
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType var1, ResourceLocation var2) {
      Iterator var3 = this.packResourcesStack.iterator();

      IoSupplier var5;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         PackResources var4 = (PackResources)var3.next();
         var5 = var4.getResource(var1, var2);
      } while(var5 == null);

      return var5;
   }

   public void listResources(PackType var1, String var2, String var3, PackResources.ResourceOutput var4) {
      HashMap var5 = new HashMap();
      Iterator var6 = this.packResourcesStack.iterator();

      while(var6.hasNext()) {
         PackResources var7 = (PackResources)var6.next();
         Objects.requireNonNull(var5);
         var7.listResources(var1, var2, var3, var5::putIfAbsent);
      }

      var5.forEach(var4);
   }

   public Set<String> getNamespaces(PackType var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = this.packResourcesStack.iterator();

      while(var3.hasNext()) {
         PackResources var4 = (PackResources)var3.next();
         var2.addAll(var4.getNamespaces(var1));
      }

      return var2;
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> var1) throws IOException {
      return this.primaryPackResources.getMetadataSection(var1);
   }

   public PackLocationInfo location() {
      return this.primaryPackResources.location();
   }

   public void close() {
      this.packResourcesStack.forEach(PackResources::close);
   }
}
