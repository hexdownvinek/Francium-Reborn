package net.fabricmc.fabric.dimension.api.v1.event.events;

import com.mojang.blaze3d.vertex.PoseStack;

@SuppressWarnings("all")
public class WorldRenderEvent {

	private static final WorldRenderEvent INSTANCE = new WorldRenderEvent();

	public PoseStack poseStack;
	public float partialTicks;

	public static WorldRenderEvent get(PoseStack poseStack, float partialTicks) {
		INSTANCE.poseStack = poseStack;
		INSTANCE.partialTicks = partialTicks;
		return INSTANCE;
	}
}
