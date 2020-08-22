package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class KilledByCrossbowTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   public KilledByCrossbowTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate[] var3 = EntityPredicate.fromJsonArray(var1.get("victims"));
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("unique_entity_types"));
      return new KilledByCrossbowTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, Collection var2, int var3) {
      this.trigger(var1.getAdvancements(), (var3x) -> {
         return var3x.matches(var1, var2, var3);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate[] victims;
      private final MinMaxBounds.Ints uniqueEntityTypes;

      public TriggerInstance(EntityPredicate[] var1, MinMaxBounds.Ints var2) {
         super(KilledByCrossbowTrigger.ID);
         this.victims = var1;
         this.uniqueEntityTypes = var2;
      }

      public static KilledByCrossbowTrigger.TriggerInstance crossbowKilled(EntityPredicate.Builder... var0) {
         EntityPredicate[] var1 = new EntityPredicate[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            EntityPredicate.Builder var3 = var0[var2];
            var1[var2] = var3.build();
         }

         return new KilledByCrossbowTrigger.TriggerInstance(var1, MinMaxBounds.Ints.ANY);
      }

      public static KilledByCrossbowTrigger.TriggerInstance crossbowKilled(MinMaxBounds.Ints var0) {
         EntityPredicate[] var1 = new EntityPredicate[0];
         return new KilledByCrossbowTrigger.TriggerInstance(var1, var0);
      }

      public boolean matches(ServerPlayer var1, Collection var2, int var3) {
         if (this.victims.length > 0) {
            ArrayList var4 = Lists.newArrayList(var2);
            EntityPredicate[] var5 = this.victims;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               EntityPredicate var8 = var5[var7];
               boolean var9 = false;
               Iterator var10 = var4.iterator();

               while(var10.hasNext()) {
                  Entity var11 = (Entity)var10.next();
                  if (var8.matches(var1, var11)) {
                     var10.remove();
                     var9 = true;
                     break;
                  }
               }

               if (!var9) {
                  return false;
               }
            }
         }

         if (this.uniqueEntityTypes == MinMaxBounds.Ints.ANY) {
            return true;
         } else {
            HashSet var12 = Sets.newHashSet();
            Iterator var13 = var2.iterator();

            while(var13.hasNext()) {
               Entity var14 = (Entity)var13.next();
               var12.add(var14.getType());
            }

            return this.uniqueEntityTypes.matches(var12.size()) && this.uniqueEntityTypes.matches(var3);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("victims", EntityPredicate.serializeArrayToJson(this.victims));
         var1.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
         return var1;
      }
   }
}
