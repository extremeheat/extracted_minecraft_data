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
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class TameAnimalTrigger implements ICriterionTrigger<TameAnimalTrigger.Instance> {
   private static final ResourceLocation field_193179_a = new ResourceLocation("tame_animal");
   private final Map<PlayerAdvancements, TameAnimalTrigger.Listeners> field_193180_b = Maps.newHashMap();

   public TameAnimalTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_193179_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<TameAnimalTrigger.Instance> var2) {
      TameAnimalTrigger.Listeners var3 = (TameAnimalTrigger.Listeners)this.field_193180_b.get(var1);
      if (var3 == null) {
         var3 = new TameAnimalTrigger.Listeners(var1);
         this.field_193180_b.put(var1, var3);
      }

      var3.func_193496_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<TameAnimalTrigger.Instance> var2) {
      TameAnimalTrigger.Listeners var3 = (TameAnimalTrigger.Listeners)this.field_193180_b.get(var1);
      if (var3 != null) {
         var3.func_193494_b(var2);
         if (var3.func_193495_a()) {
            this.field_193180_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_193180_b.remove(var1);
   }

   public TameAnimalTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.func_192481_a(var1.get("entity"));
      return new TameAnimalTrigger.Instance(var3);
   }

   public void func_193178_a(EntityPlayerMP var1, EntityAnimal var2) {
      TameAnimalTrigger.Listeners var3 = (TameAnimalTrigger.Listeners)this.field_193180_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_193497_a(var1, var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_193498_a;
      private final Set<ICriterionTrigger.Listener<TameAnimalTrigger.Instance>> field_193499_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_193498_a = var1;
      }

      public boolean func_193495_a() {
         return this.field_193499_b.isEmpty();
      }

      public void func_193496_a(ICriterionTrigger.Listener<TameAnimalTrigger.Instance> var1) {
         this.field_193499_b.add(var1);
      }

      public void func_193494_b(ICriterionTrigger.Listener<TameAnimalTrigger.Instance> var1) {
         this.field_193499_b.remove(var1);
      }

      public void func_193497_a(EntityPlayerMP var1, EntityAnimal var2) {
         ArrayList var3 = null;
         Iterator var4 = this.field_193499_b.iterator();

         ICriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (ICriterionTrigger.Listener)var4.next();
            if (((TameAnimalTrigger.Instance)var5.func_192158_a()).func_193216_a(var1, var2)) {
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
               var5.func_192159_a(this.field_193498_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate field_193217_a;

      public Instance(EntityPredicate var1) {
         super(TameAnimalTrigger.field_193179_a);
         this.field_193217_a = var1;
      }

      public static TameAnimalTrigger.Instance func_203938_c() {
         return new TameAnimalTrigger.Instance(EntityPredicate.field_192483_a);
      }

      public boolean func_193216_a(EntityPlayerMP var1, EntityAnimal var2) {
         return this.field_193217_a.func_192482_a(var1, var2);
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.field_193217_a.func_204006_a());
         return var1;
      }
   }
}
