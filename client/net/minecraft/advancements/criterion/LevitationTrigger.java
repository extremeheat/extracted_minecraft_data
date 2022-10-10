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

public class LevitationTrigger implements ICriterionTrigger<LevitationTrigger.Instance> {
   private static final ResourceLocation field_193164_a = new ResourceLocation("levitation");
   private final Map<PlayerAdvancements, LevitationTrigger.Listeners> field_193165_b = Maps.newHashMap();

   public LevitationTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_193164_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<LevitationTrigger.Instance> var2) {
      LevitationTrigger.Listeners var3 = (LevitationTrigger.Listeners)this.field_193165_b.get(var1);
      if (var3 == null) {
         var3 = new LevitationTrigger.Listeners(var1);
         this.field_193165_b.put(var1, var3);
      }

      var3.func_193449_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<LevitationTrigger.Instance> var2) {
      LevitationTrigger.Listeners var3 = (LevitationTrigger.Listeners)this.field_193165_b.get(var1);
      if (var3 != null) {
         var3.func_193446_b(var2);
         if (var3.func_193447_a()) {
            this.field_193165_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_193165_b.remove(var1);
   }

   public LevitationTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      DistancePredicate var3 = DistancePredicate.func_193421_a(var1.get("distance"));
      MinMaxBounds.IntBound var4 = MinMaxBounds.IntBound.func_211344_a(var1.get("duration"));
      return new LevitationTrigger.Instance(var3, var4);
   }

   public void func_193162_a(EntityPlayerMP var1, Vec3d var2, int var3) {
      LevitationTrigger.Listeners var4 = (LevitationTrigger.Listeners)this.field_193165_b.get(var1.func_192039_O());
      if (var4 != null) {
         var4.func_193448_a(var1, var2, var3);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_193450_a;
      private final Set<ICriterionTrigger.Listener<LevitationTrigger.Instance>> field_193451_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_193450_a = var1;
      }

      public boolean func_193447_a() {
         return this.field_193451_b.isEmpty();
      }

      public void func_193449_a(ICriterionTrigger.Listener<LevitationTrigger.Instance> var1) {
         this.field_193451_b.add(var1);
      }

      public void func_193446_b(ICriterionTrigger.Listener<LevitationTrigger.Instance> var1) {
         this.field_193451_b.remove(var1);
      }

      public void func_193448_a(EntityPlayerMP var1, Vec3d var2, int var3) {
         ArrayList var4 = null;
         Iterator var5 = this.field_193451_b.iterator();

         ICriterionTrigger.Listener var6;
         while(var5.hasNext()) {
            var6 = (ICriterionTrigger.Listener)var5.next();
            if (((LevitationTrigger.Instance)var6.func_192158_a()).func_193201_a(var1, var2, var3)) {
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
               var6.func_192159_a(this.field_193450_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final DistancePredicate field_193202_a;
      private final MinMaxBounds.IntBound field_193203_b;

      public Instance(DistancePredicate var1, MinMaxBounds.IntBound var2) {
         super(LevitationTrigger.field_193164_a);
         this.field_193202_a = var1;
         this.field_193203_b = var2;
      }

      public static LevitationTrigger.Instance func_203930_a(DistancePredicate var0) {
         return new LevitationTrigger.Instance(var0, MinMaxBounds.IntBound.field_211347_e);
      }

      public boolean func_193201_a(EntityPlayerMP var1, Vec3d var2, int var3) {
         if (!this.field_193202_a.func_193422_a(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c, var1.field_70165_t, var1.field_70163_u, var1.field_70161_v)) {
            return false;
         } else {
            return this.field_193203_b.func_211339_d(var3);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("distance", this.field_193202_a.func_203994_a());
         var1.add("duration", this.field_193203_b.func_200321_c());
         return var1;
      }
   }
}
