package net.minecraft.advancements.critereon;

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
import net.minecraft.Util;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class PlacedBlockTrigger implements CriterionTrigger<PlacedBlockTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("placed_block");
   private final Map<PlayerAdvancements, PlacedBlockTrigger.PlayerListeners> players = Maps.newHashMap();

   public PlacedBlockTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<PlacedBlockTrigger.TriggerInstance> var2) {
      PlacedBlockTrigger.PlayerListeners var3 = (PlacedBlockTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new PlacedBlockTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<PlacedBlockTrigger.TriggerInstance> var2) {
      PlacedBlockTrigger.PlayerListeners var3 = (PlacedBlockTrigger.PlayerListeners)this.players.get(var1);
      if (var3 != null) {
         var3.removeListener(var2);
         if (var3.isEmpty()) {
            this.players.remove(var1);
         }
      }

   }

   public void removePlayerListeners(PlayerAdvancements var1) {
      this.players.remove(var1);
   }

   public PlacedBlockTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      Block var3 = null;
      if (var1.has("block")) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "block"));
         var3 = (Block)Registry.BLOCK.getOptional(var4).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + var4 + "'");
         });
      }

      HashMap var11 = null;
      if (var1.has("state")) {
         if (var3 == null) {
            throw new JsonSyntaxException("Can't define block state without a specific block type");
         }

         StateDefinition var5 = var3.getStateDefinition();

         Property var8;
         Optional var10;
         for(Iterator var6 = GsonHelper.getAsJsonObject(var1, "state").entrySet().iterator(); var6.hasNext(); var11.put(var8, var10.get())) {
            Entry var7 = (Entry)var6.next();
            var8 = var5.getProperty((String)var7.getKey());
            if (var8 == null) {
               throw new JsonSyntaxException("Unknown block state property '" + (String)var7.getKey() + "' for block '" + Registry.BLOCK.getKey(var3) + "'");
            }

            String var9 = GsonHelper.convertToString((JsonElement)var7.getValue(), (String)var7.getKey());
            var10 = var8.getValue(var9);
            if (!var10.isPresent()) {
               throw new JsonSyntaxException("Invalid block state value '" + var9 + "' for property '" + (String)var7.getKey() + "' on block '" + Registry.BLOCK.getKey(var3) + "'");
            }

            if (var11 == null) {
               var11 = Maps.newHashMap();
            }
         }
      }

      LocationPredicate var12 = LocationPredicate.fromJson(var1.get("location"));
      ItemPredicate var13 = ItemPredicate.fromJson(var1.get("item"));
      return new PlacedBlockTrigger.TriggerInstance(var3, var11, var12, var13);
   }

   public void trigger(ServerPlayer var1, BlockPos var2, ItemStack var3) {
      BlockState var4 = var1.level.getBlockState(var2);
      PlacedBlockTrigger.PlayerListeners var5 = (PlacedBlockTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var5 != null) {
         var5.trigger(var4, var2, var1.getLevel(), var3);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<PlacedBlockTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<PlacedBlockTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<PlacedBlockTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(BlockState var1, BlockPos var2, ServerLevel var3, ItemStack var4) {
         ArrayList var5 = null;
         Iterator var6 = this.listeners.iterator();

         CriterionTrigger.Listener var7;
         while(var6.hasNext()) {
            var7 = (CriterionTrigger.Listener)var6.next();
            if (((PlacedBlockTrigger.TriggerInstance)var7.getTriggerInstance()).matches(var1, var2, var3, var4)) {
               if (var5 == null) {
                  var5 = Lists.newArrayList();
               }

               var5.add(var7);
            }
         }

         if (var5 != null) {
            var6 = var5.iterator();

            while(var6.hasNext()) {
               var7 = (CriterionTrigger.Listener)var6.next();
               var7.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Block block;
      private final Map<Property<?>, Object> state;
      private final LocationPredicate location;
      private final ItemPredicate item;

      public TriggerInstance(@Nullable Block var1, @Nullable Map<Property<?>, Object> var2, LocationPredicate var3, ItemPredicate var4) {
         super(PlacedBlockTrigger.ID);
         this.block = var1;
         this.state = var2;
         this.location = var3;
         this.item = var4;
      }

      public static PlacedBlockTrigger.TriggerInstance placedBlock(Block var0) {
         return new PlacedBlockTrigger.TriggerInstance(var0, (Map)null, LocationPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(BlockState var1, BlockPos var2, ServerLevel var3, ItemStack var4) {
         if (this.block != null && var1.getBlock() != this.block) {
            return false;
         } else {
            if (this.state != null) {
               Iterator var5 = this.state.entrySet().iterator();

               while(var5.hasNext()) {
                  Entry var6 = (Entry)var5.next();
                  if (var1.getValue((Property)var6.getKey()) != var6.getValue()) {
                     return false;
                  }
               }
            }

            if (!this.location.matches(var3, (float)var2.getX(), (float)var2.getY(), (float)var2.getZ())) {
               return false;
            } else {
               return this.item.matches(var4);
            }
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if (this.block != null) {
            var1.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         if (this.state != null) {
            JsonObject var2 = new JsonObject();
            Iterator var3 = this.state.entrySet().iterator();

            while(var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               var2.addProperty(((Property)var4.getKey()).getName(), Util.getPropertyName((Property)var4.getKey(), var4.getValue()));
            }

            var1.add("state", var2);
         }

         var1.add("location", this.location.serializeToJson());
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
