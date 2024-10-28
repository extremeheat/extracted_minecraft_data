package net.minecraft.world.item;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

public class MobBucketItem extends BucketItem {
   private static final MapCodec<TropicalFish.Variant> VARIANT_FIELD_CODEC;
   private final EntityType<?> type;
   private final SoundEvent emptySound;

   public MobBucketItem(EntityType<?> var1, Fluid var2, SoundEvent var3, Item.Properties var4) {
      super(var2, var4);
      this.type = var1;
      this.emptySound = var3;
   }

   public void checkExtraContent(@Nullable Player var1, Level var2, ItemStack var3, BlockPos var4) {
      if (var2 instanceof ServerLevel) {
         this.spawn((ServerLevel)var2, var3, var4);
         var2.gameEvent(var1, GameEvent.ENTITY_PLACE, var4);
      }

   }

   protected void playEmptySound(@Nullable Player var1, LevelAccessor var2, BlockPos var3) {
      var2.playSound(var1, var3, this.emptySound, SoundSource.NEUTRAL, 1.0F, 1.0F);
   }

   private void spawn(ServerLevel var1, ItemStack var2, BlockPos var3) {
      Entity var4 = this.type.create(var1, EntityType.createDefaultStackConfig(var1, var2, (Player)null), var3, EntitySpawnReason.BUCKET, true, false);
      if (var4 instanceof Bucketable var5) {
         CustomData var6 = (CustomData)var2.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
         var5.loadFromBucketTag(var6.copyTag());
         var5.setFromBucket(true);
      }

      if (var4 != null) {
         var1.addFreshEntityWithPassengers(var4);
      }

   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      if (this.type == EntityType.TROPICAL_FISH) {
         CustomData var5 = (CustomData)var1.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
         if (var5.isEmpty()) {
            return;
         }

         Optional var6 = var5.read(VARIANT_FIELD_CODEC).result();
         if (var6.isPresent()) {
            TropicalFish.Variant var7 = (TropicalFish.Variant)var6.get();
            ChatFormatting[] var8 = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};
            String var9 = "color.minecraft." + String.valueOf(var7.baseColor());
            String var10 = "color.minecraft." + String.valueOf(var7.patternColor());
            int var11 = TropicalFish.COMMON_VARIANTS.indexOf(var7);
            if (var11 != -1) {
               var3.add(Component.translatable(TropicalFish.getPredefinedName(var11)).withStyle(var8));
               return;
            }

            var3.add(var7.pattern().displayName().plainCopy().withStyle(var8));
            MutableComponent var12 = Component.translatable(var9);
            if (!var9.equals(var10)) {
               var12.append(", ").append((Component)Component.translatable(var10));
            }

            var12.withStyle(var8);
            var3.add(var12);
         }
      }

   }

   static {
      VARIANT_FIELD_CODEC = TropicalFish.Variant.CODEC.fieldOf("BucketVariantTag");
   }
}
