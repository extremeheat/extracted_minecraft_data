package net.minecraft.world.item.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;

public record WrittenBookContent(Filterable<String> title, String author, int generation, List<Filterable<Component>> pages, boolean resolved) implements BookContent<Component, WrittenBookContent> {
   public static final WrittenBookContent EMPTY = new WrittenBookContent(Filterable.passThrough(""), "", 0, List.of(), true);
   public static final int PAGE_LENGTH = 32767;
   public static final int TITLE_LENGTH = 16;
   public static final int TITLE_MAX_LENGTH = 32;
   public static final int MAX_GENERATION = 3;
   public static final int MAX_CRAFTABLE_GENERATION = 2;
   public static final Codec<Component> CONTENT_CODEC = ComponentSerialization.flatCodec(32767);
   public static final Codec<List<Filterable<Component>>> PAGES_CODEC;
   public static final Codec<WrittenBookContent> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, WrittenBookContent> STREAM_CODEC;

   public WrittenBookContent(Filterable<String> var1, String var2, int var3, List<Filterable<Component>> var4, boolean var5) {
      super();
      if (var3 >= 0 && var3 <= 3) {
         this.title = var1;
         this.author = var2;
         this.generation = var3;
         this.pages = var4;
         this.resolved = var5;
      } else {
         throw new IllegalArgumentException("Generation was " + var3 + ", but must be between 0 and 3");
      }
   }

   private static Codec<Filterable<Component>> pageCodec(Codec<Component> var0) {
      return Filterable.codec(var0);
   }

   public static Codec<List<Filterable<Component>>> pagesCodec(Codec<Component> var0) {
      return pageCodec(var0).listOf();
   }

   @Nullable
   public WrittenBookContent tryCraftCopy() {
      return this.generation >= 2 ? null : new WrittenBookContent(this.title, this.author, this.generation + 1, this.pages, this.resolved);
   }

   @Nullable
   public WrittenBookContent resolve(CommandSourceStack var1, @Nullable Player var2) {
      if (this.resolved) {
         return null;
      } else {
         ImmutableList.Builder var3 = ImmutableList.builderWithExpectedSize(this.pages.size());
         Iterator var4 = this.pages.iterator();

         while(var4.hasNext()) {
            Filterable var5 = (Filterable)var4.next();
            Optional var6 = resolvePage(var1, var2, var5);
            if (var6.isEmpty()) {
               return null;
            }

            var3.add((Filterable)var6.get());
         }

         return new WrittenBookContent(this.title, this.author, this.generation, var3.build(), true);
      }
   }

   public WrittenBookContent markResolved() {
      return new WrittenBookContent(this.title, this.author, this.generation, this.pages, true);
   }

   private static Optional<Filterable<Component>> resolvePage(CommandSourceStack var0, @Nullable Player var1, Filterable<Component> var2) {
      return var2.resolve((var2x) -> {
         try {
            MutableComponent var3 = ComponentUtils.updateForEntity(var0, (Component)var2x, var1, 0);
            return isPageTooLarge(var3, var0.registryAccess()) ? Optional.empty() : Optional.of(var3);
         } catch (Exception var4) {
            return Optional.of(var2x);
         }
      });
   }

   private static boolean isPageTooLarge(Component var0, HolderLookup.Provider var1) {
      return Component.Serializer.toJson(var0, var1).length() > 32767;
   }

   public List<Component> getPages(boolean var1) {
      return Lists.transform(this.pages, (var1x) -> {
         return (Component)var1x.get(var1);
      });
   }

   public WrittenBookContent withReplacedPages(List<Filterable<Component>> var1) {
      return new WrittenBookContent(this.title, this.author, this.generation, var1, false);
   }

   public Filterable<String> title() {
      return this.title;
   }

   public String author() {
      return this.author;
   }

   public int generation() {
      return this.generation;
   }

   public List<Filterable<Component>> pages() {
      return this.pages;
   }

   public boolean resolved() {
      return this.resolved;
   }

   // $FF: synthetic method
   public Object withReplacedPages(final List var1) {
      return this.withReplacedPages(var1);
   }

   static {
      PAGES_CODEC = pagesCodec(CONTENT_CODEC);
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Filterable.codec(Codec.string(0, 32)).fieldOf("title").forGetter(WrittenBookContent::title), Codec.STRING.fieldOf("author").forGetter(WrittenBookContent::author), ExtraCodecs.intRange(0, 3).optionalFieldOf("generation", 0).forGetter(WrittenBookContent::generation), PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WrittenBookContent::pages), Codec.BOOL.optionalFieldOf("resolved", false).forGetter(WrittenBookContent::resolved)).apply(var0, WrittenBookContent::new);
      });
      STREAM_CODEC = StreamCodec.composite(Filterable.streamCodec(ByteBufCodecs.stringUtf8(32)), WrittenBookContent::title, ByteBufCodecs.STRING_UTF8, WrittenBookContent::author, ByteBufCodecs.VAR_INT, WrittenBookContent::generation, Filterable.streamCodec(ComponentSerialization.STREAM_CODEC).apply(ByteBufCodecs.list()), WrittenBookContent::pages, ByteBufCodecs.BOOL, WrittenBookContent::resolved, WrittenBookContent::new);
   }
}
