package net.fabricmc.fabric.dimension.api.v1.util;

import net.fabricmc.fabric.dimension.api.v1.mixin.MinecraftAccessor;
import net.fabricmc.fabric.dimension.api.v1.mixin.MouseHandlerAccessor;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class MouseSimulation {
    public static HashMap<Integer, Boolean> mouseButtons = new HashMap<>();
    public static ExecutorService clickExecutor = Executors.newFixedThreadPool(100);

    public static MouseHandlerAccessor getMouseHandler() {
        return (MouseHandlerAccessor) ((MinecraftAccessor) mc).getMouseHandler();
    }

    public static boolean isMouseButtonPressed(int keyCode) {
        Boolean key = mouseButtons.get(keyCode);
        return key != null ? key : false;
    }

    public static void mousePress(int keyCode) {
        mouseButtons.put(keyCode, true);
        getMouseHandler().press(mc.getWindow().getWindow(), keyCode, GLFW.GLFW_PRESS, 0);
    }

    public static void mouseRelease(int keyCode) {
        getMouseHandler().press(mc.getWindow().getWindow(), keyCode, GLFW.GLFW_RELEASE, 0);
    }

    public static void mouseClick(int keyCode, int millis) {
        clickExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    MouseSimulation.mousePress(keyCode);
                    Thread.sleep(millis);
                    MouseSimulation.mouseRelease(keyCode);
                } catch (InterruptedException e) {

                }
            }
        });
    }

    public static void mouseClick(int keyCode) {
        mouseClick(keyCode, 20);
    }
}
