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
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class PlayerHurtEntityTrigger implements ICriterionTrigger<PlayerHurtEntityTrigger.Instance> {
   private static final ResourceLocation field_192222_a = new ResourceLocation("player_hurt_entity");
   private final Map<PlayerAdvancements, PlayerHurtEntityTrigger.Listeners> field_192223_b = Maps.newHashMap();

   public PlayerHurtEntityTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192222_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance> var2) {
      PlayerHurtEntityTrigger.Listeners var3 = (PlayerHurtEntityTrigger.Listeners)this.field_192223_b.get(var1);
      if (var3 == null) {
         var3 = new PlayerHurtEntityTrigger.Listeners(var1);
         this.field_192223_b.put(var1, var3);
      }

      var3.func_192522_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance> var2) {
      PlayerHurtEntityTrigger.Listeners var3 = (PlayerHurtEntityTrigger.Listeners)this.field_192223_b.get(var1);
      if (var3 != null) {
         var3.func_192519_b(var2);
         if (var3.func_192520_a()) {
            this.field_192223_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192223_b.remove(var1);
   }

   public PlayerHurtEntityTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      DamagePredicate var3 = DamagePredicate.func_192364_a(var1.get("damage"));
      EntityPredicate var4 = EntityPredicate.func_192481_a(var1.get("entity"));
      return new PlayerHurtEntityTrigger.Instance(var3, var4);
   }

   public void func_192220_a(EntityPlayerMP var1, Entity var2, DamageSource var3, float var4, float var5, boolean var6) {
      PlayerHurtEntityTrigger.Listeners var7 = (PlayerHurtEntityTrigger.Listeners)this.field_192223_b.get(var1.func_192039_O());
      if (var7 != null) {
         var7.func_192521_a(var1, var2, var3, var4, var5, var6);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192523_a;
      private final Set<ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance>> field_192524_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192523_a = var1;
      }

      public boolean func_192520_a() {
         return this.field_192524_b.isEmpty();
      }

      public void func_192522_a(ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance> var1) {
         this.field_192524_b.add(var1);
      }

      public void func_192519_b(ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance> var1) {
         this.field_192524_b.remove(var1);
      }

      public void func_192521_a(EntityPlayerMP var1, Entity var2, DamageSource var3, float var4, float var5, boolean var6) {
         ArrayList var7 = null;
         Iterator var8 = this.field_192524_b.iterator();

         ICriterionTrigger.Listener var9;
         while(var8.hasNext()) {
            var9 = (ICriterionTrigger.Listener)var8.next();
            if (((PlayerHurtEntityTrigger.Instance)var9.func_192158_a()).func_192278_a(var1, var2, var3, var4, var5, var6)) {
               if (var7 == null) {
                  var7 = Lists.newArrayList();
               }

               var7.add(var9);
            }
         }

         if (var7 != null) {
            var8 = var7.iterator();

            while(var8.hasNext()) {
               var9 = (ICriterionTrigger.Listener)var8.next();
               var9.func_192159_a(this.field_192523_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final DamagePredicate field_192279_a;
      private final EntityPredicate field_192280_b;

      public Instance(DamagePredicate var1, EntityPredicate var2) {
         super(PlayerHurtEntityTrigger.field_192222_a);
         this.field_192279_a = var1;
         this.field_192280_b = var2;
      }

      public static PlayerHurtEntityTrigger.Instance func_203936_a(DamagePredicate.Builder var0) {
         return new PlayerHurtEntityTrigger.Instance(var0.func_203970_b(), EntityPredicate.field_192483_a);
      }

      public boolean func_192278_a(EntityPlayerMP var1, Entity var2, DamageSource var3, float var4, float var5, boolean var6) {
         if (!this.field_192279_a.func_192365_a(var1, var3, var4, var5, var6)) {
            return false;
         } else {
            return this.field_192280_b.func_192482_a(var1, var2);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("damage", this.field_192279_a.func_203977_a());
         var1.add("entity", this.field_192280_b.func_204006_a());
         return var1;
      }
   }
}
