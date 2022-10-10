package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class ChanneledLightningTrigger implements ICriterionTrigger<ChanneledLightningTrigger.Instance> {
   private static final ResourceLocation field_204815_a = new ResourceLocation("channeled_lightning");
   private final Map<PlayerAdvancements, ChanneledLightningTrigger.Listeners> field_204816_b = Maps.newHashMap();

   public ChanneledLightningTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_204815_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance> var2) {
      ChanneledLightningTrigger.Listeners var3 = (ChanneledLightningTrigger.Listeners)this.field_204816_b.get(var1);
      if (var3 == null) {
         var3 = new ChanneledLightningTrigger.Listeners(var1);
         this.field_204816_b.put(var1, var3);
      }

      var3.func_204843_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance> var2) {
      ChanneledLightningTrigger.Listeners var3 = (ChanneledLightningTrigger.Listeners)this.field_204816_b.get(var1);
      if (var3 != null) {
         var3.func_204845_b(var2);
         if (var3.func_204844_a()) {
            this.field_204816_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_204816_b.remove(var1);
   }

   public ChanneledLightningTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate[] var3 = EntityPredicate.func_204849_b(var1.get("victims"));
      return new ChanneledLightningTrigger.Instance(var3);
   }

   public void func_204814_a(EntityPlayerMP var1, Collection<? extends Entity> var2) {
      ChanneledLightningTrigger.Listeners var3 = (ChanneledLightningTrigger.Listeners)this.field_204816_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_204846_a(var1, var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_204847_a;
      private final Set<ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance>> field_204848_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_204847_a = var1;
      }

      public boolean func_204844_a() {
         return this.field_204848_b.isEmpty();
      }

      public void func_204843_a(ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance> var1) {
         this.field_204848_b.add(var1);
      }

      public void func_204845_b(ICriterionTrigger.Listener<ChanneledLightningTrigger.Instance> var1) {
         this.field_204848_b.remove(var1);
      }

      public void func_204846_a(EntityPlayerMP var1, Collection<? extends Entity> var2) {
         ArrayList var3 = null;
         Iterator var4 = this.field_204848_b.iterator();

         ICriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (ICriterionTrigger.Listener)var4.next();
            if (((ChanneledLightningTrigger.Instance)var5.func_192158_a()).func_204823_a(var1, var2)) {
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
               var5.func_192159_a(this.field_204847_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate[] field_204825_a;

      public Instance(EntityPredicate[] var1) {
         super(ChanneledLightningTrigger.field_204815_a);
         this.field_204825_a = var1;
      }

      public static ChanneledLightningTrigger.Instance func_204824_a(EntityPredicate... var0) {
         return new ChanneledLightningTrigger.Instance(var0);
      }

      public boolean func_204823_a(EntityPlayerMP var1, Collection<? extends Entity> var2) {
         EntityPredicate[] var3 = this.field_204825_a;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EntityPredicate var6 = var3[var5];
            boolean var7 = false;
            Iterator var8 = var2.iterator();

            while(var8.hasNext()) {
               Entity var9 = (Entity)var8.next();
               if (var6.func_192482_a(var1, var9)) {
                  var7 = true;
                  break;
               }
            }

            if (!var7) {
               return false;
            }
         }

         return true;
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         var1.add("victims", EntityPredicate.func_204850_a(this.field_204825_a));
         return var1;
      }
   }
}
