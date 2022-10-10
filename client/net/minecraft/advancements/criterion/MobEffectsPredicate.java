package net.minecraft.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class MobEffectsPredicate {
   public static final MobEffectsPredicate field_193473_a = new MobEffectsPredicate(Collections.emptyMap());
   private final Map<Potion, MobEffectsPredicate.InstancePredicate> field_193474_b;

   public MobEffectsPredicate(Map<Potion, MobEffectsPredicate.InstancePredicate> var1) {
      super();
      this.field_193474_b = var1;
   }

   public static MobEffectsPredicate func_204014_a() {
      return new MobEffectsPredicate(Maps.newHashMap());
   }

   public MobEffectsPredicate func_204015_a(Potion var1) {
      this.field_193474_b.put(var1, new MobEffectsPredicate.InstancePredicate());
      return this;
   }

   public boolean func_193469_a(Entity var1) {
      if (this == field_193473_a) {
         return true;
      } else {
         return var1 instanceof EntityLivingBase ? this.func_193470_a(((EntityLivingBase)var1).func_193076_bZ()) : false;
      }
   }

   public boolean func_193472_a(EntityLivingBase var1) {
      return this == field_193473_a ? true : this.func_193470_a(var1.func_193076_bZ());
   }

   public boolean func_193470_a(Map<Potion, PotionEffect> var1) {
      if (this == field_193473_a) {
         return true;
      } else {
         Iterator var2 = this.field_193474_b.entrySet().iterator();

         Entry var3;
         PotionEffect var4;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (Entry)var2.next();
            var4 = (PotionEffect)var1.get(var3.getKey());
         } while(((MobEffectsPredicate.InstancePredicate)var3.getValue()).func_193463_a(var4));

         return false;
      }
   }

   public static MobEffectsPredicate func_193471_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = JsonUtils.func_151210_l(var0, "effects");
         HashMap var2 = Maps.newHashMap();
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            ResourceLocation var5 = new ResourceLocation((String)var4.getKey());
            Potion var6 = (Potion)IRegistry.field_212631_t.func_212608_b(var5);
            if (var6 == null) {
               throw new JsonSyntaxException("Unknown effect '" + var5 + "'");
            }

            MobEffectsPredicate.InstancePredicate var7 = MobEffectsPredicate.InstancePredicate.func_193464_a(JsonUtils.func_151210_l((JsonElement)var4.getValue(), (String)var4.getKey()));
            var2.put(var6, var7);
         }

         return new MobEffectsPredicate(var2);
      } else {
         return field_193473_a;
      }
   }

   public JsonElement func_204013_b() {
      if (this == field_193473_a) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         Iterator var2 = this.field_193474_b.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            var1.add(IRegistry.field_212631_t.func_177774_c(var3.getKey()).toString(), ((MobEffectsPredicate.InstancePredicate)var3.getValue()).func_204012_a());
         }

         return var1;
      }
   }

   public static class InstancePredicate {
      private final MinMaxBounds.IntBound field_193465_a;
      private final MinMaxBounds.IntBound field_193466_b;
      @Nullable
      private final Boolean field_193467_c;
      @Nullable
      private final Boolean field_193468_d;

      public InstancePredicate(MinMaxBounds.IntBound var1, MinMaxBounds.IntBound var2, @Nullable Boolean var3, @Nullable Boolean var4) {
         super();
         this.field_193465_a = var1;
         this.field_193466_b = var2;
         this.field_193467_c = var3;
         this.field_193468_d = var4;
      }

      public InstancePredicate() {
         this(MinMaxBounds.IntBound.field_211347_e, MinMaxBounds.IntBound.field_211347_e, (Boolean)null, (Boolean)null);
      }

      public boolean func_193463_a(@Nullable PotionEffect var1) {
         if (var1 == null) {
            return false;
         } else if (!this.field_193465_a.func_211339_d(var1.func_76458_c())) {
            return false;
         } else if (!this.field_193466_b.func_211339_d(var1.func_76459_b())) {
            return false;
         } else if (this.field_193467_c != null && this.field_193467_c != var1.func_82720_e()) {
            return false;
         } else {
            return this.field_193468_d == null || this.field_193468_d == var1.func_188418_e();
         }
      }

      public JsonElement func_204012_a() {
         JsonObject var1 = new JsonObject();
         var1.add("amplifier", this.field_193465_a.func_200321_c());
         var1.add("duration", this.field_193466_b.func_200321_c());
         var1.addProperty("ambient", this.field_193467_c);
         var1.addProperty("visible", this.field_193468_d);
         return var1;
      }

      public static MobEffectsPredicate.InstancePredicate func_193464_a(JsonObject var0) {
         MinMaxBounds.IntBound var1 = MinMaxBounds.IntBound.func_211344_a(var0.get("amplifier"));
         MinMaxBounds.IntBound var2 = MinMaxBounds.IntBound.func_211344_a(var0.get("duration"));
         Boolean var3 = var0.has("ambient") ? JsonUtils.func_151212_i(var0, "ambient") : null;
         Boolean var4 = var0.has("visible") ? JsonUtils.func_151212_i(var0, "visible") : null;
         return new MobEffectsPredicate.InstancePredicate(var1, var2, var3, var4);
      }
   }
}
