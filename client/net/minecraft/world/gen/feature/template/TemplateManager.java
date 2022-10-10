package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateManager implements IResourceManagerReloadListener {
   private static final Logger field_195431_a = LogManager.getLogger();
   private final Map<ResourceLocation, Template> field_186240_a = Maps.newHashMap();
   private final DataFixer field_191154_c;
   private final MinecraftServer field_195432_d;
   private final Path field_195433_e;

   public TemplateManager(MinecraftServer var1, File var2, DataFixer var3) {
      super();
      this.field_195432_d = var1;
      this.field_191154_c = var3;
      this.field_195433_e = var2.toPath().resolve("generated").normalize();
      var1.func_195570_aG().func_199006_a(this);
   }

   public Template func_200220_a(ResourceLocation var1) {
      Template var2 = this.func_200219_b(var1);
      if (var2 == null) {
         var2 = new Template();
         this.field_186240_a.put(var1, var2);
      }

      return var2;
   }

   @Nullable
   public Template func_200219_b(ResourceLocation var1) {
      return (Template)this.field_186240_a.computeIfAbsent(var1, (var1x) -> {
         Template var2 = this.func_195428_d(var1x);
         return var2 != null ? var2 : this.func_209201_e(var1x);
      });
   }

   public void func_195410_a(IResourceManager var1) {
      this.field_186240_a.clear();
   }

   @Nullable
   private Template func_209201_e(ResourceLocation var1) {
      ResourceLocation var2 = new ResourceLocation(var1.func_110624_b(), "structures/" + var1.func_110623_a() + ".nbt");

      try {
         IResource var3 = this.field_195432_d.func_195570_aG().func_199002_a(var2);
         Throwable var4 = null;

         Template var5;
         try {
            var5 = this.func_209205_a(var3.func_199027_b());
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var15) {
                     var4.addSuppressed(var15);
                  }
               } else {
                  var3.close();
               }
            }

         }

         return var5;
      } catch (FileNotFoundException var18) {
         return null;
      } catch (Throwable var19) {
         field_195431_a.error("Couldn't load structure {}: {}", var1, var19.toString());
         return null;
      }
   }

   @Nullable
   private Template func_195428_d(ResourceLocation var1) {
      if (!this.field_195433_e.toFile().isDirectory()) {
         return null;
      } else {
         Path var2 = this.func_209510_b(var1, ".nbt");

         try {
            FileInputStream var3 = new FileInputStream(var2.toFile());
            Throwable var4 = null;

            Template var5;
            try {
               var5 = this.func_209205_a(var3);
            } catch (Throwable var16) {
               var4 = var16;
               throw var16;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var15) {
                        var4.addSuppressed(var15);
                     }
                  } else {
                     var3.close();
                  }
               }

            }

            return var5;
         } catch (FileNotFoundException var18) {
            return null;
         } catch (IOException var19) {
            field_195431_a.error("Couldn't load structure from {}", var2, var19);
            return null;
         }
      }
   }

   private Template func_209205_a(InputStream var1) throws IOException {
      NBTTagCompound var2 = CompressedStreamTools.func_74796_a(var1);
      if (!var2.func_150297_b("DataVersion", 99)) {
         var2.func_74768_a("DataVersion", 500);
      }

      Template var3 = new Template();
      var3.func_186256_b(NBTUtil.func_210822_a(this.field_191154_c, DataFixTypes.STRUCTURE, var2, var2.func_74762_e("DataVersion")));
      return var3;
   }

   public boolean func_195429_b(ResourceLocation var1) {
      Template var2 = (Template)this.field_186240_a.get(var1);
      if (var2 == null) {
         return false;
      } else {
         Path var3 = this.func_209510_b(var1, ".nbt");
         Path var4 = var3.getParent();
         if (var4 == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(var4, new LinkOption[0]) ? var4.toRealPath() : var4);
            } catch (IOException var19) {
               field_195431_a.error("Failed to create parent directory: {}", var4);
               return false;
            }

            NBTTagCompound var5 = var2.func_189552_a(new NBTTagCompound());

            try {
               FileOutputStream var6 = new FileOutputStream(var3.toFile());
               Throwable var7 = null;

               try {
                  CompressedStreamTools.func_74799_a(var5, var6);
               } catch (Throwable var18) {
                  var7 = var18;
                  throw var18;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var17) {
                           var7.addSuppressed(var17);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }

               return true;
            } catch (Throwable var21) {
               return false;
            }
         }
      }
   }

   private Path func_209509_a(ResourceLocation var1, String var2) {
      try {
         Path var3 = this.field_195433_e.resolve(var1.func_110624_b());
         Path var4 = var3.resolve("structures");
         return Util.func_209535_a(var4, var1.func_110623_a(), var2);
      } catch (InvalidPathException var5) {
         throw new ResourceLocationException("Invalid resource path: " + var1, var5);
      }
   }

   private Path func_209510_b(ResourceLocation var1, String var2) {
      if (var1.func_110623_a().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + var1);
      } else {
         Path var3 = this.func_209509_a(var1, var2);
         if (var3.startsWith(this.field_195433_e) && Util.func_209537_a(var3) && Util.func_209536_b(var3)) {
            return var3;
         } else {
            throw new ResourceLocationException("Invalid resource path: " + var3);
         }
      }
   }

   public void func_189941_a(ResourceLocation var1) {
      this.field_186240_a.remove(var1);
   }
}
