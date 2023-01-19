package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

public class MobBucketItem extends BucketItem {
   private final EntityType<?> type;
   private final SoundEvent emptySound;

   public MobBucketItem(EntityType<?> var1, Fluid var2, SoundEvent var3, Item.Properties var4) {
      super(var2, var4);
      this.type = var1;
      this.emptySound = var3;
   }

   @Override
   public void checkExtraContent(@Nullable Player var1, Level var2, ItemStack var3, BlockPos var4) {
      if (var2 instanceof ServerLevel) {
         this.spawn((ServerLevel)var2, var3, var4);
         var2.gameEvent(var1, GameEvent.ENTITY_PLACE, var4);
      }
   }

   @Override
   protected void playEmptySound(@Nullable Player var1, LevelAccessor var2, BlockPos var3) {
      var2.playSound(var1, var3, this.emptySound, SoundSource.NEUTRAL, 1.0F, 1.0F);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void spawn(ServerLevel var1, ItemStack var2, BlockPos var3) {
      Entity var4 = this.type.spawn(var1, var2, null, var3, MobSpawnType.BUCKET, true, false);
      if (var4 instanceof Bucketable var5) {
         var5.loadFromBucketTag(var2.getOrCreateTag());
         var5.setFromBucket(true);
      }
   }

   @Override
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
                  var3.add(Component.translatable(TropicalFish.getPredefinedName(var10)).withStyle(var7));
                  return;
               }
            }

            var3.add(Component.translatable(TropicalFish.getFishTypeName(var6)).withStyle(var7));
            MutableComponent var11 = Component.translatable(var8);
            if (!var8.equals(var9)) {
               var11.append(", ").append(Component.translatable(var9));
            }

            var11.withStyle(var7);
            var3.add(var11);
         }
      }
   }
}