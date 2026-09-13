package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class ResourcePackRepository$Entry {
   private final File field_110523_b;
   private IResourcePack field_110524_c;
   private PackMetadataSection field_110521_d;
   private BufferedImage field_110522_e;
   private ResourceLocation field_110520_f;

   private ResourcePackRepository$Entry(ResourcePackRepository var1, File var2) {
      super();
      this.field_110525_a = var1;
      this.field_110523_b = var2;
   }

   public void func_110516_a() {
      this.field_110524_c = (IResourcePack)(this.field_110523_b.isDirectory()
         ? new FolderResourcePack(this.field_110523_b)
         : new FileResourcePack(this.field_110523_b));
      this.field_110521_d = (PackMetadataSection)this.field_110524_c.func_135058_a(this.field_110525_a.field_110621_c, "pack");

      try {
         this.field_110522_e = this.field_110524_c.func_110586_a();
      } catch (IOException var2) {
      }

      if (this.field_110522_e == null) {
         this.field_110522_e = this.field_110525_a.field_110620_b.func_110586_a();
      }

      this.func_110517_b();
   }

   public void func_110518_a(TextureManager var1) {
      if (this.field_110520_f == null) {
         this.field_110520_f = var1.func_110578_a("texturepackicon", new DynamicTexture(this.field_110522_e));
      }

      var1.func_110577_a(this.field_110520_f);
   }

   public void func_110517_b() {
      if (this.field_110524_c instanceof Closeable) {
         IOUtils.closeQuietly((Closeable)this.field_110524_c);
      }
   }

   public IResourcePack func_110514_c() {
      return this.field_110524_c;
   }

   public String func_110515_d() {
      return this.field_110524_c.func_130077_b();
   }

   public String func_110519_e() {
      return this.field_110521_d == null
         ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing 'pack' section)"
         : this.field_110521_d.func_152805_a().func_150254_d();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ResourcePackRepository$Entry ? this.toString().equals(var1.toString()) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.toString().hashCode();
   }

   @Override
   public String toString() {
      return String.format("%s:%s:%d", this.field_110523_b.getName(), this.field_110523_b.isDirectory() ? "folder" : "zip", this.field_110523_b.lastModified());
   }
}
