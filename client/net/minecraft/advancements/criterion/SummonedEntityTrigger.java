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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class SummonedEntityTrigger implements ICriterionTrigger<SummonedEntityTrigger.Instance> {
   private static final ResourceLocation field_192232_a = new ResourceLocation("summoned_entity");
   private final Map<PlayerAdvancements, SummonedEntityTrigger.Listeners> field_192233_b = Maps.newHashMap();

   public SummonedEntityTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192232_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<SummonedEntityTrigger.Instance> var2) {
      SummonedEntityTrigger.Listeners var3 = (SummonedEntityTrigger.Listeners)this.field_192233_b.get(var1);
      if (var3 == null) {
         var3 = new SummonedEntityTrigger.Listeners(var1);
         this.field_192233_b.put(var1, var3);
      }

      var3.func_192534_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<SummonedEntityTrigger.Instance> var2) {
      SummonedEntityTrigger.Listeners var3 = (SummonedEntityTrigger.Listeners)this.field_192233_b.get(var1);
      if (var3 != null) {
         var3.func_192531_b(var2);
         if (var3.func_192532_a()) {
            this.field_192233_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192233_b.remove(var1);
   }

   public SummonedEntityTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.func_192481_a(var1.get("entity"));
      return new SummonedEntityTrigger.Instance(var3);
   }

   public void func_192229_a(EntityPlayerMP var1, Entity var2) {
      SummonedEntityTrigger.Listeners var3 = (SummonedEntityTrigger.Listeners)this.field_192233_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_192533_a(var1, var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192535_a;
      private final Set<ICriterionTrigger.Listener<SummonedEntityTrigger.Instance>> field_192536_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192535_a = var1;
      }

      public boolean func_192532_a() {
         return this.field_192536_b.isEmpty();
      }

      public void func_192534_a(ICriterionTrigger.Listener<SummonedEntityTrigger.Instance> var1) {
         this.field_192536_b.add(var1);
      }

      public void func_192531_b(ICriterionTrigger.Listener<SummonedEntityTrigger.Instance> var1) {
         this.field_192536_b.remove(var1);
      }

      public void func_192533_a(EntityPlayerMP var1, Entity var2) {
         ArrayList var3 = null;
         Iterator var4 = this.field_192536_b.iterator();

         ICriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (ICriterionTrigger.Listener)var4.next();
            if (((SummonedEntityTrigger.Instance)var5.func_192158_a()).func_192283_a(var1, var2)) {
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
               var5.func_192159_a(this.field_192535_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate field_192284_a;

      public Instance(EntityPredicate var1) {
         super(SummonedEntityTrigger.field_192232_a);
         this.field_192284_a = var1;
      }

      public static SummonedEntityTrigger.Instance func_203937_a(EntityPredicate.Builder var0) {
         return new SummonedEntityTrigger.Instance(var0.func_204000_b());
      }

      public boolean func_192283_a(EntityPlayerMP var1, Entity var2) {
         return this.field_192284_a.func_192482_a(var1, var2);
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.field_192284_a.func_204006_a());
         return var1;
      }
   }
}
