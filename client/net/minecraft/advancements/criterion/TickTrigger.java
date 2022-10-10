package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class TickTrigger implements ICriterionTrigger<TickTrigger.Instance> {
   public static final ResourceLocation field_193183_a = new ResourceLocation("tick");
   private final Map<PlayerAdvancements, TickTrigger.Listeners> field_193184_b = Maps.newHashMap();

   public TickTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_193183_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<TickTrigger.Instance> var2) {
      TickTrigger.Listeners var3 = (TickTrigger.Listeners)this.field_193184_b.get(var1);
      if (var3 == null) {
         var3 = new TickTrigger.Listeners(var1);
         this.field_193184_b.put(var1, var3);
      }

      var3.func_193502_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<TickTrigger.Instance> var2) {
      TickTrigger.Listeners var3 = (TickTrigger.Listeners)this.field_193184_b.get(var1);
      if (var3 != null) {
         var3.func_193500_b(var2);
         if (var3.func_193501_a()) {
            this.field_193184_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_193184_b.remove(var1);
   }

   public TickTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return new TickTrigger.Instance();
   }

   public void func_193182_a(EntityPlayerMP var1) {
      TickTrigger.Listeners var2 = (TickTrigger.Listeners)this.field_193184_b.get(var1.func_192039_O());
      if (var2 != null) {
         var2.func_193503_b();
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_193504_a;
      private final Set<ICriterionTrigger.Listener<TickTrigger.Instance>> field_193505_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_193504_a = var1;
      }

      public boolean func_193501_a() {
         return this.field_193505_b.isEmpty();
      }

      public void func_193502_a(ICriterionTrigger.Listener<TickTrigger.Instance> var1) {
         this.field_193505_b.add(var1);
      }

      public void func_193500_b(ICriterionTrigger.Listener<TickTrigger.Instance> var1) {
         this.field_193505_b.remove(var1);
      }

      public void func_193503_b() {
         Iterator var1 = Lists.newArrayList(this.field_193505_b).iterator();

         while(var1.hasNext()) {
            ICriterionTrigger.Listener var2 = (ICriterionTrigger.Listener)var1.next();
            var2.func_192159_a(this.field_193504_a);
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      public Instance() {
         super(TickTrigger.field_193183_a);
      }
   }
}
