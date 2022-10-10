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
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class VillagerTradeTrigger implements ICriterionTrigger<VillagerTradeTrigger.Instance> {
   private static final ResourceLocation field_192237_a = new ResourceLocation("villager_trade");
   private final Map<PlayerAdvancements, VillagerTradeTrigger.Listeners> field_192238_b = Maps.newHashMap();

   public VillagerTradeTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192237_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> var2) {
      VillagerTradeTrigger.Listeners var3 = (VillagerTradeTrigger.Listeners)this.field_192238_b.get(var1);
      if (var3 == null) {
         var3 = new VillagerTradeTrigger.Listeners(var1);
         this.field_192238_b.put(var1, var3);
      }

      var3.func_192540_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> var2) {
      VillagerTradeTrigger.Listeners var3 = (VillagerTradeTrigger.Listeners)this.field_192238_b.get(var1);
      if (var3 != null) {
         var3.func_192538_b(var2);
         if (var3.func_192539_a()) {
            this.field_192238_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192238_b.remove(var1);
   }

   public VillagerTradeTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.func_192481_a(var1.get("villager"));
      ItemPredicate var4 = ItemPredicate.func_192492_a(var1.get("item"));
      return new VillagerTradeTrigger.Instance(var3, var4);
   }

   public void func_192234_a(EntityPlayerMP var1, EntityVillager var2, ItemStack var3) {
      VillagerTradeTrigger.Listeners var4 = (VillagerTradeTrigger.Listeners)this.field_192238_b.get(var1.func_192039_O());
      if (var4 != null) {
         var4.func_192537_a(var1, var2, var3);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192541_a;
      private final Set<ICriterionTrigger.Listener<VillagerTradeTrigger.Instance>> field_192542_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192541_a = var1;
      }

      public boolean func_192539_a() {
         return this.field_192542_b.isEmpty();
      }

      public void func_192540_a(ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> var1) {
         this.field_192542_b.add(var1);
      }

      public void func_192538_b(ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> var1) {
         this.field_192542_b.remove(var1);
      }

      public void func_192537_a(EntityPlayerMP var1, EntityVillager var2, ItemStack var3) {
         ArrayList var4 = null;
         Iterator var5 = this.field_192542_b.iterator();

         ICriterionTrigger.Listener var6;
         while(var5.hasNext()) {
            var6 = (ICriterionTrigger.Listener)var5.next();
            if (((VillagerTradeTrigger.Instance)var6.func_192158_a()).func_192285_a(var1, var2, var3)) {
               if (var4 == null) {
                  var4 = Lists.newArrayList();
               }

               var4.add(var6);
            }
         }

         if (var4 != null) {
            var5 = var4.iterator();

            while(var5.hasNext()) {
               var6 = (ICriterionTrigger.Listener)var5.next();
               var6.func_192159_a(this.field_192541_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate field_192286_a;
      private final ItemPredicate field_192287_b;

      public Instance(EntityPredicate var1, ItemPredicate var2) {
         super(VillagerTradeTrigger.field_192237_a);
         this.field_192286_a = var1;
         this.field_192287_b = var2;
      }

      public static VillagerTradeTrigger.Instance func_203939_c() {
         return new VillagerTradeTrigger.Instance(EntityPredicate.field_192483_a, ItemPredicate.field_192495_a);
      }

      public boolean func_192285_a(EntityPlayerMP var1, EntityVillager var2, ItemStack var3) {
         if (!this.field_192286_a.func_192482_a(var1, var2)) {
            return false;
         } else {
            return this.field_192287_b.func_192493_a(var3);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.field_192287_b.func_200319_a());
         var1.add("villager", this.field_192286_a.func_204006_a());
         return var1;
      }
   }
}
