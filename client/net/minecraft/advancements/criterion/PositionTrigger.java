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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;

public class PositionTrigger implements ICriterionTrigger<PositionTrigger.Instance> {
   private final ResourceLocation field_192217_a;
   private final Map<PlayerAdvancements, PositionTrigger.Listeners> field_192218_b = Maps.newHashMap();

   public PositionTrigger(ResourceLocation var1) {
      super();
      this.field_192217_a = var1;
   }

   public ResourceLocation func_192163_a() {
      return this.field_192217_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<PositionTrigger.Instance> var2) {
      PositionTrigger.Listeners var3 = (PositionTrigger.Listeners)this.field_192218_b.get(var1);
      if (var3 == null) {
         var3 = new PositionTrigger.Listeners(var1);
         this.field_192218_b.put(var1, var3);
      }

      var3.func_192510_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<PositionTrigger.Instance> var2) {
      PositionTrigger.Listeners var3 = (PositionTrigger.Listeners)this.field_192218_b.get(var1);
      if (var3 != null) {
         var3.func_192507_b(var2);
         if (var3.func_192508_a()) {
            this.field_192218_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192218_b.remove(var1);
   }

   public PositionTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      LocationPredicate var3 = LocationPredicate.func_193454_a(var1);
      return new PositionTrigger.Instance(this.field_192217_a, var3);
   }

   public void func_192215_a(EntityPlayerMP var1) {
      PositionTrigger.Listeners var2 = (PositionTrigger.Listeners)this.field_192218_b.get(var1.func_192039_O());
      if (var2 != null) {
         var2.func_193462_a(var1.func_71121_q(), var1.field_70165_t, var1.field_70163_u, var1.field_70161_v);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192511_a;
      private final Set<ICriterionTrigger.Listener<PositionTrigger.Instance>> field_192512_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192511_a = var1;
      }

      public boolean func_192508_a() {
         return this.field_192512_b.isEmpty();
      }

      public void func_192510_a(ICriterionTrigger.Listener<PositionTrigger.Instance> var1) {
         this.field_192512_b.add(var1);
      }

      public void func_192507_b(ICriterionTrigger.Listener<PositionTrigger.Instance> var1) {
         this.field_192512_b.remove(var1);
      }

      public void func_193462_a(WorldServer var1, double var2, double var4, double var6) {
         ArrayList var8 = null;
         Iterator var9 = this.field_192512_b.iterator();

         ICriterionTrigger.Listener var10;
         while(var9.hasNext()) {
            var10 = (ICriterionTrigger.Listener)var9.next();
            if (((PositionTrigger.Instance)var10.func_192158_a()).func_193204_a(var1, var2, var4, var6)) {
               if (var8 == null) {
                  var8 = Lists.newArrayList();
               }

               var8.add(var10);
            }
         }

         if (var8 != null) {
            var9 = var8.iterator();

            while(var9.hasNext()) {
               var10 = (ICriterionTrigger.Listener)var9.next();
               var10.func_192159_a(this.field_192511_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final LocationPredicate field_193205_a;

      public Instance(ResourceLocation var1, LocationPredicate var2) {
         super(var1);
         this.field_193205_a = var2;
      }

      public static PositionTrigger.Instance func_203932_a(LocationPredicate var0) {
         return new PositionTrigger.Instance(CriteriaTriggers.field_192135_o.field_192217_a, var0);
      }

      public static PositionTrigger.Instance func_203931_c() {
         return new PositionTrigger.Instance(CriteriaTriggers.field_192136_p.field_192217_a, LocationPredicate.field_193455_a);
      }

      public boolean func_193204_a(WorldServer var1, double var2, double var4, double var6) {
         return this.field_193205_a.func_193452_a(var1, var2, var4, var6);
      }

      public JsonElement func_200288_b() {
         return this.field_193205_a.func_204009_a();
      }
   }
}
