package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementManager implements IResourceManagerReloadListener {
   private static final Logger field_192782_a = LogManager.getLogger();
   private static final Gson field_192783_b = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.Builder.class, (var0, var1, var2) -> {
      JsonObject var3 = JsonUtils.func_151210_l(var0, "advancement");
      return Advancement.Builder.func_192059_a(var3, var2);
   }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
   private static final AdvancementList field_195443_e = new AdvancementList();
   public static final int field_195441_a = "advancements/".length();
   public static final int field_195442_b = ".json".length();
   private boolean field_193768_e;

   public AdvancementManager() {
      super();
   }

   private Map<ResourceLocation, Advancement.Builder> func_195439_b(IResourceManager var1) {
      HashMap var2 = Maps.newHashMap();
      Iterator var3 = var1.func_199003_a("advancements", (var0) -> {
         return var0.endsWith(".json");
      }).iterator();

      while(var3.hasNext()) {
         ResourceLocation var4 = (ResourceLocation)var3.next();
         String var5 = var4.func_110623_a();
         ResourceLocation var6 = new ResourceLocation(var4.func_110624_b(), var5.substring(field_195441_a, var5.length() - field_195442_b));

         try {
            IResource var7 = var1.func_199002_a(var4);
            Throwable var8 = null;

            try {
               Advancement.Builder var9 = (Advancement.Builder)JsonUtils.func_188178_a(field_192783_b, IOUtils.toString(var7.func_199027_b(), StandardCharsets.UTF_8), Advancement.Builder.class);
               if (var9 == null) {
                  field_192782_a.error("Couldn't load custom advancement {} from {} as it's empty or null", var6, var4);
               } else {
                  var2.put(var6, var9);
               }
            } catch (Throwable var19) {
               var8 = var19;
               throw var19;
            } finally {
               if (var7 != null) {
                  if (var8 != null) {
                     try {
                        var7.close();
                     } catch (Throwable var18) {
                        var8.addSuppressed(var18);
                     }
                  } else {
                     var7.close();
                  }
               }

            }
         } catch (IllegalArgumentException | JsonParseException var21) {
            field_192782_a.error("Parsing error loading custom advancement {}: {}", var6, var21.getMessage());
            this.field_193768_e = true;
         } catch (IOException var22) {
            field_192782_a.error("Couldn't read custom advancement {} from {}", var6, var4, var22);
            this.field_193768_e = true;
         }
      }

      return var2;
   }

   @Nullable
   public Advancement func_192778_a(ResourceLocation var1) {
      return field_195443_e.func_192084_a(var1);
   }

   public Collection<Advancement> func_195438_b() {
      return field_195443_e.func_195651_c();
   }

   public void func_195410_a(IResourceManager var1) {
      this.field_193768_e = false;
      field_195443_e.func_192087_a();
      Map var2 = this.func_195439_b(var1);
      field_195443_e.func_192083_a(var2);
      Iterator var3 = field_195443_e.func_192088_b().iterator();

      while(var3.hasNext()) {
         Advancement var4 = (Advancement)var3.next();
         if (var4.func_192068_c() != null) {
            AdvancementTreeNode.func_192323_a(var4);
         }
      }

   }
}
