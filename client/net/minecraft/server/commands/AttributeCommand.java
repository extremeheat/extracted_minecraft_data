package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.stream.Stream;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeCommand {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.attribute.failed.entity", var0));
   private static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ATTRIBUTE = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("commands.attribute.failed.no_attribute", var0, var1));
   private static final Dynamic3CommandExceptionType ERROR_NO_SUCH_MODIFIER = new Dynamic3CommandExceptionType((var0, var1, var2) -> Component.translatableEscape("commands.attribute.failed.no_modifier", var1, var0, var2));
   private static final Dynamic3CommandExceptionType ERROR_MODIFIER_ALREADY_PRESENT = new Dynamic3CommandExceptionType((var0, var1, var2) -> Component.translatableEscape("commands.attribute.failed.modifier_already_present", var2, var1, var0));

   public AttributeCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("attribute").requires((var0x) -> var0x.hasPermission(2))).then(Commands.argument("target", EntityArgument.entity()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("attribute", ResourceArgument.resource(var1, Registries.ATTRIBUTE)).then(((LiteralArgumentBuilder)Commands.literal("get").executes((var0x) -> getAttributeValue((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), 1.0))).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((var0x) -> getAttributeValue((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), DoubleArgumentType.getDouble(var0x, "scale")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("base").then(Commands.literal("set").then(Commands.argument("value", DoubleArgumentType.doubleArg()).executes((var0x) -> setAttributeBase((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), DoubleArgumentType.getDouble(var0x, "value")))))).then(((LiteralArgumentBuilder)Commands.literal("get").executes((var0x) -> getAttributeBase((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), 1.0))).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((var0x) -> getAttributeBase((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), DoubleArgumentType.getDouble(var0x, "scale")))))).then(Commands.literal("reset").executes((var0x) -> resetAttributeBase((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("modifier").then(Commands.literal("add").then(Commands.argument("id", ResourceLocationArgument.id()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("value", DoubleArgumentType.doubleArg()).then(Commands.literal("add_value").executes((var0x) -> addModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), ResourceLocationArgument.getId(var0x, "id"), DoubleArgumentType.getDouble(var0x, "value"), AttributeModifier.Operation.ADD_VALUE)))).then(Commands.literal("add_multiplied_base").executes((var0x) -> addModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), ResourceLocationArgument.getId(var0x, "id"), DoubleArgumentType.getDouble(var0x, "value"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE)))).then(Commands.literal("add_multiplied_total").executes((var0x) -> addModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), ResourceLocationArgument.getId(var0x, "id"), DoubleArgumentType.getDouble(var0x, "value"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))))))).then(Commands.literal("remove").then(Commands.argument("id", ResourceLocationArgument.id()).suggests((var0x, var1x) -> SharedSuggestionProvider.suggestResource(getAttributeModifiers(EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute")), var1x)).executes((var0x) -> removeModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), ResourceLocationArgument.getId(var0x, "id")))))).then(Commands.literal("value").then(Commands.literal("get").then(((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests((var0x, var1x) -> SharedSuggestionProvider.suggestResource(getAttributeModifiers(EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute")), var1x)).executes((var0x) -> getAttributeModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), ResourceLocationArgument.getId(var0x, "id"), 1.0))).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((var0x) -> getAttributeModifier((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ResourceArgument.getAttribute(var0x, "attribute"), ResourceLocationArgument.getId(var0x, "id"), DoubleArgumentType.getDouble(var0x, "scale")))))))))));
   }

   private static AttributeInstance getAttributeInstance(Entity var0, Holder<Attribute> var1) throws CommandSyntaxException {
      AttributeInstance var2 = getLivingEntity(var0).getAttributes().getInstance(var1);
      if (var2 == null) {
         throw ERROR_NO_SUCH_ATTRIBUTE.create(var0.getName(), getAttributeDescription(var1));
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

   private static LivingEntity getEntityWithAttribute(Entity var0, Holder<Attribute> var1) throws CommandSyntaxException {
      LivingEntity var2 = getLivingEntity(var0);
      if (!var2.getAttributes().hasAttribute(var1)) {
         throw ERROR_NO_SUCH_ATTRIBUTE.create(var0.getName(), getAttributeDescription(var1));
      } else {
         return var2;
      }
   }

   private static int getAttributeValue(CommandSourceStack var0, Entity var1, Holder<Attribute> var2, double var3) throws CommandSyntaxException {
      LivingEntity var5 = getEntityWithAttribute(var1, var2);
      double var6 = var5.getAttributeValue(var2);
      var0.sendSuccess(() -> Component.translatable("commands.attribute.value.get.success", getAttributeDescription(var2), var1.getName(), var6), false);
      return (int)(var6 * var3);
   }

   private static int getAttributeBase(CommandSourceStack var0, Entity var1, Holder<Attribute> var2, double var3) throws CommandSyntaxException {
      LivingEntity var5 = getEntityWithAttribute(var1, var2);
      double var6 = var5.getAttributeBaseValue(var2);
      var0.sendSuccess(() -> Component.translatable("commands.attribute.base_value.get.success", getAttributeDescription(var2), var1.getName(), var6), false);
      return (int)(var6 * var3);
   }

   private static int getAttributeModifier(CommandSourceStack var0, Entity var1, Holder<Attribute> var2, ResourceLocation var3, double var4) throws CommandSyntaxException {
      LivingEntity var6 = getEntityWithAttribute(var1, var2);
      AttributeMap var7 = var6.getAttributes();
      if (!var7.hasModifier(var2, var3)) {
         throw ERROR_NO_SUCH_MODIFIER.create(var1.getName(), getAttributeDescription(var2), var3);
      } else {
         double var8 = var7.getModifierValue(var2, var3);
         var0.sendSuccess(() -> Component.translatable("commands.attribute.modifier.value.get.success", Component.translationArg(var3), getAttributeDescription(var2), var1.getName(), var8), false);
         return (int)(var8 * var4);
      }
   }

   private static Stream<ResourceLocation> getAttributeModifiers(Entity var0, Holder<Attribute> var1) throws CommandSyntaxException {
      AttributeInstance var2 = getAttributeInstance(var0, var1);
      return var2.getModifiers().stream().map(AttributeModifier::id);
   }

   private static int setAttributeBase(CommandSourceStack var0, Entity var1, Holder<Attribute> var2, double var3) throws CommandSyntaxException {
      getAttributeInstance(var1, var2).setBaseValue(var3);
      var0.sendSuccess(() -> Component.translatable("commands.attribute.base_value.set.success", getAttributeDescription(var2), var1.getName(), var3), false);
      return 1;
   }

   private static int resetAttributeBase(CommandSourceStack var0, Entity var1, Holder<Attribute> var2) throws CommandSyntaxException {
      LivingEntity var3 = getLivingEntity(var1);
      if (!var3.getAttributes().resetBaseValue(var2)) {
         throw ERROR_NO_SUCH_ATTRIBUTE.create(var1.getName(), getAttributeDescription(var2));
      } else {
         double var4 = var3.getAttributeBaseValue(var2);
         var0.sendSuccess(() -> Component.translatable("commands.attribute.base_value.reset.success", getAttributeDescription(var2), var1.getName(), var4), false);
         return 1;
      }
   }

   private static int addModifier(CommandSourceStack var0, Entity var1, Holder<Attribute> var2, ResourceLocation var3, double var4, AttributeModifier.Operation var6) throws CommandSyntaxException {
      AttributeInstance var7 = getAttributeInstance(var1, var2);
      AttributeModifier var8 = new AttributeModifier(var3, var4, var6);
      if (var7.hasModifier(var3)) {
         throw ERROR_MODIFIER_ALREADY_PRESENT.create(var1.getName(), getAttributeDescription(var2), var3);
      } else {
         var7.addPermanentModifier(var8);
         var0.sendSuccess(() -> Component.translatable("commands.attribute.modifier.add.success", Component.translationArg(var3), getAttributeDescription(var2), var1.getName()), false);
         return 1;
      }
   }

   private static int removeModifier(CommandSourceStack var0, Entity var1, Holder<Attribute> var2, ResourceLocation var3) throws CommandSyntaxException {
      AttributeInstance var4 = getAttributeInstance(var1, var2);
      if (var4.removeModifier(var3)) {
         var0.sendSuccess(() -> Component.translatable("commands.attribute.modifier.remove.success", Component.translationArg(var3), getAttributeDescription(var2), var1.getName()), false);
         return 1;
      } else {
         throw ERROR_NO_SUCH_MODIFIER.create(var1.getName(), getAttributeDescription(var2), var3);
      }
   }

   private static Component getAttributeDescription(Holder<Attribute> var0) {
      return Component.translatable(((Attribute)var0.value()).getDescriptionId());
   }
}
