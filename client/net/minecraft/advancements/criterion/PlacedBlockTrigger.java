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
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldServer;

public class PlacedBlockTrigger implements ICriterionTrigger<PlacedBlockTrigger.Instance> {
   private static final ResourceLocation field_193174_a = new ResourceLocation("placed_block");
   private final Map<PlayerAdvancements, PlacedBlockTrigger.Listeners> field_193175_b = Maps.newHashMap();

   public PlacedBlockTrigger() {
      super();
   }

   public ResourceLocation func_192163_a() {
      return field_193174_a;
   }

   public void func_192165_a(PlayerAdvancements var1, ICriterionTrigger.Listener<PlacedBlockTrigger.Instance> var2) {
      PlacedBlockTrigger.Listeners var3 = (PlacedBlockTrigger.Listeners)this.field_193175_b.get(var1);
      if (var3 == null) {
         var3 = new PlacedBlockTrigger.Listeners(var1);
         this.field_193175_b.put(var1, var3);
      }

      var3.func_193490_a(var2);
   }

   public void func_192164_b(PlayerAdvancements var1, ICriterionTrigger.Listener<PlacedBlockTrigger.Instance> var2) {
      PlacedBlockTrigger.Listeners var3 = (PlacedBlockTrigger.Listeners)this.field_193175_b.get(var1);
      if (var3 != null) {
         var3.func_193487_b(var2);
         if (var3.func_193488_a()) {
            this.field_193175_b.remove(var1);
         }
      }

   }

   public void func_192167_a(PlayerAdvancements var1) {
      this.field_193175_b.remove(var1);
   }

   public PlacedBlockTrigger.Instance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
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

      LocationPredicate var12 = LocationPredicate.func_193454_a(var1.get("location"));
      ItemPredicate var13 = ItemPredicate.func_192492_a(var1.get("item"));
      return new PlacedBlockTrigger.Instance(var3, var11, var12, var13);
   }

   public void func_193173_a(EntityPlayerMP var1, BlockPos var2, ItemStack var3) {
      IBlockState var4 = var1.field_70170_p.func_180495_p(var2);
      PlacedBlockTrigger.Listeners var5 = (PlacedBlockTrigger.Listeners)this.field_193175_b.get(var1.func_192039_O());
      if (var5 != null) {
         var5.func_193489_a(var4, var2, var1.func_71121_q(), var3);
      }

   }

   // $FF: synthetic method
   public ICriterionInstance func_192166_a(JsonObject var1, JsonDeserializationContext var2) {
      return this.func_192166_a(var1, var2);
   }

   static class Listeners {
      private final PlayerAdvancements field_193491_a;
      private final Set<ICriterionTrigger.Listener<PlacedBlockTrigger.Instance>> field_193492_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements var1) {
         super();
         this.field_193491_a = var1;
      }

      public boolean func_193488_a() {
         return this.field_193492_b.isEmpty();
      }

      public void func_193490_a(ICriterionTrigger.Listener<PlacedBlockTrigger.Instance> var1) {
         this.field_193492_b.add(var1);
      }

      public void func_193487_b(ICriterionTrigger.Listener<PlacedBlockTrigger.Instance> var1) {
         this.field_193492_b.remove(var1);
      }

      public void func_193489_a(IBlockState var1, BlockPos var2, WorldServer var3, ItemStack var4) {
         ArrayList var5 = null;
         Iterator var6 = this.field_193492_b.iterator();

         ICriterionTrigger.Listener var7;
         while(var6.hasNext()) {
            var7 = (ICriterionTrigger.Listener)var6.next();
            if (((PlacedBlockTrigger.Instance)var7.func_192158_a()).func_193210_a(var1, var2, var3, var4)) {
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
               var7.func_192159_a(this.field_193491_a);
            }
         }

      }
   }

   public static class Instance extends AbstractCriterionInstance {
      private final Block field_193211_a;
      private final Map<IProperty<?>, Object> field_193212_b;
      private final LocationPredicate field_193213_c;
      private final ItemPredicate field_193214_d;

      public Instance(@Nullable Block var1, @Nullable Map<IProperty<?>, Object> var2, LocationPredicate var3, ItemPredicate var4) {
         super(PlacedBlockTrigger.field_193174_a);
         this.field_193211_a = var1;
         this.field_193212_b = var2;
         this.field_193213_c = var3;
         this.field_193214_d = var4;
      }

      public static PlacedBlockTrigger.Instance func_203934_a(Block var0) {
         return new PlacedBlockTrigger.Instance(var0, (Map)null, LocationPredicate.field_193455_a, ItemPredicate.field_192495_a);
      }

      public boolean func_193210_a(IBlockState var1, BlockPos var2, WorldServer var3, ItemStack var4) {
         if (this.field_193211_a != null && var1.func_177230_c() != this.field_193211_a) {
            return false;
         } else {
            if (this.field_193212_b != null) {
               Iterator var5 = this.field_193212_b.entrySet().iterator();

               while(var5.hasNext()) {
                  Entry var6 = (Entry)var5.next();
                  if (var1.func_177229_b((IProperty)var6.getKey()) != var6.getValue()) {
                     return false;
                  }
               }
            }

            if (!this.field_193213_c.func_193453_a(var3, (float)var2.func_177958_n(), (float)var2.func_177956_o(), (float)var2.func_177952_p())) {
               return false;
            } else {
               return this.field_193214_d.func_192493_a(var4);
            }
         }
      }

      public JsonElement func_200288_b() {
         JsonObject var1 = new JsonObject();
         if (this.field_193211_a != null) {
            var1.addProperty("block", IRegistry.field_212618_g.func_177774_c(this.field_193211_a).toString());
         }

         if (this.field_193212_b != null) {
            JsonObject var2 = new JsonObject();
            Iterator var3 = this.field_193212_b.entrySet().iterator();

            while(var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               var2.addProperty(((IProperty)var4.getKey()).func_177701_a(), Util.func_200269_a((IProperty)var4.getKey(), var4.getValue()));
            }

            var1.add("state", var2);
         }

         var1.add("location", this.field_193213_c.func_204009_a());
         var1.add("item", this.field_193214_d.func_200319_a());
         return var1;
      }
   }
}
