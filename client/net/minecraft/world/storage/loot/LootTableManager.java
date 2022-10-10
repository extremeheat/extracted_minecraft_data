package net.minecraft.world.storage.loot;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableManager implements IResourceManagerReloadListener {
   private static final Logger field_186525_a = LogManager.getLogger();
   private static final Gson field_186526_b = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
   private final Map<ResourceLocation, LootTable> field_186527_c = Maps.newHashMap();
   public static final int field_195435_a = "loot_tables/".length();
   public static final int field_195436_b = ".json".length();

   public LootTableManager() {
      super();
   }

   public LootTable func_186521_a(ResourceLocation var1) {
      return (LootTable)this.field_186527_c.getOrDefault(var1, LootTable.field_186464_a);
   }

   public void func_195410_a(IResourceManager var1) {
      this.field_186527_c.clear();
      Iterator var2 = var1.func_199003_a("loot_tables", (var0) -> {
         return var0.endsWith(".json");
      }).iterator();

      while(var2.hasNext()) {
         ResourceLocation var3 = (ResourceLocation)var2.next();
         String var4 = var3.func_110623_a();
         ResourceLocation var5 = new ResourceLocation(var3.func_110624_b(), var4.substring(field_195435_a, var4.length() - field_195436_b));

         try {
            IResource var6 = var1.func_199002_a(var3);
            Throwable var7 = null;

            try {
               LootTable var8 = (LootTable)JsonUtils.func_188178_a(field_186526_b, IOUtils.toString(var6.func_199027_b(), StandardCharsets.UTF_8), LootTable.class);
               if (var8 != null) {
                  this.field_186527_c.put(var5, var8);
               }
            } catch (Throwable var17) {
               var7 = var17;
               throw var17;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var16) {
                        var7.addSuppressed(var16);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (Throwable var19) {
            field_186525_a.error("Couldn't read loot table {} from {}", var5, var3, var19);
         }
      }

   }
}
