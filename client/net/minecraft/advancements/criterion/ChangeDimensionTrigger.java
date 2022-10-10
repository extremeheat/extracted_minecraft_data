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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

public class ChangeDimensionTrigger implements ICriterionTrigger<ChangeDimensionTrigger.Instance> {
   private static final ResourceLocation field_193144_a = new ResourceLocation("changed_dimension");
   private final Map<PlayerAdvancements, ChangeDimensionTrigger.Listeners> field_193145_b = Maps.newHashMap();

   public ChangeDimensionTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_193144_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance> var2) {
      ChangeDimensionTrigger.Listeners var3 = (ChangeDimensionTrigger.Listeners)this.field_193145_b.get(var1);
      if (var3 == null) {
         var3 = new ChangeDimensionTrigger.Listeners(var1);
         this.field_193145_b.put(var1, var3);
      }

      var3.func_193233_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance> var2) {
      ChangeDimensionTrigger.Listeners var3 = (ChangeDimensionTrigger.Listeners)this.field_193145_b.get(var1);
      if (var3 != null) {
         var3.func_193231_b(var2);
         if (var3.func_193232_a()) {
            this.field_193145_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_193145_b.remove(var1);
   }

   public ChangeDimensionTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      DimensionType var3 = var1.has("from") ? DimensionType.func_193417_a(new ResourceLocation(JsonUtils.func_151200_h(var1, "from"))) : null;
      DimensionType var4 = var1.has("to") ? DimensionType.func_193417_a(new ResourceLocation(JsonUtils.func_151200_h(var1, "to"))) : null;
      return new ChangeDimensionTrigger.Instance(var3, var4);
   }

   public void func_193143_a(EntityPlayerMP var1, DimensionType var2, DimensionType var3) {
      ChangeDimensionTrigger.Listeners var4 = (ChangeDimensionTrigger.Listeners)this.field_193145_b.get(var1.func_192039_O());
      if (var4 != null) {
         var4.func_193234_a(var2, var3);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_193235_a;
      private final Set<ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance>> field_193236_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_193235_a = var1;
      }

      public boolean func_193232_a() {
         return this.field_193236_b.isEmpty();
      }

      public void func_193233_a(ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance> var1) {
         this.field_193236_b.add(var1);
      }

      public void func_193231_b(ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance> var1) {
         this.field_193236_b.remove(var1);
      }

      public void func_193234_a(DimensionType var1, DimensionType var2) {
         ArrayList var3 = null;
         Iterator var4 = this.field_193236_b.iterator();

         ICriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (ICriterionTrigger.Listener)var4.next();
            if (((ChangeDimensionTrigger.Instance)var5.func_192158_a()).func_193190_a(var1, var2)) {
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
               var5.func_192159_a(this.field_193235_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      @Nullable
      private final DimensionType field_193191_a;
      @Nullable
      private final DimensionType field_193192_b;

      public Instance(@Nullable DimensionType var1, @Nullable DimensionType var2) {
         super(ChangeDimensionTrigger.field_193144_a);
         this.field_193191_a = var1;
         this.field_193192_b = var2;
      }

      public static ChangeDimensionTrigger.Instance func_203911_a(DimensionType var0) {
         return new ChangeDimensionTrigger.Instance((DimensionType)null, var0);
      }

      public boolean func_193190_a(DimensionType var1, DimensionType var2) {
         if (this.field_193191_a != null && this.field_193191_a != var1) {
            return false;
         } else {
            return this.field_193192_b == null || this.field_193192_b == var2;
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         if (this.field_193191_a != null) {
            var1.addProperty("from", DimensionType.func_212678_a(this.field_193191_a).toString());
         }

         if (this.field_193192_b != null) {
            var1.addProperty("to", DimensionType.func_212678_a(this.field_193192_b).toString());
         }

         return var1;
      }
   }
}
