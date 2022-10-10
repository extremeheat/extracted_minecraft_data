package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject {
   private static final Logger field_147635_d = LogManager.getLogger();
   public static final ResourceLocation field_110575_b = new ResourceLocation("textures/atlas/blocks.png");
   private final List<TextureAtlasSprite> field_94258_i = Lists.newArrayList();
   private final Set<ResourceLocation> field_195427_i = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> field_94252_e = Maps.newHashMap();
   private final String field_94254_c;
   private int field_147636_j;
   private final TextureAtlasSprite field_94249_f = MissingTextureSprite.func_195677_a();

   public TextureMap(String var1) {
      super();
      this.field_94254_c = var1;
   }

   public void func_195413_a(IResourceManager var1) throws IOException {
   }

   public void func_195426_a(IResourceManager var1, Iterable<ResourceLocation> var2) {
      this.field_195427_i.clear();
      var2.forEach((var2x) -> {
         this.func_199362_a(var1, var2x);
      });
      this.func_195421_b(var1);
   }

   public void func_195421_b(IResourceManager var1) {
      int var2 = Minecraft.func_71369_N();
      Stitcher var3 = new Stitcher(var2, var2, 0, this.field_147636_j);
      this.func_195419_g();
      int var4 = 2147483647;
      int var5 = 1 << this.field_147636_j;
      Iterator var6 = this.field_195427_i.iterator();

      while(true) {
         ResourceLocation var8;
         TextureAtlasSprite var9;
         while(true) {
            ResourceLocation var7;
            do {
               if (!var6.hasNext()) {
                  int var31 = Math.min(var4, var5);
                  int var32 = MathHelper.func_151239_c(var31);
                  if (var32 < this.field_147636_j) {
                     field_147635_d.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.field_94254_c, this.field_147636_j, var32, var31);
                     this.field_147636_j = var32;
                  }

                  this.field_94249_f.func_147963_d(this.field_147636_j);
                  var3.func_110934_a(this.field_94249_f);

                  try {
                     var3.func_94305_f();
                  } catch (StitcherException var25) {
                     throw var25;
                  }

                  field_147635_d.info("Created: {}x{} {}-atlas", var3.func_110935_a(), var3.func_110936_b(), this.field_94254_c);
                  TextureUtil.func_180600_a(this.func_110552_b(), this.field_147636_j, var3.func_110935_a(), var3.func_110936_b());
                  Iterator var33 = var3.func_94309_g().iterator();

                  while(true) {
                     do {
                        if (!var33.hasNext()) {
                           return;
                        }

                        var9 = (TextureAtlasSprite)var33.next();
                     } while(var9 != this.field_94249_f && !this.func_195422_a(var1, var9));

                     this.field_94252_e.put(var9.func_195668_m(), var9);

                     try {
                        var9.func_195663_q();
                     } catch (Throwable var24) {
                        CrashReport var35 = CrashReport.func_85055_a(var24, "Stitching texture atlas");
                        CrashReportCategory var36 = var35.func_85058_a("Texture being stitched together");
                        var36.func_71507_a("Atlas path", this.field_94254_c);
                        var36.func_71507_a("Sprite", var9);
                        throw new ReportedException(var35);
                     }

                     if (var9.func_130098_m()) {
                        this.field_94258_i.add(var9);
                     }
                  }
               }

               var7 = (ResourceLocation)var6.next();
            } while(this.field_94249_f.func_195668_m().equals(var7));

            var8 = this.func_195420_b(var7);

            try {
               IResource var10 = var1.func_199002_a(var8);
               Throwable var11 = null;

               try {
                  PngSizeInfo var12 = new PngSizeInfo(var10);
                  AnimationMetadataSection var13 = (AnimationMetadataSection)var10.func_199028_a(AnimationMetadataSection.field_195817_a);
                  var9 = new TextureAtlasSprite(var7, var12, var13);
                  break;
               } catch (Throwable var27) {
                  var11 = var27;
                  throw var27;
               } finally {
                  if (var10 != null) {
                     if (var11 != null) {
                        try {
                           var10.close();
                        } catch (Throwable var26) {
                           var11.addSuppressed(var26);
                        }
                     } else {
                        var10.close();
                     }
                  }

               }
            } catch (RuntimeException var29) {
               field_147635_d.error("Unable to parse metadata from {} : {}", var8, var29);
            } catch (IOException var30) {
               field_147635_d.error("Using missing texture, unable to load {} : {}", var8, var30);
            }
         }

         var4 = Math.min(var4, Math.min(var9.func_94211_a(), var9.func_94216_b()));
         int var34 = Math.min(Integer.lowestOneBit(var9.func_94211_a()), Integer.lowestOneBit(var9.func_94216_b()));
         if (var34 < var5) {
            field_147635_d.warn("Texture {} with size {}x{} limits mip level from {} to {}", var8, var9.func_94211_a(), var9.func_94216_b(), MathHelper.func_151239_c(var5), MathHelper.func_151239_c(var34));
            var5 = var34;
         }

         var3.func_110934_a(var9);
      }
   }

   private boolean func_195422_a(IResourceManager var1, TextureAtlasSprite var2) {
      ResourceLocation var3 = this.func_195420_b(var2.func_195668_m());
      IResource var4 = null;

      label62: {
         boolean var6;
         try {
            var4 = var1.func_199002_a(var3);
            var2.func_195664_a(var4, this.field_147636_j + 1);
            break label62;
         } catch (RuntimeException var13) {
            field_147635_d.error("Unable to parse metadata from {}", var3, var13);
            var6 = false;
         } catch (IOException var14) {
            field_147635_d.error("Using missing texture, unable to load {}", var3, var14);
            var6 = false;
            return var6;
         } finally {
            IOUtils.closeQuietly(var4);
         }

         return var6;
      }

      try {
         var2.func_147963_d(this.field_147636_j);
         return true;
      } catch (Throwable var12) {
         CrashReport var16 = CrashReport.func_85055_a(var12, "Applying mipmap");
         CrashReportCategory var7 = var16.func_85058_a("Sprite being mipmapped");
         var7.func_189529_a("Sprite name", () -> {
            return var2.func_195668_m().toString();
         });
         var7.func_189529_a("Sprite size", () -> {
            return var2.func_94211_a() + " x " + var2.func_94216_b();
         });
         var7.func_189529_a("Sprite frames", () -> {
            return var2.func_110970_k() + " frames";
         });
         var7.func_71507_a("Mipmap levels", this.field_147636_j);
         throw new ReportedException(var16);
      }
   }

   private ResourceLocation func_195420_b(ResourceLocation var1) {
      return new ResourceLocation(var1.func_110624_b(), String.format("%s/%s%s", this.field_94254_c, var1.func_110623_a(), ".png"));
   }

   public TextureAtlasSprite func_110572_b(String var1) {
      return this.func_195424_a(new ResourceLocation(var1));
   }

   public void func_94248_c() {
      this.func_195412_h();
      Iterator var1 = this.field_94258_i.iterator();

      while(var1.hasNext()) {
         TextureAtlasSprite var2 = (TextureAtlasSprite)var1.next();
         var2.func_94219_l();
      }

   }

   public void func_199362_a(IResourceManager var1, ResourceLocation var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      } else {
         this.field_195427_i.add(var2);
      }
   }

   public void func_110550_d() {
      this.func_94248_c();
   }

   public void func_147633_a(int var1) {
      this.field_147636_j = var1;
   }

   public TextureAtlasSprite func_195424_a(ResourceLocation var1) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.field_94252_e.get(var1);
      return var2 == null ? this.field_94249_f : var2;
   }

   public void func_195419_g() {
      Iterator var1 = this.field_94252_e.values().iterator();

      while(var1.hasNext()) {
         TextureAtlasSprite var2 = (TextureAtlasSprite)var1.next();
         var2.func_130103_l();
      }

      this.field_94252_e.clear();
      this.field_94258_i.clear();
   }
}
