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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantedItemTrigger implements ICriterionTrigger<EnchantedItemTrigger.Instance> {
   private static final ResourceLocation field_192191_a = new ResourceLocation("enchanted_item");
   private final Map<PlayerAdvancements, EnchantedItemTrigger.Listeners> field_192192_b = Maps.newHashMap();

   public EnchantedItemTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192191_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> var2) {
      EnchantedItemTrigger.Listeners var3 = (EnchantedItemTrigger.Listeners)this.field_192192_b.get(var1);
      if (var3 == null) {
         var3 = new EnchantedItemTrigger.Listeners(var1);
         this.field_192192_b.put(var1, var3);
      }

      var3.func_192460_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> var2) {
      EnchantedItemTrigger.Listeners var3 = (EnchantedItemTrigger.Listeners)this.field_192192_b.get(var1);
      if (var3 != null) {
         var3.func_192457_b(var2);
         if (var3.func_192458_a()) {
            this.field_192192_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192192_b.remove(var1);
   }

   public EnchantedItemTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.func_192492_a(var1.get("item"));
      MinMaxBounds.IntBound var4 = MinMaxBounds.IntBound.func_211344_a(var1.get("levels"));
      return new EnchantedItemTrigger.Instance(var3, var4);
   }

   public void func_192190_a(EntityPlayerMP var1, ItemStack var2, int var3) {
      EnchantedItemTrigger.Listeners var4 = (EnchantedItemTrigger.Listeners)this.field_192192_b.get(var1.func_192039_O());
      if (var4 != null) {
         var4.func_192459_a(var2, var3);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192461_a;
      private final Set<ICriterionTrigger.Listener<EnchantedItemTrigger.Instance>> field_192462_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192461_a = var1;
      }

      public boolean func_192458_a() {
         return this.field_192462_b.isEmpty();
      }

      public void func_192460_a(ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> var1) {
         this.field_192462_b.add(var1);
      }

      public void func_192457_b(ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> var1) {
         this.field_192462_b.remove(var1);
      }

      public void func_192459_a(ItemStack var1, int var2) {
         ArrayList var3 = null;
         Iterator var4 = this.field_192462_b.iterator();

         ICriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (ICriterionTrigger.Listener)var4.next();
            if (((EnchantedItemTrigger.Instance)var5.func_192158_a()).func_192257_a(var1, var2)) {
               if (var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var5);
            }
         }

         if (var3 != null) {
            var4 = var3.iterator();

            while(var4.hasNext()) {
               var5 = (ICriterionTrigger.Listener)var4.next();
               var5.func_192159_a(this.field_192461_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate field_192258_a;
      private final MinMaxBounds.IntBound field_192259_b;

      public Instance(ItemPredicate var1, MinMaxBounds.IntBound var2) {
         super(EnchantedItemTrigger.field_192191_a);
         this.field_192258_a = var1;
         this.field_192259_b = var2;
      }

      public static EnchantedItemTrigger.Instance func_203918_c() {
         return new EnchantedItemTrigger.Instance(ItemPredicate.field_192495_a, MinMaxBounds.IntBound.field_211347_e);
      }

      public boolean func_192257_a(ItemStack var1, int var2) {
         if (!this.field_192258_a.func_192493_a(var1)) {
            return false;
         } else {
            return this.field_192259_b.func_211339_d(var2);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.field_192258_a.func_200319_a());
         var1.add("levels", this.field_192259_b.func_200321_c());
         return var1;
      }
   }
}
