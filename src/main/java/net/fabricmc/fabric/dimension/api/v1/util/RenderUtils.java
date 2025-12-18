package net.fabricmc.fabric.dimension.api.v1.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.font.JColor;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL40C;

import java.awt.*;
import java.lang.Math;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class RenderUtils {

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void endRender() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void unscaledProjection() {
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), 0, 1000, 3000));
    }

    public static void scaledProjection() {
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, (float) (mc.getWindow().getWidth() / mc.getWindow().getGuiScale()), (float) (mc.getWindow().getHeight() / mc.getWindow().getGuiScale()), 0, 1000, 3000));
    }

    public static Vec3 getCameraPos() {
        return mc.getBlockEntityRenderDispatcher().camera.getPosition();
    }

    public static BlockPos getCameraBlockPos() {
        return mc.getBlockEntityRenderDispatcher().camera.getBlockPosition();
    }

    public static void applyRegionalRenderOffset(PoseStack poseStack) {
        Vec3 camPos = getCameraPos();
        BlockPos blockPos = getCameraBlockPos();

        int regionX = (blockPos.getX() >> 9) * 512;
        int regionZ = (blockPos.getZ() >> 9) * 512;

        poseStack.translate(regionX - camPos.x, -camPos.y, regionZ - camPos.z);
    }

    public static Vector3d getInterpolatedEntityPosition(Entity entity) {
        Vec3i entityPos = entity.getOnPos();
        Vec3 prevEntityPos = new Vec3(entity.xOld, entity.yOld, entity.zOld);
        float tickDelta = mc.getDeltaFrameTime();

        return new Vector3d(Mth.lerp(tickDelta, prevEntityPos.x, entityPos.getX()),
                Mth.lerp(tickDelta, prevEntityPos.y, entityPos.getY()),
                Mth.lerp(tickDelta, prevEntityPos.z, entityPos.getZ()));
    }

    public static Vector3d getScreenSpaceCoordinate(Vector3d pos, PoseStack poseStack) {
        Camera camera = mc.getEntityRenderDispatcher().camera;
        Matrix4f matrix = poseStack.last().pose();
        int displayHeight = mc.getWindow().getHeight();
        int[] viewport = new int[4];
        Vector3f target = new Vector3f();

        double deltaX = pos.x - camera.getPosition().x;
        double deltaY = pos.y - camera.getPosition().y;
        double deltaZ = pos.z - camera.getPosition().z;

        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(matrix);

        Matrix4f matrixProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        Matrix4f matrixModel = new Matrix4f(RenderSystem.getModelViewMatrix());

        matrixProj.mul(matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);

        return new Vector3d(target.x / mc.getWindow().getGuiScale(),
                (displayHeight - target.y) / mc.getWindow().getGuiScale(),
                target.z);
    }

    public static boolean isOnScreen(Vector3d pos) {
        return pos != null && (pos.z > -1 && pos.z < 1);
    }

    public static class R2D {

        public static double[] getCenteredPos(double width, double height) {
            return new double[] { mc.getWindow().getWidth() / 2 - width / 2, mc.getWindow().getHeight() / 2 - height / 2 };
        }

        public static float[] getCheckmarkDimensions(float firstPart, float secondPart, float angle) {
            double a = Math.toRadians(angle - 90);
            double b = Math.toRadians(angle);

            double firstPointY = Math.sin(a) * firstPart;
            double firstPointX = Math.cos(a) * firstPart;

            double secondPointY = Math.sin(b) * secondPart;
            double secondPointX = Math.cos(b) * secondPart;

            double minX = Math.min(0, Math.min(firstPointX, secondPointX));
            double maxX = Math.max(0, Math.max(firstPointX, secondPointX));

            double minY = Math.min(0, Math.min(firstPointY, secondPointY));
            double maxY = Math.max(0, Math.max(firstPointY, secondPointY));

            double width = maxX - minX;
            double height = maxY - minY;

            return new float[] { (float) (minX), (float) (minY), (float) (maxX), (float) (maxY), (float) (width), (float) (height) };
        }

        public static void renderCheckmark(PoseStack poses, Color color, double x, double y, float firstPart, float secondPart, float width, float angle) {
            poses.pushPose();
            poses.translate(x, y, 0);
            poses.mulPose(Axis.ZP.rotationDegrees(angle));
            poses.translate(-secondPart / 2, firstPart / 2, 0);
            Matrix4f matrix = poses.last().pose();
            float a = (float) (color.getAlpha()) / 255.0F;
            float r = (float) (color.getRed()) / 255.0F;
            float g = (float) (color.getGreen()) / 255.0F;
            float b = (float) (color.getBlue()) / 255.0F;
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            /*
            2 -- 3
            |    |
            |    |
            |    |
            1 -- 4
             */
            bufferBuilder.vertex(matrix, 0, -firstPart, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, 0, 0, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, width, 0, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, width, -firstPart, 0).color(r, g, b, a).endVertex();

            /*
            4 ---------- 3
            |            |
            1 ---------- 2
             */
            bufferBuilder.vertex(matrix, 0, 0, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, secondPart, 0, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, secondPart, -width, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, 0, -width, 0).color(r, g, b, a).endVertex();

            BufferUploader.drawWithShader(bufferBuilder.end());
            endRender();
            poses.popPose();
        }

        public static void beginScissor(double x, double y, double endX, double endY) {
            double width = endX - x;
            double height = endY - y;
            width = Math.max(0, width);
            height = Math.max(0, height);
            float mulScale = (float) mc.getWindow().getGuiScale();
            int invertedY = (int) ((mc.getWindow().getGuiScaledHeight() - (y + height)) * mulScale);
            RenderSystem.enableScissor((int) (x * mulScale), invertedY, (int) (width * mulScale), (int) (height * mulScale));
        }

        public static void endScissor() {
            RenderSystem.disableScissor();
        }

        public static void renderTexture(PoseStack poses, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight) {
            double x1 = x0 + width;
            double y1 = y0 + height;
            double z = 0;
            renderTexturedQuad(poses.last().pose(),
                    x0,
                    x1,
                    y0,
                    y1,
                    z,
                    (u + 0.0F) / (float) textureWidth,
                    (u + (float) regionWidth) / (float) textureWidth,
                    (v + 0.0F) / (float) textureHeight,
                    (v + (float) regionHeight) / (float) textureHeight);
        }

        public static void renderRoundedShadowInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double rad, double samples, double wid) {
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

            double toX1 = toX - rad;
            double toY1 = toY - rad;
            double fromX1 = fromX + rad;
            double fromY1 = fromY + rad;
            double[][] map = new double[][] { new double[] { toX1, toY1 }, new double[] { toX1, fromY1 }, new double[] { fromX1, fromY1 },
                    new double[] { fromX1, toY1 } };
            for (int i = 0; i < map.length; i++) {
                double[] current = map[i];
                for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                    float rad1 = (float) Math.toRadians(r);
                    float sin = (float) (Math.sin(rad1) * rad);
                    float cos = (float) (Math.cos(rad1) * rad);
                    bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).endVertex();
                    float sin1 = (float) (sin + Math.sin(rad1) * wid);
                    float cos1 = (float) (cos + Math.cos(rad1) * wid);
                    bufferBuilder.vertex(matrix, (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(cr, cg, cb, 0f).endVertex();
                }
            }
            {
                double[] current = map[0];
                float rad1 = (float) Math.toRadians(0);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).endVertex();
                float sin1 = (float) (sin + Math.sin(rad1) * wid);
                float cos1 = (float) (cos + Math.cos(rad1) * wid);
                bufferBuilder.vertex(matrix, (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(cr, cg, cb, 0f).endVertex();
            }
            BufferUploader.drawWithShader(bufferBuilder.end());
        }

        public static void renderRoundedShadow(PoseStack poses, Color innerColor, double fromX, double fromY, double toX, double toY, double rad, double samples, double shadowWidth) {
            int color = innerColor.getRGB();
            Matrix4f matrix = poses.last().pose();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            renderRoundedShadowInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, rad, samples, shadowWidth);
            endRender();
        }

        public static void renderLoadingSpinner(PoseStack stack, float alpha, double x, double y, double rad, double width, double segments) {
            float v = alpha;
            stack.pushPose();
            stack.translate(x, y, 0);
            float rot = (System.currentTimeMillis() % 2000) / 2000f;
            stack.mulPose(Axis.ZP.rotationDegrees(rot * 360f));
            double segments1 = Mth.clamp(segments, 2, 90);

            Matrix4f matrix = stack.last().pose();
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
            for (double r = 0; r < 90; r += (90 / segments1)) {
                double rad1 = Math.toRadians(r);
                double sin = Math.sin(rad1);
                double cos = Math.cos(rad1);
                double offX = sin * rad;
                double offY = cos * rad;
                float prog = (float) r / 360f;
                prog -= rot;
                prog %= 1;
                Color hsb = Color.getHSBColor(prog, .6f, 1f);
                float g = hsb.getRed() / 255f;
                float h = hsb.getGreen() / 255f;
                float k = hsb.getBlue() / 255f;
                bufferBuilder.vertex(matrix, (float) offX, (float) offY, 0).color(g, h, k, v).endVertex();
                bufferBuilder.vertex(matrix, (float) (offX + sin * width), (float) (offY + cos * width), 0).color(g, h, k, v).endVertex();

            }
            BufferUploader.drawWithShader(bufferBuilder.end());
            stack.popPose();
            endRender();
        }

        private static void renderTexturedQuad(Matrix4f matrix, double x0, double x1, double y0, double y1, double z, float u0, float u1, float v0, float v1) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(matrix, (float) x0, (float) y1, (float) z).uv(u0, v1).endVertex();
            bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).uv(u1, v1).endVertex();
            bufferBuilder.vertex(matrix, (float) x1, (float) y0, (float) z).uv(u1, v0).endVertex();
            bufferBuilder.vertex(matrix, (float) x0, (float) y0, (float) z).uv(u0, v0).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        }

        public static void runWithinBlendMask(Runnable maskDrawer, Runnable regularDrawer) {
            RenderSystem.enableBlend();
            RenderSystem.colorMask(false, false, false, true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            maskDrawer.run();

            RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);

            regularDrawer.run();

            RenderSystem.defaultBlendFunc();
        }

        public static void renderCircle(PoseStack poses, Color c, double originX, double originY, double rad, int segments) {
            int segments1 = Mth.clamp(segments, 4, 360);
            int color = c.getRGB();

            Matrix4f matrix = poses.last().pose();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            for (int i = 0; i < 360; i += Math.min((360 / segments1), 360 - i)) {
                double radians = Math.toRadians(i);
                double sin = Math.sin(radians) * rad;
                double cos = Math.cos(radians) * rad;
                bufferBuilder.vertex(matrix, (float) (originX + sin), (float) (originY + cos), 0).color(g, h, k, f).endVertex();
            }
            BufferUploader.drawWithShader(bufferBuilder.end());
        }

        public static boolean isOnScreen(Vector3d pos) {
            return pos != null && (pos.z > -1 && pos.z < 1);
        }

        public static Vector3d getScreenSpaceCoordinate(Vector3d pos, PoseStack stack) {
            Camera camera = mc.getEntityRenderDispatcher().camera;
            Matrix4f matrix = stack.last().pose();
            int displayHeight = mc.getWindow().getHeight();
            int[] viewport = new int[4];
            Vector3f target = new Vector3f();

            double deltaX = pos.x - camera.getPosition().x;
            double deltaY = pos.y - camera.getPosition().y;
            double deltaZ = pos.z - camera.getPosition().z;

            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

            Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(matrix);

            Matrix4f matrixProj = new Matrix4f(RenderSystem.getProjectionMatrix());
            Matrix4f matrixModel = new Matrix4f(RenderSystem.getModelViewMatrix());

            matrixProj.mul(matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);

            return new Vector3d(target.x / mc.getWindow().getGuiScale(),
                    (displayHeight - target.y) / mc.getWindow().getGuiScale(),
                    target.z);
        }

        public static Vector3d screenSpaceToWorldOffset(double x, double y, double z) {
            double yCopy = y;
            double xCopy = x;
            Matrix4f projMat = RenderSystem.getProjectionMatrix();
            xCopy /= mc.getWindow().getScreenWidth();
            yCopy /= mc.getWindow().getScreenHeight();
            xCopy = xCopy * 2.0 - 1.0;
            yCopy = yCopy * 2.0 - 1.0;
            Vector4f pos = new Vector4f((float) xCopy, (float) yCopy, (float) z, 1.0F);
            pos.mul(projMat);
            if (pos.w() == 0.0F) {
                return null;
            } else {
                pos.normalize();
                return new Vector3d(pos.x(), pos.y(), pos.z());
            }
        }

        public static void renderQuad(PoseStack poses, Color c, double x1, double y1, double x2, double y2) {
            double x11 = x1;
            double x21 = x2;
            double y11 = y1;
            double y21 = y2;
            int color = c.getRGB();
            double j;
            if (x11 < x21) {
                j = x11;
                x11 = x21;
                x21 = j;
            }

            if (y11 < y21) {
                j = y11;
                y11 = y21;
                y21 = j;
            }
            Matrix4f matrix = poses.last().pose();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.vertex(matrix, (float) x11, (float) y21, 0.0F).color(g, h, k, f).endVertex();
            bufferBuilder.vertex(matrix, (float) x21, (float) y21, 0.0F).color(g, h, k, f).endVertex();
            bufferBuilder.vertex(matrix, (float) x21, (float) y11, 0.0F).color(g, h, k, f).endVertex();
            bufferBuilder.vertex(matrix, (float) x11, (float) y11, 0.0F).color(g, h, k, f).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
            endRender();
        }

        public static void renderQuadGradient(PoseStack poses, Color c2, Color c1, double x1, double y1, double x2, double y2, boolean vertical) {
            double x11 = x1;
            double x21 = x2;
            double y11 = y1;
            double y21 = y2;
            float r1 = c1.getRed() / 255f;
            float g1 = c1.getGreen() / 255f;
            float b1 = c1.getBlue() / 255f;
            float a1 = c1.getAlpha() / 255f;
            float r2 = c2.getRed() / 255f;
            float g2 = c2.getGreen() / 255f;
            float b2 = c2.getBlue() / 255f;
            float a2 = c2.getAlpha() / 255f;

            double j;

            if (x11 < x21) {
                j = x11;
                x11 = x21;
                x21 = j;
            }

            if (y11 < y21) {
                j = y11;
                y11 = y21;
                y21 = j;
            }
            Matrix4f matrix = poses.last().pose();
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            setupRender();

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            if (vertical) {
                bufferBuilder.vertex(matrix, (float) x11, (float) y11, 0.0F).color(r1, g1, b1, a1).endVertex();
                bufferBuilder.vertex(matrix, (float) x11, (float) y21, 0.0F).color(r2, g2, b2, a2).endVertex();
                bufferBuilder.vertex(matrix, (float) x21, (float) y21, 0.0F).color(r2, g2, b2, a2).endVertex();
                bufferBuilder.vertex(matrix, (float) x21, (float) y11, 0.0F).color(r1, g1, b1, a1).endVertex();
            } else {
                bufferBuilder.vertex(matrix, (float) x11, (float) y11, 0.0F).color(r1, g1, b1, a1).endVertex();
                bufferBuilder.vertex(matrix, (float) x11, (float) y21, 0.0F).color(r1, g1, b1, a1).endVertex();
                bufferBuilder.vertex(matrix, (float) x21, (float) y21, 0.0F).color(r2, g2, b2, a2).endVertex();
                bufferBuilder.vertex(matrix, (float) x21, (float) y11, 0.0F).color(r2, g2, b2, a2).endVertex();
            }

            BufferUploader.drawWithShader(bufferBuilder.end());
            endRender();
        }

        public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double rad, double samples) {
            renderRoundedQuadInternal(matrix, cr, cg, cb, ca, fromX, fromY, toX, toY, rad, rad, rad, rad, samples);
        }

        public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radC1, double radC2, double radC3, double radC4, double samples) {
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

            double[][] map = new double[][] { new double[] { toX - radC4, toY - radC4, radC4 }, new double[] { toX - radC2, fromY + radC2, radC2 },
                    new double[] { fromX + radC1, fromY + radC1, radC1 }, new double[] { fromX + radC3, toY - radC3, radC3 } };
            for (int i = 0; i < 4; i++) {
                double[] current = map[i];
                double rad = current[2];
                for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                    float rad1 = (float) Math.toRadians(r);
                    float sin = (float) (Math.sin(rad1) * rad);
                    float cos = (float) (Math.cos(rad1) * rad);
                    bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).endVertex();
                }
                float rad1 = (float) Math.toRadians((360 / 4d + i * 90d));
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).endVertex();
            }
            BufferUploader.drawWithShader(bufferBuilder.end());
        }

        public static void renderRoundedQuadWithShadow(PoseStack poses, Color c, double fromX, double fromY, double toX, double toY, double rad, double samples) {
            int color = c.getRGB();
            Matrix4f matrix = poses.last().pose();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            renderRoundedQuadInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, rad, rad, rad, rad, samples);

            renderRoundedShadow(poses, new Color(10, 10, 10, 100), fromX, fromY, toX, toY, rad, samples, 3);
            endRender();
        }

        public static void renderRoundedQuad(PoseStack poses, Color c, double fromX, double fromY, double toX, double toY, double radC1, double radC2, double radC3, double radC4, double samples) {
            int color = c.getRGB();
            Matrix4f matrix = poses.last().pose();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            renderRoundedQuadInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, radC1, radC2, radC3, radC4, samples);
            endRender();
        }

        public static void renderRoundedQuad(PoseStack stack, Color c, double x, double y, double x1, double y1, double rad, double samples) {
            renderRoundedQuad(stack, c, x, y, x1, y1, rad, rad, rad, rad, samples);
        }

        public static void renderRoundedOutlineInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radC1, double radC2, double radC3, double radC4, double width, double samples) {
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

            double[][] map = new double[][] { new double[] { toX - radC4, toY - radC4, radC4 }, new double[] { toX - radC2, fromY + radC2, radC2 },
                    new double[] { fromX + radC1, fromY + radC1, radC1 }, new double[] { fromX + radC3, toY - radC3, radC3 } };
            for (int i = 0; i < 4; i++) {
                double[] current = map[i];
                double rad = current[2];
                for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                    float rad1 = (float) Math.toRadians(r);
                    double sin1 = Math.sin(rad1);
                    float sin = (float) (sin1 * rad);
                    double cos1 = Math.cos(rad1);
                    float cos = (float) (cos1 * rad);
                    bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).endVertex();
                    bufferBuilder.vertex(matrix, (float) (current[0] + sin + sin1 * width), (float) (current[1] + cos + cos1 * width), 0.0F).color(cr, cg, cb, ca).endVertex();
                }
                float rad1 = (float) Math.toRadians((360 / 4d + i * 90d));
                double sin1 = Math.sin(rad1);
                float sin = (float) (sin1 * rad);
                double cos1 = Math.cos(rad1);
                float cos = (float) (cos1 * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).endVertex();
                bufferBuilder.vertex(matrix, (float) (current[0] + sin + sin1 * width), (float) (current[1] + cos + cos1 * width), 0.0F).color(cr, cg, cb, ca).endVertex();
            }
            int i = 0;
            double[] current = map[i];
            double rad = current[2];
            float cos = (float) (rad);
            bufferBuilder.vertex(matrix, (float) current[0], (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).endVertex();
            bufferBuilder.vertex(matrix, (float) (current[0]), (float) (current[1] + cos + width), 0.0F).color(cr, cg, cb, ca).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        }

        public static void renderRoundedOutline(PoseStack poses, Color c, double fromX, double fromY, double toX, double toY, double rad1, double rad2, double rad3, double rad4, double width, double samples) {
            int color = c.getRGB();
            Matrix4f matrix = poses.last().pose();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            renderRoundedOutlineInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, rad1, rad2, rad3, rad4, width, samples);
            endRender();
        }

        public static void renderLine(PoseStack stack, Color c, double x, double y, double x1, double y1) {
            float g = c.getRed() / 255f;
            float h = c.getGreen() / 255f;
            float k = c.getBlue() / 255f;
            float f = c.getAlpha() / 255f;
            Matrix4f m = stack.last().pose();
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.vertex(m, (float) x, (float) y, 0f).color(g, h, k, f).endVertex();
            bufferBuilder.vertex(m, (float) x1, (float) y1, 0f).color(g, h, k, f).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
            endRender();
        }

    }

}
