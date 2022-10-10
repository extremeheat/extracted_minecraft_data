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

public class FilledBucketTrigger implements ICriterionTrigger<FilledBucketTrigger.Instance> {
   private static final ResourceLocation field_204818_a = new ResourceLocation("filled_bucket");
   private final Map<PlayerAdvancements, FilledBucketTrigger.Listeners> field_204819_b = Maps.newHashMap();

   public FilledBucketTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_204818_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<FilledBucketTrigger.Instance> var2) {
      FilledBucketTrigger.Listeners var3 = (FilledBucketTrigger.Listeners)this.field_204819_b.get(var1);
      if (var3 == null) {
         var3 = new FilledBucketTrigger.Listeners(var1);
         this.field_204819_b.put(var1, var3);
      }

      var3.func_204852_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<FilledBucketTrigger.Instance> var2) {
      FilledBucketTrigger.Listeners var3 = (FilledBucketTrigger.Listeners)this.field_204819_b.get(var1);
      if (var3 != null) {
         var3.func_204855_b(var2);
         if (var3.func_204853_a()) {
            this.field_204819_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_204819_b.remove(var1);
   }

   public FilledBucketTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.func_192492_a(var1.get("item"));
      return new FilledBucketTrigger.Instance(var3);
   }

   public void func_204817_a(EntityPlayerMP var1, ItemStack var2) {
      FilledBucketTrigger.Listeners var3 = (FilledBucketTrigger.Listeners)this.field_204819_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_204854_a(var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_204856_a;
      private final Set<ICriterionTrigger.Listener<FilledBucketTrigger.Instance>> field_204857_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_204856_a = var1;
      }

      public boolean func_204853_a() {
         return this.field_204857_b.isEmpty();
      }

      public void func_204852_a(ICriterionTrigger.Listener<FilledBucketTrigger.Instance> var1) {
         this.field_204857_b.add(var1);
      }

      public void func_204855_b(ICriterionTrigger.Listener<FilledBucketTrigger.Instance> var1) {
         this.field_204857_b.remove(var1);
      }

      public void func_204854_a(ItemStack var1) {
         ArrayList var2 = null;
         Iterator var3 = this.field_204857_b.iterator();

         ICriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (ICriterionTrigger.Listener)var3.next();
            if (((FilledBucketTrigger.Instance)var4.func_192158_a()).func_204826_a(var1)) {
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
               var4.func_192159_a(this.field_204856_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate field_204828_a;

      public Instance(ItemPredicate var1) {
         super(FilledBucketTrigger.field_204818_a);
         this.field_204828_a = var1;
      }

      public static FilledBucketTrigger.Instance func_204827_a(ItemPredicate var0) {
         return new FilledBucketTrigger.Instance(var0);
      }

      public boolean func_204826_a(ItemStack var1) {
         return this.field_204828_a.func_192493_a(var1);
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.field_204828_a.func_200319_a());
         return var1;
      }
   }
}
