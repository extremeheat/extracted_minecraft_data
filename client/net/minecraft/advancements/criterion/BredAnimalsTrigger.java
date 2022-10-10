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
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class BredAnimalsTrigger implements ICriterionTrigger<BredAnimalsTrigger.Instance> {
   private static final ResourceLocation field_192171_a = new ResourceLocation("bred_animals");
   private final Map<PlayerAdvancements, BredAnimalsTrigger.Listeners> field_192172_b = Maps.newHashMap();

   public BredAnimalsTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192171_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> var2) {
      BredAnimalsTrigger.Listeners var3 = (BredAnimalsTrigger.Listeners)this.field_192172_b.get(var1);
      if (var3 == null) {
         var3 = new BredAnimalsTrigger.Listeners(var1);
         this.field_192172_b.put(var1, var3);
      }

      var3.func_192343_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> var2) {
      BredAnimalsTrigger.Listeners var3 = (BredAnimalsTrigger.Listeners)this.field_192172_b.get(var1);
      if (var3 != null) {
         var3.func_192340_b(var2);
         if (var3.func_192341_a()) {
            this.field_192172_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192172_b.remove(var1);
   }

   public BredAnimalsTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.func_192481_a(var1.get("parent"));
      EntityPredicate var4 = EntityPredicate.func_192481_a(var1.get("partner"));
      EntityPredicate var5 = EntityPredicate.func_192481_a(var1.get("child"));
      return new BredAnimalsTrigger.Instance(var3, var4, var5);
   }

   public void func_192168_a(EntityPlayerMP var1, EntityAnimal var2, EntityAnimal var3, @Nullable EntityAgeable var4) {
      BredAnimalsTrigger.Listeners var5 = (BredAnimalsTrigger.Listeners)this.field_192172_b.get(var1.func_192039_O());
      if (var5 != null) {
         var5.func_192342_a(var1, var2, var3, var4);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192344_a;
      private final Set<ICriterionTrigger.Listener<BredAnimalsTrigger.Instance>> field_192345_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192344_a = var1;
      }

      public boolean func_192341_a() {
         return this.field_192345_b.isEmpty();
      }

      public void func_192343_a(ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> var1) {
         this.field_192345_b.add(var1);
      }

      public void func_192340_b(ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> var1) {
         this.field_192345_b.remove(var1);
      }

      public void func_192342_a(EntityPlayerMP var1, EntityAnimal var2, EntityAnimal var3, @Nullable EntityAgeable var4) {
         ArrayList var5 = null;
         Iterator var6 = this.field_192345_b.iterator();

         ICriterionTrigger.Listener var7;
         while(var6.hasNext()) {
            var7 = (ICriterionTrigger.Listener)var6.next();
            if (((BredAnimalsTrigger.Instance)var7.func_192158_a()).func_192246_a(var1, var2, var3, var4)) {
               if (var5 == null) {
                  var5 = Lists.newArrayList();
               }

               var5.add(var7);
            }
         }

         if (var5 != null) {
            var6 = var5.iterator();

            while(var6.hasNext()) {
               var7 = (ICriterionTrigger.Listener)var6.next();
               var7.func_192159_a(this.field_192344_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate field_192247_a;
      private final EntityPredicate field_192248_b;
      private final EntityPredicate field_192249_c;

      public Instance(EntityPredicate var1, EntityPredicate var2, EntityPredicate var3) {
         super(BredAnimalsTrigger.field_192171_a);
         this.field_192247_a = var1;
         this.field_192248_b = var2;
         this.field_192249_c = var3;
      }

      public static BredAnimalsTrigger.Instance func_203908_c() {
         return new BredAnimalsTrigger.Instance(EntityPredicate.field_192483_a, EntityPredicate.field_192483_a, EntityPredicate.field_192483_a);
      }

      public static BredAnimalsTrigger.Instance func_203909_a(EntityPredicate.Builder var0) {
         return new BredAnimalsTrigger.Instance(var0.func_204000_b(), EntityPredicate.field_192483_a, EntityPredicate.field_192483_a);
      }

      public boolean func_192246_a(EntityPlayerMP var1, EntityAnimal var2, EntityAnimal var3, @Nullable EntityAgeable var4) {
         if (!this.field_192249_c.func_192482_a(var1, var4)) {
            return false;
         } else {
            return this.field_192247_a.func_192482_a(var1, var2) && this.field_192248_b.func_192482_a(var1, var3) || this.field_192247_a.func_192482_a(var1, var3) && this.field_192248_b.func_192482_a(var1, var2);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("parent", this.field_192247_a.func_204006_a());
         var1.add("partner", this.field_192248_b.func_204006_a());
         var1.add("child", this.field_192249_c.func_204006_a());
         return var1;
      }
   }
}
