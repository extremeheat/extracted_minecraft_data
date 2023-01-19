package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;

public class EntityUUIDFix extends AbstractUUIDFix {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<String> ABSTRACT_HORSES = Sets.newHashSet();
   private static final Set<String> TAMEABLE_ANIMALS = Sets.newHashSet();
   private static final Set<String> ANIMALS = Sets.newHashSet();
   private static final Set<String> MOBS = Sets.newHashSet();
   private static final Set<String> LIVING_ENTITIES = Sets.newHashSet();
   private static final Set<String> PROJECTILES = Sets.newHashSet();

   public EntityUUIDFix(Schema var1) {
      super(var1, References.ENTITY);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityUUIDFixes", this.getInputSchema().getType(this.typeReference), var1 -> {
         var1 = var1.update(DSL.remainderFinder(), EntityUUIDFix::updateEntityUUID);

         for(String var3 : ABSTRACT_HORSES) {
            var1 = this.updateNamedChoice(var1, var3, EntityUUIDFix::updateAnimalOwner);
         }

         for(String var18 : TAMEABLE_ANIMALS) {
            var1 = this.updateNamedChoice(var1, var18, EntityUUIDFix::updateAnimalOwner);
         }

         for(String var19 : ANIMALS) {
            var1 = this.updateNamedChoice(var1, var19, EntityUUIDFix::updateAnimal);
         }

         for(String var20 : MOBS) {
            var1 = this.updateNamedChoice(var1, var20, EntityUUIDFix::updateMob);
         }

         for(String var21 : LIVING_ENTITIES) {
            var1 = this.updateNamedChoice(var1, var21, EntityUUIDFix::updateLivingEntity);
         }

         for(String var22 : PROJECTILES) {
            var1 = this.updateNamedChoice(var1, var22, EntityUUIDFix::updateProjectile);
         }

         var1 = this.updateNamedChoice(var1, "minecraft:bee", EntityUUIDFix::updateHurtBy);
         var1 = this.updateNamedChoice(var1, "minecraft:zombified_piglin", EntityUUIDFix::updateHurtBy);
         var1 = this.updateNamedChoice(var1, "minecraft:fox", EntityUUIDFix::updateFox);
         var1 = this.updateNamedChoice(var1, "minecraft:item", EntityUUIDFix::updateItem);
         var1 = this.updateNamedChoice(var1, "minecraft:shulker_bullet", EntityUUIDFix::updateShulkerBullet);
         var1 = this.updateNamedChoice(var1, "minecraft:area_effect_cloud", EntityUUIDFix::updateAreaEffectCloud);
         var1 = this.updateNamedChoice(var1, "minecraft:zombie_villager", EntityUUIDFix::updateZombieVillager);
         var1 = this.updateNamedChoice(var1, "minecraft:evoker_fangs", EntityUUIDFix::updateEvokerFangs);
         return this.updateNamedChoice(var1, "minecraft:piglin", EntityUUIDFix::updatePiglin);
      });
   }

   private static Dynamic<?> updatePiglin(Dynamic<?> var0) {
      return var0.update(
         "Brain",
         var0x -> var0x.update(
               "memories", var0xx -> var0xx.update("minecraft:angry_at", var0xxx -> (Dynamic)replaceUUIDString(var0xxx, "value", "value").orElseGet(() -> {
                        LOGGER.warn("angry_at has no value.");
                        return var0xxx;
                     }))
            )
      );
   }

   private static Dynamic<?> updateEvokerFangs(Dynamic<?> var0) {
      return (Dynamic<?>)replaceUUIDLeastMost(var0, "OwnerUUID", "Owner").orElse(var0);
   }

   private static Dynamic<?> updateZombieVillager(Dynamic<?> var0) {
      return (Dynamic<?>)replaceUUIDLeastMost(var0, "ConversionPlayer", "ConversionPlayer").orElse(var0);
   }

   private static Dynamic<?> updateAreaEffectCloud(Dynamic<?> var0) {
      return (Dynamic<?>)replaceUUIDLeastMost(var0, "OwnerUUID", "Owner").orElse(var0);
   }

   private static Dynamic<?> updateShulkerBullet(Dynamic<?> var0) {
      var0 = (Dynamic)replaceUUIDMLTag(var0, "Owner", "Owner").orElse(var0);
      return (Dynamic<?>)replaceUUIDMLTag(var0, "Target", "Target").orElse(var0);
   }

   private static Dynamic<?> updateItem(Dynamic<?> var0) {
      var0 = (Dynamic)replaceUUIDMLTag(var0, "Owner", "Owner").orElse(var0);
      return (Dynamic<?>)replaceUUIDMLTag(var0, "Thrower", "Thrower").orElse(var0);
   }

   private static Dynamic<?> updateFox(Dynamic<?> var0) {
      Optional var1 = var0.get("TrustedUUIDs")
         .result()
         .map(var1x -> var0.createList(var1x.asStream().map(var0xx -> (Dynamic)createUUIDFromML(var0xx).orElseGet(() -> {
                  LOGGER.warn("Trusted contained invalid data.");
                  return var0xx;
               }))));
      return (Dynamic<?>)DataFixUtils.orElse(var1.map(var1x -> var0.remove("TrustedUUIDs").set("Trusted", var1x)), var0);
   }

   private static Dynamic<?> updateHurtBy(Dynamic<?> var0) {
      return (Dynamic<?>)replaceUUIDString(var0, "HurtBy", "HurtBy").orElse(var0);
   }

   private static Dynamic<?> updateAnimalOwner(Dynamic<?> var0) {
      Dynamic var1 = updateAnimal(var0);
      return (Dynamic<?>)replaceUUIDString(var1, "OwnerUUID", "Owner").orElse(var1);
   }

   private static Dynamic<?> updateAnimal(Dynamic<?> var0) {
      Dynamic var1 = updateMob(var0);
      return (Dynamic<?>)replaceUUIDLeastMost(var1, "LoveCause", "LoveCause").orElse(var1);
   }

   private static Dynamic<?> updateMob(Dynamic<?> var0) {
      return updateLivingEntity(var0).update("Leash", var0x -> (Dynamic)replaceUUIDLeastMost(var0x, "UUID", "UUID").orElse(var0x));
   }

   public static Dynamic<?> updateLivingEntity(Dynamic<?> var0) {
      return var0.update(
         "Attributes",
         var1 -> var0.createList(
               var1.asStream()
                  .map(
                     var0xx -> var0xx.update(
                           "Modifiers",
                           var1x -> var0xx.createList(
                                 var1x.asStream().map(var0xxxx -> (Dynamic)replaceUUIDLeastMost(var0xxxx, "UUID", "UUID").orElse(var0xxxx))
                              )
                        )
                  )
            )
      );
   }

   private static Dynamic<?> updateProjectile(Dynamic<?> var0) {
      return (Dynamic<?>)DataFixUtils.orElse(var0.get("OwnerUUID").result().map(var1 -> var0.remove("OwnerUUID").set("Owner", var1)), var0);
   }

   public static Dynamic<?> updateEntityUUID(Dynamic<?> var0) {
      return (Dynamic<?>)replaceUUIDLeastMost(var0, "UUID", "UUID").orElse(var0);
   }

   static {
      ABSTRACT_HORSES.add("minecraft:donkey");
      ABSTRACT_HORSES.add("minecraft:horse");
      ABSTRACT_HORSES.add("minecraft:llama");
      ABSTRACT_HORSES.add("minecraft:mule");
      ABSTRACT_HORSES.add("minecraft:skeleton_horse");
      ABSTRACT_HORSES.add("minecraft:trader_llama");
      ABSTRACT_HORSES.add("minecraft:zombie_horse");
      TAMEABLE_ANIMALS.add("minecraft:cat");
      TAMEABLE_ANIMALS.add("minecraft:parrot");
      TAMEABLE_ANIMALS.add("minecraft:wolf");
      ANIMALS.add("minecraft:bee");
      ANIMALS.add("minecraft:chicken");
      ANIMALS.add("minecraft:cow");
      ANIMALS.add("minecraft:fox");
      ANIMALS.add("minecraft:mooshroom");
      ANIMALS.add("minecraft:ocelot");
      ANIMALS.add("minecraft:panda");
      ANIMALS.add("minecraft:pig");
      ANIMALS.add("minecraft:polar_bear");
      ANIMALS.add("minecraft:rabbit");
      ANIMALS.add("minecraft:sheep");
      ANIMALS.add("minecraft:turtle");
      ANIMALS.add("minecraft:hoglin");
      MOBS.add("minecraft:bat");
      MOBS.add("minecraft:blaze");
      MOBS.add("minecraft:cave_spider");
      MOBS.add("minecraft:cod");
      MOBS.add("minecraft:creeper");
      MOBS.add("minecraft:dolphin");
      MOBS.add("minecraft:drowned");
      MOBS.add("minecraft:elder_guardian");
      MOBS.add("minecraft:ender_dragon");
      MOBS.add("minecraft:enderman");
      MOBS.add("minecraft:endermite");
      MOBS.add("minecraft:evoker");
      MOBS.add("minecraft:ghast");
      MOBS.add("minecraft:giant");
      MOBS.add("minecraft:guardian");
      MOBS.add("minecraft:husk");
      MOBS.add("minecraft:illusioner");
      MOBS.add("minecraft:magma_cube");
      MOBS.add("minecraft:pufferfish");
      MOBS.add("minecraft:zombified_piglin");
      MOBS.add("minecraft:salmon");
      MOBS.add("minecraft:shulker");
      MOBS.add("minecraft:silverfish");
      MOBS.add("minecraft:skeleton");
      MOBS.add("minecraft:slime");
      MOBS.add("minecraft:snow_golem");
      MOBS.add("minecraft:spider");
      MOBS.add("minecraft:squid");
      MOBS.add("minecraft:stray");
      MOBS.add("minecraft:tropical_fish");
      MOBS.add("minecraft:vex");
      MOBS.add("minecraft:villager");
      MOBS.add("minecraft:iron_golem");
      MOBS.add("minecraft:vindicator");
      MOBS.add("minecraft:pillager");
      MOBS.add("minecraft:wandering_trader");
      MOBS.add("minecraft:witch");
      MOBS.add("minecraft:wither");
      MOBS.add("minecraft:wither_skeleton");
      MOBS.add("minecraft:zombie");
      MOBS.add("minecraft:zombie_villager");
      MOBS.add("minecraft:phantom");
      MOBS.add("minecraft:ravager");
      MOBS.add("minecraft:piglin");
      LIVING_ENTITIES.add("minecraft:armor_stand");
      PROJECTILES.add("minecraft:arrow");
      PROJECTILES.add("minecraft:dragon_fireball");
      PROJECTILES.add("minecraft:firework_rocket");
      PROJECTILES.add("minecraft:fireball");
      PROJECTILES.add("minecraft:llama_spit");
      PROJECTILES.add("minecraft:small_fireball");
      PROJECTILES.add("minecraft:snowball");
      PROJECTILES.add("minecraft:spectral_arrow");
      PROJECTILES.add("minecraft:egg");
      PROJECTILES.add("minecraft:ender_pearl");
      PROJECTILES.add("minecraft:experience_bottle");
      PROJECTILES.add("minecraft:potion");
      PROJECTILES.add("minecraft:trident");
      PROJECTILES.add("minecraft:wither_skull");
   }
}
