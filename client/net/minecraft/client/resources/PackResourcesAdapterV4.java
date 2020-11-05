package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.world.level.block.state.properties.ChestType;

public class PackResourcesAdapterV4 implements PackResources {
   private static final Map<String, Pair<ChestType, ResourceLocation>> CHESTS = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put("textures/entity/chest/normal_left.png", new Pair(ChestType.LEFT, new ResourceLocation("textures/entity/chest/normal_double.png")));
      var0.put("textures/entity/chest/normal_right.png", new Pair(ChestType.RIGHT, new ResourceLocation("textures/entity/chest/normal_double.png")));
      var0.put("textures/entity/chest/normal.png", new Pair(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/normal.png")));
      var0.put("textures/entity/chest/trapped_left.png", new Pair(ChestType.LEFT, new ResourceLocation("textures/entity/chest/trapped_double.png")));
      var0.put("textures/entity/chest/trapped_right.png", new Pair(ChestType.RIGHT, new ResourceLocation("textures/entity/chest/trapped_double.png")));
      var0.put("textures/entity/chest/trapped.png", new Pair(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/trapped.png")));
      var0.put("textures/entity/chest/christmas_left.png", new Pair(ChestType.LEFT, new ResourceLocation("textures/entity/chest/christmas_double.png")));
      var0.put("textures/entity/chest/christmas_right.png", new Pair(ChestType.RIGHT, new ResourceLocation("textures/entity/chest/christmas_double.png")));
      var0.put("textures/entity/chest/christmas.png", new Pair(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/christmas.png")));
      var0.put("textures/entity/chest/ender.png", new Pair(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/ender.png")));
   });
   private static final List<String> PATTERNS = Lists.newArrayList(new String[]{"base", "border", "bricks", "circle", "creeper", "cross", "curly_border", "diagonal_left", "diagonal_right", "diagonal_up_left", "diagonal_up_right", "flower", "globe", "gradient", "gradient_up", "half_horizontal", "half_horizontal_bottom", "half_vertical", "half_vertical_right", "mojang", "rhombus", "skull", "small_stripes", "square_bottom_left", "square_bottom_right", "square_top_left", "square_top_right", "straight_cross", "stripe_bottom", "stripe_center", "stripe_downleft", "stripe_downright", "stripe_left", "stripe_middle", "stripe_right", "stripe_top", "triangle_bottom", "triangle_top", "triangles_bottom", "triangles_top"});
   private static final Set<String> SHIELDS;
   private static final Set<String> BANNERS;
   public static final ResourceLocation SHIELD_BASE;
   public static final ResourceLocation BANNER_BASE;
   public static final ResourceLocation OLD_IRON_GOLEM_LOCATION;
   private final PackResources pack;

   public PackResourcesAdapterV4(PackResources var1) {
      super();
      this.pack = var1;
   }

   public InputStream getRootResource(String var1) throws IOException {
      return this.pack.getRootResource(var1);
   }

   public boolean hasResource(PackType var1, ResourceLocation var2) {
      if (!"minecraft".equals(var2.getNamespace())) {
         return this.pack.hasResource(var1, var2);
      } else {
         String var3 = var2.getPath();
         if ("textures/misc/enchanted_item_glint.png".equals(var3)) {
            return false;
         } else if ("textures/entity/iron_golem/iron_golem.png".equals(var3)) {
            return this.pack.hasResource(var1, OLD_IRON_GOLEM_LOCATION);
         } else if (!"textures/entity/conduit/wind.png".equals(var3) && !"textures/entity/conduit/wind_vertical.png".equals(var3)) {
            if (SHIELDS.contains(var3)) {
               return this.pack.hasResource(var1, SHIELD_BASE) && this.pack.hasResource(var1, var2);
            } else if (!BANNERS.contains(var3)) {
               Pair var4 = (Pair)CHESTS.get(var3);
               return var4 != null && this.pack.hasResource(var1, (ResourceLocation)var4.getSecond()) ? true : this.pack.hasResource(var1, var2);
            } else {
               return this.pack.hasResource(var1, BANNER_BASE) && this.pack.hasResource(var1, var2);
            }
         } else {
            return false;
         }
      }
   }

   public InputStream getResource(PackType var1, ResourceLocation var2) throws IOException {
      if (!"minecraft".equals(var2.getNamespace())) {
         return this.pack.getResource(var1, var2);
      } else {
         String var3 = var2.getPath();
         if ("textures/entity/iron_golem/iron_golem.png".equals(var3)) {
            return this.pack.getResource(var1, OLD_IRON_GOLEM_LOCATION);
         } else {
            InputStream var19;
            if (SHIELDS.contains(var3)) {
               var19 = fixPattern(this.pack.getResource(var1, SHIELD_BASE), this.pack.getResource(var1, var2), 64, 2, 2, 12, 22);
               if (var19 != null) {
                  return var19;
               }
            } else if (BANNERS.contains(var3)) {
               var19 = fixPattern(this.pack.getResource(var1, BANNER_BASE), this.pack.getResource(var1, var2), 64, 0, 0, 42, 41);
               if (var19 != null) {
                  return var19;
               }
            } else {
               if (!"textures/entity/enderdragon/dragon.png".equals(var3) && !"textures/entity/enderdragon/dragon_exploding.png".equals(var3)) {
                  if (!"textures/entity/conduit/closed_eye.png".equals(var3) && !"textures/entity/conduit/open_eye.png".equals(var3)) {
                     Pair var18 = (Pair)CHESTS.get(var3);
                     if (var18 != null) {
                        ChestType var20 = (ChestType)var18.getFirst();
                        InputStream var21 = this.pack.getResource(var1, (ResourceLocation)var18.getSecond());
                        if (var20 == ChestType.SINGLE) {
                           return fixSingleChest(var21);
                        }

                        if (var20 == ChestType.LEFT) {
                           return fixLeftChest(var21);
                        }

                        if (var20 == ChestType.RIGHT) {
                           return fixRightChest(var21);
                        }
                     }

                     return this.pack.getResource(var1, var2);
                  }

                  return fixConduitEyeTexture(this.pack.getResource(var1, var2));
               }

               NativeImage var4 = NativeImage.read(this.pack.getResource(var1, var2));
               Throwable var5 = null;

               try {
                  int var6 = var4.getWidth() / 256;

                  for(int var7 = 88 * var6; var7 < 200 * var6; ++var7) {
                     for(int var8 = 56 * var6; var8 < 112 * var6; ++var8) {
                        var4.setPixelRGBA(var8, var7, 0);
                     }
                  }

                  ByteArrayInputStream var22 = new ByteArrayInputStream(var4.asByteArray());
                  return var22;
               } catch (Throwable var16) {
                  var5 = var16;
                  throw var16;
               } finally {
                  if (var4 != null) {
                     if (var5 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var15) {
                           var5.addSuppressed(var15);
                        }
                     } else {
                        var4.close();
                     }
                  }

               }
            }

            return this.pack.getResource(var1, var2);
         }
      }
   }

   @Nullable
   public static InputStream fixPattern(InputStream var0, InputStream var1, int var2, int var3, int var4, int var5, int var6) throws IOException {
      NativeImage var7 = NativeImage.read(var0);
      Throwable var8 = null;

      try {
         NativeImage var9 = NativeImage.read(var1);
         Throwable var10 = null;

         try {
            int var11 = var7.getWidth();
            int var12 = var7.getHeight();
            if (var11 != var9.getWidth() || var12 != var9.getHeight()) {
               return null;
            } else {
               NativeImage var13 = new NativeImage(var11, var12, true);
               Throwable var14 = null;

               try {
                  int var15 = var11 / var2;

                  for(int var16 = var4 * var15; var16 < var6 * var15; ++var16) {
                     for(int var17 = var3 * var15; var17 < var5 * var15; ++var17) {
                        int var18 = NativeImage.getR(var9.getPixelRGBA(var17, var16));
                        int var19 = var7.getPixelRGBA(var17, var16);
                        var13.setPixelRGBA(var17, var16, NativeImage.combine(var18, NativeImage.getB(var19), NativeImage.getG(var19), NativeImage.getR(var19)));
                     }
                  }

                  ByteArrayInputStream var71 = new ByteArrayInputStream(var13.asByteArray());
                  return var71;
               } catch (Throwable var65) {
                  var14 = var65;
                  throw var65;
               } finally {
                  if (var13 != null) {
                     if (var14 != null) {
                        try {
                           var13.close();
                        } catch (Throwable var64) {
                           var14.addSuppressed(var64);
                        }
                     } else {
                        var13.close();
                     }
                  }

               }
            }
         } catch (Throwable var67) {
            var10 = var67;
            throw var67;
         } finally {
            if (var9 != null) {
               if (var10 != null) {
                  try {
                     var9.close();
                  } catch (Throwable var63) {
                     var10.addSuppressed(var63);
                  }
               } else {
                  var9.close();
               }
            }

         }
      } catch (Throwable var69) {
         var8 = var69;
         throw var69;
      } finally {
         if (var7 != null) {
            if (var8 != null) {
               try {
                  var7.close();
               } catch (Throwable var62) {
                  var8.addSuppressed(var62);
               }
            } else {
               var7.close();
            }
         }

      }
   }

   public static InputStream fixConduitEyeTexture(InputStream var0) throws IOException {
      NativeImage var1 = NativeImage.read(var0);
      Throwable var2 = null;

      Object var7;
      try {
         int var3 = var1.getWidth();
         int var4 = var1.getHeight();
         NativeImage var5 = new NativeImage(2 * var3, 2 * var4, true);
         Throwable var6 = null;

         try {
            copyRect(var1, var5, 0, 0, 0, 0, var3, var4, 1, false, false);
            var7 = new ByteArrayInputStream(var5.asByteArray());
         } catch (Throwable var30) {
            var7 = var30;
            var6 = var30;
            throw var30;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var29) {
                     var6.addSuppressed(var29);
                  }
               } else {
                  var5.close();
               }
            }

         }
      } catch (Throwable var32) {
         var2 = var32;
         throw var32;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var28) {
                  var2.addSuppressed(var28);
               }
            } else {
               var1.close();
            }
         }

      }

