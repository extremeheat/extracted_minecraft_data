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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

public class NetherTravelTrigger implements ICriterionTrigger<NetherTravelTrigger.Instance> {
   private static final ResourceLocation field_193169_a = new ResourceLocation("nether_travel");
   private final Map<PlayerAdvancements, NetherTravelTrigger.Listeners> field_193170_b = Maps.newHashMap();

   public NetherTravelTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_193169_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<NetherTravelTrigger.Instance> var2) {
      NetherTravelTrigger.Listeners var3 = (NetherTravelTrigger.Listeners)this.field_193170_b.get(var1);
      if (var3 == null) {
         var3 = new NetherTravelTrigger.Listeners(var1);
         this.field_193170_b.put(var1, var3);
      }

      var3.func_193484_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<NetherTravelTrigger.Instance> var2) {
      NetherTravelTrigger.Listeners var3 = (NetherTravelTrigger.Listeners)this.field_193170_b.get(var1);
      if (var3 != null) {
         var3.func_193481_b(var2);
         if (var3.func_193482_a()) {
            this.field_193170_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_193170_b.remove(var1);
   }

   public NetherTravelTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      LocationPredicate var3 = LocationPredicate.func_193454_a(var1.get("entered"));
      LocationPredicate var4 = LocationPredicate.func_193454_a(var1.get("exited"));
      DistancePredicate var5 = DistancePredicate.func_193421_a(var1.get("distance"));
      return new NetherTravelTrigger.Instance(var3, var4, var5);
   }

   public void func_193168_a(EntityPlayerMP var1, Vec3d var2) {
      NetherTravelTrigger.Listeners var3 = (NetherTravelTrigger.Listeners)this.field_193170_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_193483_a(var1.func_71121_q(), var2, var1.field_70165_t, var1.field_70163_u, var1.field_70161_v);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_193485_a;
      private final Set<ICriterionTrigger.Listener<NetherTravelTrigger.Instance>> field_193486_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_193485_a = var1;
      }

      public boolean func_193482_a() {
         return this.field_193486_b.isEmpty();
      }

      public void func_193484_a(ICriterionTrigger.Listener<NetherTravelTrigger.Instance> var1) {
         this.field_193486_b.add(var1);
      }

      public void func_193481_b(ICriterionTrigger.Listener<NetherTravelTrigger.Instance> var1) {
         this.field_193486_b.remove(var1);
      }

      public void func_193483_a(WorldServer var1, Vec3d var2, double var3, double var5, double var7) {
         ArrayList var9 = null;
         Iterator var10 = this.field_193486_b.iterator();

         ICriterionTrigger.Listener var11;
         while(var10.hasNext()) {
            var11 = (ICriterionTrigger.Listener)var10.next();
            if (((NetherTravelTrigger.Instance)var11.func_192158_a()).func_193206_a(var1, var2, var3, var5, var7)) {
               if (var9 == null) {
                  var9 = Lists.newArrayList();
               }

               var9.add(var11);
            }
         }

         if (var9 != null) {
            var10 = var9.iterator();

            while(var10.hasNext()) {
               var11 = (ICriterionTrigger.Listener)var10.next();
               var11.func_192159_a(this.field_193485_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final LocationPredicate field_193207_a;
      private final LocationPredicate field_193208_b;
      private final DistancePredicate field_193209_c;

      public Instance(LocationPredicate var1, LocationPredicate var2, DistancePredicate var3) {
         super(NetherTravelTrigger.field_193169_a);
         this.field_193207_a = var1;
         this.field_193208_b = var2;
         this.field_193209_c = var3;
      }

      public static NetherTravelTrigger.Instance func_203933_a(DistancePredicate var0) {
         return new NetherTravelTrigger.Instance(LocationPredicate.field_193455_a, LocationPredicate.field_193455_a, var0);
      }

      public boolean func_193206_a(WorldServer var1, Vec3d var2, double var3, double var5, double var7) {
         if (!this.field_193207_a.func_193452_a(var1, var2.field_72450_a, var2.field_72448_b, var2.field_72449_c)) {
            return false;
         } else if (!this.field_193208_b.func_193452_a(var1, var3, var5, var7)) {
            return false;
         } else {
            return this.field_193209_c.func_193422_a(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c, var3, var5, var7);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("entered", this.field_193207_a.func_204009_a());
         var1.add("exited", this.field_193208_b.func_204009_a());
         var1.add("distance", this.field_193209_c.func_203994_a());
         return var1;
      }
   }
}
