package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.flag.FeatureFlagSet;
import org.slf4j.Logger;

public class Pack {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String id;
   private final Pack.ResourcesSupplier resources;
   private final Component title;
   private final Component description;
   private final PackCompatibility compatibility;
   private final FeatureFlagSet requestedFeatures;
   private final Pack.Position defaultPosition;
   private final boolean required;
   private final boolean fixedPosition;
   private final PackSource packSource;

   @Nullable
   public static Pack readMetaAndCreate(
      String var0, Component var1, boolean var2, Pack.ResourcesSupplier var3, PackType var4, Pack.Position var5, PackSource var6
   ) {
      Pack.Info var7 = readPackInfo(var0, var3);
      return var7 != null ? create(var0, var1, var2, var3, var7, var4, var5, false, var6) : null;
   }

   public static Pack create(
      String var0, Component var1, boolean var2, Pack.ResourcesSupplier var3, Pack.Info var4, PackType var5, Pack.Position var6, boolean var7, PackSource var8
   ) {
      return new Pack(var0, var2, var3, var1, var4, var4.compatibility(var5), var6, var7, var8);
   }

   private Pack(
      String var1,
      boolean var2,
      Pack.ResourcesSupplier var3,
      Component var4,
      Pack.Info var5,
      PackCompatibility var6,
      Pack.Position var7,
      boolean var8,
      PackSource var9
   ) {
      super();
      this.id = var1;
      this.resources = var3;
      this.title = var4;
      this.description = var5.description();
      this.compatibility = var6;
      this.requestedFeatures = var5.requestedFeatures();
      this.required = var2;
      this.defaultPosition = var7;
      this.fixedPosition = var8;
      this.packSource = var9;
   }

   @Nullable
   public static Pack.Info readPackInfo(String var0, Pack.ResourcesSupplier var1) {
      try {
         Pack.Info var6;
         try (PackResources var2 = var1.open(var0)) {
            PackMetadataSection var3 = var2.getMetadataSection(PackMetadataSection.TYPE);
            if (var3 == null) {
               LOGGER.warn("Missing metadata in pack {}", var0);
               return null;
            }

            FeatureFlagsMetadataSection var4 = var2.getMetadataSection(FeatureFlagsMetadataSection.TYPE);
            FeatureFlagSet var5 = var4 != null ? var4.flags() : FeatureFlagSet.of();
            var6 = new Pack.Info(var3.getDescription(), var3.getPackFormat(), var5);
         }

         return var6;
      } catch (Exception var9) {
         LOGGER.warn("Failed to read pack metadata", var9);
         return null;
      }
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.description;
   }

   public Component getChatLink(boolean var1) {
      return ComponentUtils.wrapInSquareBrackets(this.packSource.decorate(Component.literal(this.id)))
         .withStyle(
            var2 -> var2.withColor(var1 ? ChatFormatting.GREEN : ChatFormatting.RED)
                  .withInsertion(StringArgumentType.escapeIfRequired(this.id))
                  .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(this.title).append("\n").append(this.description)))
         );
   }

   public PackCompatibility getCompatibility() {
      return this.compatibility;
   }

   public FeatureFlagSet getRequestedFeatures() {
      return this.requestedFeatures;
   }

   public PackResources open() {
      return this.resources.open(this.id);
   }

   public String getId() {
      return this.id;
   }

   public boolean isRequired() {
      return this.required;
   }

   public boolean isFixedPosition() {
      return this.fixedPosition;
   }

   public Pack.Position getDefaultPosition() {
      return this.defaultPosition;
   }

   public PackSource getPackSource() {
      return this.packSource;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Pack)) {
         return false;
      } else {
         Pack var2 = (Pack)var1;
         return this.id.equals(var2.id);
      }
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   public static record Info(Component a, int b, FeatureFlagSet c) {
      private final Component description;
      private final int format;
      private final FeatureFlagSet requestedFeatures;

      public Info(Component var1, int var2, FeatureFlagSet var3) {
         super();
         this.description = var1;
         this.format = var2;
         this.requestedFeatures = var3;
      }

      public PackCompatibility compatibility(PackType var1) {
         return PackCompatibility.forFormat(this.format, var1);
      }
   }

   public static enum Position {
      TOP,
      BOTTOM;

      private Position() {
      }

      public <T> int insert(List<T> var1, T var2, Function<T, Pack> var3, boolean var4) {
         Pack.Position var5 = var4 ? this.opposite() : this;
         if (var5 == BOTTOM) {
            int var8;
            for(var8 = 0; var8 < var1.size(); ++var8) {
               Pack var9 = (Pack)var3.apply(var1.get(var8));
               if (!var9.isFixedPosition() || var9.getDefaultPosition() != this) {
                  break;
               }
            }

            var1.add(var8, var2);
            return var8;
         } else {
            int var6;
            for(var6 = var1.size() - 1; var6 >= 0; --var6) {
               Pack var7 = (Pack)var3.apply(var1.get(var6));
               if (!var7.isFixedPosition() || var7.getDefaultPosition() != this) {
                  break;
               }
            }

            var1.add(var6 + 1, var2);
            return var6 + 1;
         }
      }

      public Pack.Position opposite() {
         return this == TOP ? BOTTOM : TOP;
      }
   }

   @FunctionalInterface
   public interface ResourcesSupplier {
      PackResources open(String var1);
   }
}
