package net.fabricmc.fabric.dimension.api.v1.event.events;

@SuppressWarnings("all")
public class KeyPressEvent {

	private static final KeyPressEvent INSTANCE = new KeyPressEvent();

	public int key, scanCode, action;

	public static KeyPressEvent get(int key, int scanCode, int action) {
		INSTANCE.key = key;
		INSTANCE.scanCode = scanCode;
		INSTANCE.action = action;
		return INSTANCE;
	}

}
