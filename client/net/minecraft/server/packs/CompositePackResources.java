package net.minecraft.server.packs;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
   @Override
   public IoSupplier<InputStream> getRootResource(String... var1) {
      return this.primaryPackResources.getRootResource(var1);
   }

   @Nullable
   @Override
   public IoSupplier<InputStream> getResource(PackType var1, ResourceLocation var2) {
      for(PackResources var4 : this.packResourcesStack) {
         IoSupplier var5 = var4.getResource(var1, var2);
         if (var5 != null) {
            return var5;
         }
      }

      return null;
   }

   @Override
   public void listResources(PackType var1, String var2, String var3, PackResources.ResourceOutput var4) {
      HashMap var5 = new HashMap();

      for(PackResources var7 : this.packResourcesStack) {
         var7.listResources(var1, var2, var3, var5::putIfAbsent);
      }

      var5.forEach(var4);
   }

   @Override
   public Set<String> getNamespaces(PackType var1) {
      HashSet var2 = new HashSet();

      for(PackResources var4 : this.packResourcesStack) {
         var2.addAll(var4.getNamespaces(var1));
      }

      return var2;
   }

   @Nullable
   @Override
   public <T> T getMetadataSection(MetadataSectionSerializer<T> var1) throws IOException {
      return this.primaryPackResources.getMetadataSection(var1);
   }

   @Override
   public String packId() {
      return this.primaryPackResources.packId();
   }

   @Override
   public boolean isBuiltin() {
      return this.primaryPackResources.isBuiltin();
   }

   @Override
   public void close() {
      this.packResourcesStack.forEach(PackResources::close);
   }
}
