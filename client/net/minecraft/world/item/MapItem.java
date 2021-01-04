package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapItem extends ComplexItem {
   public MapItem(Item.Properties var1) {
      super(var1);
   }

   public static ItemStack create(Level var0, int var1, int var2, byte var3, boolean var4, boolean var5) {
      ItemStack var6 = new ItemStack(Items.FILLED_MAP);
      createAndStoreSavedData(var6, var0, var1, var2, var3, var4, var5, var0.dimension.getType());
      return var6;
   }

   @Nullable
   public static MapItemSavedData getSavedData(ItemStack var0, Level var1) {
      return var1.getMapData(makeKey(getMapId(var0)));
   }

   @Nullable
   public static MapItemSavedData getOrCreateSavedData(ItemStack var0, Level var1) {
      MapItemSavedData var2 = getSavedData(var0, var1);
      if (var2 == null && !var1.isClientSide) {
         var2 = createAndStoreSavedData(var0, var1, var1.getLevelData().getXSpawn(), var1.getLevelData().getZSpawn(), 3, false, false, var1.dimension.getType());
      }

      return var2;
   }

   public static int getMapId(ItemStack var0) {
      CompoundTag var1 = var0.getTag();
      return var1 != null && var1.contains("map", 99) ? var1.getInt("map") : 0;
   }

   private static MapItemSavedData createAndStoreSavedData(ItemStack var0, Level var1, int var2, int var3, int var4, boolean var5, boolean var6, DimensionType var7) {
      int var8 = var1.getFreeMapId();
      MapItemSavedData var9 = new MapItemSavedData(makeKey(var8));
      var9.setProperties(var2, var3, var4, var5, var6, var7);
      var1.setMapData(var9);
      var0.getOrCreateTag().putInt("map", var8);
      return var9;
   }

   public static String makeKey(int var0) {
      return "map_" + var0;
   }

   public void update(Level var1, Entity var2, MapItemSavedData var3) {
      if (var1.dimension.getType() == var3.dimension && var2 instanceof Player) {
         int var4 = 1 << var3.scale;
         int var5 = var3.x;
         int var6 = var3.z;
         int var7 = Mth.floor(var2.x - (double)var5) / var4 + 64;
         int var8 = Mth.floor(var2.z - (double)var6) / var4 + 64;
         int var9 = 128 / var4;
         if (var1.dimension.isHasCeiling()) {
            var9 /= 2;
         }

         MapItemSavedData.HoldingPlayer var10 = var3.getHoldingPlayer((Player)var2);
         ++var10.step;
         boolean var11 = false;

         for(int var12 = var7 - var9 + 1; var12 < var7 + var9; ++var12) {
            if ((var12 & 15) == (var10.step & 15) || var11) {
               var11 = false;
               double var13 = 0.0D;

               for(int var15 = var8 - var9 - 1; var15 < var8 + var9; ++var15) {
                  if (var12 >= 0 && var15 >= -1 && var12 < 128 && var15 < 128) {
                     int var16 = var12 - var7;
                     int var17 = var15 - var8;
                     boolean var18 = var16 * var16 + var17 * var17 > (var9 - 2) * (var9 - 2);
                     int var19 = (var5 / var4 + var12 - 64) * var4;
                     int var20 = (var6 / var4 + var15 - 64) * var4;
                     LinkedHashMultiset var21 = LinkedHashMultiset.create();
                     LevelChunk var22 = var1.getChunkAt(new BlockPos(var19, 0, var20));
                     if (!var22.isEmpty()) {
                        ChunkPos var23 = var22.getPos();
                        int var24 = var19 & 15;
                        int var25 = var20 & 15;
                        int var26 = 0;
                        double var27 = 0.0D;
                        if (var1.dimension.isHasCeiling()) {
                           int var29 = var19 + var20 * 231871;
                           var29 = var29 * var29 * 31287121 + var29 * 11;
                           if ((var29 >> 20 & 1) == 0) {
                              var21.add(Blocks.DIRT.defaultBlockState().getMapColor(var1, BlockPos.ZERO), 10);
                           } else {
                              var21.add(Blocks.STONE.defaultBlockState().getMapColor(var1, BlockPos.ZERO), 100);
                           }

                           var27 = 100.0D;
                        } else {
                           BlockPos.MutableBlockPos var37 = new BlockPos.MutableBlockPos();
                           BlockPos.MutableBlockPos var30 = new BlockPos.MutableBlockPos();

                           for(int var31 = 0; var31 < var4; ++var31) {
                              for(int var32 = 0; var32 < var4; ++var32) {
                                 int var33 = var22.getHeight(Heightmap.Types.WORLD_SURFACE, var31 + var24, var32 + var25) + 1;
                                 BlockState var34;
                                 if (var33 <= 1) {
                                    var34 = Blocks.BEDROCK.defaultBlockState();
                                 } else {
                                    do {
                                       --var33;
                                       var37.set(var23.getMinBlockX() + var31 + var24, var33, var23.getMinBlockZ() + var32 + var25);
                                       var34 = var22.getBlockState(var37);
                                    } while(var34.getMapColor(var1, var37) == MaterialColor.NONE && var33 > 0);

                                    if (var33 > 0 && !var34.getFluidState().isEmpty()) {
                                       int var35 = var33 - 1;
                                       var30.set((Vec3i)var37);

                                       BlockState var36;
                                       do {
                                          var30.setY(var35--);
                                          var36 = var22.getBlockState(var30);
                                          ++var26;
                                       } while(var35 > 0 && !var36.getFluidState().isEmpty());

                                       var34 = this.getCorrectStateForFluidBlock(var1, var34, var37);
                                    }
                                 }

                                 var3.checkBanners(var1, var23.getMinBlockX() + var31 + var24, var23.getMinBlockZ() + var32 + var25);
                                 var27 += (double)var33 / (double)(var4 * var4);
                                 var21.add(var34.getMapColor(var1, var37));
                              }
                           }
                        }

                        var26 /= var4 * var4;
                        double var38 = (var27 - var13) * 4.0D / (double)(var4 + 4) + ((double)(var12 + var15 & 1) - 0.5D) * 0.4D;
                        byte var39 = 1;
                        if (var38 > 0.6D) {
                           var39 = 2;
                        }

                        if (var38 < -0.6D) {
                           var39 = 0;
                        }

                        MaterialColor var40 = (MaterialColor)Iterables.getFirst(Multisets.copyHighestCountFirst(var21), MaterialColor.NONE);
                        if (var40 == MaterialColor.WATER) {
                           var38 = (double)var26 * 0.1D + (double)(var12 + var15 & 1) * 0.2D;
                           var39 = 1;
                           if (var38 < 0.5D) {
                              var39 = 2;
                           }

                           if (var38 > 0.9D) {
                              var39 = 0;
                           }
                        }

                        var13 = var27;
                        if (var15 >= 0 && var16 * var16 + var17 * var17 < var9 * var9 && (!var18 || (var12 + var15 & 1) != 0)) {
                           byte var41 = var3.colors[var12 + var15 * 128];
                           byte var42 = (byte)(var40.id * 4 + var39);
                           if (var41 != var42) {
                              var3.colors[var12 + var15 * 128] = var42;
                              var3.setDirty(var12, var15);
                              var11 = true;
                           }
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

   private static boolean isLand(Biome[] var0, int var1, int var2, int var3) {
      return var0[var2 * var1 + var3 * var1 * 128 * var1].getDepth() >= 0.0F;
   }

   public static void renderBiomePreviewMap(Level var0, ItemStack var1) {
      MapItemSavedData var2 = getOrCreateSavedData(var1, var0);
      if (var2 != null) {
         if (var0.dimension.getType() == var2.dimension) {
            int var3 = 1 << var2.scale;
            int var4 = var2.x;
            int var5 = var2.z;
            Biome[] var6 = var0.getChunkSource().getGenerator().getBiomeSource().getBiomeBlock((var4 / var3 - 64) * var3, (var5 / var3 - 64) * var3, 128 * var3, 128 * var3, false);

            for(int var7 = 0; var7 < 128; ++var7) {
               for(int var8 = 0; var8 < 128; ++var8) {
                  if (var7 > 0 && var8 > 0 && var7 < 127 && var8 < 127) {
                     Biome var9 = var6[var7 * var3 + var8 * var3 * 128 * var3];
                     int var10 = 8;
                     if (isLand(var6, var3, var7 - 1, var8 - 1)) {
                        --var10;
                     }

                     if (isLand(var6, var3, var7 - 1, var8 + 1)) {
                        --var10;
                     }

                     if (isLand(var6, var3, var7 - 1, var8)) {
                        --var10;
                     }

                     if (isLand(var6, var3, var7 + 1, var8 - 1)) {
                        --var10;
                     }

                     if (isLand(var6, var3, var7 + 1, var8 + 1)) {
                        --var10;
                     }

                     if (isLand(var6, var3, var7 + 1, var8)) {
                        --var10;
                     }

                     if (isLand(var6, var3, var7, var8 - 1)) {
                        --var10;
                     }

                     if (isLand(var6, var3, var7, var8 + 1)) {
                        --var10;
                     }

                     int var11 = 3;
                     MaterialColor var12 = MaterialColor.NONE;
                     if (var9.getDepth() < 0.0F) {
                        var12 = MaterialColor.COLOR_ORANGE;
                        if (var10 > 7 && var8 % 2 == 0) {
                           var11 = (var7 + (int)(Mth.sin((float)var8 + 0.0F) * 7.0F)) / 8 % 5;
                           if (var11 == 3) {
                              var11 = 1;
                           } else if (var11 == 4) {
                              var11 = 0;
                           }
                        } else if (var10 > 7) {
                           var12 = MaterialColor.NONE;
                        } else if (var10 > 5) {
                           var11 = 1;
                        } else if (var10 > 3) {
                           var11 = 0;
                        } else if (var10 > 1) {
                           var11 = 0;
                        }
                     } else if (var10 > 0) {
                        var12 = MaterialColor.COLOR_BROWN;
                        if (var10 > 3) {
                           var11 = 1;
                        } else {
                           var11 = 3;
                        }
                     }

                     if (var12 != MaterialColor.NONE) {
                        var2.colors[var7 + var8 * 128] = (byte)(var12.id * 4 + var11);
                        var2.setDirty(var7, var8);
                     }
                  }
               }
            }

         }
      }
   }

   public void inventoryTick(ItemStack var1, Level var2, Entity var3, int var4, boolean var5) {
      if (!var2.isClientSide) {
         MapItemSavedData var6 = getOrCreateSavedData(var1, var2);
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
      return getOrCreateSavedData(var1, var2).getUpdatePacket(var1, var2, var3);
   }

   public void onCraftedBy(ItemStack var1, Level var2, Player var3) {
      CompoundTag var4 = var1.getTag();
      if (var4 != null && var4.contains("map_scale_direction", 99)) {
         scaleMap(var1, var2, var4.getInt("map_scale_direction"));
         var4.remove("map_scale_direction");
      }

   }

   protected static void scaleMap(ItemStack var0, Level var1, int var2) {
      MapItemSavedData var3 = getOrCreateSavedData(var0, var1);
      if (var3 != null) {
         createAndStoreSavedData(var0, var1, var3.x, var3.z, Mth.clamp(var3.scale + var2, 0, 4), var3.trackingPosition, var3.unlimitedTracking, var3.dimension);
      }

   }

   @Nullable
   public static ItemStack lockMap(Level var0, ItemStack var1) {
      MapItemSavedData var2 = getOrCreateSavedData(var1, var0);
      if (var2 != null) {
         ItemStack var3 = var1.copy();
         MapItemSavedData var4 = createAndStoreSavedData(var3, var0, 0, 0, var2.scale, var2.trackingPosition, var2.unlimitedTracking, var2.dimension);
         var4.lockData(var2);
         return var3;
      } else {
         return null;
      }
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      MapItemSavedData var5 = var2 == null ? null : getOrCreateSavedData(var1, var2);
      if (var5 != null && var5.locked) {
         var3.add((new TranslatableComponent("filled_map.locked", new Object[]{getMapId(var1)})).withStyle(ChatFormatting.GRAY));
      }

      if (var4.isAdvanced()) {
         if (var5 != null) {
            var3.add((new TranslatableComponent("filled_map.id", new Object[]{getMapId(var1)})).withStyle(ChatFormatting.GRAY));
            var3.add((new TranslatableComponent("filled_map.scale", new Object[]{1 << var5.scale})).withStyle(ChatFormatting.GRAY));
            var3.add((new TranslatableComponent("filled_map.level", new Object[]{var5.scale, 4})).withStyle(ChatFormatting.GRAY));
         } else {
            var3.add((new TranslatableComponent("filled_map.unknown", new Object[0])).withStyle(ChatFormatting.GRAY));
         }
      }

   }

   public static int getColor(ItemStack var0) {
      CompoundTag var1 = var0.getTagElement("display");
      if (var1 != null && var1.contains("MapColor", 99)) {
         int var2 = var1.getInt("MapColor");
         return -16777216 | var2 & 16777215;
      } else {
         return -12173266;
      }
   }

   public InteractionResult useOn(UseOnContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      if (var2.is(BlockTags.BANNERS)) {
         if (!var1.level.isClientSide) {
            MapItemSavedData var3 = getOrCreateSavedData(var1.getItemInHand(), var1.getLevel());
            var3.toggleBanner(var1.getLevel(), var1.getClickedPos());
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.useOn(var1);
      }
   }
}
