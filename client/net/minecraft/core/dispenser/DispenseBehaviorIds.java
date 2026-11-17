package net.minecraft.core.dispenser;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface DispenseBehaviorIds {
   ResourceKey<List<DispenseBehavior>> ARMOR_STAND = key("armor_stand");
   ResourceKey<List<DispenseBehavior>> BOAT = key("boat");
   ResourceKey<List<DispenseBehavior>> BONE_MEAL = key("bone_meal");
   ResourceKey<List<DispenseBehavior>> BRUSH = key("brush");
   ResourceKey<List<DispenseBehavior>> CARVED_PUMPKIN = key("carved_pumpkin");
   ResourceKey<List<DispenseBehavior>> CHEST = key("chest");
   ResourceKey<List<DispenseBehavior>> DEFAULT = key("default");
   ResourceKey<List<DispenseBehavior>> DEFAULT_PROJECTILE = key("default_projectile");
   ResourceKey<List<DispenseBehavior>> DROPPER = key("dropper");
   ResourceKey<List<DispenseBehavior>> EMPTY_BUCKET = key("empty_bucket");
   ResourceKey<List<DispenseBehavior>> EQUIPMENT = key("equipment");
   ResourceKey<List<DispenseBehavior>> FILLED_BUCKET = key("filled_bucket");
   ResourceKey<List<DispenseBehavior>> FIRE_CHARGE = key("fire_charge");
   ResourceKey<List<DispenseBehavior>> FIREWORK_ROCKET = key("firework_rocket");
   ResourceKey<List<DispenseBehavior>> FLINT_AND_STEEL = key("flint_and_steel");
   ResourceKey<List<DispenseBehavior>> GLASS_BOTTLE = key("glass_bottle");
   ResourceKey<List<DispenseBehavior>> GLOWSTONE = key("glowstone");
   ResourceKey<List<DispenseBehavior>> HONEYCOMB = key("honeycomb");
   ResourceKey<List<DispenseBehavior>> MINECART = key("minecart");
   ResourceKey<List<DispenseBehavior>> POTION = key("potion");
   ResourceKey<List<DispenseBehavior>> SHEARS = key("shears");
   ResourceKey<List<DispenseBehavior>> SHULKER_BOX = key("shulker_box");
   ResourceKey<List<DispenseBehavior>> SPAWN_EGG = key("spawn_egg");
   ResourceKey<List<DispenseBehavior>> THROWN_POTION = key("thrown_potion");
   ResourceKey<List<DispenseBehavior>> TNT = key("tnt");
   ResourceKey<List<DispenseBehavior>> WIND_CHARGE = key("wind_charge");
   ResourceKey<List<DispenseBehavior>> WITHER_SKELETON_SKULL = key("wither_skeleton_skull");

   private static ResourceKey<List<DispenseBehavior>> key(final String id) {
      return ResourceKey.create(Registries.DISPENSE_BEHAVIOR, Identifier.withDefaultNamespace(id));
   }
}
