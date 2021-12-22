package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetAttributesFunction extends LootItemConditionalFunction {
   final List<SetAttributesFunction.Modifier> modifiers;

   SetAttributesFunction(LootItemCondition[] var1, List<SetAttributesFunction.Modifier> var2) {
      super(var1);
      this.modifiers = ImmutableList.copyOf(var2);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_ATTRIBUTES;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return (Set)this.modifiers.stream().flatMap((var0) -> {
         return var0.amount.getReferencedContextParams().stream();
      }).collect(ImmutableSet.toImmutableSet());
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Random var3 = var2.getRandom();
      Iterator var4 = this.modifiers.iterator();

      while(var4.hasNext()) {
         SetAttributesFunction.Modifier var5 = (SetAttributesFunction.Modifier)var4.next();
         UUID var6 = var5.field_374;
         if (var6 == null) {
            var6 = UUID.randomUUID();
         }

         EquipmentSlot var7 = (EquipmentSlot)Util.getRandom((Object[])var5.slots, var3);
         var1.addAttributeModifier(var5.attribute, new AttributeModifier(var6, var5.name, (double)var5.amount.getFloat(var2), var5.operation), var7);
      }

      return var1;
   }

   public static SetAttributesFunction.ModifierBuilder modifier(String var0, Attribute var1, AttributeModifier.Operation var2, NumberProvider var3) {
      return new SetAttributesFunction.ModifierBuilder(var0, var1, var2, var3);
   }

   public static SetAttributesFunction.Builder setAttributes() {
      return new SetAttributesFunction.Builder();
   }

   private static class Modifier {
      final String name;
      final Attribute attribute;
      final AttributeModifier.Operation operation;
      final NumberProvider amount;
      // $FF: renamed from: id java.util.UUID
      @Nullable
      final UUID field_374;
      final EquipmentSlot[] slots;

      Modifier(String var1, Attribute var2, AttributeModifier.Operation var3, NumberProvider var4, EquipmentSlot[] var5, @Nullable UUID var6) {
         super();
         this.name = var1;
         this.attribute = var2;
         this.operation = var3;
         this.amount = var4;
         this.field_374 = var6;
         this.slots = var5;
      }

      public JsonObject serialize(JsonSerializationContext var1) {
         JsonObject var2 = new JsonObject();
         var2.addProperty("name", this.name);
         var2.addProperty("attribute", Registry.ATTRIBUTE.getKey(this.attribute).toString());
         var2.addProperty("operation", operationToString(this.operation));
         var2.add("amount", var1.serialize(this.amount));
         if (this.field_374 != null) {
            var2.addProperty("id", this.field_374.toString());
         }

         if (this.slots.length == 1) {
            var2.addProperty("slot", this.slots[0].getName());
         } else {
            JsonArray var3 = new JsonArray();
            EquipmentSlot[] var4 = this.slots;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               EquipmentSlot var7 = var4[var6];
               var3.add(new JsonPrimitive(var7.getName()));
            }

            var2.add("slot", var3);
         }

         return var2;
      }

      public static SetAttributesFunction.Modifier deserialize(JsonObject var0, JsonDeserializationContext var1) {
         String var2 = GsonHelper.getAsString(var0, "name");
         ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var0, "attribute"));
         Attribute var4 = (Attribute)Registry.ATTRIBUTE.get(var3);
         if (var4 == null) {
            throw new JsonSyntaxException("Unknown attribute: " + var3);
         } else {
            AttributeModifier.Operation var5 = operationFromString(GsonHelper.getAsString(var0, "operation"));
            NumberProvider var6 = (NumberProvider)GsonHelper.getAsObject(var0, "amount", var1, NumberProvider.class);
            UUID var8 = null;
            EquipmentSlot[] var7;
            if (GsonHelper.isStringValue(var0, "slot")) {
               var7 = new EquipmentSlot[]{EquipmentSlot.byName(GsonHelper.getAsString(var0, "slot"))};
            } else {
               if (!GsonHelper.isArrayNode(var0, "slot")) {
                  throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
               }

               JsonArray var9 = GsonHelper.getAsJsonArray(var0, "slot");
               var7 = new EquipmentSlot[var9.size()];
               int var10 = 0;

               JsonElement var12;
               for(Iterator var11 = var9.iterator(); var11.hasNext(); var7[var10++] = EquipmentSlot.byName(GsonHelper.convertToString(var12, "slot"))) {
                  var12 = (JsonElement)var11.next();
               }

               if (var7.length == 0) {
                  throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
               }
            }

            if (var0.has("id")) {
               String var14 = GsonHelper.getAsString(var0, "id");

               try {
                  var8 = UUID.fromString(var14);
               } catch (IllegalArgumentException var13) {
                  throw new JsonSyntaxException("Invalid attribute modifier id '" + var14 + "' (must be UUID format, with dashes)");
               }
            }

            return new SetAttributesFunction.Modifier(var2, var4, var5, var6, var7, var8);
         }
      }

      private static String operationToString(AttributeModifier.Operation var0) {
         switch(var0) {
         case ADDITION:
            return "addition";
         case MULTIPLY_BASE:
            return "multiply_base";
         case MULTIPLY_TOTAL:
            return "multiply_total";
         default:
            throw new IllegalArgumentException("Unknown operation " + var0);
         }
      }

      private static AttributeModifier.Operation operationFromString(String var0) {
         byte var2 = -1;
         switch(var0.hashCode()) {
         case -1226589444:
            if (var0.equals("addition")) {
               var2 = 0;
            }
            break;
         case -78229492:
            if (var0.equals("multiply_base")) {
               var2 = 1;
            }
            break;
         case 1886894441:
            if (var0.equals("multiply_total")) {
               var2 = 2;
            }
         }

         switch(var2) {
         case 0:
            return AttributeModifier.Operation.ADDITION;
         case 1:
            return AttributeModifier.Operation.MULTIPLY_BASE;
         case 2:
            return AttributeModifier.Operation.MULTIPLY_TOTAL;
         default:
            throw new JsonSyntaxException("Unknown attribute modifier operation " + var0);
         }
      }
   }

   public static class ModifierBuilder {
      private final String name;
      private final Attribute attribute;
      private final AttributeModifier.Operation operation;
      private final NumberProvider amount;
      // $FF: renamed from: id java.util.UUID
      @Nullable
      private UUID field_458;
      private final Set<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);

      public ModifierBuilder(String var1, Attribute var2, AttributeModifier.Operation var3, NumberProvider var4) {
         super();
         this.name = var1;
         this.attribute = var2;
         this.operation = var3;
         this.amount = var4;
      }

      public SetAttributesFunction.ModifierBuilder forSlot(EquipmentSlot var1) {
         this.slots.add(var1);
         return this;
      }

      public SetAttributesFunction.ModifierBuilder withUuid(UUID var1) {
         this.field_458 = var1;
         return this;
      }

      public SetAttributesFunction.Modifier build() {
         return new SetAttributesFunction.Modifier(this.name, this.attribute, this.operation, this.amount, (EquipmentSlot[])this.slots.toArray(new EquipmentSlot[0]), this.field_458);
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetAttributesFunction.Builder> {
      private final List<SetAttributesFunction.Modifier> modifiers = Lists.newArrayList();

      public Builder() {
         super();
      }

      protected SetAttributesFunction.Builder getThis() {
         return this;
      }

      public SetAttributesFunction.Builder withModifier(SetAttributesFunction.ModifierBuilder var1) {
         this.modifiers.add(var1.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetAttributesFunction(this.getConditions(), this.modifiers);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetAttributesFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetAttributesFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         JsonArray var4 = new JsonArray();
         Iterator var5 = var2.modifiers.iterator();

         while(var5.hasNext()) {
            SetAttributesFunction.Modifier var6 = (SetAttributesFunction.Modifier)var5.next();
            var4.add(var6.serialize(var3));
         }

         var1.add("modifiers", var4);
      }

      public SetAttributesFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         JsonArray var4 = GsonHelper.getAsJsonArray(var1, "modifiers");
         ArrayList var5 = Lists.newArrayListWithExpectedSize(var4.size());
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            JsonElement var7 = (JsonElement)var6.next();
            var5.add(SetAttributesFunction.Modifier.deserialize(GsonHelper.convertToJsonObject(var7, "modifier"), var2));
         }

         if (var5.isEmpty()) {
            throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
         } else {
            return new SetAttributesFunction(var3, var5);
         }
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
