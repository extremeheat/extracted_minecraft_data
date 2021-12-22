package net.minecraft.world.item;

import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompassItem extends Item implements Vanishable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final String TAG_LODESTONE_POS = "LodestonePos";
   public static final String TAG_LODESTONE_DIMENSION = "LodestoneDimension";
   public static final String TAG_LODESTONE_TRACKED = "LodestoneTracked";

   public CompassItem(Item.Properties var1) {
      super(var1);
   }

   public static boolean isLodestoneCompass(ItemStack var0) {
      CompoundTag var1 = var0.getTag();
      return var1 != null && (var1.contains("LodestoneDimension") || var1.contains("LodestonePos"));
   }

   public boolean isFoil(ItemStack var1) {
      return isLodestoneCompass(var1) || super.isFoil(var1);
   }

   public static Optional<ResourceKey<Level>> getLodestoneDimension(CompoundTag var0) {
      return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, var0.get("LodestoneDimension")).result();
   }

   public void inventoryTick(ItemStack var1, Level var2, Entity var3, int var4, boolean var5) {
      if (!var2.isClientSide) {
         if (isLodestoneCompass(var1)) {
            CompoundTag var6 = var1.getOrCreateTag();
            if (var6.contains("LodestoneTracked") && !var6.getBoolean("LodestoneTracked")) {
               return;
            }

            Optional var7 = getLodestoneDimension(var6);
            if (var7.isPresent() && var7.get() == var2.dimension() && var6.contains("LodestonePos")) {
               BlockPos var8 = NbtUtils.readBlockPos(var6.getCompound("LodestonePos"));
               if (!var2.isInWorldBounds(var8) || !((ServerLevel)var2).getPoiManager().existsAtPosition(PoiType.LODESTONE, var8)) {
                  var6.remove("LodestonePos");
               }
            }
         }

      }
   }

   public InteractionResult useOn(UseOnContext var1) {
      BlockPos var2 = var1.getClickedPos();
      Level var3 = var1.getLevel();
      if (!var3.getBlockState(var2).is(Blocks.LODESTONE)) {
         return super.useOn(var1);
      } else {
         var3.playSound((Player)null, (BlockPos)var2, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
         Player var4 = var1.getPlayer();
         ItemStack var5 = var1.getItemInHand();
         boolean var6 = !var4.getAbilities().instabuild && var5.getCount() == 1;
         if (var6) {
            this.addLodestoneTags(var3.dimension(), var2, var5.getOrCreateTag());
         } else {
            ItemStack var7 = new ItemStack(Items.COMPASS, 1);
            CompoundTag var8 = var5.hasTag() ? var5.getTag().copy() : new CompoundTag();
            var7.setTag(var8);
            if (!var4.getAbilities().instabuild) {
               var5.shrink(1);
            }

            this.addLodestoneTags(var3.dimension(), var2, var8);
            if (!var4.getInventory().add(var7)) {
               var4.drop(var7, false);
            }
         }

         return InteractionResult.sidedSuccess(var3.isClientSide);
      }
   }

   private void addLodestoneTags(ResourceKey<Level> var1, BlockPos var2, CompoundTag var3) {
      var3.put("LodestonePos", NbtUtils.writeBlockPos(var2));
      DataResult var10000 = Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, var1);
      Logger var10001 = LOGGER;
      Objects.requireNonNull(var10001);
      var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
         var3.put("LodestoneDimension", var1x);
      });
      var3.putBoolean("LodestoneTracked", true);
   }

   public String getDescriptionId(ItemStack var1) {
      return isLodestoneCompass(var1) ? "item.minecraft.lodestone_compass" : super.getDescriptionId(var1);
   }
}
