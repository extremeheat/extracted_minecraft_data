package net.minecraft.client.settings;

import net.minecraft.util.MathHelper;

public enum GameSettings$Options {
   INVERT_MOUSE("options.invertMouse", false, true),
   SENSITIVITY("options.sensitivity", true, false),
   FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
   GAMMA("options.gamma", true, false),
   SATURATION("options.saturation", true, false),
   RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
   VIEW_BOBBING("options.viewBobbing", false, true),
   ANAGLYPH("options.anaglyph", false, true),
   ADVANCED_OPENGL("options.advancedOpengl", false, true),
   FRAMERATE_LIMIT("options.framerateLimit", true, false, 10.0F, 260.0F, 10.0F),
   FBO_ENABLE("options.fboEnable", false, true),
   DIFFICULTY("options.difficulty", false, false),
   GRAPHICS("options.graphics", false, false),
   AMBIENT_OCCLUSION("options.ao", false, false),
   GUI_SCALE("options.guiScale", false, false),
   RENDER_CLOUDS("options.renderClouds", false, true),
   PARTICLES("options.particles", false, false),
   CHAT_VISIBILITY("options.chat.visibility", false, false),
   CHAT_COLOR("options.chat.color", false, true),
   CHAT_LINKS("options.chat.links", false, true),
   CHAT_OPACITY("options.chat.opacity", true, false),
   CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
   SNOOPER_ENABLED("options.snooper", false, true),
   USE_FULLSCREEN("options.fullscreen", false, true),
   ENABLE_VSYNC("options.vsync", false, true),
   SHOW_CAPE("options.showCape", false, true),
   TOUCHSCREEN("options.touchscreen", false, true),
   CHAT_SCALE("options.chat.scale", true, false),
   CHAT_WIDTH("options.chat.width", true, false),
   CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
   CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
   MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
   ANISOTROPIC_FILTERING("options.anisotropicFiltering", true, false, 1.0F, 16.0F, 0.0F),
   FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
   STREAM_BYTES_PER_PIXEL("options.stream.bytesPerPixel", true, false),
   STREAM_VOLUME_MIC("options.stream.micVolumne", true, false),
   STREAM_VOLUME_SYSTEM("options.stream.systemVolume", true, false),
   STREAM_KBPS("options.stream.kbps", true, false),
   STREAM_FPS("options.stream.fps", true, false),
   STREAM_COMPRESSION("options.stream.compression", false, false),
   STREAM_SEND_METADATA("options.stream.sendMetadata", false, true),
   STREAM_CHAT_ENABLED("options.stream.chat.enabled", false, false),
   STREAM_CHAT_USER_FILTER("options.stream.chat.userFilter", false, false),
   STREAM_MIC_TOGGLE_BEHAVIOR("options.stream.micToggleBehavior", false, false);

   private final boolean field_74385_A;
   private final boolean field_74386_B;
   private final String field_74387_C;
   private final float field_148270_M;
   private float field_148271_N;
   private float field_148272_O;

   public static GameSettings$Options func_74379_a(int var0) {
      for(GameSettings$Options var4 : values()) {
         if (var4.func_74381_c() == var0) {
            return var4;
         }
      }

      return null;
   }

   private GameSettings$Options(String var3, boolean var4, boolean var5) {
      this(var3, var4, var5, 0.0F, 1.0F, 0.0F);
   }

   private GameSettings$Options(String var3, boolean var4, boolean var5, float var6, float var7, float var8) {
      this.field_74387_C = var3;
      this.field_74385_A = var4;
      this.field_74386_B = var5;
      this.field_148271_N = var6;
      this.field_148272_O = var7;
      this.field_148270_M = var8;
   }

   public boolean func_74380_a() {
      return this.field_74385_A;
   }

   public boolean func_74382_b() {
      return this.field_74386_B;
   }

   public int func_74381_c() {
      return this.ordinal();
   }

   public String func_74378_d() {
      return this.field_74387_C;
   }

   public float func_148267_f() {
      return this.field_148272_O;
   }

   public void func_148263_a(float var1) {
      this.field_148272_O = var1;
   }

   public float func_148266_c(float var1) {
      return MathHelper.func_76131_a((this.func_148268_e(var1) - this.field_148271_N) / (this.field_148272_O - this.field_148271_N), 0.0F, 1.0F);
   }

   public float func_148262_d(float var1) {
      return this.func_148268_e(this.field_148271_N + (this.field_148272_O - this.field_148271_N) * MathHelper.func_76131_a(var1, 0.0F, 1.0F));
   }

   public float func_148268_e(float var1) {
      var1 = this.func_148264_f(var1);
      return MathHelper.func_76131_a(var1, this.field_148271_N, this.field_148272_O);
   }

   protected float func_148264_f(float var1) {
      if (this.field_148270_M > 0.0F) {
         var1 = this.field_148270_M * (float)Math.round(var1 / this.field_148270_M);
      }

      return var1;
   }
}
