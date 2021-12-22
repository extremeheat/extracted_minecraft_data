package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledByCrossbowTrigger extends SimpleCriterionTrigger<KilledByCrossbowTrigger.TriggerInstance> {
   // $FF: renamed from: ID net.minecraft.resources.ResourceLocation
   static final ResourceLocation field_64 = new ResourceLocation("killed_by_crossbow");

   public KilledByCrossbowTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return field_64;
   }

   public KilledByCrossbowTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      EntityPredicate.Composite[] var4 = EntityPredicate.Composite.fromJsonArray(var1, "victims", var3);
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var1.get("unique_entity_types"));
      return new KilledByCrossbowTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, Collection<Entity> var2) {
      ArrayList var3 = Lists.newArrayList();
      HashSet var4 = Sets.newHashSet();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Entity var6 = (Entity)var5.next();
         var4.add(var6.getType());
         var3.add(EntityPredicate.createContext(var1, var6));
      }

      this.trigger(var1, (var2x) -> {
         return var2x.matches(var3, var4.size());
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite[] victims;
      private final MinMaxBounds.Ints uniqueEntityTypes;

      public TriggerInstance(EntityPredicate.Composite var1, EntityPredicate.Composite[] var2, MinMaxBounds.Ints var3) {
         super(KilledByCrossbowTrigger.field_64, var1);
         this.victims = var2;
         this.uniqueEntityTypes = var3;
      }

      public static KilledByCrossbowTrigger.TriggerInstance crossbowKilled(EntityPredicate.Builder... var0) {
         EntityPredicate.Composite[] var1 = new EntityPredicate.Composite[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            EntityPredicate.Builder var3 = var0[var2];
            var1[var2] = EntityPredicate.Composite.wrap(var3.build());
         }

         return new KilledByCrossbowTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var1, MinMaxBounds.Ints.ANY);
      }

      public static KilledByCrossbowTrigger.TriggerInstance crossbowKilled(MinMaxBounds.Ints var0) {
         EntityPredicate.Composite[] var1 = new EntityPredicate.Composite[0];
         return new KilledByCrossbowTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var1, var0);
      }

      public boolean matches(Collection<LootContext> var1, int var2) {
         if (this.victims.length > 0) {
            ArrayList var3 = Lists.newArrayList(var1);
            EntityPredicate.Composite[] var4 = this.victims;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               EntityPredicate.Composite var7 = var4[var6];
               boolean var8 = false;
               Iterator var9 = var3.iterator();

               while(var9.hasNext()) {
                  LootContext var10 = (LootContext)var9.next();
                  if (var7.matches(var10)) {
                     var9.remove();
                     var8 = true;
                     break;
                  }
               }

               if (!var8) {
                  return false;
               }
            }
         }

         return this.uniqueEntityTypes.matches(var2);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("victims", EntityPredicate.Composite.toJson(this.victims, var1));
         var2.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
         return var2;
      }
   }
}
