package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import net.minecraft.world.level.Level;

public class SpawnArmorTrimsCommand {
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
      Registry var4 = var2.registryAccess().lookupOrThrow(Registries.TRIM_PATTERN);
      Registry var5 = var2.registryAccess().lookupOrThrow(Registries.TRIM_MATERIAL);
      HolderLookup var6 = var2.holderLookup(Registries.ITEM);
      Map var7 = var6.listElements().map(Holder.Reference::value).filter(var0x -> {
         Equippable var1x = var0x.components().get(DataComponents.EQUIPPABLE);
         return var1x != null && var1x.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR && var1x.model().isPresent();
      }).collect(Collectors.groupingBy(var0x -> var0x.components().get(DataComponents.EQUIPPABLE).model().get()));
      var4.stream()
         .sorted(Comparator.comparing(var1x -> TRIM_PATTERN_ORDER.applyAsInt(var4.getResourceKey(var1x).orElse(null))))
         .forEachOrdered(
            var3x -> var5.stream()
                  .sorted(Comparator.comparing(var1xx -> TRIM_MATERIAL_ORDER.applyAsInt(var5.getResourceKey(var1xx).orElse(null))))
                  .forEachOrdered(var4x -> var3.add(new ArmorTrim(var5.wrapAsHolder(var4x), var4.wrapAsHolder(var3x))))
         );
      BlockPos var8 = var1.blockPosition().relative(var1.getDirection(), 5);
      int var9 = var7.size() - 1;
      double var10 = 3.0;
      int var12 = 0;
      int var13 = 0;

      for (ArmorTrim var15 : var3) {
         for (List var17 : var7.values()) {
            double var18 = (double)var8.getX() + 0.5 - (double)(var12 % var5.size()) * 3.0;
            double var20 = (double)var8.getY() + 0.5 + (double)(var13 % var9) * 3.0;
            double var22 = (double)var8.getZ() + 0.5 + (double)(var12 / var5.size() * 10);
            ArmorStand var24 = new ArmorStand(var2, var18, var20, var22);
            var24.setYRot(180.0F);
            var24.setNoGravity(true);

            for (Item var26 : var17) {
               Equippable var27 = Objects.requireNonNull(var26.components().get(DataComponents.EQUIPPABLE));
               ItemStack var28 = new ItemStack(var26);
               var28.set(DataComponents.TRIM, var15);
               var24.setItemSlot(var27.slot(), var28);
               if (var28.is(Items.TURTLE_HELMET)) {
                  var24.setCustomName(var15.pattern().value().copyWithStyle(var15.material()).copy().append(" ").append(var15.material().value().description()));
                  var24.setCustomNameVisible(true);
               } else {
                  var24.setInvisible(true);
               }
            }

            var2.addFreshEntity(var24);
            var13++;
         }

         var12++;
      }

      var0.sendSuccess(() -> Component.literal("Armorstands with trimmed armor spawned around you"), true);
      return 1;
   }
}
