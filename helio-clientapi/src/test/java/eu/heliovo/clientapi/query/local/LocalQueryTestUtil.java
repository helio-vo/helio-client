package eu.heliovo.clientapi.query.local;

import java.io.File;

public class LocalQueryTestUtil {

	public static void recursiveDelete(File file) {
		File[] files= file.listFiles();
		if (files != null)
			for (File each : files)
				recursiveDelete(each);
		file.delete();
	}
}
