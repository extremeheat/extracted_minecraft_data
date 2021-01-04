package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnopenedPack implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final PackMetadataSection BROKEN_ASSETS_FALLBACK;
   private final String id;
   private final Supplier<Pack> supplier;
   private final Component title;
   private final Component description;
   private final PackCompatibility compatibility;
   private final UnopenedPack.Position defaultPosition;
   private final boolean required;
   private final boolean fixedPosition;

   @Nullable
   public static <T extends UnopenedPack> T create(String var0, boolean var1, Supplier<Pack> var2, UnopenedPack.UnopenedPackConstructor<T> var3, UnopenedPack.Position var4) {
      try {
         Pack var5 = (Pack)var2.get();
         Throwable var6 = null;

         UnopenedPack var8;
         try {
            PackMetadataSection var7 = (PackMetadataSection)var5.getMetadataSection(PackMetadataSection.SERIALIZER);
            if (var1 && var7 == null) {
               LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
               var7 = BROKEN_ASSETS_FALLBACK;
            }

            if (var7 == null) {
               LOGGER.warn("Couldn't find pack meta for pack {}", var0);
               return null;
            }

            var8 = var3.create(var0, var1, var2, var5, var7, var4);
         } catch (Throwable var19) {
            var6 = var19;
            throw var19;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var18) {
                     var6.addSuppressed(var18);
                  }
               } else {
                  var5.close();
               }
            }

         }

         return var8;
      } catch (IOException var21) {
         LOGGER.warn("Couldn't get pack info for: {}", var21.toString());
         return null;
      }
   }

   public UnopenedPack(String var1, boolean var2, Supplier<Pack> var3, Component var4, Component var5, PackCompatibility var6, UnopenedPack.Position var7, boolean var8) {
      super();
      this.id = var1;
      this.supplier = var3;
      this.title = var4;
      this.description = var5;
      this.compatibility = var6;
      this.required = var2;
      this.defaultPosition = var7;
      this.fixedPosition = var8;
   }

   public UnopenedPack(String var1, boolean var2, Supplier<Pack> var3, Pack var4, PackMetadataSection var5, UnopenedPack.Position var6) {
      this(var1, var2, var3, new TextComponent(var4.getName()), var5.getDescription(), PackCompatibility.forFormat(var5.getPackFormat()), var6, false);
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.description;
   }

   public Component getChatLink(boolean var1) {
      return ComponentUtils.wrapInSquareBrackets(new TextComponent(this.id)).withStyle((var2) -> {
         var2.setColor(var1 ? ChatFormatting.GREEN : ChatFormatting.RED).setInsertion(StringArgumentType.escapeIfRequired(this.id)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new TextComponent("")).append(this.title).append("\n").append(this.description)));
      });
   }

   public PackCompatibility getCompatibility() {
      return this.compatibility;
   }

   public Pack open() {
      return (Pack)this.supplier.get();
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

   public UnopenedPack.Position getDefaultPosition() {
      return this.defaultPosition;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof UnopenedPack)) {
         return false;
      } else {
         UnopenedPack var2 = (UnopenedPack)var1;
         return this.id.equals(var2.id);
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public void close() {
   }

   static {
      BROKEN_ASSETS_FALLBACK = new PackMetadataSection((new TranslatableComponent("resourcePack.broken_assets", new Object[0])).withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.ITALIC}), SharedConstants.getCurrentVersion().getPackVersion());
   }

   public static enum Position {
      TOP,
      BOTTOM;

      private Position() {
      }

      public <T, P extends UnopenedPack> int insert(List<T> var1, T var2, Function<T, P> var3, boolean var4) {
         UnopenedPack.Position var5 = var4 ? this.opposite() : this;
         int var6;
         UnopenedPack var7;
         if (var5 == BOTTOM) {
            for(var6 = 0; var6 < var1.size(); ++var6) {
               var7 = (UnopenedPack)var3.apply(var1.get(var6));
               if (!var7.isFixedPosition() || var7.getDefaultPosition() != this) {
                  break;
               }
            }

            var1.add(var6, var2);
            return var6;
         } else {
            for(var6 = var1.size() - 1; var6 >= 0; --var6) {
               var7 = (UnopenedPack)var3.apply(var1.get(var6));
               if (!var7.isFixedPosition() || var7.getDefaultPosition() != this) {
                  break;
               }
            }

            var1.add(var6 + 1, var2);
            return var6 + 1;
         }
      }

      public UnopenedPack.Position opposite() {
         return this == TOP ? BOTTOM : TOP;
      }
   }

   @FunctionalInterface
   public interface UnopenedPackConstructor<T extends UnopenedPack> {
      @Nullable
      T create(String var1, boolean var2, Supplier<Pack> var3, Pack var4, PackMetadataSection var5, UnopenedPack.Position var6);
   }
}
