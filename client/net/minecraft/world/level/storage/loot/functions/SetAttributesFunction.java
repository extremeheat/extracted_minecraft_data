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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
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

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_ATTRIBUTES;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.modifiers.stream().flatMap(var0 -> var0.amount.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      RandomSource var3 = var2.getRandom();

      for(SetAttributesFunction.Modifier var5 : this.modifiers) {
         UUID var6 = var5.id;
         if (var6 == null) {
            var6 = UUID.randomUUID();
         }

         EquipmentSlot var7 = Util.getRandom(var5.slots, var3);
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

      @Override
      public LootItemFunction build() {
         return new SetAttributesFunction(this.getConditions(), this.modifiers);
      }
   }

   static class Modifier {
      final String name;
      final Attribute attribute;
      final AttributeModifier.Operation operation;
      final NumberProvider amount;
      @Nullable
      final UUID id;
      final EquipmentSlot[] slots;

      Modifier(String var1, Attribute var2, AttributeModifier.Operation var3, NumberProvider var4, EquipmentSlot[] var5, @Nullable UUID var6) {
         super();
         this.name = var1;
         this.attribute = var2;
         this.operation = var3;
         this.amount = var4;
         this.id = var6;
         this.slots = var5;
      }

      public JsonObject serialize(JsonSerializationContext var1) {
         JsonObject var2 = new JsonObject();
         var2.addProperty("name", this.name);
         var2.addProperty("attribute", BuiltInRegistries.ATTRIBUTE.getKey(this.attribute).toString());
         var2.addProperty("operation", operationToString(this.operation));
         var2.add("amount", var1.serialize(this.amount));
         if (this.id != null) {
            var2.addProperty("id", this.id.toString());
         }

         if (this.slots.length == 1) {
            var2.addProperty("slot", this.slots[0].getName());
         } else {
            JsonArray var3 = new JsonArray();

            for(EquipmentSlot var7 : this.slots) {
               var3.add(new JsonPrimitive(var7.getName()));
            }

            var2.add("slot", var3);
         }

         return var2;
      }

      public static SetAttributesFunction.Modifier deserialize(JsonObject var0, JsonDeserializationContext var1) {
         String var2 = GsonHelper.getAsString(var0, "name");
         ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var0, "attribute"));
         Attribute var4 = BuiltInRegistries.ATTRIBUTE.get(var3);
         if (var4 == null) {
            throw new JsonSyntaxException("Unknown attribute: " + var3);
         } else {
            AttributeModifier.Operation var5 = operationFromString(GsonHelper.getAsString(var0, "operation"));
            NumberProvider var6 = GsonHelper.getAsObject(var0, "amount", var1, NumberProvider.class);
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

               for(JsonElement var12 : var9) {
                  var7[var10++] = EquipmentSlot.byName(GsonHelper.convertToString(var12, "slot"));
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
         switch(var0) {
            case "addition":
               return AttributeModifier.Operation.ADDITION;
            case "multiply_base":
               return AttributeModifier.Operation.MULTIPLY_BASE;
            case "multiply_total":
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
      @Nullable
      private UUID id;
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
         this.id = var1;
         return this;
      }

      public SetAttributesFunction.Modifier build() {
         return new SetAttributesFunction.Modifier(this.name, this.attribute, this.operation, this.amount, this.slots.toArray(new EquipmentSlot[0]), this.id);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetAttributesFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetAttributesFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, var2, var3);
         JsonArray var4 = new JsonArray();

         for(SetAttributesFunction.Modifier var6 : var2.modifiers) {
            var4.add(var6.serialize(var3));
         }

         var1.add("modifiers", var4);
      }

      public SetAttributesFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         JsonArray var4 = GsonHelper.getAsJsonArray(var1, "modifiers");
         ArrayList var5 = Lists.newArrayListWithExpectedSize(var4.size());

         for(JsonElement var7 : var4) {
            var5.add(SetAttributesFunction.Modifier.deserialize(GsonHelper.convertToJsonObject(var7, "modifier"), var2));
         }

         if (var5.isEmpty()) {
            throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
         } else {
            return new SetAttributesFunction(var3, var5);
         }
      }
   }
}
