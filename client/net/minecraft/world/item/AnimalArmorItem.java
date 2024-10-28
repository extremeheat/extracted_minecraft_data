package net.minecraft.world.item;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class AnimalArmorItem extends ArmorItem {
   private final ResourceLocation textureLocation;
   @Nullable
   private final ResourceLocation overlayTextureLocation;
   private final BodyType bodyType;

   public AnimalArmorItem(Holder<ArmorMaterial> var1, BodyType var2, boolean var3, Item.Properties var4) {
      super(var1, ArmorItem.Type.BODY, var4);
      this.bodyType = var2;
      ResourceLocation var5 = (ResourceLocation)var2.textureLocator.apply(((ResourceKey)var1.unwrapKey().orElseThrow()).location());
      this.textureLocation = var5.withSuffix(".png");
      if (var3) {
         this.overlayTextureLocation = var5.withSuffix("_overlay.png");
      } else {
         this.overlayTextureLocation = null;
      }

   }

   public ResourceLocation getTexture() {
      return this.textureLocation;
   }

   @Nullable
   public ResourceLocation getOverlayTexture() {
      return this.overlayTextureLocation;
   }

   public BodyType getBodyType() {
      return this.bodyType;
   }

   public SoundEvent getBreakingSound() {
      return this.bodyType.breakingSound;
   }

   public boolean isEnchantable(ItemStack var1) {
      return false;
   }

   public static enum BodyType {
      EQUESTRIAN((var0) -> {
         return var0.withPath((var0x) -> {
            return "textures/entity/horse/armor/horse_armor_" + var0x;
         });
      }, SoundEvents.ITEM_BREAK),
      CANINE((var0) -> {
         return var0.withPath("textures/entity/wolf/wolf_armor");
      }, SoundEvents.WOLF_ARMOR_BREAK);

      final Function<ResourceLocation, ResourceLocation> textureLocator;
      final SoundEvent breakingSound;

      private BodyType(final Function var3, final SoundEvent var4) {
         this.textureLocator = var3;
         this.breakingSound = var4;
      }

      // $FF: synthetic method
      private static BodyType[] $values() {
         return new BodyType[]{EQUESTRIAN, CANINE};
      }
   }
}
