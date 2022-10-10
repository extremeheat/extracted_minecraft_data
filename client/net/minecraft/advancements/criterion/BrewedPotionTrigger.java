package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class BrewedPotionTrigger implements ICriterionTrigger<BrewedPotionTrigger.Instance> {
   private static final ResourceLocation field_192176_a = new ResourceLocation("brewed_potion");
   private final Map<PlayerAdvancements, BrewedPotionTrigger.Listeners> field_192177_b = Maps.newHashMap();

   public BrewedPotionTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192176_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> var2) {
      BrewedPotionTrigger.Listeners var3 = (BrewedPotionTrigger.Listeners)this.field_192177_b.get(var1);
      if (var3 == null) {
         var3 = new BrewedPotionTrigger.Listeners(var1);
         this.field_192177_b.put(var1, var3);
      }

      var3.func_192349_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> var2) {
      BrewedPotionTrigger.Listeners var3 = (BrewedPotionTrigger.Listeners)this.field_192177_b.get(var1);
      if (var3 != null) {
         var3.func_192346_b(var2);
         if (var3.func_192347_a()) {
            this.field_192177_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192177_b.remove(var1);
   }

   public BrewedPotionTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      PotionType var3 = null;
      if (var1.has("potion")) {
         ResourceLocation var4 = new ResourceLocation(JsonUtils.func_151200_h(var1, "potion"));
         if (!IRegistry.field_212621_j.func_212607_c(var4)) {
            throw new JsonSyntaxException("Unknown potion '" + var4 + "'");
         }

         var3 = (PotionType)IRegistry.field_212621_j.func_82594_a(var4);
      }

      return new BrewedPotionTrigger.Instance(var3);
   }

   public void func_192173_a(EntityPlayerMP var1, PotionType var2) {
      BrewedPotionTrigger.Listeners var3 = (BrewedPotionTrigger.Listeners)this.field_192177_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_192348_a(var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192350_a;
      private final Set<ICriterionTrigger.Listener<BrewedPotionTrigger.Instance>> field_192351_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192350_a = var1;
      }

      public boolean func_192347_a() {
         return this.field_192351_b.isEmpty();
      }

      public void func_192349_a(ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> var1) {
         this.field_192351_b.add(var1);
      }

      public void func_192346_b(ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> var1) {
         this.field_192351_b.remove(var1);
      }

      public void func_192348_a(PotionType var1) {
         ArrayList var2 = null;
         Iterator var3 = this.field_192351_b.iterator();

         ICriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (ICriterionTrigger.Listener)var3.next();
            if (((BrewedPotionTrigger.Instance)var4.func_192158_a()).func_192250_a(var1)) {
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
               var4.func_192159_a(this.field_192350_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final PotionType field_192251_a;

      public Instance(@Nullable PotionType var1) {
         super(BrewedPotionTrigger.field_192176_a);
         this.field_192251_a = var1;
      }

      public static BrewedPotionTrigger.Instance func_203910_c() {
         return new BrewedPotionTrigger.Instance((PotionType)null);
      }

      public boolean func_192250_a(PotionType var1) {
         return this.field_192251_a == null || this.field_192251_a == var1;
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         if (this.field_192251_a != null) {
            var1.addProperty("potion", IRegistry.field_212621_j.func_177774_c(this.field_192251_a).toString());
         }

         return var1;
      }
   }
}
