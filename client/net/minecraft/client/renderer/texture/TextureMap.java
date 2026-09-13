package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject, IIconRegister {
   private static final Logger field_147635_d = LogManager.getLogger();
   public static final ResourceLocation field_110575_b = new ResourceLocation("textures/atlas/blocks.png");
   public static final ResourceLocation field_110576_c = new ResourceLocation("textures/atlas/items.png");
   private final List field_94258_i = Lists.newArrayList();
   private final Map field_110574_e = Maps.newHashMap();
   private final Map field_94252_e = Maps.newHashMap();
   private final int field_94255_a;
   private final String field_94254_c;
   private int field_147636_j;
   private int field_147637_k = 1;
   private final TextureAtlasSprite field_94249_f = new TextureAtlasSprite("missingno");

   public TextureMap(int var1, String var2) {
      super();
      this.field_94255_a = var1;
      this.field_94254_c = var2;
      this.func_110573_f();
   }

   private void func_110569_e() {
      int[] var1;
      if ((float)this.field_147637_k > 1.0F) {
         boolean var2 = true;
         boolean var3 = true;
         boolean var4 = true;
         this.field_94249_f.func_110966_b(32);
         this.field_94249_f.func_110969_c(32);
         var1 = new int[1024];
         System.arraycopy(TextureUtil.field_110999_b, 0, var1, 0, TextureUtil.field_110999_b.length);
         TextureUtil.func_147948_a(var1, 16, 16, 8);
      } else {
         var1 = TextureUtil.field_110999_b;
         this.field_94249_f.func_110966_b(16);
         this.field_94249_f.func_110969_c(16);
      }

      int[][] var5 = new int[this.field_147636_j + 1][];
      var5[0] = var1;
      this.field_94249_f.func_110968_a(Lists.newArrayList(new int[][][]{var5}));
   }

   @Override
   public void func_110551_a(IResourceManager var1) {
      this.func_110569_e();
      this.func_147631_c();
      this.func_110571_b(var1);
   }

   public void func_110571_b(IResourceManager var1) {
      int var2 = Minecraft.func_71369_N();
      Stitcher var3 = new Stitcher(var2, var2, true, 0, this.field_147636_j);
      this.field_94252_e.clear();
      this.field_94258_i.clear();
      int var4 = 2147483647;

      for(Entry var6 : this.field_110574_e.entrySet()) {
         ResourceLocation var7 = new ResourceLocation((String)var6.getKey());
         TextureAtlasSprite var8 = (TextureAtlasSprite)var6.getValue();
         ResourceLocation var9 = this.func_147634_a(var7, 0);

         try {
            IResource var10 = var1.func_110536_a(var9);
            BufferedImage[] var11 = new BufferedImage[1 + this.field_147636_j];
            var11[0] = ImageIO.read(var10.func_110527_b());
            TextureMetadataSection var12 = (TextureMetadataSection)var10.func_110526_a("texture");
            if (var12 != null) {
               List var13 = var12.func_148535_c();
               if (!var13.isEmpty()) {
                  int var14 = var11[0].getWidth();
                  int var15 = var11[0].getHeight();
                  if (MathHelper.func_151236_b(var14) != var14 || MathHelper.func_151236_b(var15) != var15) {
                     throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                  }
               }

               for(int var39 : var13) {
                  if (var39 > 0 && var39 < var11.length - 1 && var11[var39] == null) {
                     ResourceLocation var16 = this.func_147634_a(var7, var39);

                     try {
                        var11[var39] = ImageIO.read(var1.func_110536_a(var16).func_110527_b());
                     } catch (IOException var21) {
                        field_147635_d.error("Unable to load miplevel {} from: {}", new Object[]{var39, var16, var21});
                     }
                  }
               }
            }

            AnimationMetadataSection var37 = (AnimationMetadataSection)var10.func_110526_a("animation");
            var8.func_147964_a(var11, var37, (float)this.field_147637_k > 1.0F);
         } catch (RuntimeException var22) {
            field_147635_d.error("Unable to parse metadata from " + var9, var22);
            continue;
         } catch (IOException var23) {
            field_147635_d.error("Using missing texture, unable to load " + var9, var23);
            continue;
         }

         var4 = Math.min(var4, Math.min(var8.func_94211_a(), var8.func_94216_b()));
         var3.func_110934_a(var8);
      }

      int var24 = MathHelper.func_151239_c(var4);
      if (var24 < this.field_147636_j) {
         field_147635_d.debug(
            "{}: dropping miplevel from {} to {}, because of minTexel: {}", new Object[]{this.field_94254_c, this.field_147636_j, var24, var4}
         );
         this.field_147636_j = var24;
      }

      for(TextureAtlasSprite var27 : this.field_110574_e.values()) {
         try {
            var27.func_147963_d(this.field_147636_j);
         } catch (Throwable var20) {
            CrashReport var32 = CrashReport.func_85055_a(var20, "Applying mipmap");
            CrashReportCategory var34 = var32.func_85058_a("Sprite being mipmapped");
            var34.func_71500_a("Sprite name", new TextureMap$1(this, var27));
            var34.func_71500_a("Sprite size", new TextureMap$2(this, var27));
            var34.func_71500_a("Sprite frames", new TextureMap$3(this, var27));
            var34.func_71507_a("Mipmap levels", this.field_147636_j);
            throw new ReportedException(var32);
         }
      }

      this.field_94249_f.func_147963_d(this.field_147636_j);
      var3.func_110934_a(this.field_94249_f);

      try {
         var3.func_94305_f();
      } catch (StitcherException var19) {
         throw var19;
      }

      field_147635_d.info("Created: {}x{} {}-atlas", new Object[]{var3.func_110935_a(), var3.func_110936_b(), this.field_94254_c});
      TextureUtil.func_147946_a(this.func_110552_b(), this.field_147636_j, var3.func_110935_a(), var3.func_110936_b(), (float)this.field_147637_k);
      HashMap var26 = Maps.newHashMap(this.field_110574_e);

      for(TextureAtlasSprite var30 : var3.func_94309_g()) {
         String var33 = var30.func_94215_i();
         var26.remove(var33);
         this.field_94252_e.put(var33, var30);

         try {
            TextureUtil.func_147955_a(
               var30.func_147965_a(0), var30.func_94211_a(), var30.func_94216_b(), var30.func_130010_a(), var30.func_110967_i(), false, false
            );
         } catch (Throwable var18) {
            CrashReport var35 = CrashReport.func_85055_a(var18, "Stitching texture atlas");
            CrashReportCategory var36 = var35.func_85058_a("Texture being stitched together");
            var36.func_71507_a("Atlas path", this.field_94254_c);
            var36.func_71507_a("Sprite", var30);
            throw new ReportedException(var35);
         }

         if (var30.func_130098_m()) {
            this.field_94258_i.add(var30);
         } else {
            var30.func_130103_l();
         }
      }

      for(TextureAtlasSprite var31 : var26.values()) {
         var31.func_94217_a(this.field_94249_f);
      }
   }

   private ResourceLocation func_147634_a(ResourceLocation var1, int var2) {
      return var2 == 0
         ? new ResourceLocation(var1.func_110624_b(), String.format("%s/%s%s", this.field_94254_c, var1.func_110623_a(), ".png"))
         : new ResourceLocation(var1.func_110624_b(), String.format("%s/mipmaps/%s.%d%s", this.field_94254_c, var1.func_110623_a(), var2, ".png"));
   }

   private void func_110573_f() {
      this.field_110574_e.clear();
      if (this.field_94255_a == 0) {
         for(Block var2 : Block.field_149771_c) {
            if (var2.func_149688_o() != Material.field_151579_a) {
               var2.func_149651_a(this);
            }
         }

         Minecraft.func_71410_x().field_71438_f.func_94140_a(this);
         RenderManager.field_78727_a.func_94178_a(this);
      }

      for(Item var4 : Item.field_150901_e) {
         if (var4 != null && var4.func_94901_k() == this.field_94255_a) {
            var4.func_94581_a(this);
         }
      }
   }

   public TextureAtlasSprite func_110572_b(String var1) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.field_94252_e.get(var1);
      if (var2 == null) {
         var2 = this.field_94249_f;
      }

      return var2;
   }

   public void func_94248_c() {
      TextureUtil.func_94277_a(this.func_110552_b());

      for(TextureAtlasSprite var2 : this.field_94258_i) {
         var2.func_94219_l();
      }
   }

   @Override
   public IIcon func_94245_a(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      } else if (var1.indexOf(47) == -1 && var1.indexOf(92) == -1) {
         Object var2 = (TextureAtlasSprite)this.field_110574_e.get(var1);
         if (var2 == null) {
            if (this.field_94255_a == 1) {
               if ("clock".equals(var1)) {
                  var2 = new TextureClock(var1);
               } else if ("compass".equals(var1)) {
                  var2 = new TextureCompass(var1);
               } else {
                  var2 = new TextureAtlasSprite(var1);
               }
            } else {
               var2 = new TextureAtlasSprite(var1);
            }

            this.field_110574_e.put(var1, var2);
         }

         return (IIcon)var2;
      } else {
         throw new IllegalArgumentException("Name cannot contain slashes!");
      }
   }

   public int func_130086_a() {
      return this.field_94255_a;
   }

   @Override
   public void func_110550_d() {
      this.func_94248_c();
   }

   public void func_147633_a(int var1) {
      this.field_147636_j = var1;
   }

   public void func_147632_b(int var1) {
      this.field_147637_k = var1;
   }
}
