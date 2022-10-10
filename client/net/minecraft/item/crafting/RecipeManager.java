package net.minecraft.item.crafting;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager implements IResourceManagerReloadListener {
   private static final Logger field_199521_c = LogManager.getLogger();
   public static final int field_199519_a = "recipes/".length();
   public static final int field_199520_b = ".json".length();
   private final Map<ResourceLocation, IRecipe> field_199522_d = Maps.newHashMap();
   private boolean field_199523_e;

   public RecipeManager() {
      super();
   }

   public void func_195410_a(IResourceManager var1) {
      Gson var2 = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
      this.field_199523_e = false;
      this.field_199522_d.clear();
      Iterator var3 = var1.func_199003_a("recipes", (var0) -> {
         return var0.endsWith(".json");
      }).iterator();

      while(var3.hasNext()) {
         ResourceLocation var4 = (ResourceLocation)var3.next();
         String var5 = var4.func_110623_a();
         ResourceLocation var6 = new ResourceLocation(var4.func_110624_b(), var5.substring(field_199519_a, var5.length() - field_199520_b));

         try {
            IResource var7 = var1.func_199002_a(var4);
            Throwable var8 = null;

            try {
               JsonObject var9 = (JsonObject)JsonUtils.func_188178_a(var2, IOUtils.toString(var7.func_199027_b(), StandardCharsets.UTF_8), JsonObject.class);
               if (var9 == null) {
                  field_199521_c.error("Couldn't load recipe {} as it's null or empty", var6);
               } else {
                  this.func_199509_a(RecipeSerializers.func_199572_a(var6, var9));
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
            field_199521_c.error("Parsing error loading recipe {}", var6, var21);
            this.field_199523_e = true;
         } catch (IOException var22) {
            field_199521_c.error("Couldn't read custom advancement {} from {}", var6, var4, var22);
            this.field_199523_e = true;
         }
      }

      field_199521_c.info("Loaded {} recipes", this.field_199522_d.size());
   }

   public void func_199509_a(IRecipe var1) {
      if (this.field_199522_d.containsKey(var1.func_199560_c())) {
         throw new IllegalStateException("Duplicate recipe ignored with ID " + var1.func_199560_c());
      } else {
         this.field_199522_d.put(var1.func_199560_c(), var1);
      }
   }

   public ItemStack func_199514_a(IInventory var1, World var2) {
      Iterator var3 = this.field_199522_d.values().iterator();

      IRecipe var4;
      do {
         if (!var3.hasNext()) {
            return ItemStack.field_190927_a;
         }

         var4 = (IRecipe)var3.next();
      } while(!var4.func_77569_a(var1, var2));

      return var4.func_77572_b(var1);
   }

   @Nullable
   public IRecipe func_199515_b(IInventory var1, World var2) {
      Iterator var3 = this.field_199522_d.values().iterator();

      IRecipe var4;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (IRecipe)var3.next();
      } while(!var4.func_77569_a(var1, var2));

      return var4;
   }

   public NonNullList<ItemStack> func_199513_c(IInventory var1, World var2) {
      Iterator var3 = this.field_199522_d.values().iterator();

      while(var3.hasNext()) {
         IRecipe var4 = (IRecipe)var3.next();
         if (var4.func_77569_a(var1, var2)) {
            return var4.func_179532_b(var1);
         }
      }

      NonNullList var5 = NonNullList.func_191197_a(var1.func_70302_i_(), ItemStack.field_190927_a);

      for(int var6 = 0; var6 < var5.size(); ++var6) {
         var5.set(var6, var1.func_70301_a(var6));
      }

      return var5;
   }

   @Nullable
   public IRecipe func_199517_a(ResourceLocation var1) {
      return (IRecipe)this.field_199522_d.get(var1);
   }

   public Collection<IRecipe> func_199510_b() {
      return this.field_199522_d.values();
   }

   public Collection<ResourceLocation> func_199511_c() {
      return this.field_199522_d.keySet();
   }

   public void func_199518_d() {
      this.field_199522_d.clear();
   }
}
