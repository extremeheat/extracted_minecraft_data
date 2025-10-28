package net.minecraft.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

public class KeyMapping implements Comparable<KeyMapping> {
   private static final Map<String, KeyMapping> ALL = Maps.newHashMap();
   private static final Map<InputConstants.Key, List<KeyMapping>> MAP = Maps.newHashMap();
   private final String name;
   private final InputConstants.Key defaultKey;
   private final Category category;
   protected InputConstants.Key key;
   private boolean isDown;
   private int clickCount;
   private final int order;

   public static void click(InputConstants.Key var0) {
      forAllKeyMappings(var0, (var0x) -> ++var0x.clickCount);
   }

   public static void set(InputConstants.Key var0, boolean var1) {
      forAllKeyMappings(var0, (var1x) -> var1x.setDown(var1));
   }

   private static void forAllKeyMappings(InputConstants.Key var0, Consumer<KeyMapping> var1) {
      List var2 = (List)MAP.get(var0);
      if (var2 != null && !var2.isEmpty()) {
         for(KeyMapping var4 : var2) {
            var1.accept(var4);
         }
      }

   }

   public static void setAll() {
      Window var0 = Minecraft.getInstance().getWindow();

      for(KeyMapping var2 : ALL.values()) {
         if (var2.shouldSetOnIngameFocus()) {
            var2.setDown(InputConstants.isKeyDown(var0, var2.key.getValue()));
         }
      }

   }

   public static void releaseAll() {
      for(KeyMapping var1 : ALL.values()) {
         var1.release();
      }

   }

   public static void restoreToggleStatesOnScreenClosed() {
      for(KeyMapping var1 : ALL.values()) {
         if (var1 instanceof ToggleKeyMapping var2) {
            if (var2.shouldRestoreStateOnScreenClosed()) {
               var2.setDown(true);
            }
         }
      }

   }

   public static void resetToggleKeys() {
      for(KeyMapping var1 : ALL.values()) {
         if (var1 instanceof ToggleKeyMapping var2) {
            var2.reset();
         }
      }

   }

   public static void resetMapping() {
      MAP.clear();

      for(KeyMapping var1 : ALL.values()) {
         var1.registerMapping(var1.key);
      }

   }

   public KeyMapping(String var1, int var2, Category var3) {
      this(var1, InputConstants.Type.KEYSYM, var2, var3);
   }

   public KeyMapping(String var1, InputConstants.Type var2, int var3, Category var4) {
      this(var1, var2, var3, var4, 0);
   }

   public KeyMapping(String var1, InputConstants.Type var2, int var3, Category var4, int var5) {
      super();
      this.name = var1;
      this.key = var2.getOrCreate(var3);
      this.defaultKey = this.key;
      this.category = var4;
      this.order = var5;
      ALL.put(var1, this);
      this.registerMapping(this.key);
   }

   public boolean isDown() {
      return this.isDown;
   }

   public Category getCategory() {
      return this.category;
   }

   public boolean consumeClick() {
      if (this.clickCount == 0) {
         return false;
      } else {
         --this.clickCount;
         return true;
      }
   }

   protected void release() {
      this.clickCount = 0;
      this.setDown(false);
   }

   protected boolean shouldSetOnIngameFocus() {
      return this.key.getType() == InputConstants.Type.KEYSYM && this.key.getValue() != InputConstants.UNKNOWN.getValue();
   }

   public String getName() {
      return this.name;
   }

   public InputConstants.Key getDefaultKey() {
      return this.defaultKey;
   }

   public void setKey(InputConstants.Key var1) {
      this.key = var1;
   }

   public int compareTo(KeyMapping var1) {
      if (this.category == var1.category) {
         return this.order == var1.order ? I18n.get(this.name).compareTo(I18n.get(var1.name)) : Integer.compare(this.order, var1.order);
      } else {
         return Integer.compare(KeyMapping.Category.SORT_ORDER.indexOf(this.category), KeyMapping.Category.SORT_ORDER.indexOf(var1.category));
      }
   }

   public static Supplier<Component> createNameSupplier(String var0) {
      KeyMapping var1 = (KeyMapping)ALL.get(var0);
      if (var1 == null) {
         return () -> Component.translatable(var0);
      } else {
         Objects.requireNonNull(var1);
         return var1::getTranslatedKeyMessage;
      }
   }

   public boolean same(KeyMapping var1) {
      return this.key.equals(var1.key);
   }

   public boolean isUnbound() {
      return this.key.equals(InputConstants.UNKNOWN);
   }

   public boolean matches(KeyEvent var1) {
      if (var1.key() == InputConstants.UNKNOWN.getValue()) {
         return this.key.getType() == InputConstants.Type.SCANCODE && this.key.getValue() == var1.scancode();
      } else {
         return this.key.getType() == InputConstants.Type.KEYSYM && this.key.getValue() == var1.key();
      }
   }

   public boolean matchesMouse(MouseButtonEvent var1) {
      return this.key.getType() == InputConstants.Type.MOUSE && this.key.getValue() == var1.button();
   }

   public Component getTranslatedKeyMessage() {
      return this.key.getDisplayName();
   }

   public boolean isDefault() {
      return this.key.equals(this.defaultKey);
   }

   public String saveString() {
      return this.key.getName();
   }

   public void setDown(boolean var1) {
      this.isDown = var1;
   }

   private void registerMapping(InputConstants.Key var1) {
      ((List)MAP.computeIfAbsent(var1, (var0) -> new ArrayList())).add(this);
   }

   public static @Nullable KeyMapping get(String var0) {
      return (KeyMapping)ALL.get(var0);
   }

   // $FF: synthetic method
   public int compareTo(final Object var1) {
      return this.compareTo((KeyMapping)var1);
   }

   public static record Category(ResourceLocation id) {
      static final List<Category> SORT_ORDER = new ArrayList();
      public static final Category MOVEMENT = register("movement");
      public static final Category MISC = register("misc");
      public static final Category MULTIPLAYER = register("multiplayer");
      public static final Category GAMEPLAY = register("gameplay");
      public static final Category INVENTORY = register("inventory");
      public static final Category CREATIVE = register("creative");
      public static final Category SPECTATOR = register("spectator");
      public static final Category DEBUG = register("debug");

      public Category(ResourceLocation var1) {
         super();
         this.id = var1;
      }

      private static Category register(String var0) {
         return register(ResourceLocation.withDefaultNamespace(var0));
      }

      public static Category register(ResourceLocation var0) {
         Category var1 = new Category(var0);
         if (SORT_ORDER.contains(var1)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Category '%s' is already registered.", var0));
         } else {
            SORT_ORDER.add(var1);
            return var1;
         }
      }

      public Component label() {
         return Component.translatable(this.id.toLanguageKey("key.category"));
      }
   }
}