      return (InputStream)var7;
   }

   public static InputStream fixLeftChest(InputStream var0) throws IOException {
      NativeImage var1 = NativeImage.read(var0);
      Throwable var2 = null;

      ByteArrayInputStream var8;
      try {
         int var3 = var1.getWidth();
         int var4 = var1.getHeight();
         NativeImage var5 = new NativeImage(var3 / 2, var4, true);
         Throwable var6 = null;

         try {
            int var7 = var4 / 64;
            copyRect(var1, var5, 29, 0, 29, 0, 15, 14, var7, false, true);
            copyRect(var1, var5, 59, 0, 14, 0, 15, 14, var7, false, true);
            copyRect(var1, var5, 29, 14, 43, 14, 15, 5, var7, true, true);
            copyRect(var1, var5, 44, 14, 29, 14, 14, 5, var7, true, true);
            copyRect(var1, var5, 58, 14, 14, 14, 15, 5, var7, true, true);
            copyRect(var1, var5, 29, 19, 29, 19, 15, 14, var7, false, true);
            copyRect(var1, var5, 59, 19, 14, 19, 15, 14, var7, false, true);
            copyRect(var1, var5, 29, 33, 43, 33, 15, 10, var7, true, true);
            copyRect(var1, var5, 44, 33, 29, 33, 14, 10, var7, true, true);
            copyRect(var1, var5, 58, 33, 14, 33, 15, 10, var7, true, true);
            copyRect(var1, var5, 2, 0, 2, 0, 1, 1, var7, false, true);
            copyRect(var1, var5, 4, 0, 1, 0, 1, 1, var7, false, true);
            copyRect(var1, var5, 2, 1, 3, 1, 1, 4, var7, true, true);
            copyRect(var1, var5, 3, 1, 2, 1, 1, 4, var7, true, true);
            copyRect(var1, var5, 4, 1, 1, 1, 1, 4, var7, true, true);
            var8 = new ByteArrayInputStream(var5.asByteArray());
         } catch (Throwable var31) {
            var6 = var31;
            throw var31;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var30) {
                     var6.addSuppressed(var30);
                  }
               } else {
                  var5.close();
               }
            }

         }
      } catch (Throwable var33) {
         var2 = var33;
         throw var33;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var29) {
                  var2.addSuppressed(var29);
               }
            } else {
               var1.close();
            }
         }

      }

      return var8;
   }

   public static InputStream fixRightChest(InputStream var0) throws IOException {
      NativeImage var1 = NativeImage.read(var0);
      Throwable var2 = null;

      ByteArrayInputStream var8;
      try {
         int var3 = var1.getWidth();
         int var4 = var1.getHeight();
         NativeImage var5 = new NativeImage(var3 / 2, var4, true);
         Throwable var6 = null;

         try {
            int var7 = var4 / 64;
            copyRect(var1, var5, 14, 0, 29, 0, 15, 14, var7, false, true);
            copyRect(var1, var5, 44, 0, 14, 0, 15, 14, var7, false, true);
            copyRect(var1, var5, 0, 14, 0, 14, 14, 5, var7, true, true);
            copyRect(var1, var5, 14, 14, 43, 14, 15, 5, var7, true, true);
            copyRect(var1, var5, 73, 14, 14, 14, 15, 5, var7, true, true);
            copyRect(var1, var5, 14, 19, 29, 19, 15, 14, var7, false, true);
            copyRect(var1, var5, 44, 19, 14, 19, 15, 14, var7, false, true);
            copyRect(var1, var5, 0, 33, 0, 33, 14, 10, var7, true, true);
            copyRect(var1, var5, 14, 33, 43, 33, 15, 10, var7, true, true);
            copyRect(var1, var5, 73, 33, 14, 33, 15, 10, var7, true, true);
            copyRect(var1, var5, 1, 0, 2, 0, 1, 1, var7, false, true);
            copyRect(var1, var5, 3, 0, 1, 0, 1, 1, var7, false, true);
            copyRect(var1, var5, 0, 1, 0, 1, 1, 4, var7, true, true);
            copyRect(var1, var5, 1, 1, 3, 1, 1, 4, var7, true, true);
            copyRect(var1, var5, 5, 1, 1, 1, 1, 4, var7, true, true);
            var8 = new ByteArrayInputStream(var5.asByteArray());
         } catch (Throwable var31) {
            var6 = var31;
            throw var31;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var30) {
                     var6.addSuppressed(var30);
                  }
               } else {
                  var5.close();
               }
            }

         }
      } catch (Throwable var33) {
         var2 = var33;
         throw var33;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var29) {
                  var2.addSuppressed(var29);
               }
            } else {
               var1.close();
            }
         }

      }

      return var8;
   }

   public static InputStream fixSingleChest(InputStream var0) throws IOException {
      NativeImage var1 = NativeImage.read(var0);
      Throwable var2 = null;

      ByteArrayInputStream var8;
      try {
         int var3 = var1.getWidth();
         int var4 = var1.getHeight();
         NativeImage var5 = new NativeImage(var3, var4, true);
         Throwable var6 = null;

         try {
            int var7 = var4 / 64;
            copyRect(var1, var5, 14, 0, 28, 0, 14, 14, var7, false, true);
            copyRect(var1, var5, 28, 0, 14, 0, 14, 14, var7, false, true);
            copyRect(var1, var5, 0, 14, 0, 14, 14, 5, var7, true, true);
            copyRect(var1, var5, 14, 14, 42, 14, 14, 5, var7, true, true);
            copyRect(var1, var5, 28, 14, 28, 14, 14, 5, var7, true, true);
            copyRect(var1, var5, 42, 14, 14, 14, 14, 5, var7, true, true);
            copyRect(var1, var5, 14, 19, 28, 19, 14, 14, var7, false, true);
            copyRect(var1, var5, 28, 19, 14, 19, 14, 14, var7, false, true);
            copyRect(var1, var5, 0, 33, 0, 33, 14, 10, var7, true, true);
            copyRect(var1, var5, 14, 33, 42, 33, 14, 10, var7, true, true);
            copyRect(var1, var5, 28, 33, 28, 33, 14, 10, var7, true, true);
            copyRect(var1, var5, 42, 33, 14, 33, 14, 10, var7, true, true);
            copyRect(var1, var5, 1, 0, 3, 0, 2, 1, var7, false, true);
            copyRect(var1, var5, 3, 0, 1, 0, 2, 1, var7, false, true);
            copyRect(var1, var5, 0, 1, 0, 1, 1, 4, var7, true, true);
            copyRect(var1, var5, 1, 1, 4, 1, 2, 4, var7, true, true);
            copyRect(var1, var5, 3, 1, 3, 1, 1, 4, var7, true, true);
            copyRect(var1, var5, 4, 1, 1, 1, 2, 4, var7, true, true);
            var8 = new ByteArrayInputStream(var5.asByteArray());
         } catch (Throwable var31) {
            var6 = var31;
            throw var31;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var30) {
                     var6.addSuppressed(var30);
                  }
               } else {
                  var5.close();
               }
            }

         }
      } catch (Throwable var33) {
         var2 = var33;
         throw var33;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var29) {
                  var2.addSuppressed(var29);
               }
            } else {
               var1.close();
            }
         }

      }

      return var8;
   }

   public Collection<ResourceLocation> getResources(PackType var1, String var2, String var3, int var4, Predicate<String> var5) {
      return this.pack.getResources(var1, var2, var3, var4, var5);
   }

   public Set<String> getNamespaces(PackType var1) {
      return this.pack.getNamespaces(var1);
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> var1) throws IOException {
      return this.pack.getMetadataSection(var1);
   }

   public String getName() {
      return this.pack.getName();
   }

   public void close() {
      this.pack.close();
   }

   private static void copyRect(NativeImage var0, NativeImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10) {
      var7 *= var8;
      var6 *= var8;
      var4 *= var8;
      var5 *= var8;
      var2 *= var8;
      var3 *= var8;

      for(int var11 = 0; var11 < var7; ++var11) {
         for(int var12 = 0; var12 < var6; ++var12) {
            var1.setPixelRGBA(var4 + var12, var5 + var11, var0.getPixelRGBA(var2 + (var9 ? var6 - 1 - var12 : var12), var3 + (var10 ? var7 - 1 - var11 : var11)));
         }
      }

   }

   static {
      SHIELDS = (Set)PATTERNS.stream().map((var0) -> {
         return "textures/entity/shield/" + var0 + ".png";
      }).collect(Collectors.toSet());
      BANNERS = (Set)PATTERNS.stream().map((var0) -> {
         return "textures/entity/banner/" + var0 + ".png";
      }).collect(Collectors.toSet());
      SHIELD_BASE = new ResourceLocation("textures/entity/shield_base.png");
      BANNER_BASE = new ResourceLocation("textures/entity/banner_base.png");
      OLD_IRON_GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem.png");
   }
}
