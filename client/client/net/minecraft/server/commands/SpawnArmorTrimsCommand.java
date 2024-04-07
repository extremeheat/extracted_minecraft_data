package net.minecraft.server.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.Level;

public class SpawnArmorTrimsCommand {
   private static final Map<Pair<Holder<ArmorMaterial>, EquipmentSlot>, Item> MATERIAL_AND_SLOT_TO_ITEM = Util.make(Maps.newHashMap(), var0 -> {
      var0.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.HEAD), Items.CHAINMAIL_HELMET);
      var0.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.CHEST), Items.CHAINMAIL_CHESTPLATE);
      var0.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.LEGS), Items.CHAINMAIL_LEGGINGS);
      var0.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.FEET), Items.CHAINMAIL_BOOTS);
      var0.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.HEAD), Items.IRON_HELMET);
      var0.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.CHEST), Items.IRON_CHESTPLATE);
      var0.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.LEGS), Items.IRON_LEGGINGS);
      var0.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.FEET), Items.IRON_BOOTS);
      var0.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.HEAD), Items.GOLDEN_HELMET);
      var0.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.CHEST), Items.GOLDEN_CHESTPLATE);
      var0.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.LEGS), Items.GOLDEN_LEGGINGS);
      var0.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.FEET), Items.GOLDEN_BOOTS);
      var0.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.HEAD), Items.NETHERITE_HELMET);
      var0.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.CHEST), Items.NETHERITE_CHESTPLATE);
      var0.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.LEGS), Items.NETHERITE_LEGGINGS);
      var0.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.FEET), Items.NETHERITE_BOOTS);
      var0.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD), Items.DIAMOND_HELMET);
      var0.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST), Items.DIAMOND_CHESTPLATE);
      var0.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS), Items.DIAMOND_LEGGINGS);
      var0.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.FEET), Items.DIAMOND_BOOTS);
      var0.put(Pair.of(ArmorMaterials.TURTLE, EquipmentSlot.HEAD), Items.TURTLE_HELMET);
   });
   private static final List<ResourceKey<TrimPattern>> VANILLA_TRIM_PATTERNS = List.of(
      TrimPatterns.SENTRY,
      TrimPatterns.DUNE,
      TrimPatterns.COAST,
      TrimPatterns.WILD,
      TrimPatterns.WARD,
      TrimPatterns.EYE,
      TrimPatterns.VEX,
      TrimPatterns.TIDE,
      TrimPatterns.SNOUT,
      TrimPatterns.RIB,
      TrimPatterns.SPIRE,
      TrimPatterns.WAYFINDER,
      TrimPatterns.SHAPER,
      TrimPatterns.SILENCE,
      TrimPatterns.RAISER,
      TrimPatterns.HOST,
      TrimPatterns.FLOW,
      TrimPatterns.BOLT
   );
   private static final List<ResourceKey<TrimMaterial>> VANILLA_TRIM_MATERIALS = List.of(
      TrimMaterials.QUARTZ,
      TrimMaterials.IRON,
      TrimMaterials.NETHERITE,
      TrimMaterials.REDSTONE,
      TrimMaterials.COPPER,
      TrimMaterials.GOLD,
      TrimMaterials.EMERALD,
      TrimMaterials.DIAMOND,
      TrimMaterials.LAPIS,
      TrimMaterials.AMETHYST
   );
   private static final ToIntFunction<ResourceKey<TrimPattern>> TRIM_PATTERN_ORDER = Util.createIndexLookup(VANILLA_TRIM_PATTERNS);
   private static final ToIntFunction<ResourceKey<TrimMaterial>> TRIM_MATERIAL_ORDER = Util.createIndexLookup(VANILLA_TRIM_MATERIALS);

   public SpawnArmorTrimsCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawn_armor_trims").requires(var0x -> var0x.hasPermission(2)))
            .executes(var0x -> spawnArmorTrims((CommandSourceStack)var0x.getSource(), ((CommandSourceStack)var0x.getSource()).getPlayerOrException()))
      );
   }

   private static int spawnArmorTrims(CommandSourceStack var0, Player var1) {
      Level var2 = var1.level();
      NonNullList var3 = NonNullList.create();
      Registry var4 = var2.registryAccess().registryOrThrow(Registries.TRIM_PATTERN);
      Registry var5 = var2.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL);
      var4.stream()
         .sorted(Comparator.comparing(var1x -> TRIM_PATTERN_ORDER.applyAsInt(var4.getResourceKey(var1x).orElse(null))))
         .forEachOrdered(
            var3x -> var5.stream()
                  .sorted(Comparator.comparing(var1xx -> TRIM_MATERIAL_ORDER.applyAsInt(var5.getResourceKey(var1xx).orElse(null))))
                  .forEachOrdered(var4x -> var3.add(new ArmorTrim(var5.wrapAsHolder(var4x), var4.wrapAsHolder(var3x))))
         );
      BlockPos var6 = var1.blockPosition().relative(var1.getDirection(), 5);
      Registry var7 = var0.registryAccess().registryOrThrow(Registries.ARMOR_MATERIAL);
      int var8 = var7.size() - 1;
      double var9 = 3.0;
      int var11 = 0;
      int var12 = 0;

      for (ArmorTrim var14 : var3) {
         for (ArmorMaterial var16 : var7) {
            if (var16 != ArmorMaterials.LEATHER.value()) {
               double var17 = (double)var6.getX() + 0.5 - (double)(var11 % var5.size()) * 3.0;
               double var19 = (double)var6.getY() + 0.5 + (double)(var12 % var8) * 3.0;
               double var21 = (double)var6.getZ() + 0.5 + (double)(var11 / var5.size() * 10);
               ArmorStand var23 = new ArmorStand(var2, var17, var19, var21);
               var23.setYRot(180.0F);
               var23.setNoGravity(true);

               for (EquipmentSlot var27 : EquipmentSlot.values()) {
                  Item var28 = MATERIAL_AND_SLOT_TO_ITEM.get(Pair.of(var16, var27));
                  if (var28 != null) {
                     ItemStack var29 = new ItemStack(var28);
                     var29.set(DataComponents.TRIM, var14);
                     var23.setItemSlot(var27, var29);
                     if (var28 instanceof ArmorItem) {
                        ArmorItem var30 = (ArmorItem)var28;
                        if (var30.getMaterial().is(ArmorMaterials.TURTLE)) {
                           var23.setCustomName(
                              var14.pattern().value().copyWithStyle(var14.material()).copy().append(" ").append(var14.material().value().description())
                           );
                           var23.setCustomNameVisible(true);
                           continue;
                        }
                     }

                     var23.setInvisible(true);
                  }
               }

               var2.addFreshEntity(var23);
               var12++;
            }
         }

         var11++;
      }

      var0.sendSuccess(() -> Component.literal("Armorstands with trimmed armor spawned around you"), true);
      return 1;
   }
}
