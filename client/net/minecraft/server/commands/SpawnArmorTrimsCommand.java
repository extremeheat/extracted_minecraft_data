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
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
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
   private static final Map<Pair<ArmorMaterial, EquipmentSlot>, Item> MATERIAL_AND_SLOT_TO_ITEM = Util.make(Maps.newHashMap(), var0 -> {
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
      TrimPatterns.SPIRE
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
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawn_armor_trims")
               .requires(var0x -> var0x.hasPermission(2) && var0x.getLevel().enabledFeatures().contains(FeatureFlags.UPDATE_1_20)))
            .executes(var0x -> spawnArmorTrims((CommandSourceStack)var0x.getSource(), ((CommandSourceStack)var0x.getSource()).getPlayerOrException()))
      );
   }

   private static int spawnArmorTrims(CommandSourceStack var0, Player var1) {
      Level var2 = var1.getLevel();
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
      int var7 = ArmorMaterials.values().length - 1;
      double var8 = 3.0;
      int var10 = 0;
      int var11 = 0;

      for(ArmorTrim var13 : var3) {
         for(ArmorMaterials var17 : ArmorMaterials.values()) {
            if (var17 != ArmorMaterials.LEATHER) {
               double var18 = (double)var6.getX() + 0.5 - (double)(var10 % var5.size()) * 3.0;
               double var20 = (double)var6.getY() + 0.5 + (double)(var11 % var7) * 3.0;
               double var22 = (double)var6.getZ() + 0.5 + (double)(var10 / var5.size() * 10);
               ArmorStand var24 = new ArmorStand(var2, var18, var20, var22);
               var24.setYRot(180.0F);
               var24.setNoGravity(true);

               for(EquipmentSlot var28 : EquipmentSlot.values()) {
                  Item var29 = MATERIAL_AND_SLOT_TO_ITEM.get(Pair.of(var17, var28));
                  if (var29 != null) {
                     ItemStack var30 = new ItemStack(var29);
                     ArmorTrim.setTrim(var2.registryAccess(), var30, var13);
                     var24.setItemSlot(var28, var30);
                     if (var29 instanceof ArmorItem var31 && var31.getMaterial() == ArmorMaterials.TURTLE) {
                        var24.setCustomName(
                           var13.pattern().value().copyWithStyle(var13.material()).copy().append(" ").append(var13.material().value().description())
                        );
                        var24.setCustomNameVisible(true);
                        continue;
                     }

                     var24.setInvisible(true);
                  }
               }

               var2.addFreshEntity(var24);
               ++var11;
            }
         }

         ++var10;
      }

      var0.sendSuccess(Component.literal("Armorstands with trimmed armor spawned around you"), true);
      return 1;
   }
}
