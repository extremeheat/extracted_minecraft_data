package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SetNameFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   final Component name;
   @Nullable
   final LootContext.EntityTarget resolutionContext;

   SetNameFunction(LootItemCondition[] var1, @Nullable Component var2, @Nullable LootContext.EntityTarget var3) {
      super(var1);
      this.name = var2;
      this.resolutionContext = var3;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_NAME;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of();
   }

   public static UnaryOperator<Component> createResolver(LootContext var0, @Nullable LootContext.EntityTarget var1) {
      if (var1 != null) {
         Entity var2 = (Entity)var0.getParamOrNull(var1.getParam());
         if (var2 != null) {
            CommandSourceStack var3 = var2.createCommandSourceStack().withPermission(2);
            return (var2x) -> {
               try {
                  return ComponentUtils.updateForEntity(var3, (Component)var2x, var2, 0);
               } catch (CommandSyntaxException var4) {
                  LOGGER.warn("Failed to resolve text component", var4);
                  return var2x;
               }
            };
         }
      }

      return (var0x) -> {
         return var0x;
      };
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (this.name != null) {
         var1.setHoverName((Component)createResolver(var2, this.resolutionContext).apply(this.name));
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setName(Component var0) {
      return simpleBuilder((var1) -> {
         return new SetNameFunction(var1, var0, (LootContext.EntityTarget)null);
      });
   }

   public static LootItemConditionalFunction.Builder<?> setName(Component var0, LootContext.EntityTarget var1) {
      return simpleBuilder((var2) -> {
         return new SetNameFunction(var2, var0, var1);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetNameFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetNameFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         if (var2.name != null) {
            var1.add("name", Component.Serializer.toJsonTree(var2.name));
         }

         if (var2.resolutionContext != null) {
            var1.add("entity", var3.serialize(var2.resolutionContext));
         }

      }

      public SetNameFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         MutableComponent var4 = Component.Serializer.fromJson(var1.get("name"));
         LootContext.EntityTarget var5 = (LootContext.EntityTarget)GsonHelper.getAsObject(var1, "entity", (Object)null, var2, LootContext.EntityTarget.class);
         return new SetNameFunction(var3, var4, var5);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
