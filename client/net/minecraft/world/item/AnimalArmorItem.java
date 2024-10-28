package net.minecraft.world.item;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.equipment.ArmorMaterial;

public class AnimalArmorItem extends Item {
   private final BodyType bodyType;

   public AnimalArmorItem(ArmorMaterial var1, BodyType var2, Item.Properties var3) {
      super(var1.animalProperties(var3, var2.allowedEntities));
      this.bodyType = var2;
   }

   public AnimalArmorItem(ArmorMaterial var1, BodyType var2, Holder<SoundEvent> var3, boolean var4, Item.Properties var5) {
      super(var1.animalProperties(var5, var3, var4, var2.allowedEntities));
      this.bodyType = var2;
   }

   public SoundEvent getBreakingSound() {
      return this.bodyType.breakingSound;
   }

   public static enum BodyType {
      EQUESTRIAN(SoundEvents.ITEM_BREAK, new EntityType[]{EntityType.HORSE}),
      CANINE(SoundEvents.WOLF_ARMOR_BREAK, new EntityType[]{EntityType.WOLF});

      final SoundEvent breakingSound;
      final HolderSet<EntityType<?>> allowedEntities;

      private BodyType(final SoundEvent var3, final EntityType... var4) {
         this.breakingSound = var3;
         this.allowedEntities = HolderSet.direct(EntityType::builtInRegistryHolder, (Object[])var4);
      }

      // $FF: synthetic method
      private static BodyType[] $values() {
         return new BodyType[]{EQUESTRIAN, CANINE};
      }
   }
}
