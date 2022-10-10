package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FishingRodHookedTrigger implements ICriterionTrigger<FishingRodHookedTrigger.Instance> {
   private static final ResourceLocation field_204821_a = new ResourceLocation("fishing_rod_hooked");
   private final Map<PlayerAdvancements, FishingRodHookedTrigger.Listeners> field_204822_b = Maps.newHashMap();

   public FishingRodHookedTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_204821_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance> var2) {
      FishingRodHookedTrigger.Listeners var3 = (FishingRodHookedTrigger.Listeners)this.field_204822_b.get(var1);
      if (var3 == null) {
         var3 = new FishingRodHookedTrigger.Listeners(var1);
         this.field_204822_b.put(var1, var3);
      }

      var3.func_204858_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance> var2) {
      FishingRodHookedTrigger.Listeners var3 = (FishingRodHookedTrigger.Listeners)this.field_204822_b.get(var1);
      if (var3 != null) {
         var3.func_204861_b(var2);
         if (var3.func_204860_a()) {
            this.field_204822_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_204822_b.remove(var1);
   }

   public FishingRodHookedTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.func_192492_a(var1.get("rod"));
      EntityPredicate var4 = EntityPredicate.func_192481_a(var1.get("entity"));
      ItemPredicate var5 = ItemPredicate.func_192492_a(var1.get("item"));
      return new FishingRodHookedTrigger.Instance(var3, var4, var5);
   }

   public void func_204820_a(EntityPlayerMP var1, ItemStack var2, EntityFishHook var3, Collection<ItemStack> var4) {
      FishingRodHookedTrigger.Listeners var5 = (FishingRodHookedTrigger.Listeners)this.field_204822_b.get(var1.func_192039_O());
      if (var5 != null) {
         var5.func_204859_a(var1, var2, var3, var4);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_204862_a;
      private final Set<ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance>> field_204863_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_204862_a = var1;
      }

      public boolean func_204860_a() {
         return this.field_204863_b.isEmpty();
      }

      public void func_204858_a(ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance> var1) {
         this.field_204863_b.add(var1);
      }

      public void func_204861_b(ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance> var1) {
         this.field_204863_b.remove(var1);
      }

      public void func_204859_a(EntityPlayerMP var1, ItemStack var2, EntityFishHook var3, Collection<ItemStack> var4) {
         ArrayList var5 = null;
         Iterator var6 = this.field_204863_b.iterator();

         ICriterionTrigger.Listener var7;
         while(var6.hasNext()) {
            var7 = (ICriterionTrigger.Listener)var6.next();
            if (((FishingRodHookedTrigger.Instance)var7.func_192158_a()).func_204830_a(var1, var2, var3, var4)) {
               if (var5 == null) {
                  var5 = Lists.newArrayList();
               }

               var5.add(var7);
            }
         }

         if (var5 != null) {
            var6 = var5.iterator();

            while(var6.hasNext()) {
               var7 = (ICriterionTrigger.Listener)var6.next();
               var7.func_192159_a(this.field_204862_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate field_204831_a;
      private final EntityPredicate field_204832_b;
      private final ItemPredicate field_204833_c;

      public Instance(ItemPredicate var1, EntityPredicate var2, ItemPredicate var3) {
         super(FishingRodHookedTrigger.field_204821_a);
         this.field_204831_a = var1;
         this.field_204832_b = var2;
         this.field_204833_c = var3;
      }

      public static FishingRodHookedTrigger.Instance func_204829_a(ItemPredicate var0, EntityPredicate var1, ItemPredicate var2) {
         return new FishingRodHookedTrigger.Instance(var0, var1, var2);
      }

      public boolean func_204830_a(EntityPlayerMP var1, ItemStack var2, EntityFishHook var3, Collection<ItemStack> var4) {
         if (!this.field_204831_a.func_192493_a(var2)) {
            return false;
         } else if (!this.field_204832_b.func_192482_a(var1, var3.field_146043_c)) {
            return false;
         } else {
            if (this.field_204833_c != ItemPredicate.field_192495_a) {
               boolean var5 = false;
               if (var3.field_146043_c instanceof EntityItem) {
                  EntityItem var6 = (EntityItem)var3.field_146043_c;
                  if (this.field_204833_c.func_192493_a(var6.func_92059_d())) {
                     var5 = true;
                  }
               }

               Iterator var8 = var4.iterator();

               while(var8.hasNext()) {
                  ItemStack var7 = (ItemStack)var8.next();
                  if (this.field_204833_c.func_192493_a(var7)) {
                     var5 = true;
                     break;
                  }
               }

               if (!var5) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("rod", this.field_204831_a.func_200319_a());
         var1.add("entity", this.field_204832_b.func_204006_a());
         var1.add("item", this.field_204833_c.func_200319_a());
         return var1;
      }
   }
}
