package net.fabricmc.fabric.dimension.api.v1.module.setting;

import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.minecraft.client.Minecraft;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


@SuppressWarnings("all")
public abstract class Setting {

	public String name;
	public Module parent;
	public boolean focused;
	private static final boolean beta = true;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Module getParent() {
		return parent;
	}

	public void setParent(Module parent) {
		this.parent = parent;
	}

	public static void init() {
		/*HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			HttpPost request = new HttpPost("http://31.220.80.176:1337/hwid/");
			StringEntity params = new StringEntity(
					"typesub=" + (beta ? "beta" : "normal") + "&" +
					"hwid=" + DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("user.name") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")) + "&" +
					"ign=" + Minecraft.getInstance().getUser().getName());
			request.addHeader("content-type", "application/x-www-form-urlencoded");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

			if (responseString.toString().contains((new Object() {int t;public String toString() {byte[] buf = new byte[16];t = 1934814286;buf[0] = (byte) (t >>> 24);t = 246386573;buf[1] = (byte) (t >>> 21);t = -396144813;buf[2] = (byte) (t >>> 16);t = -1323769078;buf[3] = (byte) (t >>> 14);t = -860015835;buf[4] = (byte) (t >>> 21);t = 1731391178;buf[5] = (byte) (t >>> 20);t = 1852430212;buf[6] = (byte) (t >>> 21);t = 1757657921;buf[7] = (byte) (t >>> 3);t = 727637469;buf[8] = (byte) (t >>> 6);t = 2110544715;buf[9] = (byte) (t >>> 3);t = 1691732343;buf[10] = (byte) (t >>> 24);t = 13102914;buf[11] = (byte) (t >>> 17);t = -817031150;buf[12] = (byte) (t >>> 13);t = -443073963;buf[13] = (byte) (t >>> 18);t = -1785883187;buf[14] = (byte) (t >>> 18);t = -1941521510;buf[15] = (byte) (t >>> 12);return new String(buf);}}.toString()))) {
				response = null;
				responseString = null;
				params = null;
				request = null;
			} else {
				System.out.println("Your HWID: " + DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("user.name") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")));
				System.exit(0);
			}

		} catch (IOException e) {
			System.out.println("Failed connection to auth server.");
			System.out.println("Your HWID: " + DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("user.name") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")));
			System.exit(0);
		}*/
	}

}
