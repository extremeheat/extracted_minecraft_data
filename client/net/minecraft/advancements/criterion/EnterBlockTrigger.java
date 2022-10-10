package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;

public class EnterBlockTrigger implements ICriterionTrigger<EnterBlockTrigger.Instance> {
   private static final ResourceLocation field_192196_a = new ResourceLocation("enter_block");
   private final Map<PlayerAdvancements, EnterBlockTrigger.Listeners> field_192197_b = Maps.newHashMap();

   public EnterBlockTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_192196_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<EnterBlockTrigger.Instance> var2) {
      EnterBlockTrigger.Listeners var3 = (EnterBlockTrigger.Listeners)this.field_192197_b.get(var1);
      if (var3 == null) {
         var3 = new EnterBlockTrigger.Listeners(var1);
         this.field_192197_b.put(var1, var3);
      }

      var3.func_192472_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<EnterBlockTrigger.Instance> var2) {
      EnterBlockTrigger.Listeners var3 = (EnterBlockTrigger.Listeners)this.field_192197_b.get(var1);
      if (var3 != null) {
         var3.func_192469_b(var2);
         if (var3.func_192470_a()) {
            this.field_192197_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_192197_b.remove(var1);
   }

   public EnterBlockTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      Block var3 = null;
      if (var1.has("block")) {
         ResourceLocation var4 = new ResourceLocation(JsonUtils.func_151200_h(var1, "block"));
         if (!IRegistry.field_212618_g.func_212607_c(var4)) {
            throw new JsonSyntaxException("Unknown block type '" + var4 + "'");
         }

         var3 = (Block)IRegistry.field_212618_g.func_82594_a(var4);
      }

      HashMap var11 = null;
      if (var1.has("state")) {
         if (var3 == null) {
            throw new JsonSyntaxException("Can't define block state without a specific block type");
         }

         StateContainer var5 = var3.func_176194_O();

         IProperty var8;
         Optional var10;
         for(Iterator var6 = JsonUtils.func_152754_s(var1, "state").entrySet().iterator(); var6.hasNext(); var11.put(var8, var10.get())) {
            Entry var7 = (Entry)var6.next();
            var8 = var5.func_185920_a((String)var7.getKey());
            if (var8 == null) {
               throw new JsonSyntaxException("Unknown block state property '" + (String)var7.getKey() + "' for block '" + IRegistry.field_212618_g.func_177774_c(var3) + "'");
            }

            String var9 = JsonUtils.func_151206_a((JsonElement)var7.getValue(), (String)var7.getKey());
            var10 = var8.func_185929_b(var9);
            if (!var10.isPresent()) {
               throw new JsonSyntaxException("Invalid block state value '" + var9 + "' for property '" + (String)var7.getKey() + "' on block '" + IRegistry.field_212618_g.func_177774_c(var3) + "'");
            }

            if (var11 == null) {
               var11 = Maps.newHashMap();
            }
         }
      }

      return new EnterBlockTrigger.Instance(var3, var11);
   }

   public void func_192193_a(EntityPlayerMP var1, IBlockState var2) {
      EnterBlockTrigger.Listeners var3 = (EnterBlockTrigger.Listeners)this.field_192197_b.get(var1.func_192039_O());
      if (var3 != null) {
         var3.func_192471_a(var2);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_192473_a;
      private final Set<ICriterionTrigger.Listener<EnterBlockTrigger.Instance>> field_192474_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_192473_a = var1;
      }

      public boolean func_192470_a() {
         return this.field_192474_b.isEmpty();
      }

      public void func_192472_a(ICriterionTrigger.Listener<EnterBlockTrigger.Instance> var1) {
         this.field_192474_b.add(var1);
      }

      public void func_192469_b(ICriterionTrigger.Listener<EnterBlockTrigger.Instance> var1) {
         this.field_192474_b.remove(var1);
      }

      public void func_192471_a(IBlockState var1) {
         ArrayList var2 = null;
         Iterator var3 = this.field_192474_b.iterator();

         ICriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (ICriterionTrigger.Listener)var3.next();
            if (((EnterBlockTrigger.Instance)var4.func_192158_a()).func_192260_a(var1)) {
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
               var4.func_192159_a(this.field_192473_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final Block field_192261_a;
      private final Map<IProperty<?>, Object> field_192262_b;

      public Instance(@Nullable Block var1, @Nullable Map<IProperty<?>, Object> var2) {
         super(EnterBlockTrigger.field_192196_a);
         this.field_192261_a = var1;
         this.field_192262_b = var2;
      }

      public static EnterBlockTrigger.Instance func_203920_a(Block var0) {
         return new EnterBlockTrigger.Instance(var0, (Map)null);
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         if (this.field_192261_a != null) {
            var1.addProperty("block", IRegistry.field_212618_g.func_177774_c(this.field_192261_a).toString());
            if (this.field_192262_b != null && !this.field_192262_b.isEmpty()) {
               JsonObject var2 = new JsonObject();
               Iterator var3 = this.field_192262_b.entrySet().iterator();

               while(var3.hasNext()) {
                  Entry var4 = (Entry)var3.next();
                  var2.addProperty(((IProperty)var4.getKey()).func_177701_a(), Util.func_200269_a((IProperty)var4.getKey(), var4.getValue()));
               }

               var1.add("state", var2);
            }
         }

         return var1;
      }

      public boolean func_192260_a(IBlockState var1) {
         if (this.field_192261_a != null && var1.func_177230_c() != this.field_192261_a) {
            return false;
         } else {
            if (this.field_192262_b != null) {
               Iterator var2 = this.field_192262_b.entrySet().iterator();

               while(var2.hasNext()) {
                  Entry var3 = (Entry)var2.next();
                  if (var1.func_177229_b((IProperty)var3.getKey()) != var3.getValue()) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }
}
