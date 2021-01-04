package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMapFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final MapDecoration.Type DEFAULT_DECORATION;
   private final String destination;
   private final MapDecoration.Type mapDecoration;
   private final byte zoom;
   private final int searchRadius;
   private final boolean skipKnownStructures;

   private ExplorationMapFunction(LootItemCondition[] var1, String var2, MapDecoration.Type var3, byte var4, int var5, boolean var6) {
      super(var1);
      this.destination = var2;
      this.mapDecoration = var3;
      this.zoom = var4;
      this.searchRadius = var5;
      this.skipKnownStructures = var6;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_POS);
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.getItem() != Items.MAP) {
         return var1;
      } else {
         BlockPos var3 = (BlockPos)var2.getParamOrNull(LootContextParams.BLOCK_POS);
         if (var3 != null) {
            ServerLevel var4 = var2.getLevel();
            BlockPos var5 = var4.findNearestMapFeature(this.destination, var3, this.searchRadius, this.skipKnownStructures);
            if (var5 != null) {
               ItemStack var6 = MapItem.create(var4, var5.getX(), var5.getZ(), this.zoom, true, true);
               MapItem.renderBiomePreviewMap(var4, var6);
               MapItemSavedData.addTargetDecoration(var6, var5, "+", this.mapDecoration);
               var6.setHoverName(new TranslatableComponent("filled_map." + this.destination.toLowerCase(Locale.ROOT), new Object[0]));
               return var6;
            }
         }

         return var1;
      }
   }

   public static ExplorationMapFunction.Builder makeExplorationMap() {
      return new ExplorationMapFunction.Builder();
   }

   // $FF: synthetic method
   ExplorationMapFunction(LootItemCondition[] var1, String var2, MapDecoration.Type var3, byte var4, int var5, boolean var6, Object var7) {
      this(var1, var2, var3, var4, var5, var6);
   }

   static {
      DEFAULT_DECORATION = MapDecoration.Type.MANSION;
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<ExplorationMapFunction> {
      protected Serializer() {
         super(new ResourceLocation("exploration_map"), ExplorationMapFunction.class);
      }

      public void serialize(JsonObject var1, ExplorationMapFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         if (!var2.destination.equals("Buried_Treasure")) {
            var1.add("destination", var3.serialize(var2.destination));
         }

         if (var2.mapDecoration != ExplorationMapFunction.DEFAULT_DECORATION) {
            var1.add("decoration", var3.serialize(var2.mapDecoration.toString().toLowerCase(Locale.ROOT)));
         }

         if (var2.zoom != 2) {
            var1.addProperty("zoom", var2.zoom);
         }

         if (var2.searchRadius != 50) {
            var1.addProperty("search_radius", var2.searchRadius);
         }

         if (!var2.skipKnownStructures) {
            var1.addProperty("skip_existing_chunks", var2.skipKnownStructures);
         }

      }

      public ExplorationMapFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         String var4 = var1.has("destination") ? GsonHelper.getAsString(var1, "destination") : "Buried_Treasure";
         var4 = Feature.STRUCTURES_REGISTRY.containsKey(var4.toLowerCase(Locale.ROOT)) ? var4 : "Buried_Treasure";
         String var5 = var1.has("decoration") ? GsonHelper.getAsString(var1, "decoration") : "mansion";
         MapDecoration.Type var6 = ExplorationMapFunction.DEFAULT_DECORATION;

         try {
            var6 = MapDecoration.Type.valueOf(var5.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var10) {
            ExplorationMapFunction.LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + ExplorationMapFunction.DEFAULT_DECORATION, var5);
         }

         byte var7 = GsonHelper.getAsByte(var1, "zoom", (byte)2);
         int var8 = GsonHelper.getAsInt(var1, "search_radius", 50);
         boolean var9 = GsonHelper.getAsBoolean(var1, "skip_existing_chunks", true);
         return new ExplorationMapFunction(var3, var4, var6, var7, var8, var9);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder<ExplorationMapFunction.Builder> {
      private String destination = "Buried_Treasure";
      private MapDecoration.Type mapDecoration;
      private byte zoom;
      private int searchRadius;
      private boolean skipKnownStructures;

      public Builder() {
         super();
         this.mapDecoration = ExplorationMapFunction.DEFAULT_DECORATION;
         this.zoom = 2;
         this.searchRadius = 50;
         this.skipKnownStructures = true;
      }

      protected ExplorationMapFunction.Builder getThis() {
         return this;
      }

      public ExplorationMapFunction.Builder setDestination(String var1) {
         this.destination = var1;
         return this;
      }

      public ExplorationMapFunction.Builder setMapDecoration(MapDecoration.Type var1) {
         this.mapDecoration = var1;
         return this;
      }

      public ExplorationMapFunction.Builder setZoom(byte var1) {
         this.zoom = var1;
         return this;
      }

      public ExplorationMapFunction.Builder setSkipKnownStructures(boolean var1) {
         this.skipKnownStructures = var1;
         return this;
      }

      public LootItemFunction build() {
         return new ExplorationMapFunction(this.getConditions(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
