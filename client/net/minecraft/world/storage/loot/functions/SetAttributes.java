package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetAttributes extends LootFunction {
   private static final Logger field_186560_a = LogManager.getLogger();
   private final SetAttributes.Modifier[] field_186561_b;

   public SetAttributes(LootCondition[] var1, SetAttributes.Modifier[] var2) {
      super(var1);
      this.field_186561_b = var2;
   }

   public ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3) {
      SetAttributes.Modifier[] var4 = this.field_186561_b;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         SetAttributes.Modifier var7 = var4[var6];
         UUID var8 = var7.field_186600_e;
         if (var8 == null) {
            var8 = UUID.randomUUID();
         }

         EntityEquipmentSlot var9 = var7.field_186601_f[var2.nextInt(var7.field_186601_f.length)];
         var1.func_185129_a(var7.field_186597_b, new AttributeModifier(var8, var7.field_186596_a, (double)var7.field_186599_d.func_186507_b(var2), var7.field_186598_c), var9);
      }

      return var1;
   }

   static class Modifier {
      private final String field_186596_a;
      private final String field_186597_b;
      private final int field_186598_c;
      private final RandomValueRange field_186599_d;
      @Nullable
      private final UUID field_186600_e;
      private final EntityEquipmentSlot[] field_186601_f;

      private Modifier(String var1, String var2, int var3, RandomValueRange var4, EntityEquipmentSlot[] var5, @Nullable UUID var6) {
         super();
         this.field_186596_a = var1;
         this.field_186597_b = var2;
         this.field_186598_c = var3;
         this.field_186599_d = var4;
         this.field_186600_e = var6;
         this.field_186601_f = var5;
      }

      public JsonObject func_186592_a(JsonSerializationContext var1) {
         JsonObject var2 = new JsonObject();
         var2.addProperty("name", this.field_186596_a);
         var2.addProperty("attribute", this.field_186597_b);
         var2.addProperty("operation", func_186594_a(this.field_186598_c));
         var2.add("amount", var1.serialize(this.field_186599_d));
         if (this.field_186600_e != null) {
            var2.addProperty("id", this.field_186600_e.toString());
         }

         if (this.field_186601_f.length == 1) {
            var2.addProperty("slot", this.field_186601_f[0].func_188450_d());
         } else {
            JsonArray var3 = new JsonArray();
            EntityEquipmentSlot[] var4 = this.field_186601_f;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               EntityEquipmentSlot var7 = var4[var6];
               var3.add(new JsonPrimitive(var7.func_188450_d()));
            }

            var2.add("slot", var3);
         }

         return var2;
      }

      public static SetAttributes.Modifier func_186586_a(JsonObject var0, JsonDeserializationContext var1) {
         String var2 = JsonUtils.func_151200_h(var0, "name");
         String var3 = JsonUtils.func_151200_h(var0, "attribute");
         int var4 = func_186595_a(JsonUtils.func_151200_h(var0, "operation"));
         RandomValueRange var5 = (RandomValueRange)JsonUtils.func_188174_a(var0, "amount", var1, RandomValueRange.class);
         UUID var7 = null;
         EntityEquipmentSlot[] var6;
         if (JsonUtils.func_151205_a(var0, "slot")) {
            var6 = new EntityEquipmentSlot[]{EntityEquipmentSlot.func_188451_a(JsonUtils.func_151200_h(var0, "slot"))};
         } else {
            if (!JsonUtils.func_151202_d(var0, "slot")) {
               throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
            }

            JsonArray var8 = JsonUtils.func_151214_t(var0, "slot");
            var6 = new EntityEquipmentSlot[var8.size()];
            int var9 = 0;

            JsonElement var11;
            for(Iterator var10 = var8.iterator(); var10.hasNext(); var6[var9++] = EntityEquipmentSlot.func_188451_a(JsonUtils.func_151206_a(var11, "slot"))) {
               var11 = (JsonElement)var10.next();
            }

            if (var6.length == 0) {
               throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
            }
         }

         if (var0.has("id")) {
            String var13 = JsonUtils.func_151200_h(var0, "id");

            try {
               var7 = UUID.fromString(var13);
            } catch (IllegalArgumentException var12) {
               throw new JsonSyntaxException("Invalid attribute modifier id '" + var13 + "' (must be UUID format, with dashes)");
            }
         }

         return new SetAttributes.Modifier(var2, var3, var4, var5, var6, var7);
      }

      private static String func_186594_a(int var0) {
         switch(var0) {
         case 0:
            return "addition";
         case 1:
            return "multiply_base";
         case 2:
            return "multiply_total";
         default:
            throw new IllegalArgumentException("Unknown operation " + var0);
         }
      }

      private static int func_186595_a(String var0) {
         if ("addition".equals(var0)) {
            return 0;
         } else if ("multiply_base".equals(var0)) {
            return 1;
         } else if ("multiply_total".equals(var0)) {
            return 2;
         } else {
            throw new JsonSyntaxException("Unknown attribute modifier operation " + var0);
         }
      }
   }

   public static class Serializer extends LootFunction.Serializer<SetAttributes> {
      public Serializer() {
         super(new ResourceLocation("set_attributes"), SetAttributes.class);
      }

      public void func_186532_a(JsonObject var1, SetAttributes var2, JsonSerializationContext var3) {
         JsonArray var4 = new JsonArray();
         SetAttributes.Modifier[] var5 = var2.field_186561_b;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            SetAttributes.Modifier var8 = var5[var7];
            var4.add(var8.func_186592_a(var3));
         }

         var1.add("modifiers", var4);
      }

      public SetAttributes func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         JsonArray var4 = JsonUtils.func_151214_t(var1, "modifiers");
         SetAttributes.Modifier[] var5 = new SetAttributes.Modifier[var4.size()];
         int var6 = 0;

         JsonElement var8;
         for(Iterator var7 = var4.iterator(); var7.hasNext(); var5[var6++] = SetAttributes.Modifier.func_186586_a(JsonUtils.func_151210_l(var8, "modifier"), var2)) {
            var8 = (JsonElement)var7.next();
         }

         if (var5.length == 0) {
            throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
         } else {
            return new SetAttributes(var3, var5);
         }
      }

      // $FF: synthetic method
      public LootFunction func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return this.func_186530_b(var1, var2, var3);
      }
   }
}
