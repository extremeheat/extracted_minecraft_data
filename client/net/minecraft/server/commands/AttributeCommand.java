package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeCommand {
   private static final SuggestionProvider<CommandSourceStack> AVAILABLE_ATTRIBUTES = (var0, var1) -> {
      return SharedSuggestionProvider.suggestResource((Iterable)Registry.ATTRIBUTE.keySet(), var1);
   };
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.attribute.failed.entity", new Object[]{var0});
   });
   private static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ATTRIBUTE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.attribute.failed.no_attribute", new Object[]{var0, var1});
   });
   private static final Dynamic3CommandExceptionType ERROR_NO_SUCH_MODIFIER = new Dynamic3CommandExceptionType((var0, var1, var2) -> {
      return new TranslatableComponent("commands.attribute.failed.no_modifier", new Object[]{var1, var0, var2});
   });
   private static final Dynamic3CommandExceptionType ERROR_MODIFIER_ALREADY_PRESENT = new Dynamic3CommandExceptionType((var0, var1, var2) -> {
      return new TranslatableComponent("commands.attribute.failed.modifier_already_present", new Object[]{var2, var1, var0});
   });

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("attribute").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("target", EntityArgument.entity()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("attribute", ResourceLocationArgument.id()).suggests(AVAILABLE_ATTRIBUTES).then(((LiteralArgumentBuilder)Commands.literal("get").executes((var0x) -> {
         return getAttributeValue((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), 1.0D);
      })).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((var0x) -> {
         return getAttributeValue((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), DoubleArgumentType.getDouble(var0x, "scale"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("base").then(Commands.literal("set").then(Commands.argument("value", DoubleArgumentType.doubleArg()).executes((var0x) -> {
         return setAttributeBase((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), DoubleArgumentType.getDouble(var0x, "value"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("get").executes((var0x) -> {
         return getAttributeBase((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), 1.0D);
      })).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((var0x) -> {
         return getAttributeBase((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), DoubleArgumentType.getDouble(var0x, "scale"));
      }))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("modifier").then(Commands.literal("add").then(Commands.argument("uuid", UuidArgument.uuid()).then(Commands.argument("name", StringArgumentType.string()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("value", DoubleArgumentType.doubleArg()).then(Commands.literal("add").executes((var0x) -> {
         return addModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), UuidArgument.getUuid(var0x, "uuid"), StringArgumentType.getString(var0x, "name"), DoubleArgumentType.getDouble(var0x, "value"), AttributeModifier.Operation.ADDITION);
      }))).then(Commands.literal("multiply").executes((var0x) -> {
         return addModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), UuidArgument.getUuid(var0x, "uuid"), StringArgumentType.getString(var0x, "name"), DoubleArgumentType.getDouble(var0x, "value"), AttributeModifier.Operation.MULTIPLY_TOTAL);
      }))).then(Commands.literal("multiply_base").executes((var0x) -> {
         return addModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), UuidArgument.getUuid(var0x, "uuid"), StringArgumentType.getString(var0x, "name"), DoubleArgumentType.getDouble(var0x, "value"), AttributeModifier.Operation.MULTIPLY_BASE);
      }))))))).then(Commands.literal("remove").then(Commands.argument("uuid", UuidArgument.uuid()).executes((var0x) -> {
         return removeModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), UuidArgument.getUuid(var0x, "uuid"));
      })))).then(Commands.literal("value").then(Commands.literal("get").then(((RequiredArgumentBuilder)Commands.argument("uuid", UuidArgument.uuid()).executes((var0x) -> {
         return getAttributeModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), UuidArgument.getUuid(var0x, "uuid"), 1.0D);
      })).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((var0x) -> {
         return getAttributeModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceLocationArgument.getAttribute(var0x, "attribute"), UuidArgument.getUuid(var0x, "uuid"), DoubleArgumentType.getDouble(var0x, "scale"));
      })))))))));
   }

   private static AttributeInstance getAttributeInstance(Entity var0, Attribute var1) throws CommandSyntaxException {
      AttributeInstance var2 = getLivingEntity(var0).getAttributes().getInstance(var1);
      if (var2 == null) {
         throw ERROR_NO_SUCH_ATTRIBUTE.create(var0.getName(), new TranslatableComponent(var1.getDescriptionId()));
      } else {
         return var2;
      }
   }

   private static LivingEntity getLivingEntity(Entity var0) throws CommandSyntaxException {
      if (!(var0 instanceof LivingEntity)) {
         throw ERROR_NOT_LIVING_ENTITY.create(var0.getName());
      } else {
         return (LivingEntity)var0;
      }
   }

   private static LivingEntity getEntityWithAttribute(Entity var0, Attribute var1) throws CommandSyntaxException {
      LivingEntity var2 = getLivingEntity(var0);
      if (!var2.getAttributes().hasAttribute(var1)) {
         throw ERROR_NO_SUCH_ATTRIBUTE.create(var0.getName(), new TranslatableComponent(var1.getDescriptionId()));
      } else {
         return var2;
      }
   }

   private static int getAttributeValue(CommandSourceStack var0, Entity var1, Attribute var2, double var3) throws CommandSyntaxException {
      LivingEntity var5 = getEntityWithAttribute(var1, var2);
      double var6 = var5.getAttributeValue(var2);
      var0.sendSuccess(new TranslatableComponent("commands.attribute.value.get.success", new Object[]{new TranslatableComponent(var2.getDescriptionId()), var1.getName(), var6}), false);
      return (int)(var6 * var3);
   }

   private static int getAttributeBase(CommandSourceStack var0, Entity var1, Attribute var2, double var3) throws CommandSyntaxException {
      LivingEntity var5 = getEntityWithAttribute(var1, var2);
      double var6 = var5.getAttributeBaseValue(var2);
      var0.sendSuccess(new TranslatableComponent("commands.attribute.base_value.get.success", new Object[]{new TranslatableComponent(var2.getDescriptionId()), var1.getName(), var6}), false);
      return (int)(var6 * var3);
   }

   private static int getAttributeModifier(CommandSourceStack var0, Entity var1, Attribute var2, UUID var3, double var4) throws CommandSyntaxException {
      LivingEntity var6 = getEntityWithAttribute(var1, var2);
      AttributeMap var7 = var6.getAttributes();
      if (!var7.hasModifier(var2, var3)) {
         throw ERROR_NO_SUCH_MODIFIER.create(var1.getName(), new TranslatableComponent(var2.getDescriptionId()), var3);
      } else {
         double var8 = var7.getModifierValue(var2, var3);
         var0.sendSuccess(new TranslatableComponent("commands.attribute.modifier.value.get.success", new Object[]{var3, new TranslatableComponent(var2.getDescriptionId()), var1.getName(), var8}), false);
         return (int)(var8 * var4);
      }
   }

   private static int setAttributeBase(CommandSourceStack var0, Entity var1, Attribute var2, double var3) throws CommandSyntaxException {
      getAttributeInstance(var1, var2).setBaseValue(var3);
      var0.sendSuccess(new TranslatableComponent("commands.attribute.base_value.set.success", new Object[]{new TranslatableComponent(var2.getDescriptionId()), var1.getName(), var3}), false);
      return 1;
   }

   private static int addModifier(CommandSourceStack var0, Entity var1, Attribute var2, UUID var3, String var4, double var5, AttributeModifier.Operation var7) throws CommandSyntaxException {
      AttributeInstance var8 = getAttributeInstance(var1, var2);
      AttributeModifier var9 = new AttributeModifier(var3, var4, var5, var7);
      if (var8.hasModifier(var9)) {
         throw ERROR_MODIFIER_ALREADY_PRESENT.create(var1.getName(), new TranslatableComponent(var2.getDescriptionId()), var3);
      } else {
         var8.addPermanentModifier(var9);
         var0.sendSuccess(new TranslatableComponent("commands.attribute.modifier.add.success", new Object[]{var3, new TranslatableComponent(var2.getDescriptionId()), var1.getName()}), false);
         return 1;
      }
   }

   private static int removeModifier(CommandSourceStack var0, Entity var1, Attribute var2, UUID var3) throws CommandSyntaxException {
      AttributeInstance var4 = getAttributeInstance(var1, var2);
      if (var4.removePermanentModifier(var3)) {
         var0.sendSuccess(new TranslatableComponent("commands.attribute.modifier.remove.success", new Object[]{var3, new TranslatableComponent(var2.getDescriptionId()), var1.getName()}), false);
         return 1;
      } else {
         throw ERROR_NO_SUCH_MODIFIER.create(var1.getName(), new TranslatableComponent(var2.getDescriptionId()), var3);
      }
   }
}
