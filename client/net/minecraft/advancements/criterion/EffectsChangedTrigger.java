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

public class EffectsChangedTrigger implements ICriterionTrigger<EffectsChangedTrigger.Instance> {
   private static final ResourceLocation field_193154_a = new ResourceLocation("effects_changed");
   private final Map<PlayerAdvancements, EffectsChangedTrigger.Listeners> field_193155_b = Maps.newHashMap();

   public EffectsChangedTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_193154_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> var2) {
      EffectsChangedTrigger.Listeners var3 = (EffectsChangedTrigger.Listeners)this.field_193155_b.get(var1);
      if (var3 == null) {
         var3 = new EffectsChangedTrigger.Listeners(var1);
         this.field_193155_b.put(var1, var3);
      }

      var3.func_193431_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> var2) {
      EffectsChangedTrigger.Listeners var3 = (EffectsChangedTrigger.Listeners)this.field_193155_b.get(var1);
      if (var3 != null) {
         var3.func_193429_b(var2);
         if (var3.func_193430_a()) {
            this.field_193155_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_193155_b.remove(var1);
   }

   public EffectsChangedTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      MobEffectsPredicate var3 = MobEffectsPredicate.func_193471_a(var1.get("effects"));
      return new EffectsChangedTrigger.Instance(var3);
   }

   public void func_193153_a(EntityPlayerMP var1) {
      EffectsChangedTrigger.Listeners var2 = (EffectsChangedTrigger.Listeners)this.field_193155_b.get(var1.func_192039_O());
      if (var2 != null) {
         var2.func_193432_a(var1);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_193433_a;
      private final Set<ICriterionTrigger.Listener<EffectsChangedTrigger.Instance>> field_193434_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_193433_a = var1;
      }

      public boolean func_193430_a() {
         return this.field_193434_b.isEmpty();
      }

      public void func_193431_a(ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> var1) {
         this.field_193434_b.add(var1);
      }

      public void func_193429_b(ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> var1) {
         this.field_193434_b.remove(var1);
      }

      public void func_193432_a(EntityPlayerMP var1) {
         ArrayList var2 = null;
         Iterator var3 = this.field_193434_b.iterator();

         ICriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (ICriterionTrigger.Listener)var3.next();
            if (((EffectsChangedTrigger.Instance)var4.func_192158_a()).func_193195_a(var1)) {
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
               var4.func_192159_a(this.field_193433_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final MobEffectsPredicate field_193196_a;

      public Instance(MobEffectsPredicate var1) {
         super(EffectsChangedTrigger.field_193154_a);
         this.field_193196_a = var1;
      }

      public static EffectsChangedTrigger.Instance func_203917_a(MobEffectsPredicate var0) {
         return new EffectsChangedTrigger.Instance(var0);
      }

      public boolean func_193195_a(EntityPlayerMP var1) {
         return this.field_193196_a.func_193472_a(var1);
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("effects", this.field_193196_a.func_204013_b());
         return var1;
      }
   }
}
