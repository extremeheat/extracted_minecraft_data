package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
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
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class InventoryChangeTrigger implements ICriterionTrigger<InventoryChangeTrigger.Instance> {
   private static final ResourceLocation field_192209_a = new ResourceLocation("inventory_changed");
   private final Map<PlayerAdvancements, InventoryChangeTrigger.Listeners> field_192210_b = Maps.newHashMap();

   public InventoryChangeTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192209_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> var2) {
      InventoryChangeTrigger.Listeners var3 = (InventoryChangeTrigger.Listeners)this.field_192210_b.get(var1);
      if (var3 == null) {
         var3 = new InventoryChangeTrigger.Listeners(var1);
         this.field_192210_b.put(var1, var3);
      }

      var3.func_192489_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> var2) {
      InventoryChangeTrigger.Listeners var3 = (InventoryChangeTrigger.Listeners)this.field_192210_b.get(var1);
      if (var3 != null) {
         var3.func_192487_b(var2);
         if (var3.func_192488_a()) {
            this.field_192210_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192210_b.remove(var1);
   }

   public InventoryChangeTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      JsonObject var3 = JsonUtils.func_151218_a(var1, "slots", new JsonObject());
      MinMaxBounds.IntBound var4 = MinMaxBounds.IntBound.func_211344_a(var3.get("occupied"));
      MinMaxBounds.IntBound var5 = MinMaxBounds.IntBound.func_211344_a(var3.get("full"));
      MinMaxBounds.IntBound var6 = MinMaxBounds.IntBound.func_211344_a(var3.get("empty"));
      ItemPredicate[] var7 = ItemPredicate.func_192494_b(var1.get("items"));
      return new InventoryChangeTrigger.Instance(var4, var5, var6, var7);
   }

   public void func_192208_a(EntityPlayerMP var1, InventoryPlayer var2) {
      InventoryChangeTrigger.Listeners var3 = (InventoryChangeTrigger.Listeners)this.field_192210_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_192486_a(var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192490_a;
      private final Set<ICriterionTrigger.Listener<InventoryChangeTrigger.Instance>> field_192491_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192490_a = var1;
      }

      public boolean func_192488_a() {
         return this.field_192491_b.isEmpty();
      }

      public void func_192489_a(ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> var1) {
         this.field_192491_b.add(var1);
      }

      public void func_192487_b(ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> var1) {
         this.field_192491_b.remove(var1);
      }

      public void func_192486_a(InventoryPlayer var1) {
         ArrayList var2 = null;
         Iterator var3 = this.field_192491_b.iterator();

         ICriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (ICriterionTrigger.Listener)var3.next();
            if (((InventoryChangeTrigger.Instance)var4.func_192158_a()).func_192265_a(var1)) {
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
               var4.func_192159_a(this.field_192490_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final MinMaxBounds.IntBound field_192266_a;
      private final MinMaxBounds.IntBound field_192267_b;
      private final MinMaxBounds.IntBound field_192268_c;
      private final ItemPredicate[] field_192269_d;

      public Instance(MinMaxBounds.IntBound var1, MinMaxBounds.IntBound var2, MinMaxBounds.IntBound var3, ItemPredicate[] var4) {
         super(InventoryChangeTrigger.field_192209_a);
         this.field_192266_a = var1;
         this.field_192267_b = var2;
         this.field_192268_c = var3;
         this.field_192269_d = var4;
      }

      public static InventoryChangeTrigger.Instance func_203923_a(ItemPredicate... var0) {
         return new InventoryChangeTrigger.Instance(MinMaxBounds.IntBound.field_211347_e, MinMaxBounds.IntBound.field_211347_e, MinMaxBounds.IntBound.field_211347_e, var0);
      }

      public static InventoryChangeTrigger.Instance func_203922_a(IItemProvider... var0) {
         ItemPredicate[] var1 = new ItemPredicate[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = new ItemPredicate((Tag)null, var0[var2].func_199767_j(), MinMaxBounds.IntBound.field_211347_e, MinMaxBounds.IntBound.field_211347_e, new EnchantmentPredicate[0], (PotionType)null, NBTPredicate.field_193479_a);
         }

         return func_203923_a(var1);
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         if (!this.field_192266_a.func_211335_c() || !this.field_192267_b.func_211335_c() || !this.field_192268_c.func_211335_c()) {
            JsonObject var2 = new JsonObject();
            var2.add("occupied", this.field_192266_a.func_200321_c());
            var2.add("full", this.field_192267_b.func_200321_c());
            var2.add("empty", this.field_192268_c.func_200321_c());
            var1.add("slots", var2);
         }

         if (this.field_192269_d.length > 0) {
            JsonArray var7 = new JsonArray();
            ItemPredicate[] var3 = this.field_192269_d;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               ItemPredicate var6 = var3[var5];
               var7.add(var6.func_200319_a());
            }

            var1.add("items", var7);
         }

         return var1;
      }

      public boolean func_192265_a(InventoryPlayer var1) {
         int var2 = 0;
         int var3 = 0;
         int var4 = 0;
         ArrayList var5 = Lists.newArrayList(this.field_192269_d);

         for(int var6 = 0; var6 < var1.func_70302_i_(); ++var6) {
            ItemStack var7 = var1.func_70301_a(var6);
            if (var7.func_190926_b()) {
               ++var3;
            } else {
               ++var4;
               if (var7.func_190916_E() >= var7.func_77976_d()) {
                  ++var2;
               }

               Iterator var8 = var5.iterator();

               while(var8.hasNext()) {
                  ItemPredicate var9 = (ItemPredicate)var8.next();
                  if (var9.func_192493_a(var7)) {
                     var8.remove();
                  }
               }
            }
         }

         if (!this.field_192267_b.func_211339_d(var2)) {
            return false;
         } else if (!this.field_192268_c.func_211339_d(var3)) {
            return false;
         } else if (!this.field_192266_a.func_211339_d(var4)) {
            return false;
         } else if (!var5.isEmpty()) {
            return false;
         } else {
            return true;
         }
      }
   }
}
