package net.minecraft.world.item;

import net.minecraft.core.HolderSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.equipment.ArmorMaterial;

public class AnimalArmorItem extends Item {
   private final AnimalArmorItem.BodyType bodyType;

   public AnimalArmorItem(ArmorMaterial var1, AnimalArmorItem.BodyType var2, Item.Properties var3) {
      super(var1.animalProperties(var3, var2.allowedEntities));
      this.bodyType = var2;
   }

   public AnimalArmorItem(ArmorMaterial var1, AnimalArmorItem.BodyType var2, SoundEvent var3, Item.Properties var4) {
      super(var1.animalProperties(var4, var3, var2.allowedEntities));
      this.bodyType = var2;
   }

   @Override
   public SoundEvent getBreakingSound() {
      return this.bodyType.breakingSound;
   }

   public static enum BodyType {
      EQUESTRIAN(SoundEvents.ITEM_BREAK, EntityType.HORSE),
      CANINE(SoundEvents.WOLF_ARMOR_BREAK, EntityType.WOLF);

      final SoundEvent breakingSound;
      final HolderSet<EntityType<?>> allowedEntities;

      private BodyType(final SoundEvent nullxx, final EntityType<?>... nullxxx) {
         this.breakingSound = nullxx;
         this.allowedEntities = HolderSet.direct(EntityType::builtInRegistryHolder, nullxxx);
      }
   }
}
