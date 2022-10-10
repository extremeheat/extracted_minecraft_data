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
import net.minecraft.potion.PotionType;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ConsumeItemTrigger implements ICriterionTrigger<ConsumeItemTrigger.Instance> {
   private static final ResourceLocation field_193149_a = new ResourceLocation("consume_item");
   private final Map<PlayerAdvancements, ConsumeItemTrigger.Listeners> field_193150_b = Maps.newHashMap();

   public ConsumeItemTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_193149_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<ConsumeItemTrigger.Instance> var2) {
      ConsumeItemTrigger.Listeners var3 = (ConsumeItemTrigger.Listeners)this.field_193150_b.get(var1);
      if (var3 == null) {
         var3 = new ConsumeItemTrigger.Listeners(var1);
         this.field_193150_b.put(var1, var3);
      }

      var3.func_193239_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<ConsumeItemTrigger.Instance> var2) {
      ConsumeItemTrigger.Listeners var3 = (ConsumeItemTrigger.Listeners)this.field_193150_b.get(var1);
      if (var3 != null) {
         var3.func_193237_b(var2);
         if (var3.func_193238_a()) {
            this.field_193150_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_193150_b.remove(var1);
   }

   public ConsumeItemTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return new ConsumeItemTrigger.Instance(ItemPredicate.func_192492_a(var1.get("item")));
   }

   public void func_193148_a(EntityPlayerMP var1, ItemStack var2) {
      ConsumeItemTrigger.Listeners var3 = (ConsumeItemTrigger.Listeners)this.field_193150_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_193240_a(var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_193241_a;
      private final Set<ICriterionTrigger.Listener<ConsumeItemTrigger.Instance>> field_193242_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_193241_a = var1;
      }

      public boolean func_193238_a() {
         return this.field_193242_b.isEmpty();
      }

      public void func_193239_a(ICriterionTrigger.Listener<ConsumeItemTrigger.Instance> var1) {
         this.field_193242_b.add(var1);
      }

      public void func_193237_b(ICriterionTrigger.Listener<ConsumeItemTrigger.Instance> var1) {
         this.field_193242_b.remove(var1);
      }

      public void func_193240_a(ItemStack var1) {
         ArrayList var2 = null;
         Iterator var3 = this.field_193242_b.iterator();

         ICriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (ICriterionTrigger.Listener)var3.next();
            if (((ConsumeItemTrigger.Instance)var4.func_192158_a()).func_193193_a(var1)) {
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
               var4.func_192159_a(this.field_193241_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate field_193194_a;

      public Instance(ItemPredicate var1) {
         super(ConsumeItemTrigger.field_193149_a);
         this.field_193194_a = var1;
      }

      public static ConsumeItemTrigger.Instance func_203914_c() {
         return new ConsumeItemTrigger.Instance(ItemPredicate.field_192495_a);
      }

      public static ConsumeItemTrigger.Instance func_203913_a(IItemProvider var0) {
         return new ConsumeItemTrigger.Instance(new ItemPredicate((Tag)null, var0.func_199767_j(), MinMaxBounds.IntBound.field_211347_e, MinMaxBounds.IntBound.field_211347_e, new EnchantmentPredicate[0], (PotionType)null, NBTPredicate.field_193479_a));
      }

      public boolean func_193193_a(ItemStack var1) {
         return this.field_193194_a.func_192493_a(var1);
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.field_193194_a.func_200319_a());
         return var1;
      }
   }
}
