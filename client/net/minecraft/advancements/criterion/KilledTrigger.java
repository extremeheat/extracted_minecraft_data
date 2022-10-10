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
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class KilledTrigger implements ICriterionTrigger<KilledTrigger.Instance> {
   private final Map<PlayerAdvancements, KilledTrigger.Listeners> field_192213_a = Maps.newHashMap();
   private final ResourceLocation field_192214_b;

   public KilledTrigger(ResourceLocation var1) {
      super();
      this.field_192214_b = var1;
   }

   public ResourceLocation func_192163_a() {
      return this.field_192214_b;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<KilledTrigger.Instance> var2) {
      KilledTrigger.Listeners var3 = (KilledTrigger.Listeners)this.field_192213_a.get(var1);
      if (var3 == null) {
         var3 = new KilledTrigger.Listeners(var1);
         this.field_192213_a.put(var1, var3);
      }

      var3.func_192504_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<KilledTrigger.Instance> var2) {
      KilledTrigger.Listeners var3 = (KilledTrigger.Listeners)this.field_192213_a.get(var1);
      if (var3 != null) {
         var3.func_192501_b(var2);
         if (var3.func_192502_a()) {
            this.field_192213_a.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192213_a.remove(var1);
   }

   public KilledTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return new KilledTrigger.Instance(this.field_192214_b, EntityPredicate.func_192481_a(var1.get("entity")), DamageSourcePredicate.func_192447_a(var1.get("killing_blow")));
   }

   public void func_192211_a(EntityPlayerMP var1, Entity var2, DamageSource var3) {
      KilledTrigger.Listeners var4 = (KilledTrigger.Listeners)this.field_192213_a.get(var1.func_192039_O());
      if (var4 != null) {
         var4.func_192503_a(var1, var2, var3);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192505_a;
      private final Set<ICriterionTrigger.Listener<KilledTrigger.Instance>> field_192506_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192505_a = var1;
      }

      public boolean func_192502_a() {
         return this.field_192506_b.isEmpty();
      }

      public void func_192504_a(ICriterionTrigger.Listener<KilledTrigger.Instance> var1) {
         this.field_192506_b.add(var1);
      }

      public void func_192501_b(ICriterionTrigger.Listener<KilledTrigger.Instance> var1) {
         this.field_192506_b.remove(var1);
      }

      public void func_192503_a(EntityPlayerMP var1, Entity var2, DamageSource var3) {
         ArrayList var4 = null;
         Iterator var5 = this.field_192506_b.iterator();

         ICriterionTrigger.Listener var6;
         while(var5.hasNext()) {
            var6 = (ICriterionTrigger.Listener)var5.next();
            if (((KilledTrigger.Instance)var6.func_192158_a()).func_192270_a(var1, var2, var3)) {
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
               var6.func_192159_a(this.field_192505_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate field_192271_a;
      private final DamageSourcePredicate field_192272_b;

      public Instance(ResourceLocation var1, EntityPredicate var2, DamageSourcePredicate var3) {
         super(var1);
         this.field_192271_a = var2;
         this.field_192272_b = var3;
      }

      public static KilledTrigger.Instance func_203928_a(EntityPredicate.Builder var0) {
         return new KilledTrigger.Instance(CriteriaTriggers.field_192122_b.field_192214_b, var0.func_204000_b(), DamageSourcePredicate.field_192449_a);
      }

      public static KilledTrigger.Instance func_203927_c() {
         return new KilledTrigger.Instance(CriteriaTriggers.field_192122_b.field_192214_b, EntityPredicate.field_192483_a, DamageSourcePredicate.field_192449_a);
      }

      public static KilledTrigger.Instance func_203929_a(EntityPredicate.Builder var0, DamageSourcePredicate.Builder var1) {
         return new KilledTrigger.Instance(CriteriaTriggers.field_192122_b.field_192214_b, var0.func_204000_b(), var1.func_203979_b());
      }

      public static KilledTrigger.Instance func_203926_d() {
         return new KilledTrigger.Instance(CriteriaTriggers.field_192123_c.field_192214_b, EntityPredicate.field_192483_a, DamageSourcePredicate.field_192449_a);
      }

      public boolean func_192270_a(EntityPlayerMP var1, Entity var2, DamageSource var3) {
         return !this.field_192272_b.func_193418_a(var1, var3) ? false : this.field_192271_a.func_192482_a(var1, var2);
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.field_192271_a.func_204006_a());
         var1.add("killing_blow", this.field_192272_b.func_203991_a());
         return var1;
      }
   }
}
