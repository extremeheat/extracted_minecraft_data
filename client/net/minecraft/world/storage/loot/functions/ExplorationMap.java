package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMap extends LootFunction {
   private static final Logger field_204317_a = LogManager.getLogger();
   private final String field_204318_b;
   private final MapDecoration.Type field_204319_c;
   private final byte field_204320_d;
   private final int field_204321_e;
   private final boolean field_212428_f;

   public ExplorationMap(LootCondition[] var1, String var2, MapDecoration.Type var3, byte var4, int var5, boolean var6) {
      super(var1);
      this.field_204318_b = var2;
      this.field_204319_c = var3;
      this.field_204320_d = var4;
      this.field_204321_e = var5;
      this.field_212428_f = var6;
   }

   public ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3) {
      if (var1.func_77973_b() != Items.field_151148_bJ) {
         return var1;
      } else {
         BlockPos var4 = var3.func_204315_e();
         if (var4 == null) {
            return var1;
         } else {
            WorldServer var5 = var3.func_202879_g();
            BlockPos var6 = var5.func_211157_a(this.field_204318_b, var4, this.field_204321_e, this.field_212428_f);
            if (var6 != null) {
               ItemStack var7 = ItemMap.func_195952_a(var5, var6.func_177958_n(), var6.func_177952_p(), this.field_204320_d, true, true);
               ItemMap.func_190905_a(var5, var7);
               MapData.func_191094_a(var7, var6, "+", this.field_204319_c);
               var7.func_200302_a(new TextComponentTranslation("filled_map." + this.field_204318_b.toLowerCase(Locale.ROOT), new Object[0]));
               return var7;
            } else {
               return var1;
            }
         }
      }
   }

   public static class Serializer extends LootFunction.Serializer<ExplorationMap> {
      protected Serializer() {
         super(new ResourceLocation("exploration_map"), ExplorationMap.class);
      }

      public void func_186532_a(JsonObject var1, ExplorationMap var2, JsonSerializationContext var3) {
         var1.add("destination", var3.serialize(var2.field_204318_b));
         var1.add("decoration", var3.serialize(var2.field_204319_c.toString().toLowerCase(Locale.ROOT)));
      }

      public ExplorationMap func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         String var4 = var1.has("destination") ? JsonUtils.func_151200_h(var1, "destination") : "Buried_Treasure";
         var4 = Feature.field_202300_at.containsKey(var4.toLowerCase(Locale.ROOT)) ? var4 : "Buried_Treasure";
         String var5 = var1.has("decoration") ? JsonUtils.func_151200_h(var1, "decoration") : "mansion";
         MapDecoration.Type var6 = MapDecoration.Type.MANSION;

         try {
            var6 = MapDecoration.Type.valueOf(var5.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var10) {
            ExplorationMap.field_204317_a.error("Error while parsing loot table decoration entry. Found {}. Defaulting to MANSION", var5);
         }

         byte var7 = var1.has("zoom") ? JsonUtils.func_204331_o(var1, "zoom") : 2;
         int var8 = var1.has("search_radius") ? JsonUtils.func_151203_m(var1, "search_radius") : 50;
         boolean var9 = var1.has("skip_existing_chunks") ? JsonUtils.func_151212_i(var1, "skip_existing_chunks") : true;
         return new ExplorationMap(var3, var4, var6, var7, var8, var9);
      }

      // $FF: synthetic method
      public LootFunction func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return this.func_186530_b(var1, var2, var3);
      }
   }
}
