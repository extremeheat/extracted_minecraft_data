package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapItem extends ComplexItem {
   public static final int IMAGE_WIDTH = 128;
   public static final int IMAGE_HEIGHT = 128;

   public MapItem(Item.Properties var1) {
      super(var1);
   }

   public static ItemStack create(Level var0, int var1, int var2, byte var3, boolean var4, boolean var5) {
      ItemStack var6 = new ItemStack(Items.FILLED_MAP);
      MapId var7 = createNewSavedData(var0, var1, var2, var3, var4, var5, var0.dimension());
      var6.set(DataComponents.MAP_ID, var7);
      return var6;
   }

   @Nullable
   public static MapItemSavedData getSavedData(@Nullable MapId var0, Level var1) {
      return var0 == null ? null : var1.getMapData(var0);
   }

   @Nullable
   public static MapItemSavedData getSavedData(ItemStack var0, Level var1) {
      MapId var2 = (MapId)var0.get(DataComponents.MAP_ID);
      return getSavedData(var2, var1);
   }

   private static MapId createNewSavedData(Level var0, int var1, int var2, int var3, boolean var4, boolean var5, ResourceKey<Level> var6) {
      MapItemSavedData var7 = MapItemSavedData.createFresh((double)var1, (double)var2, (byte)var3, var4, var5, var6);
      MapId var8 = var0.getFreeMapId();
      var0.setMapData(var8, var7);
      return var8;
   }

   public void update(Level var1, Entity var2, MapItemSavedData var3) {
      if (var1.dimension() == var3.dimension && var2 instanceof Player) {
         int var4 = 1 << var3.scale;
         int var5 = var3.centerX;
         int var6 = var3.centerZ;
         int var7 = Mth.floor(var2.getX() - (double)var5) / var4 + 64;
         int var8 = Mth.floor(var2.getZ() - (double)var6) / var4 + 64;
         int var9 = 128 / var4;
         if (var1.dimensionType().hasCeiling()) {
            var9 /= 2;
         }

         MapItemSavedData.HoldingPlayer var10 = var3.getHoldingPlayer((Player)var2);
         ++var10.step;
         BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
         BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();
         boolean var13 = false;

         for(int var14 = var7 - var9 + 1; var14 < var7 + var9; ++var14) {
            if ((var14 & 15) == (var10.step & 15) || var13) {
               var13 = false;
               double var15 = 0.0;

               for(int var17 = var8 - var9 - 1; var17 < var8 + var9; ++var17) {
                  if (var14 >= 0 && var17 >= -1 && var14 < 128 && var17 < 128) {
                     int var18 = Mth.square(var14 - var7) + Mth.square(var17 - var8);
                     boolean var19 = var18 > (var9 - 2) * (var9 - 2);
                     int var20 = (var5 / var4 + var14 - 64) * var4;
                     int var21 = (var6 / var4 + var17 - 64) * var4;
                     LinkedHashMultiset var22 = LinkedHashMultiset.create();
                     LevelChunk var23 = var1.getChunk(SectionPos.blockToSectionCoord(var20), SectionPos.blockToSectionCoord(var21));
                     if (!var23.isEmpty()) {
                        int var24 = 0;
                        double var25 = 0.0;
                        int var27;
                        if (var1.dimensionType().hasCeiling()) {
                           var27 = var20 + var21 * 231871;
                           var27 = var27 * var27 * 31287121 + var27 * 11;
                           if ((var27 >> 20 & 1) == 0) {
                              var22.add(Blocks.DIRT.defaultBlockState().getMapColor(var1, BlockPos.ZERO), 10);
                           } else {
                              var22.add(Blocks.STONE.defaultBlockState().getMapColor(var1, BlockPos.ZERO), 100);
                           }

                           var25 = 100.0;
                        } else {
                           for(var27 = 0; var27 < var4; ++var27) {
                              for(int var28 = 0; var28 < var4; ++var28) {
                                 var11.set(var20 + var27, 0, var21 + var28);
                                 int var29 = var23.getHeight(Heightmap.Types.WORLD_SURFACE, var11.getX(), var11.getZ()) + 1;
                                 BlockState var30;
                                 if (var29 <= var1.getMinBuildHeight() + 1) {
                                    var30 = Blocks.BEDROCK.defaultBlockState();
                                 } else {
                                    do {
                                       --var29;
                                       var11.setY(var29);
                                       var30 = var23.getBlockState(var11);
                                    } while(var30.getMapColor(var1, var11) == MapColor.NONE && var29 > var1.getMinBuildHeight());

                                    if (var29 > var1.getMinBuildHeight() && !var30.getFluidState().isEmpty()) {
                                       int var31 = var29 - 1;
                                       var12.set(var11);

                                       BlockState var32;
                                       do {
                                          var12.setY(var31--);
                                          var32 = var23.getBlockState(var12);
                                          ++var24;
                                       } while(var31 > var1.getMinBuildHeight() && !var32.getFluidState().isEmpty());

                                       var30 = this.getCorrectStateForFluidBlock(var1, var30, var11);
                                    }
                                 }

                                 var3.checkBanners(var1, var11.getX(), var11.getZ());
                                 var25 += (double)var29 / (double)(var4 * var4);
                                 var22.add(var30.getMapColor(var1, var11));
                              }
                           }
                        }

                        var24 /= var4 * var4;
                        MapColor var33 = (MapColor)Iterables.getFirst(Multisets.copyHighestCountFirst(var22), MapColor.NONE);
                        MapColor.Brightness var34;
                        double var35;
                        if (var33 == MapColor.WATER) {
                           var35 = (double)var24 * 0.1 + (double)(var14 + var17 & 1) * 0.2;
                           if (var35 < 0.5) {
                              var34 = MapColor.Brightness.HIGH;
                           } else if (var35 > 0.9) {
                              var34 = MapColor.Brightness.LOW;
                           } else {
                              var34 = MapColor.Brightness.NORMAL;
                           }
                        } else {
                           var35 = (var25 - var15) * 4.0 / (double)(var4 + 4) + ((double)(var14 + var17 & 1) - 0.5) * 0.4;
                           if (var35 > 0.6) {
                              var34 = MapColor.Brightness.HIGH;
                           } else if (var35 < -0.6) {
                              var34 = MapColor.Brightness.LOW;
                           } else {
                              var34 = MapColor.Brightness.NORMAL;
                           }
                        }

                        var15 = var25;
                        if (var17 >= 0 && var18 < var9 * var9 && (!var19 || (var14 + var17 & 1) != 0)) {
                           var13 |= var3.updateColor(var14, var17, var33.getPackedId(var34));
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private BlockState getCorrectStateForFluidBlock(Level var1, BlockState var2, BlockPos var3) {
      FluidState var4 = var2.getFluidState();
      return !var4.isEmpty() && !var2.isFaceSturdy(var1, var3, Direction.UP) ? var4.createLegacyBlock() : var2;
   }

   private static boolean isBiomeWatery(boolean[] var0, int var1, int var2) {
      return var0[var2 * 128 + var1];
   }

   public static void renderBiomePreviewMap(ServerLevel var0, ItemStack var1) {
      MapItemSavedData var2 = getSavedData((ItemStack)var1, var0);
      if (var2 != null) {
         if (var0.dimension() == var2.dimension) {
            int var3 = 1 << var2.scale;
            int var4 = var2.centerX;
            int var5 = var2.centerZ;
            boolean[] var6 = new boolean[16384];
            int var7 = var4 / var3 - 64;
            int var8 = var5 / var3 - 64;
            BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

            int var10;
            int var11;
            for(var10 = 0; var10 < 128; ++var10) {
               for(var11 = 0; var11 < 128; ++var11) {
                  Holder var12 = var0.getBiome(var9.set((var7 + var11) * var3, 0, (var8 + var10) * var3));
                  var6[var10 * 128 + var11] = var12.is(BiomeTags.WATER_ON_MAP_OUTLINES);
               }
            }

            for(var10 = 1; var10 < 127; ++var10) {
               for(var11 = 1; var11 < 127; ++var11) {
                  int var15 = 0;

                  for(int var13 = -1; var13 < 2; ++var13) {
                     for(int var14 = -1; var14 < 2; ++var14) {
                        if ((var13 != 0 || var14 != 0) && isBiomeWatery(var6, var10 + var13, var11 + var14)) {
                           ++var15;
                        }
                     }
                  }

                  MapColor.Brightness var16 = MapColor.Brightness.LOWEST;
                  MapColor var17 = MapColor.NONE;
                  if (isBiomeWatery(var6, var10, var11)) {
                     var17 = MapColor.COLOR_ORANGE;
                     if (var15 > 7 && var11 % 2 == 0) {
                        switch ((var10 + (int)(Mth.sin((float)var11 + 0.0F) * 7.0F)) / 8 % 5) {
                           case 0:
                           case 4:
                              var16 = MapColor.Brightness.LOW;
                              break;
                           case 1:
                           case 3:
                              var16 = MapColor.Brightness.NORMAL;
                              break;
                           case 2:
                              var16 = MapColor.Brightness.HIGH;
                        }
                     } else if (var15 > 7) {
                        var17 = MapColor.NONE;
                     } else if (var15 > 5) {
                        var16 = MapColor.Brightness.NORMAL;
                     } else if (var15 > 3) {
                        var16 = MapColor.Brightness.LOW;
                     } else if (var15 > 1) {
                        var16 = MapColor.Brightness.LOW;
                     }
                  } else if (var15 > 0) {
                     var17 = MapColor.COLOR_BROWN;
                     if (var15 > 3) {
                        var16 = MapColor.Brightness.NORMAL;
                     } else {
                        var16 = MapColor.Brightness.LOWEST;
                     }
                  }

                  if (var17 != MapColor.NONE) {
                     var2.setColor(var10, var11, var17.getPackedId(var16));
                  }
               }
            }

         }
      }
   }

   public void inventoryTick(ItemStack var1, Level var2, Entity var3, int var4, boolean var5) {
      if (!var2.isClientSide) {
         MapItemSavedData var6 = getSavedData(var1, var2);
         if (var6 != null) {
            if (var3 instanceof Player) {
               Player var7 = (Player)var3;
               var6.tickCarriedBy(var7, var1);
            }

            if (!var6.locked && (var5 || var3 instanceof Player && ((Player)var3).getOffhandItem() == var1)) {
               this.update(var2, var3, var6);
            }

         }
      }
   }

   @Nullable
   public Packet<?> getUpdatePacket(ItemStack var1, Level var2, Player var3) {
      MapId var4 = (MapId)var1.get(DataComponents.MAP_ID);
      MapItemSavedData var5 = getSavedData(var4, var2);
      return var5 != null ? var5.getUpdatePacket(var4, var3) : null;
   }

   public void onCraftedPostProcess(ItemStack var1, Level var2) {
      MapPostProcessing var3 = (MapPostProcessing)var1.remove(DataComponents.MAP_POST_PROCESSING);
      if (var3 != null) {
         switch (var3) {
            case LOCK -> lockMap(var2, var1);
            case SCALE -> scaleMap(var1, var2);
         }

      }
   }

   private static void scaleMap(ItemStack var0, Level var1) {
      MapItemSavedData var2 = getSavedData(var0, var1);
      if (var2 != null) {
         MapId var3 = var1.getFreeMapId();
         var1.setMapData(var3, var2.scaled());
         var0.set(DataComponents.MAP_ID, var3);
      }

   }

   public static void lockMap(Level var0, ItemStack var1) {
      MapItemSavedData var2 = getSavedData(var1, var0);
      if (var2 != null) {
         MapId var3 = var0.getFreeMapId();
         MapItemSavedData var4 = var2.locked();
         var0.setMapData(var3, var4);
         var1.set(DataComponents.MAP_ID, var3);
      }

   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      MapId var5 = (MapId)var1.get(DataComponents.MAP_ID);
      MapItemSavedData var6 = var5 != null ? var2.mapData(var5) : null;
      MapPostProcessing var7 = (MapPostProcessing)var1.get(DataComponents.MAP_POST_PROCESSING);
      if (var6 != null && (var6.locked || var7 == MapPostProcessing.LOCK)) {
         var3.add(Component.translatable("filled_map.locked", var5.id()).withStyle(ChatFormatting.GRAY));
      }

      if (var4.isAdvanced()) {
         if (var6 != null) {
            if (var7 == null) {
               var3.add(getTooltipForId(var5));
            }

            int var8 = var7 == MapPostProcessing.SCALE ? 1 : 0;
            int var9 = Math.min(var6.scale + var8, 4);
            var3.add(Component.translatable("filled_map.scale", 1 << var9).withStyle(ChatFormatting.GRAY));
            var3.add(Component.translatable("filled_map.level", var9, 4).withStyle(ChatFormatting.GRAY));
         } else {
            var3.add(Component.translatable("filled_map.unknown").withStyle(ChatFormatting.GRAY));
         }
      }

   }

   public static Component getTooltipForId(MapId var0) {
      return Component.translatable("filled_map.id", var0.id()).withStyle(ChatFormatting.GRAY);
   }

   public InteractionResult useOn(UseOnContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      if (var2.is(BlockTags.BANNERS)) {
         if (!var1.getLevel().isClientSide) {
            MapItemSavedData var3 = getSavedData(var1.getItemInHand(), var1.getLevel());
            if (var3 != null && !var3.toggleBanner(var1.getLevel(), var1.getClickedPos())) {
               return InteractionResult.FAIL;
            }
         }

         return InteractionResult.sidedSuccess(var1.getLevel().isClientSide);
      } else {
         return super.useOn(var1);
      }
   }
}
