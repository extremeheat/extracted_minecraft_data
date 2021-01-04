package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;

public class FishBucketItem extends BucketItem {
   private final EntityType<?> type;

   public FishBucketItem(EntityType<?> var1, Fluid var2, Item.Properties var3) {
      super(var2, var3);
      this.type = var1;
   }

   public void checkExtraContent(Level var1, ItemStack var2, BlockPos var3) {
      if (!var1.isClientSide) {
         this.spawn(var1, var2, var3);
      }

   }

   protected void playEmptySound(@Nullable Player var1, LevelAccessor var2, BlockPos var3) {
      var2.playSound(var1, var3, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
   }

   private void spawn(Level var1, ItemStack var2, BlockPos var3) {
      Entity var4 = this.type.spawn(var1, var2, (Player)null, var3, MobSpawnType.BUCKET, true, false);
      if (var4 != null) {
         ((AbstractFish)var4).setFromBucket(true);
      }

   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      if (this.type == EntityType.TROPICAL_FISH) {
         CompoundTag var5 = var1.getTag();
         if (var5 != null && var5.contains("BucketVariantTag", 3)) {
            int var6 = var5.getInt("BucketVariantTag");
            ChatFormatting[] var7 = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};
            String var8 = "color.minecraft." + TropicalFish.getBaseColor(var6);
            String var9 = "color.minecraft." + TropicalFish.getPatternColor(var6);

            for(int var10 = 0; var10 < TropicalFish.COMMON_VARIANTS.length; ++var10) {
               if (var6 == TropicalFish.COMMON_VARIANTS[var10]) {
                  var3.add((new TranslatableComponent(TropicalFish.getPredefinedName(var10), new Object[0])).withStyle(var7));
                  return;
               }
            }

            var3.add((new TranslatableComponent(TropicalFish.getFishTypeName(var6), new Object[0])).withStyle(var7));
            TranslatableComponent var11 = new TranslatableComponent(var8, new Object[0]);
            if (!var8.equals(var9)) {
               var11.append(", ").append((Component)(new TranslatableComponent(var9, new Object[0])));
            }

            var11.withStyle(var7);
            var3.add(var11);
         }
      }

   }
}
