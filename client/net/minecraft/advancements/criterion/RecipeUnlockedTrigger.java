package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeUnlockedTrigger implements ICriterionTrigger<RecipeUnlockedTrigger.Instance> {
   private static final ResourceLocation field_192227_a = new ResourceLocation("recipe_unlocked");
   private final Map<PlayerAdvancements, RecipeUnlockedTrigger.Listeners> field_192228_b = Maps.newHashMap();

   public RecipeUnlockedTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192227_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> var2) {
      RecipeUnlockedTrigger.Listeners var3 = (RecipeUnlockedTrigger.Listeners)this.field_192228_b.get(var1);
      if (var3 == null) {
         var3 = new RecipeUnlockedTrigger.Listeners(var1);
         this.field_192228_b.put(var1, var3);
      }

      var3.func_192528_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> var2) {
      RecipeUnlockedTrigger.Listeners var3 = (RecipeUnlockedTrigger.Listeners)this.field_192228_b.get(var1);
      if (var3 != null) {
         var3.func_192525_b(var2);
         if (var3.func_192527_a()) {
            this.field_192228_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192228_b.remove(var1);
   }

   public RecipeUnlockedTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      ResourceLocation var3 = new ResourceLocation(JsonUtils.func_151200_h(var1, "recipe"));
      return new RecipeUnlockedTrigger.Instance(var3);
   }

   public void func_192225_a(EntityPlayerMP var1, IRecipe var2) {
      RecipeUnlockedTrigger.Listeners var3 = (RecipeUnlockedTrigger.Listeners)this.field_192228_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_193493_a(var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192529_a;
      private final Set<ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance>> field_192530_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192529_a = var1;
      }

      public boolean func_192527_a() {
         return this.field_192530_b.isEmpty();
      }

      public void func_192528_a(ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> var1) {
         this.field_192530_b.add(var1);
      }

      public void func_192525_b(ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> var1) {
         this.field_192530_b.remove(var1);
      }

      public void func_193493_a(IRecipe var1) {
         ArrayList var2 = null;
         Iterator var3 = this.field_192530_b.iterator();

         ICriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (ICriterionTrigger.Listener)var3.next();
            if (((RecipeUnlockedTrigger.Instance)var4.func_192158_a()).func_193215_a(var1)) {
               if (var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if (var2 != null) {
            var3 = var2.iterator();

            while(var3.hasNext()) {
               var4 = (ICriterionTrigger.Listener)var3.next();
               var4.func_192159_a(this.field_192529_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final ResourceLocation field_212243_a;

      public Instance(ResourceLocation var1) {
         super(RecipeUnlockedTrigger.field_192227_a);
         this.field_212243_a = var1;
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("recipe", this.field_212243_a.toString());
         return var1;
      }

      public boolean func_193215_a(IRecipe var1) {
         return this.field_212243_a.equals(var1.func_199560_c());
      }
   }
}
