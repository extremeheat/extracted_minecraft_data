package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourceIndexFolder;
import net.minecraft.util.Session;

public class GameConfiguration {
   public final GameConfiguration.UserInformation field_178745_a;
   public final GameConfiguration.DisplayInformation field_178743_b;
   public final GameConfiguration.FolderInformation field_178744_c;
   public final GameConfiguration.GameInformation field_178741_d;
   public final GameConfiguration.ServerInformation field_178742_e;

   public GameConfiguration(GameConfiguration.UserInformation var1, GameConfiguration.DisplayInformation var2, GameConfiguration.FolderInformation var3, GameConfiguration.GameInformation var4, GameConfiguration.ServerInformation var5) {
      super();
      this.field_178745_a = var1;
      this.field_178743_b = var2;
      this.field_178744_c = var3;
      this.field_178741_d = var4;
      this.field_178742_e = var5;
   }

   public static class ServerInformation {
      public final String field_178754_a;
      public final int field_178753_b;

      public ServerInformation(String var1, int var2) {
         super();
         this.field_178754_a = var1;
         this.field_178753_b = var2;
      }
   }

   public static class FolderInformation {
      public final File field_178760_a;
      public final File field_178758_b;
      public final File field_178759_c;
      public final String field_178757_d;

      public FolderInformation(File var1, File var2, File var3, @Nullable String var4) {
         super();
         this.field_178760_a = var1;
         this.field_178758_b = var2;
         this.field_178759_c = var3;
         this.field_178757_d = var4;
      }

      public ResourceIndex func_187052_a() {
         return (ResourceIndex)(this.field_178757_d == null ? new ResourceIndexFolder(this.field_178759_c) : new ResourceIndex(this.field_178759_c, this.field_178757_d));
      }
   }

   public static class DisplayInformation {
      public final int field_178764_a;
      public final int field_178762_b;
      public final Optional<Integer> field_199045_c;
      public final Optional<Integer> field_199046_d;
      public final boolean field_178763_c;

      public DisplayInformation(int var1, int var2, Optional<Integer> var3, Optional<Integer> var4, boolean var5) {
         super();
         this.field_178764_a = var1;
         this.field_178762_b = var2;
         this.field_199045_c = var3;
         this.field_199046_d = var4;
         this.field_178763_c = var5;
      }
   }

   public static class UserInformation {
      public final Session field_178752_a;
      public final PropertyMap field_178750_b;
      public final PropertyMap field_181172_c;
      public final Proxy field_178751_c;

      public UserInformation(Session var1, PropertyMap var2, PropertyMap var3, Proxy var4) {
         super();
         this.field_178752_a = var1;
         this.field_178750_b = var2;
         this.field_181172_c = var3;
         this.field_178751_c = var4;
      }
   }

   public static class GameInformation {
      public final boolean field_178756_a;
      public final String field_178755_b;
      public final String field_187053_c;

      public GameInformation(boolean var1, String var2, String var3) {
         super();
         this.field_178756_a = var1;
         this.field_178755_b = var2;
         this.field_187053_c = var3;
      }
   }
}
