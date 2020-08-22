package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;

public class BlockItem extends Item {
   @Deprecated
   private final Block block;

   public BlockItem(Block var1, Item.Properties var2) {
      super(var2);
      this.block = var1;
   }

   public InteractionResult useOn(UseOnContext var1) {
      InteractionResult var2 = this.place(new BlockPlaceContext(var1));
      return var2 != InteractionResult.SUCCESS && this.isEdible() ? this.use(var1.level, var1.player, var1.hand).getResult() : var2;
   }

   public InteractionResult place(BlockPlaceContext var1) {
      if (!var1.canPlace()) {
         return InteractionResult.FAIL;
      } else {
         BlockPlaceContext var2 = this.updatePlacementContext(var1);
         if (var2 == null) {
            return InteractionResult.FAIL;
         } else {
            BlockState var3 = this.getPlacementState(var2);
            if (var3 == null) {
               return InteractionResult.FAIL;
            } else if (!this.placeBlock(var2, var3)) {
               return InteractionResult.FAIL;
            } else {
               BlockPos var4 = var2.getClickedPos();
               Level var5 = var2.getLevel();
               Player var6 = var2.getPlayer();
               ItemStack var7 = var2.getItemInHand();
               BlockState var8 = var5.getBlockState(var4);
               Block var9 = var8.getBlock();
               if (var9 == var3.getBlock()) {
                  var8 = this.updateBlockStateFromTag(var4, var5, var7, var8);
                  this.updateCustomBlockEntityTag(var4, var5, var6, var7, var8);
                  var9.setPlacedBy(var5, var4, var8, var6, var7);
                  if (var6 instanceof ServerPlayer) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var6, var4, var7);
                  }
               }

               SoundType var10 = var8.getSoundType();
               var5.playSound(var6, var4, this.getPlaceSound(var8), SoundSource.BLOCKS, (var10.getVolume() + 1.0F) / 2.0F, var10.getPitch() * 0.8F);
               var7.shrink(1);
               return InteractionResult.SUCCESS;
            }
         }
      }
   }

   protected SoundEvent getPlaceSound(BlockState var1) {
      return var1.getSoundType().getPlaceSound();
   }

   @Nullable
   public BlockPlaceContext updatePlacementContext(BlockPlaceContext var1) {
      return var1;
   }

   protected boolean updateCustomBlockEntityTag(BlockPos var1, Level var2, @Nullable Player var3, ItemStack var4, BlockState var5) {
      return updateCustomBlockEntityTag(var2, var3, var1, var4);
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext var1) {
      BlockState var2 = this.getBlock().getStateForPlacement(var1);
      return var2 != null && this.canPlace(var1, var2) ? var2 : null;
   }

   private BlockState updateBlockStateFromTag(BlockPos var1, Level var2, ItemStack var3, BlockState var4) {
      BlockState var5 = var4;
      CompoundTag var6 = var3.getTag();
      if (var6 != null) {
         CompoundTag var7 = var6.getCompound("BlockStateTag");
         StateDefinition var8 = var4.getBlock().getStateDefinition();
         Iterator var9 = var7.getAllKeys().iterator();

         while(var9.hasNext()) {
            String var10 = (String)var9.next();
            Property var11 = var8.getProperty(var10);
            if (var11 != null) {
               String var12 = var7.get(var10).getAsString();
               var5 = updateState(var5, var11, var12);
            }
         }
      }

      if (var5 != var4) {
         var2.setBlock(var1, var5, 2);
      }

      return var5;
   }

   private static BlockState updateState(BlockState var0, Property var1, String var2) {
      return (BlockState)var1.getValue(var2).map((var2x) -> {
         return (BlockState)var0.setValue(var1, var2x);
      }).orElse(var0);
   }

   protected boolean canPlace(BlockPlaceContext var1, BlockState var2) {
      Player var3 = var1.getPlayer();
      CollisionContext var4 = var3 == null ? CollisionContext.empty() : CollisionContext.of(var3);
      return (!this.mustSurvive() || var2.canSurvive(var1.getLevel(), var1.getClickedPos())) && var1.getLevel().isUnobstructed(var2, var1.getClickedPos(), var4);
   }

   protected boolean mustSurvive() {
      return true;
   }

   protected boolean placeBlock(BlockPlaceContext var1, BlockState var2) {
      return var1.getLevel().setBlock(var1.getClickedPos(), var2, 11);
   }

   public static boolean updateCustomBlockEntityTag(Level var0, @Nullable Player var1, BlockPos var2, ItemStack var3) {
      MinecraftServer var4 = var0.getServer();
      if (var4 == null) {
         return false;
      } else {
         CompoundTag var5 = var3.getTagElement("BlockEntityTag");
         if (var5 != null) {
            BlockEntity var6 = var0.getBlockEntity(var2);
            if (var6 != null) {
               if (!var0.isClientSide && var6.onlyOpCanSetNbt() && (var1 == null || !var1.canUseGameMasterBlocks())) {
                  return false;
               }

               CompoundTag var7 = var6.save(new CompoundTag());
               CompoundTag var8 = var7.copy();
               var7.merge(var5);
               var7.putInt("x", var2.getX());
               var7.putInt("y", var2.getY());
               var7.putInt("z", var2.getZ());
               if (!var7.equals(var8)) {
                  var6.load(var7);
                  var6.setChanged();
                  return true;
               }
            }
         }

         return false;
      }
   }

   public String getDescriptionId() {
      return this.getBlock().getDescriptionId();
   }

   public void fillItemCategory(CreativeModeTab var1, NonNullList var2) {
      if (this.allowdedIn(var1)) {
         this.getBlock().fillItemCategory(var1, var2);
      }

   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      this.getBlock().appendHoverText(var1, var2, var3, var4);
   }

   public Block getBlock() {
      return this.block;
   }

   public void registerBlocks(Map var1, Item var2) {
      var1.put(this.getBlock(), var2);
   }
}
