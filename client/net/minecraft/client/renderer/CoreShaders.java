package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class CoreShaders {
   private static final List<ShaderProgram> PROGRAMS = new ArrayList();
   public static final ShaderProgram BLIT_SCREEN;
   public static final ShaderProgram LIGHTMAP;
   public static final ShaderProgram PARTICLE;
   public static final ShaderProgram POSITION;
   public static final ShaderProgram POSITION_COLOR;
   public static final ShaderProgram POSITION_COLOR_LIGHTMAP;
   public static final ShaderProgram POSITION_COLOR_TEX_LIGHTMAP;
   public static final ShaderProgram POSITION_TEX;
   public static final ShaderProgram POSITION_TEX_COLOR;
   public static final ShaderProgram RENDERTYPE_SOLID;
   public static final ShaderProgram RENDERTYPE_CUTOUT_MIPPED;
   public static final ShaderProgram RENDERTYPE_CUTOUT;
   public static final ShaderProgram RENDERTYPE_TRANSLUCENT;
   public static final ShaderProgram RENDERTYPE_TRANSLUCENT_MOVING_BLOCK;
   public static final ShaderProgram RENDERTYPE_ARMOR_CUTOUT_NO_CULL;
   public static final ShaderProgram RENDERTYPE_ARMOR_TRANSLUCENT;
   public static final ShaderProgram RENDERTYPE_ENTITY_SOLID;
   public static final ShaderProgram RENDERTYPE_ENTITY_CUTOUT;
   public static final ShaderProgram RENDERTYPE_ENTITY_CUTOUT_NO_CULL;
   public static final ShaderProgram RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET;
   public static final ShaderProgram RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL;
   public static final ShaderProgram RENDERTYPE_ENTITY_TRANSLUCENT;
   public static final ShaderProgram RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE;
   public static final ShaderProgram RENDERTYPE_ENTITY_SMOOTH_CUTOUT;
   public static final ShaderProgram RENDERTYPE_BEACON_BEAM;
   public static final ShaderProgram RENDERTYPE_ENTITY_DECAL;
   public static final ShaderProgram RENDERTYPE_ENTITY_NO_OUTLINE;
   public static final ShaderProgram RENDERTYPE_ENTITY_SHADOW;
   public static final ShaderProgram RENDERTYPE_ENTITY_ALPHA;
   public static final ShaderProgram RENDERTYPE_EYES;
   public static final ShaderProgram RENDERTYPE_ENERGY_SWIRL;
   public static final ShaderProgram RENDERTYPE_LEASH;
   public static final ShaderProgram RENDERTYPE_WATER_MASK;
   public static final ShaderProgram RENDERTYPE_OUTLINE;
   public static final ShaderProgram RENDERTYPE_ARMOR_ENTITY_GLINT;
   public static final ShaderProgram RENDERTYPE_GLINT_TRANSLUCENT;
   public static final ShaderProgram RENDERTYPE_GLINT;
   public static final ShaderProgram RENDERTYPE_ENTITY_GLINT;
   public static final ShaderProgram RENDERTYPE_TEXT;
   public static final ShaderProgram RENDERTYPE_TEXT_BACKGROUND;
   public static final ShaderProgram RENDERTYPE_TEXT_INTENSITY;
   public static final ShaderProgram RENDERTYPE_TEXT_SEE_THROUGH;
   public static final ShaderProgram RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH;
   public static final ShaderProgram RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH;
   public static final ShaderProgram RENDERTYPE_LIGHTNING;
   public static final ShaderProgram RENDERTYPE_TRIPWIRE;
   public static final ShaderProgram RENDERTYPE_END_PORTAL;
   public static final ShaderProgram RENDERTYPE_END_GATEWAY;
   public static final ShaderProgram RENDERTYPE_CLOUDS;
   public static final ShaderProgram RENDERTYPE_LINES;
   public static final ShaderProgram RENDERTYPE_CRUMBLING;
   public static final ShaderProgram RENDERTYPE_GUI;
   public static final ShaderProgram RENDERTYPE_GUI_OVERLAY;
   public static final ShaderProgram RENDERTYPE_GUI_TEXT_HIGHLIGHT;
   public static final ShaderProgram RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY;
   public static final ShaderProgram RENDERTYPE_BREEZE_WIND;

   public CoreShaders() {
      super();
   }

   private static ShaderProgram register(String var0, VertexFormat var1) {
      return register(var0, var1, ShaderDefines.EMPTY);
   }

   private static ShaderProgram register(String var0, VertexFormat var1, ShaderDefines var2) {
      ShaderProgram var3 = new ShaderProgram(ResourceLocation.withDefaultNamespace("core/" + var0), var1, var2);
      PROGRAMS.add(var3);
      return var3;
   }

   public static List<ShaderProgram> getProgramsToPreload() {
      return PROGRAMS;
   }

   static {
      BLIT_SCREEN = register("blit_screen", DefaultVertexFormat.BLIT_SCREEN);
      LIGHTMAP = register("lightmap", DefaultVertexFormat.BLIT_SCREEN);
      PARTICLE = register("particle", DefaultVertexFormat.PARTICLE);
      POSITION = register("position", DefaultVertexFormat.POSITION);
      POSITION_COLOR = register("position_color", DefaultVertexFormat.POSITION_COLOR);
      POSITION_COLOR_LIGHTMAP = register("position_color_lightmap", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
      POSITION_COLOR_TEX_LIGHTMAP = register("position_color_tex_lightmap", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
      POSITION_TEX = register("position_tex", DefaultVertexFormat.POSITION_TEX);
      POSITION_TEX_COLOR = register("position_tex_color", DefaultVertexFormat.POSITION_TEX_COLOR);
      RENDERTYPE_SOLID = register("rendertype_solid", DefaultVertexFormat.BLOCK);
      RENDERTYPE_CUTOUT_MIPPED = register("rendertype_cutout_mipped", DefaultVertexFormat.BLOCK);
      RENDERTYPE_CUTOUT = register("rendertype_cutout", DefaultVertexFormat.BLOCK);
      RENDERTYPE_TRANSLUCENT = register("rendertype_translucent", DefaultVertexFormat.BLOCK);
      RENDERTYPE_TRANSLUCENT_MOVING_BLOCK = register("rendertype_translucent_moving_block", DefaultVertexFormat.BLOCK);
      RENDERTYPE_ARMOR_CUTOUT_NO_CULL = register("rendertype_armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ARMOR_TRANSLUCENT = register("rendertype_armor_translucent", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENTITY_SOLID = register("rendertype_entity_solid", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENTITY_CUTOUT = register("rendertype_entity_cutout", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENTITY_CUTOUT_NO_CULL = register("rendertype_entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET = register("rendertype_entity_cutout_no_cull_z_offset", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL = register("rendertype_item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENTITY_TRANSLUCENT = register("rendertype_entity_translucent", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE = register("rendertype_entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENTITY_SMOOTH_CUTOUT = register("rendertype_entity_smooth_cutout", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_BEACON_BEAM = register("rendertype_beacon_beam", DefaultVertexFormat.BLOCK);
      RENDERTYPE_ENTITY_DECAL = register("rendertype_entity_decal", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENTITY_NO_OUTLINE = register("rendertype_entity_no_outline", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENTITY_SHADOW = register("rendertype_entity_shadow", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENTITY_ALPHA = register("rendertype_entity_alpha", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_EYES = register("rendertype_eyes", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_ENERGY_SWIRL = register("rendertype_energy_swirl", DefaultVertexFormat.NEW_ENTITY);
      RENDERTYPE_LEASH = register("rendertype_leash", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
      RENDERTYPE_WATER_MASK = register("rendertype_water_mask", DefaultVertexFormat.POSITION);
      RENDERTYPE_OUTLINE = register("rendertype_outline", DefaultVertexFormat.POSITION_TEX_COLOR);
      RENDERTYPE_ARMOR_ENTITY_GLINT = register("rendertype_armor_entity_glint", DefaultVertexFormat.POSITION_TEX);
      RENDERTYPE_GLINT_TRANSLUCENT = register("rendertype_glint_translucent", DefaultVertexFormat.POSITION_TEX);
      RENDERTYPE_GLINT = register("rendertype_glint", DefaultVertexFormat.POSITION_TEX);
      RENDERTYPE_ENTITY_GLINT = register("rendertype_entity_glint", DefaultVertexFormat.POSITION_TEX);
      RENDERTYPE_TEXT = register("rendertype_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
      RENDERTYPE_TEXT_BACKGROUND = register("rendertype_text_background", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
      RENDERTYPE_TEXT_INTENSITY = register("rendertype_text_intensity", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
      RENDERTYPE_TEXT_SEE_THROUGH = register("rendertype_text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
      RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH = register("rendertype_text_background_see_through", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
      RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH = register("rendertype_text_intensity_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
      RENDERTYPE_LIGHTNING = register("rendertype_lightning", DefaultVertexFormat.POSITION_COLOR);
      RENDERTYPE_TRIPWIRE = register("rendertype_tripwire", DefaultVertexFormat.BLOCK);
      RENDERTYPE_END_PORTAL = register("rendertype_end_portal", DefaultVertexFormat.POSITION);
      RENDERTYPE_END_GATEWAY = register("rendertype_end_gateway", DefaultVertexFormat.POSITION);
      RENDERTYPE_CLOUDS = register("rendertype_clouds", DefaultVertexFormat.POSITION_COLOR);
      RENDERTYPE_LINES = register("rendertype_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL);
      RENDERTYPE_CRUMBLING = register("rendertype_crumbling", DefaultVertexFormat.BLOCK);
      RENDERTYPE_GUI = register("rendertype_gui", DefaultVertexFormat.POSITION_COLOR);
      RENDERTYPE_GUI_OVERLAY = register("rendertype_gui_overlay", DefaultVertexFormat.POSITION_COLOR);
      RENDERTYPE_GUI_TEXT_HIGHLIGHT = register("rendertype_gui_text_highlight", DefaultVertexFormat.POSITION_COLOR);
      RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY = register("rendertype_gui_ghost_recipe_overlay", DefaultVertexFormat.POSITION_COLOR);
      RENDERTYPE_BREEZE_WIND = register("rendertype_breeze_wind", DefaultVertexFormat.NEW_ENTITY);
   }
}
