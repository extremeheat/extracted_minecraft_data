package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

public interface DispenseBehaviorTypes {
   static void bootstrap(final WritableRegistry<MapCodec<? extends DispenseBehavior>> registry) {
      Registry.register(registry, (String)"brush_armadillo", BrushArmadilloDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"charge_respawn_anchor", ChargeRespawnAnchorDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"convert_to_mud", ConvertToMudDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"drop_item", DropItemDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"empty_bucket", EmptyBucketDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"equip", EquipDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"equip_chest", EquipChestBehavior.MAP_CODEC);
      Registry.register(registry, (String)"equip_sulfur_cube", EquipSulfurCubeDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"fill_bottle", FillBottleDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"fill_bucket", FillBucketDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"grow_plant", GrowPlantDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"ignite", IgniteDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"insert_into_container", InsertIntoContainerDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"place_armor_stand", PlaceArmorStandDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"place_boat", PlaceBoatDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"place_minecart", PlaceMinecartDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"place_block", PlaceBlockDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"shear", ShearDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"shoot_projectile", ShootProjectileDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"spawn_golem_from_carved_pumpkin", SpawnGolemFromCarvedPumpkinDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"spawn_mob", SpawnMobDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"spawn_tnt", SpawnTNTDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"spawn_wither_from_wither_skeleton_skull", SpawnWitherFromWitherSkeletonSkullDispenseBehavior.MAP_CODEC);
      Registry.register(registry, (String)"transform_block", TransformBlockDispenseBehavior.MAP_CODEC);
   }
}
