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

public class ItemDurabilityTrigger implements ICriterionTrigger<ItemDurabilityTrigger.Instance> {
   private static final ResourceLocation field_193159_a = new ResourceLocation("item_durability_changed");
   private final Map<PlayerAdvancements, ItemDurabilityTrigger.Listeners> field_193160_b = Maps.newHashMap();

   public ItemDurabilityTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_193159_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> var2) {
      ItemDurabilityTrigger.Listeners var3 = (ItemDurabilityTrigger.Listeners)this.field_193160_b.get(var1);
      if (var3 == null) {
         var3 = new ItemDurabilityTrigger.Listeners(var1);
         this.field_193160_b.put(var1, var3);
      }

      var3.func_193440_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> var2) {
      ItemDurabilityTrigger.Listeners var3 = (ItemDurabilityTrigger.Listeners)this.field_193160_b.get(var1);
      if (var3 != null) {
         var3.func_193438_b(var2);
         if (var3.func_193439_a()) {
            this.field_193160_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_193160_b.remove(var1);
   }

   public ItemDurabilityTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.func_192492_a(var1.get("item"));
      MinMaxBounds.IntBound var4 = MinMaxBounds.IntBound.func_211344_a(var1.get("durability"));
      MinMaxBounds.IntBound var5 = MinMaxBounds.IntBound.func_211344_a(var1.get("delta"));
      return new ItemDurabilityTrigger.Instance(var3, var4, var5);
   }

   public void func_193158_a(EntityPlayerMP var1, ItemStack var2, int var3) {
      ItemDurabilityTrigger.Listeners var4 = (ItemDurabilityTrigger.Listeners)this.field_193160_b.get(var1.func_192039_O());
      if (var4 != null) {
         var4.func_193441_a(var2, var3);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_193442_a;
      private final Set<ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance>> field_193443_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_193442_a = var1;
      }

      public boolean func_193439_a() {
         return this.field_193443_b.isEmpty();
      }

      public void func_193440_a(ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> var1) {
         this.field_193443_b.add(var1);
      }

      public void func_193438_b(ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> var1) {
         this.field_193443_b.remove(var1);
      }

      public void func_193441_a(ItemStack var1, int var2) {
         ArrayList var3 = null;
         Iterator var4 = this.field_193443_b.iterator();

         ICriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (ICriterionTrigger.Listener)var4.next();
            if (((ItemDurabilityTrigger.Instance)var5.func_192158_a()).func_193197_a(var1, var2)) {
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
               var5.func_192159_a(this.field_193442_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate field_193198_a;
      private final MinMaxBounds.IntBound field_193199_b;
      private final MinMaxBounds.IntBound field_193200_c;

      public Instance(ItemPredicate var1, MinMaxBounds.IntBound var2, MinMaxBounds.IntBound var3) {
         super(ItemDurabilityTrigger.field_193159_a);
         this.field_193198_a = var1;
         this.field_193199_b = var2;
         this.field_193200_c = var3;
      }

      public static ItemDurabilityTrigger.Instance func_211182_a(ItemPredicate var0, MinMaxBounds.IntBound var1) {
         return new ItemDurabilityTrigger.Instance(var0, var1, MinMaxBounds.IntBound.field_211347_e);
      }

      public boolean func_193197_a(ItemStack var1, int var2) {
         if (!this.field_193198_a.func_192493_a(var1)) {
            return false;
         } else if (!this.field_193199_b.func_211339_d(var1.func_77958_k() - var2)) {
            return false;
         } else {
            return this.field_193200_c.func_211339_d(var1.func_77952_i() - var2);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.field_193198_a.func_200319_a());
         var1.add("durability", this.field_193199_b.func_200321_c());
         var1.add("delta", this.field_193200_c.func_200321_c());
         return var1;
      }
   }
}
