package net.minecraft.advancements.critereon;

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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MobEffectsPredicate {
   public static final MobEffectsPredicate ANY = new MobEffectsPredicate(Collections.emptyMap());
   private final Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> effects;

   public MobEffectsPredicate(Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> var1) {
      super();
      this.effects = var1;
   }

   public static MobEffectsPredicate effects() {
      return new MobEffectsPredicate(Maps.newHashMap());
   }

   public MobEffectsPredicate and(MobEffect var1) {
      this.effects.put(var1, new MobEffectsPredicate.MobEffectInstancePredicate());
      return this;
   }

   public boolean matches(Entity var1) {
      if (this == ANY) {
         return true;
      } else {
         return var1 instanceof LivingEntity ? this.matches(((LivingEntity)var1).getActiveEffectsMap()) : false;
      }
   }

   public boolean matches(LivingEntity var1) {
      return this == ANY ? true : this.matches(var1.getActiveEffectsMap());
   }

   public boolean matches(Map<MobEffect, MobEffectInstance> var1) {
      if (this == ANY) {
         return true;
      } else {
         Iterator var2 = this.effects.entrySet().iterator();

         Entry var3;
         MobEffectInstance var4;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (Entry)var2.next();
            var4 = (MobEffectInstance)var1.get(var3.getKey());
         } while(((MobEffectsPredicate.MobEffectInstancePredicate)var3.getValue()).matches(var4));

         return false;
      }
   }

   public static MobEffectsPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "effects");
         HashMap var2 = Maps.newHashMap();
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            ResourceLocation var5 = new ResourceLocation((String)var4.getKey());
            MobEffect var6 = (MobEffect)Registry.MOB_EFFECT.getOptional(var5).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown effect '" + var5 + "'");
            });
            MobEffectsPredicate.MobEffectInstancePredicate var7 = MobEffectsPredicate.MobEffectInstancePredicate.fromJson(GsonHelper.convertToJsonObject((JsonElement)var4.getValue(), (String)var4.getKey()));
            var2.put(var6, var7);
         }

         return new MobEffectsPredicate(var2);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         Iterator var2 = this.effects.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            var1.add(Registry.MOB_EFFECT.getKey(var3.getKey()).toString(), ((MobEffectsPredicate.MobEffectInstancePredicate)var3.getValue()).serializeToJson());
         }

         return var1;
      }
   }

   public static class MobEffectInstancePredicate {
      private final MinMaxBounds.Ints amplifier;
      private final MinMaxBounds.Ints duration;
      @Nullable
      private final Boolean ambient;
      @Nullable
      private final Boolean visible;

      public MobEffectInstancePredicate(MinMaxBounds.Ints var1, MinMaxBounds.Ints var2, @Nullable Boolean var3, @Nullable Boolean var4) {
         super();
         this.amplifier = var1;
         this.duration = var2;
         this.ambient = var3;
         this.visible = var4;
      }

      public MobEffectInstancePredicate() {
         this(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, (Boolean)null, (Boolean)null);
      }

      public boolean matches(@Nullable MobEffectInstance var1) {
         if (var1 == null) {
            return false;
         } else if (!this.amplifier.matches(var1.getAmplifier())) {
            return false;
         } else if (!this.duration.matches(var1.getDuration())) {
            return false;
         } else if (this.ambient != null && this.ambient != var1.isAmbient()) {
            return false;
         } else {
            return this.visible == null || this.visible == var1.isVisible();
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("amplifier", this.amplifier.serializeToJson());
         var1.add("duration", this.duration.serializeToJson());
         var1.addProperty("ambient", this.ambient);
         var1.addProperty("visible", this.visible);
         return var1;
      }

      public static MobEffectsPredicate.MobEffectInstancePredicate fromJson(JsonObject var0) {
         MinMaxBounds.Ints var1 = MinMaxBounds.Ints.fromJson(var0.get("amplifier"));
         MinMaxBounds.Ints var2 = MinMaxBounds.Ints.fromJson(var0.get("duration"));
         Boolean var3 = var0.has("ambient") ? GsonHelper.getAsBoolean(var0, "ambient") : null;
         Boolean var4 = var0.has("visible") ? GsonHelper.getAsBoolean(var0, "visible") : null;
         return new MobEffectsPredicate.MobEffectInstancePredicate(var1, var2, var3, var4);
      }
   }
}
