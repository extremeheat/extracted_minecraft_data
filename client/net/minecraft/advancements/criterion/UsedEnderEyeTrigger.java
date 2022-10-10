package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
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
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeTrigger implements ICriterionTrigger<UsedEnderEyeTrigger.Instance> {
   private static final ResourceLocation field_192242_a = new ResourceLocation("used_ender_eye");
   private final Map<PlayerAdvancements, UsedEnderEyeTrigger.Listeners> field_192243_b = Maps.newHashMap();

   public UsedEnderEyeTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192242_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance> var2) {
      UsedEnderEyeTrigger.Listeners var3 = (UsedEnderEyeTrigger.Listeners)this.field_192243_b.get(var1);
      if (var3 == null) {
         var3 = new UsedEnderEyeTrigger.Listeners(var1);
         this.field_192243_b.put(var1, var3);
      }

      var3.func_192546_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance> var2) {
      UsedEnderEyeTrigger.Listeners var3 = (UsedEnderEyeTrigger.Listeners)this.field_192243_b.get(var1);
      if (var3 != null) {
         var3.func_192544_b(var2);
         if (var3.func_192545_a()) {
            this.field_192243_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192243_b.remove(var1);
   }

   public UsedEnderEyeTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      MinMaxBounds.FloatBound var3 = MinMaxBounds.FloatBound.func_211356_a(var1.get("distance"));
      return new UsedEnderEyeTrigger.Instance(var3);
   }

   public void func_192239_a(EntityPlayerMP var1, BlockPos var2) {
      UsedEnderEyeTrigger.Listeners var3 = (UsedEnderEyeTrigger.Listeners)this.field_192243_b.get(var1.func_192039_O());
      if (var3 != null) {
         double var4 = var1.field_70165_t - (double)var2.func_177958_n();
         double var6 = var1.field_70161_v - (double)var2.func_177952_p();
         var3.func_192543_a(var4 * var4 + var6 * var6);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192547_a;
      private final Set<ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance>> field_192548_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192547_a = var1;
      }

      public boolean func_192545_a() {
         return this.field_192548_b.isEmpty();
      }

      public void func_192546_a(ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance> var1) {
         this.field_192548_b.add(var1);
      }

      public void func_192544_b(ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance> var1) {
         this.field_192548_b.remove(var1);
      }

      public void func_192543_a(double var1) {
         ArrayList var3 = null;
         Iterator var4 = this.field_192548_b.iterator();

         ICriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (ICriterionTrigger.Listener)var4.next();
            if (((UsedEnderEyeTrigger.Instance)var5.func_192158_a()).func_192288_a(var1)) {
               if (var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var5);
            }
         }

         if (var3 != null) {
            var4 = var3.iterator();

            while(var4.hasNext()) {
               var5 = (ICriterionTrigger.Listener)var4.next();
               var5.func_192159_a(this.field_192547_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final MinMaxBounds.FloatBound field_192289_a;

      public Instance(MinMaxBounds.FloatBound var1) {
         super(UsedEnderEyeTrigger.field_192242_a);
         this.field_192289_a = var1;
      }

      public boolean func_192288_a(double var1) {
         return this.field_192289_a.func_211351_a(var1);
      }
   }
}
