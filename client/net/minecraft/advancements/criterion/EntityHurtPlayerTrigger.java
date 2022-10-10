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
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EntityHurtPlayerTrigger implements ICriterionTrigger<EntityHurtPlayerTrigger.Instance> {
   private static final ResourceLocation field_192201_a = new ResourceLocation("entity_hurt_player");
   private final Map<PlayerAdvancements, EntityHurtPlayerTrigger.Listeners> field_192202_b = Maps.newHashMap();

   public EntityHurtPlayerTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192201_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance> var2) {
      EntityHurtPlayerTrigger.Listeners var3 = (EntityHurtPlayerTrigger.Listeners)this.field_192202_b.get(var1);
      if (var3 == null) {
         var3 = new EntityHurtPlayerTrigger.Listeners(var1);
         this.field_192202_b.put(var1, var3);
      }

      var3.func_192477_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance> var2) {
      EntityHurtPlayerTrigger.Listeners var3 = (EntityHurtPlayerTrigger.Listeners)this.field_192202_b.get(var1);
      if (var3 != null) {
         var3.func_192475_b(var2);
         if (var3.func_192476_a()) {
            this.field_192202_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192202_b.remove(var1);
   }

   public EntityHurtPlayerTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      DamagePredicate var3 = DamagePredicate.func_192364_a(var1.get("damage"));
      return new EntityHurtPlayerTrigger.Instance(var3);
   }

   public void func_192200_a(EntityPlayerMP var1, DamageSource var2, float var3, float var4, boolean var5) {
      EntityHurtPlayerTrigger.Listeners var6 = (EntityHurtPlayerTrigger.Listeners)this.field_192202_b.get(var1.func_192039_O());
      if (var6 != null) {
         var6.func_192478_a(var1, var2, var3, var4, var5);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192479_a;
      private final Set<ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance>> field_192480_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192479_a = var1;
      }

      public boolean func_192476_a() {
         return this.field_192480_b.isEmpty();
      }

      public void func_192477_a(ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance> var1) {
         this.field_192480_b.add(var1);
      }

      public void func_192475_b(ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance> var1) {
         this.field_192480_b.remove(var1);
      }

      public void func_192478_a(EntityPlayerMP var1, DamageSource var2, float var3, float var4, boolean var5) {
         ArrayList var6 = null;
         Iterator var7 = this.field_192480_b.iterator();

         ICriterionTrigger.Listener var8;
         while(var7.hasNext()) {
            var8 = (ICriterionTrigger.Listener)var7.next();
            if (((EntityHurtPlayerTrigger.Instance)var8.func_192158_a()).func_192263_a(var1, var2, var3, var4, var5)) {
               if (var6 == null) {
                  var6 = Lists.newArrayList();
               }

               var6.add(var8);
            }
         }

         if (var6 != null) {
            var7 = var6.iterator();

            while(var7.hasNext()) {
               var8 = (ICriterionTrigger.Listener)var7.next();
               var8.func_192159_a(this.field_192479_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final DamagePredicate field_192264_a;

      public Instance(DamagePredicate var1) {
         super(EntityHurtPlayerTrigger.field_192201_a);
         this.field_192264_a = var1;
      }

      public static EntityHurtPlayerTrigger.Instance func_203921_a(DamagePredicate.Builder var0) {
         return new EntityHurtPlayerTrigger.Instance(var0.func_203970_b());
      }

      public boolean func_192263_a(EntityPlayerMP var1, DamageSource var2, float var3, float var4, boolean var5) {
         return this.field_192264_a.func_192365_a(var1, var2, var3, var4, var5);
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("damage", this.field_192264_a.func_203977_a());
         return var1;
      }
   }
}
