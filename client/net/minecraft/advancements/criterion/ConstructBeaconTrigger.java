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
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;

public class ConstructBeaconTrigger implements ICriterionTrigger<ConstructBeaconTrigger.Instance> {
   private static final ResourceLocation field_192181_a = new ResourceLocation("construct_beacon");
   private final Map<PlayerAdvancements, ConstructBeaconTrigger.Listeners> field_192182_b = Maps.newHashMap();

   public ConstructBeaconTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192181_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> var2) {
      ConstructBeaconTrigger.Listeners var3 = (ConstructBeaconTrigger.Listeners)this.field_192182_b.get(var1);
      if (var3 == null) {
         var3 = new ConstructBeaconTrigger.Listeners(var1);
         this.field_192182_b.put(var1, var3);
      }

      var3.func_192355_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> var2) {
      ConstructBeaconTrigger.Listeners var3 = (ConstructBeaconTrigger.Listeners)this.field_192182_b.get(var1);
      if (var3 != null) {
         var3.func_192353_b(var2);
         if (var3.func_192354_a()) {
            this.field_192182_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192182_b.remove(var1);
   }

   public ConstructBeaconTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      MinMaxBounds.IntBound var3 = MinMaxBounds.IntBound.func_211344_a(var1.get("level"));
      return new ConstructBeaconTrigger.Instance(var3);
   }

   public void func_192180_a(EntityPlayerMP var1, TileEntityBeacon var2) {
      ConstructBeaconTrigger.Listeners var3 = (ConstructBeaconTrigger.Listeners)this.field_192182_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_192352_a(var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192356_a;
      private final Set<ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance>> field_192357_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192356_a = var1;
      }

      public boolean func_192354_a() {
         return this.field_192357_b.isEmpty();
      }

      public void func_192355_a(ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> var1) {
         this.field_192357_b.add(var1);
      }

      public void func_192353_b(ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> var1) {
         this.field_192357_b.remove(var1);
      }

      public void func_192352_a(TileEntityBeacon var1) {
         ArrayList var2 = null;
         Iterator var3 = this.field_192357_b.iterator();

         ICriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (ICriterionTrigger.Listener)var3.next();
            if (((ConstructBeaconTrigger.Instance)var4.func_192158_a()).func_192252_a(var1)) {
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
               var4.func_192159_a(this.field_192356_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final MinMaxBounds.IntBound field_192253_a;

      public Instance(MinMaxBounds.IntBound var1) {
         super(ConstructBeaconTrigger.field_192181_a);
         this.field_192253_a = var1;
      }

      public static ConstructBeaconTrigger.Instance func_203912_a(MinMaxBounds.IntBound var0) {
         return new ConstructBeaconTrigger.Instance(var0);
      }

      public boolean func_192252_a(TileEntityBeacon var1) {
         return this.field_192253_a.func_211339_d(var1.func_191979_s());
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("level", this.field_192253_a.func_200321_c());
         return var1;
      }
   }
}
