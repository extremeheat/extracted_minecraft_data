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
   public static final int DEFAULT_CHEST_SIZE = 64;
   public static final int DEFAULT_SHIELD_SIZE = 64;
   public static final int DEFAULT_BANNER_SIZE = 64;
   public static final ResourceLocation OLD_IRON_GOLEM_LOCATION;
   public static final String NEW_IRON_GOLEM_PATH = "textures/entity/iron_golem/iron_golem.png";
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
            InputStream var11;
            if (SHIELDS.contains(var3)) {
               var11 = fixPattern(this.pack.getResource(var1, SHIELD_BASE), this.pack.getResource(var1, var2), 64, 2, 2, 12, 22);
               if (var11 != null) {
                  return var11;
               }
            } else if (BANNERS.contains(var3)) {
               var11 = fixPattern(this.pack.getResource(var1, BANNER_BASE), this.pack.getResource(var1, var2), 64, 0, 0, 42, 41);
               if (var11 != null) {
                  return var11;
               }
            } else {
               if ("textures/entity/enderdragon/dragon.png".equals(var3) || "textures/entity/enderdragon/dragon_exploding.png".equals(var3)) {
                  NativeImage var10 = NativeImage.read(this.pack.getResource(var1, var2));

                  ByteArrayInputStream var14;
                  try {
                     int var12 = var10.getWidth() / 256;
                     int var13 = 88 * var12;

                     while(true) {
                        if (var13 >= 200 * var12) {
                           var14 = new ByteArrayInputStream(var10.asByteArray());
                           break;
                        }

                        for(int var7 = 56 * var12; var7 < 112 * var12; ++var7) {
                           var10.setPixelRGBA(var7, var13, 0);
                        }

                        ++var13;
                     }
                  } catch (Throwable var9) {
                     if (var10 != null) {
                        try {
                           var10.close();
                        } catch (Throwable var8) {
                           var9.addSuppressed(var8);
                        }
                     }

                     throw var9;
                  }

                  if (var10 != null) {
                     var10.close();
                  }

                  return var14;
               }

               if ("textures/entity/conduit/closed_eye.png".equals(var3) || "textures/entity/conduit/open_eye.png".equals(var3)) {
                  return fixConduitEyeTexture(this.pack.getResource(var1, var2));
               }

               Pair var4 = (Pair)CHESTS.get(var3);
               if (var4 != null) {
                  ChestType var5 = (ChestType)var4.getFirst();
                  InputStream var6 = this.pack.getResource(var1, (ResourceLocation)var4.getSecond());
                  if (var5 == ChestType.SINGLE) {
                     return fixSingleChest(var6);
                  }

                  if (var5 == ChestType.LEFT) {
                     return fixLeftChest(var6);
                  }

                  if (var5 == ChestType.RIGHT) {
                     return fixRightChest(var6);
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

      ByteArrayInputStream var23;
      label105: {
         try {
            NativeImage var8;
            label107: {
               var8 = NativeImage.read(var1);

               try {
                  int var9 = var7.getWidth();
                  int var10 = var7.getHeight();
                  if (var9 != var8.getWidth() || var10 != var8.getHeight()) {
                     break label107;
                  }

                  NativeImage var11 = new NativeImage(var9, var10, true);

                  try {
                     int var12 = var9 / var2;
                     int var13 = var4 * var12;

                     while(true) {
                        if (var13 >= var6 * var12) {
                           var23 = new ByteArrayInputStream(var11.asByteArray());
                           break;
                        }

                        for(int var14 = var3 * var12; var14 < var5 * var12; ++var14) {
                           int var15 = NativeImage.getR(var8.getPixelRGBA(var14, var13));
                           int var16 = var7.getPixelRGBA(var14, var13);
                           var11.setPixelRGBA(var14, var13, NativeImage.combine(var15, NativeImage.getB(var16), NativeImage.getG(var16), NativeImage.getR(var16)));
                        }

                        ++var13;
                     }
                  } catch (Throwable var20) {
                     try {
                        var11.close();
                     } catch (Throwable var19) {
                        var20.addSuppressed(var19);
                     }

                     throw var20;
                  }

                  var11.close();
               } catch (Throwable var21) {
                  if (var8 != null) {
                     try {
                        var8.close();
                     } catch (Throwable var18) {
                        var21.addSuppressed(var18);
                     }
                  }

                  throw var21;
               }

               if (var8 != null) {
                  var8.close();
               }
               break label105;
            }

            if (var8 != null) {
               var8.close();
            }
         } catch (Throwable var22) {
            if (var7 != null) {
               try {
                  var7.close();
               } catch (Throwable var17) {
                  var22.addSuppressed(var17);
               }
            }

            throw var22;
         }

         if (var7 != null) {
            var7.close();
         }

         return null;
      }

      if (var7 != null) {
         var7.close();
      }

      return var23;
   }

   public static InputStream fixConduitEyeTexture(InputStream var0) throws IOException {
      NativeImage var1 = NativeImage.read(var0);

      ByteArrayInputStream var5;
      try {
         int var2 = var1.getWidth();
         int var3 = var1.getHeight();
         NativeImage var4 = new NativeImage(2 * var2, 2 * var3, true);

         try {
            copyRect(var1, var4, 0, 0, 0, 0, var2, var3, 1, false, false);
            var5 = new ByteArrayInputStream(var4.asByteArray());
         } catch (Throwable var9) {
            try {
               var4.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         var4.close();
      } catch (Throwable var10) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (var1 != null) {
         var1.close();
      }

      return var5;
   }

   public static InputStream fixLeftChest(InputStream var0) throws IOException {
      NativeImage var1 = NativeImage.read(var0);

      ByteArrayInputStream var6;
      try {
         int var2 = var1.getWidth();
         int var3 = var1.getHeight();
         NativeImage var4 = new NativeImage(var2 / 2, var3, true);

         try {
            int var5 = var3 / 64;
            copyRect(var1, var4, 29, 0, 29, 0, 15, 14, var5, false, true);
            copyRect(var1, var4, 59, 0, 14, 0, 15, 14, var5, false, true);
            copyRect(var1, var4, 29, 14, 43, 14, 15, 5, var5, true, true);
            copyRect(var1, var4, 44, 14, 29, 14, 14, 5, var5, true, true);
            copyRect(var1, var4, 58, 14, 14, 14, 15, 5, var5, true, true);
            copyRect(var1, var4, 29, 19, 29, 19, 15, 14, var5, false, true);
            copyRect(var1, var4, 59, 19, 14, 19, 15, 14, var5, false, true);
            copyRect(var1, var4, 29, 33, 43, 33, 15, 10, var5, true, true);
            copyRect(var1, var4, 44, 33, 29, 33, 14, 10, var5, true, true);
            copyRect(var1, var4, 58, 33, 14, 33, 15, 10, var5, true, true);
            copyRect(var1, var4, 2, 0, 2, 0, 1, 1, var5, false, true);
            copyRect(var1, var4, 4, 0, 1, 0, 1, 1, var5, false, true);
            copyRect(var1, var4, 2, 1, 3, 1, 1, 4, var5, true, true);
            copyRect(var1, var4, 3, 1, 2, 1, 1, 4, var5, true, true);
            copyRect(var1, var4, 4, 1, 1, 1, 1, 4, var5, true, true);
            var6 = new ByteArrayInputStream(var4.asByteArray());
         } catch (Throwable var9) {
            try {
               var4.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         var4.close();
      } catch (Throwable var10) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (var1 != null) {
         var1.close();
      }

      return var6;
   }

   public static InputStream fixRightChest(InputStream var0) throws IOException {
      NativeImage var1 = NativeImage.read(var0);

      ByteArrayInputStream var6;
      try {
         int var2 = var1.getWidth();
         int var3 = var1.getHeight();
         NativeImage var4 = new NativeImage(var2 / 2, var3, true);

         try {
            int var5 = var3 / 64;
            copyRect(var1, var4, 14, 0, 29, 0, 15, 14, var5, false, true);
            copyRect(var1, var4, 44, 0, 14, 0, 15, 14, var5, false, true);
            copyRect(var1, var4, 0, 14, 0, 14, 14, 5, var5, true, true);
            copyRect(var1, var4, 14, 14, 43, 14, 15, 5, var5, true, true);
            copyRect(var1, var4, 73, 14, 14, 14, 15, 5, var5, true, true);
            copyRect(var1, var4, 14, 19, 29, 19, 15, 14, var5, false, true);
            copyRect(var1, var4, 44, 19, 14, 19, 15, 14, var5, false, true);
            copyRect(var1, var4, 0, 33, 0, 33, 14, 10, var5, true, true);
            copyRect(var1, var4, 14, 33, 43, 33, 15, 10, var5, true, true);
            copyRect(var1, var4, 73, 33, 14, 33, 15, 10, var5, true, true);
            copyRect(var1, var4, 1, 0, 2, 0, 1, 1, var5, false, true);
            copyRect(var1, var4, 3, 0, 1, 0, 1, 1, var5, false, true);
            copyRect(var1, var4, 0, 1, 0, 1, 1, 4, var5, true, true);
            copyRect(var1, var4, 1, 1, 3, 1, 1, 4, var5, true, true);
            copyRect(var1, var4, 5, 1, 1, 1, 1, 4, var5, true, true);
            var6 = new ByteArrayInputStream(var4.asByteArray());
         } catch (Throwable var9) {
            try {
               var4.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         var4.close();
      } catch (Throwable var10) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (var1 != null) {
         var1.close();
      }

      return var6;
   }

   public static InputStream fixSingleChest(InputStream var0) throws IOException {
      NativeImage var1 = NativeImage.read(var0);

      ByteArrayInputStream var6;
      try {
         int var2 = var1.getWidth();
         int var3 = var1.getHeight();
         NativeImage var4 = new NativeImage(var2, var3, true);

         try {
            int var5 = var3 / 64;
            copyRect(var1, var4, 14, 0, 28, 0, 14, 14, var5, false, true);
            copyRect(var1, var4, 28, 0, 14, 0, 14, 14, var5, false, true);
            copyRect(var1, var4, 0, 14, 0, 14, 14, 5, var5, true, true);
            copyRect(var1, var4, 14, 14, 42, 14, 14, 5, var5, true, true);
            copyRect(var1, var4, 28, 14, 28, 14, 14, 5, var5, true, true);
            copyRect(var1, var4, 42, 14, 14, 14, 14, 5, var5, true, true);
            copyRect(var1, var4, 14, 19, 28, 19, 14, 14, var5, false, true);
            copyRect(var1, var4, 28, 19, 14, 19, 14, 14, var5, false, true);
            copyRect(var1, var4, 0, 33, 0, 33, 14, 10, var5, true, true);
            copyRect(var1, var4, 14, 33, 42, 33, 14, 10, var5, true, true);
            copyRect(var1, var4, 28, 33, 28, 33, 14, 10, var5, true, true);
            copyRect(var1, var4, 42, 33, 14, 33, 14, 10, var5, true, true);
            copyRect(var1, var4, 1, 0, 3, 0, 2, 1, var5, false, true);
            copyRect(var1, var4, 3, 0, 1, 0, 2, 1, var5, false, true);
            copyRect(var1, var4, 0, 1, 0, 1, 1, 4, var5, true, true);
            copyRect(var1, var4, 1, 1, 4, 1, 2, 4, var5, true, true);
            copyRect(var1, var4, 3, 1, 3, 1, 1, 4, var5, true, true);
            copyRect(var1, var4, 4, 1, 1, 1, 2, 4, var5, true, true);
            var6 = new ByteArrayInputStream(var4.asByteArray());
         } catch (Throwable var9) {
            try {
               var4.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         var4.close();
      } catch (Throwable var10) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (var1 != null) {
         var1.close();
      }

      return var6;
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
