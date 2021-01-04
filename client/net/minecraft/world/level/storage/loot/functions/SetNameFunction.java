package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetNameFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Component name;
   @Nullable
   private final LootContext.EntityTarget resolutionContext;

   private SetNameFunction(LootItemCondition[] var1, @Nullable Component var2, @Nullable LootContext.EntityTarget var3) {
      super(var1);
      this.name = var2;
      this.resolutionContext = var3;
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
                  return ComponentUtils.updateForEntity(var3, var2x, var2, 0);
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

   // $FF: synthetic method
   SetNameFunction(LootItemCondition[] var1, Component var2, LootContext.EntityTarget var3, Object var4) {
      this(var1, var2, var3);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetNameFunction> {
      public Serializer() {
         super(new ResourceLocation("set_name"), SetNameFunction.class);
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
         Component var4 = Component.Serializer.fromJson(var1.get("name"));
         LootContext.EntityTarget var5 = (LootContext.EntityTarget)GsonHelper.getAsObject(var1, "entity", (Object)null, var2, LootContext.EntityTarget.class);
         return new SetNameFunction(var3, var4, var5);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
