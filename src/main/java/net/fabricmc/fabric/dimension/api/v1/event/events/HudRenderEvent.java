package net.fabricmc.fabric.dimension.api.v1.event.events;

import com.mojang.blaze3d.vertex.PoseStack;

@SuppressWarnings("all")
public class HudRenderEvent {

	private static final HudRenderEvent INSTANCE = new HudRenderEvent();

	public PoseStack poseStack;
	public float tickDelta;

	public static HudRenderEvent get(PoseStack poseStack, float tickDelta) {
		INSTANCE.poseStack = poseStack;
		INSTANCE.tickDelta = tickDelta;
		return INSTANCE;
	}

}
