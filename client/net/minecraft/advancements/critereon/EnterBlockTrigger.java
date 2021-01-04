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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class EnterBlockTrigger implements CriterionTrigger<EnterBlockTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("enter_block");
   private final Map<PlayerAdvancements, EnterBlockTrigger.PlayerListeners> players = Maps.newHashMap();

   public EnterBlockTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<EnterBlockTrigger.TriggerInstance> var2) {
      EnterBlockTrigger.PlayerListeners var3 = (EnterBlockTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new EnterBlockTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<EnterBlockTrigger.TriggerInstance> var2) {
      EnterBlockTrigger.PlayerListeners var3 = (EnterBlockTrigger.PlayerListeners)this.players.get(var1);
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

   public EnterBlockTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
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

      return new EnterBlockTrigger.TriggerInstance(var3, var11);
   }

   public void trigger(ServerPlayer var1, BlockState var2) {
      EnterBlockTrigger.PlayerListeners var3 = (EnterBlockTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var3 != null) {
         var3.trigger(var2);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<EnterBlockTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<EnterBlockTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<EnterBlockTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(BlockState var1) {
         ArrayList var2 = null;
         Iterator var3 = this.listeners.iterator();

         CriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (CriterionTrigger.Listener)var3.next();
            if (((EnterBlockTrigger.TriggerInstance)var4.getTriggerInstance()).matches(var1)) {
               if (var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if (var2 != null) {
            var3 = var2.iterator();

            while(var3.hasNext()) {
               var4 = (CriterionTrigger.Listener)var3.next();
               var4.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Block block;
      private final Map<Property<?>, Object> state;

      public TriggerInstance(@Nullable Block var1, @Nullable Map<Property<?>, Object> var2) {
         super(EnterBlockTrigger.ID);
         this.block = var1;
         this.state = var2;
      }

      public static EnterBlockTrigger.TriggerInstance entersBlock(Block var0) {
         return new EnterBlockTrigger.TriggerInstance(var0, (Map)null);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if (this.block != null) {
            var1.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
            if (this.state != null && !this.state.isEmpty()) {
               JsonObject var2 = new JsonObject();
               Iterator var3 = this.state.entrySet().iterator();

               while(var3.hasNext()) {
                  Entry var4 = (Entry)var3.next();
                  var2.addProperty(((Property)var4.getKey()).getName(), Util.getPropertyName((Property)var4.getKey(), var4.getValue()));
               }

               var1.add("state", var2);
            }
         }

         return var1;
      }

      public boolean matches(BlockState var1) {
         if (this.block != null && var1.getBlock() != this.block) {
            return false;
         } else {
            if (this.state != null) {
               Iterator var2 = this.state.entrySet().iterator();

               while(var2.hasNext()) {
                  Entry var3 = (Entry)var2.next();
                  if (var1.getValue((Property)var3.getKey()) != var3.getValue()) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }
}
