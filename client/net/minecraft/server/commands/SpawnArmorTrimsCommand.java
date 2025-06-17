package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;

public class SpawnArmorTrimsCommand {
   private static final List<ResourceKey<TrimPattern>> VANILLA_TRIM_PATTERNS;
   private static final List<ResourceKey<TrimMaterial>> VANILLA_TRIM_MATERIALS;
   private static final ToIntFunction<ResourceKey<TrimPattern>> TRIM_PATTERN_ORDER;
   private static final ToIntFunction<ResourceKey<TrimMaterial>> TRIM_MATERIAL_ORDER;
   private static final DynamicCommandExceptionType ERROR_INVALID_PATTERN;

   public SpawnArmorTrimsCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawn_armor_trims").requires(Commands.hasPermission(2))).then(Commands.literal("*_lag_my_game").executes((var0x) -> spawnAllArmorTrims((CommandSourceStack)var0x.getSource(), ((CommandSourceStack)var0x.getSource()).getPlayerOrException())))).then(Commands.argument("pattern", ResourceKeyArgument.key(Registries.TRIM_PATTERN)).executes((var0x) -> spawnArmorTrim((CommandSourceStack)var0x.getSource(), ((CommandSourceStack)var0x.getSource()).getPlayerOrException(), ResourceKeyArgument.getRegistryKey(var0x, "pattern", Registries.TRIM_PATTERN, ERROR_INVALID_PATTERN)))));
   }

   private static int spawnAllArmorTrims(CommandSourceStack var0, Player var1) {
      return spawnArmorTrims(var0, var1, var0.getServer().registryAccess().lookupOrThrow(Registries.TRIM_PATTERN).listElements());
   }

   private static int spawnArmorTrim(CommandSourceStack var0, Player var1, ResourceKey<TrimPattern> var2) {
      return spawnArmorTrims(var0, var1, Stream.of((Holder.Reference)var0.getServer().registryAccess().lookupOrThrow(Registries.TRIM_PATTERN).get(var2).orElseThrow()));
   }

   private static int spawnArmorTrims(CommandSourceStack var0, Player var1, Stream<Holder.Reference<TrimPattern>> var2) {
      ServerLevel var3 = var0.getLevel();
      List var4 = var2.sorted(Comparator.comparing((var0x) -> TRIM_PATTERN_ORDER.applyAsInt(var0x.key()))).toList();
      List var5 = var3.registryAccess().lookupOrThrow(Registries.TRIM_MATERIAL).listElements().sorted(Comparator.comparing((var0x) -> TRIM_MATERIAL_ORDER.applyAsInt(var0x.key()))).toList();
      List var6 = findEquippableItemsWithAssets(var3.registryAccess().lookupOrThrow(Registries.ITEM));
      BlockPos var7 = var1.blockPosition().relative((Direction)var1.getDirection(), 5);
      double var8 = 3.0;

      for(int var10 = 0; var10 < var5.size(); ++var10) {
         Holder.Reference var11 = (Holder.Reference)var5.get(var10);

         for(int var12 = 0; var12 < var4.size(); ++var12) {
            Holder.Reference var13 = (Holder.Reference)var4.get(var12);
            ArmorTrim var14 = new ArmorTrim(var11, var13);

            for(int var15 = 0; var15 < var6.size(); ++var15) {
               Holder.Reference var16 = (Holder.Reference)var6.get(var15);
               double var17 = (double)var7.getX() + 0.5 - (double)var15 * 3.0;
               double var19 = (double)var7.getY() + 0.5 + (double)var10 * 3.0;
               double var21 = (double)var7.getZ() + 0.5 + (double)(var12 * 10);
               ArmorStand var23 = new ArmorStand(var3, var17, var19, var21);
               var23.setYRot(180.0F);
               var23.setNoGravity(true);
               ItemStack var24 = new ItemStack(var16);
               Equippable var25 = (Equippable)Objects.requireNonNull((Equippable)var24.get(DataComponents.EQUIPPABLE));
               var24.set(DataComponents.TRIM, var14);
               var23.setItemSlot(var25.slot(), var24);
               if (var15 == 0) {
                  var23.setCustomName(((TrimPattern)var14.pattern().value()).copyWithStyle(var14.material()).copy().append(" & ").append(((TrimMaterial)var14.material().value()).description()));
                  var23.setCustomNameVisible(true);
               } else {
                  var23.setInvisible(true);
               }

               var3.addFreshEntity(var23);
            }
         }
      }

      var0.sendSuccess(() -> Component.literal("Armorstands with trimmed armor spawned around you"), true);
      return 1;
   }

   private static List<Holder.Reference<Item>> findEquippableItemsWithAssets(HolderLookup<Item> var0) {
      ArrayList var1 = new ArrayList();
      var0.listElements().forEach((var1x) -> {
         Equippable var2 = (Equippable)((Item)var1x.value()).components().get(DataComponents.EQUIPPABLE);
         if (var2 != null && var2.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR && var2.assetId().isPresent()) {
            var1.add(var1x);
         }

      });
      return var1;
   }

   static {
      VANILLA_TRIM_PATTERNS = List.of(TrimPatterns.SENTRY, TrimPatterns.DUNE, TrimPatterns.COAST, TrimPatterns.WILD, TrimPatterns.WARD, TrimPatterns.EYE, TrimPatterns.VEX, TrimPatterns.TIDE, TrimPatterns.SNOUT, TrimPatterns.RIB, TrimPatterns.SPIRE, TrimPatterns.WAYFINDER, TrimPatterns.SHAPER, TrimPatterns.SILENCE, TrimPatterns.RAISER, TrimPatterns.HOST, TrimPatterns.FLOW, TrimPatterns.BOLT);
      VANILLA_TRIM_MATERIALS = List.of(TrimMaterials.QUARTZ, TrimMaterials.IRON, TrimMaterials.NETHERITE, TrimMaterials.REDSTONE, TrimMaterials.COPPER, TrimMaterials.GOLD, TrimMaterials.EMERALD, TrimMaterials.DIAMOND, TrimMaterials.LAPIS, TrimMaterials.AMETHYST, TrimMaterials.RESIN);
      TRIM_PATTERN_ORDER = Util.<ResourceKey<TrimPattern>>createIndexLookup(VANILLA_TRIM_PATTERNS);
      TRIM_MATERIAL_ORDER = Util.<ResourceKey<TrimMaterial>>createIndexLookup(VANILLA_TRIM_MATERIALS);
      ERROR_INVALID_PATTERN = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("Invalid pattern", var0));
   }
}
