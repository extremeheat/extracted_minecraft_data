package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollection<T> {
   private static final Logger field_199918_a = LogManager.getLogger();
   private static final Gson field_199919_b = new Gson();
   private static final int field_199920_c = ".json".length();
   private final Map<ResourceLocation, Tag<T>> field_199921_d = Maps.newHashMap();
   private final Function<ResourceLocation, T> field_200040_e;
   private final Predicate<ResourceLocation> field_200156_f;
   private final String field_199923_f;
   private final boolean field_200041_g;
   private final String field_200157_i;

   public TagCollection(Predicate<ResourceLocation> var1, Function<ResourceLocation, T> var2, String var3, boolean var4, String var5) {
      super();
      this.field_200156_f = var1;
      this.field_200040_e = var2;
      this.field_199923_f = var3;
      this.field_200041_g = var4;
      this.field_200157_i = var5;
   }

   public void func_199912_a(Tag<T> var1) {
      if (this.field_199921_d.containsKey(var1.func_199886_b())) {
         throw new IllegalArgumentException("Duplicate " + this.field_200157_i + " tag '" + var1.func_199886_b() + "'");
      } else {
         this.field_199921_d.put(var1.func_199886_b(), var1);
      }
   }

   @Nullable
   public Tag<T> func_199910_a(ResourceLocation var1) {
      return (Tag)this.field_199921_d.get(var1);
   }

   public Tag<T> func_199915_b(ResourceLocation var1) {
      Tag var2 = (Tag)this.field_199921_d.get(var1);
      return var2 == null ? new Tag(var1) : var2;
   }

   public Collection<ResourceLocation> func_199908_a() {
      return this.field_199921_d.keySet();
   }

   public Collection<ResourceLocation> func_199913_a(T var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.field_199921_d.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (((Tag)var4.getValue()).func_199685_a_(var1)) {
            var2.add(var4.getKey());
         }
      }

      return var2;
   }

   public void func_199917_b() {
      this.field_199921_d.clear();
   }

   public void func_199909_a(IResourceManager var1) {
      HashMap var2 = Maps.newHashMap();
      Iterator var3 = var1.func_199003_a(this.field_199923_f, (var0) -> {
         return var0.endsWith(".json");
      }).iterator();

      while(var3.hasNext()) {
         ResourceLocation var4 = (ResourceLocation)var3.next();
         String var5 = var4.func_110623_a();
         ResourceLocation var6 = new ResourceLocation(var4.func_110624_b(), var5.substring(this.field_199923_f.length() + 1, var5.length() - field_199920_c));

         try {
            Iterator var7 = var1.func_199004_b(var4).iterator();

            while(var7.hasNext()) {
               IResource var8 = (IResource)var7.next();

               try {
                  JsonObject var9 = (JsonObject)JsonUtils.func_188178_a(field_199919_b, IOUtils.toString(var8.func_199027_b(), StandardCharsets.UTF_8), JsonObject.class);
                  if (var9 == null) {
                     field_199918_a.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.field_200157_i, var6, var4, var8.func_199026_d());
                  } else {
                     Tag.Builder var10 = (Tag.Builder)var2.getOrDefault(var6, Tag.Builder.func_200047_a());
                     var10.func_200158_a(this.field_200156_f, this.field_200040_e, var9);
                     var2.put(var6, var10);
                  }
               } catch (RuntimeException | IOException var15) {
                  field_199918_a.error("Couldn't read {} tag list {} from {} in data pack {}", this.field_200157_i, var6, var4, var8.func_199026_d(), var15);
               } finally {
                  IOUtils.closeQuietly(var8);
               }
            }
         } catch (IOException var17) {
            field_199918_a.error("Couldn't read {} tag list {} from {}", this.field_200157_i, var6, var4, var17);
         }
      }

      label149:
      while(!var2.isEmpty()) {
         boolean var18 = false;
         Iterator var19 = var2.entrySet().iterator();

         Entry var21;
         while(var19.hasNext()) {
            var21 = (Entry)var19.next();
            if (((Tag.Builder)var21.getValue()).func_200160_a(this::func_199910_a)) {
               var18 = true;
               this.func_199912_a(((Tag.Builder)var21.getValue()).func_200051_a((ResourceLocation)var21.getKey()));
               var19.remove();
            }
         }

         if (!var18) {
            var19 = var2.entrySet().iterator();

            while(true) {
               if (!var19.hasNext()) {
                  break label149;
               }

               var21 = (Entry)var19.next();
               field_199918_a.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.field_200157_i, var21.getKey());
            }
         }
      }

      var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var20 = (Entry)var3.next();
         this.func_199912_a(((Tag.Builder)var20.getValue()).func_200045_a(this.field_200041_g).func_200051_a((ResourceLocation)var20.getKey()));
      }

   }

   public Map<ResourceLocation, Tag<T>> func_200039_c() {
      return this.field_199921_d;
   }
}
