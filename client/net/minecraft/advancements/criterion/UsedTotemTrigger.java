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
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class UsedTotemTrigger implements ICriterionTrigger<UsedTotemTrigger.Instance> {
   private static final ResourceLocation field_193188_a = new ResourceLocation("used_totem");
   private final Map<PlayerAdvancements, UsedTotemTrigger.Listeners> field_193189_b = Maps.newHashMap();

   public UsedTotemTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_193188_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<UsedTotemTrigger.Instance> var2) {
      UsedTotemTrigger.Listeners var3 = (UsedTotemTrigger.Listeners)this.field_193189_b.get(var1);
      if (var3 == null) {
         var3 = new UsedTotemTrigger.Listeners(var1);
         this.field_193189_b.put(var1, var3);
      }

      var3.func_193508_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<UsedTotemTrigger.Instance> var2) {
      UsedTotemTrigger.Listeners var3 = (UsedTotemTrigger.Listeners)this.field_193189_b.get(var1);
      if (var3 != null) {
         var3.func_193506_b(var2);
         if (var3.func_193507_a()) {
            this.field_193189_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_193189_b.remove(var1);
   }

   public UsedTotemTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.func_192492_a(var1.get("item"));
      return new UsedTotemTrigger.Instance(var3);
   }

   public void func_193187_a(EntityPlayerMP var1, ItemStack var2) {
      UsedTotemTrigger.Listeners var3 = (UsedTotemTrigger.Listeners)this.field_193189_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_193509_a(var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_193510_a;
      private final Set<ICriterionTrigger.Listener<UsedTotemTrigger.Instance>> field_193511_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_193510_a = var1;
      }

      public boolean func_193507_a() {
         return this.field_193511_b.isEmpty();
      }

      public void func_193508_a(ICriterionTrigger.Listener<UsedTotemTrigger.Instance> var1) {
         this.field_193511_b.add(var1);
      }

      public void func_193506_b(ICriterionTrigger.Listener<UsedTotemTrigger.Instance> var1) {
         this.field_193511_b.remove(var1);
      }

      public void func_193509_a(ItemStack var1) {
         ArrayList var2 = null;
         Iterator var3 = this.field_193511_b.iterator();

         ICriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (ICriterionTrigger.Listener)var3.next();
            if (((UsedTotemTrigger.Instance)var4.func_192158_a()).func_193218_a(var1)) {
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
               var4.func_192159_a(this.field_193510_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate field_193219_a;

      public Instance(ItemPredicate var1) {
         super(UsedTotemTrigger.field_193188_a);
         this.field_193219_a = var1;
      }

      public static UsedTotemTrigger.Instance func_203941_a(IItemProvider var0) {
         return new UsedTotemTrigger.Instance(ItemPredicate.Builder.func_200309_a().func_200308_a(var0).func_200310_b());
      }

      public boolean func_193218_a(ItemStack var1) {
         return this.field_193219_a.func_192493_a(var1);
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.field_193219_a.func_200319_a());
         return var1;
      }
   }
}
