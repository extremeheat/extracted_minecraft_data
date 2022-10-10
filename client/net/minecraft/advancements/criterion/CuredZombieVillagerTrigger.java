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
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class CuredZombieVillagerTrigger implements ICriterionTrigger<CuredZombieVillagerTrigger.Instance> {
   private static final ResourceLocation field_192186_a = new ResourceLocation("cured_zombie_villager");
   private final Map<PlayerAdvancements, CuredZombieVillagerTrigger.Listeners> field_192187_b = Maps.newHashMap();

   public CuredZombieVillagerTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192186_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> var2) {
      CuredZombieVillagerTrigger.Listeners var3 = (CuredZombieVillagerTrigger.Listeners)this.field_192187_b.get(var1);
      if (var3 == null) {
         var3 = new CuredZombieVillagerTrigger.Listeners(var1);
         this.field_192187_b.put(var1, var3);
      }

      var3.func_192360_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> var2) {
      CuredZombieVillagerTrigger.Listeners var3 = (CuredZombieVillagerTrigger.Listeners)this.field_192187_b.get(var1);
      if (var3 != null) {
         var3.func_192358_b(var2);
         if (var3.func_192359_a()) {
            this.field_192187_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192187_b.remove(var1);
   }

   public CuredZombieVillagerTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.func_192481_a(var1.get("zombie"));
      EntityPredicate var4 = EntityPredicate.func_192481_a(var1.get("villager"));
      return new CuredZombieVillagerTrigger.Instance(var3, var4);
   }

   public void func_192183_a(EntityPlayerMP var1, EntityZombie var2, EntityVillager var3) {
      CuredZombieVillagerTrigger.Listeners var4 = (CuredZombieVillagerTrigger.Listeners)this.field_192187_b.get(var1.func_192039_O());
      if (var4 != null) {
         var4.func_192361_a(var1, var2, var3);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192362_a;
      private final Set<ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance>> field_192363_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192362_a = var1;
      }

      public boolean func_192359_a() {
         return this.field_192363_b.isEmpty();
      }

      public void func_192360_a(ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> var1) {
         this.field_192363_b.add(var1);
      }

      public void func_192358_b(ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> var1) {
         this.field_192363_b.remove(var1);
      }

      public void func_192361_a(EntityPlayerMP var1, EntityZombie var2, EntityVillager var3) {
         ArrayList var4 = null;
         Iterator var5 = this.field_192363_b.iterator();

         ICriterionTrigger.Listener var6;
         while(var5.hasNext()) {
            var6 = (ICriterionTrigger.Listener)var5.next();
            if (((CuredZombieVillagerTrigger.Instance)var6.func_192158_a()).func_192254_a(var1, var2, var3)) {
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
               var6.func_192159_a(this.field_192362_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate field_192255_a;
      private final EntityPredicate field_192256_b;

      public Instance(EntityPredicate var1, EntityPredicate var2) {
         super(CuredZombieVillagerTrigger.field_192186_a);
         this.field_192255_a = var1;
         this.field_192256_b = var2;
      }

      public static CuredZombieVillagerTrigger.Instance func_203916_c() {
         return new CuredZombieVillagerTrigger.Instance(EntityPredicate.field_192483_a, EntityPredicate.field_192483_a);
      }

      public boolean func_192254_a(EntityPlayerMP var1, EntityZombie var2, EntityVillager var3) {
         if (!this.field_192255_a.func_192482_a(var1, var2)) {
            return false;
         } else {
            return this.field_192256_b.func_192482_a(var1, var3);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("zombie", this.field_192255_a.func_204006_a());
         var1.add("villager", this.field_192256_b.func_204006_a());
         return var1;
      }
   }
}
